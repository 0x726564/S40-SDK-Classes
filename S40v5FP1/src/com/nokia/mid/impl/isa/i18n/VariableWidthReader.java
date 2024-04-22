package com.nokia.mid.impl.isa.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class VariableWidthReader extends ReaderImpl {
   private byte[] readAhead;
   private int pos;
   private boolean utf8;

   VariableWidthReader() {
   }

   public VariableWidthReader(InputStream in, byte[] enc) throws UnsupportedEncodingException {
      super(in, enc);
      String tmp = new String(enc, 0, enc.length - 1, "ASCII");
      tmp = tmp.toUpperCase().replace('_', '-');
      if (tmp.equals("UTF-8") || tmp.equals("UTF8")) {
         this.utf8 = true;
         this.maxByteLen = 3;
      }

      this.readAhead = new byte[this.maxByteLen];
      this.pos = 0;
   }

   public void mark(int sLimit) throws IOException {
      throw new IOException("mark() not supported");
   }

   public boolean markSupported() {
      return false;
   }

   public synchronized int read(char[] cbuf, int off, int len) throws IOException {
      int nbCharRead = 0;
      boolean eof = false;

      label61:
      while(nbCharRead < len) {
         int c = this.get(nbCharRead == 0);
         if (c == -1) {
            eof = true;
            if (this.pos == 0) {
               break;
            }
         } else {
            if (c == -2) {
               return nbCharRead;
            }

            this.readAhead[this.pos++] = (byte)(c & 255);
         }

         while(nbCharRead < len && this.pos > 0) {
            int byteLen = CharsetConv.byteToChar(this.nativeConvInfo, this.readAhead, 0, this.pos, cbuf, nbCharRead + off);
            switch(byteLen) {
            case -2:
               if (!eof && this.pos < this.maxByteLen) {
                  continue label61;
               }

               byteLen = this.pos;
               cbuf[nbCharRead + off] = '�';
               ++nbCharRead;
               break;
            case -1:
               if (this.utf8) {
                  byteLen = this.lengthOfInvalidUTF8Sequence();
               } else {
                  byteLen = this.pos;
               }

               cbuf[nbCharRead + off] = '�';
               ++nbCharRead;
               break;
            case 0:
               this.pos = 0;
               continue label61;
            default:
               if (byteLen <= 0) {
                  throw new IOException("Problem during character conversion");
               }

               ++nbCharRead;
            }

            this.pos -= byteLen;
            System.arraycopy(this.readAhead, byteLen, this.readAhead, 0, this.pos);
         }
      }

      return nbCharRead == 0 ? -1 : nbCharRead;
   }

   public void reset() throws IOException {
      this.pos = 0;
      super.reset();
   }

   public void close() throws IOException {
      super.close();
   }

   private int get(boolean block) throws IOException {
      return !block && this.in.available() <= 0 ? -2 : this.in.read();
   }

   private int lengthOfInvalidUTF8Sequence() {
      if ((this.readAhead[0] & 128) == 0) {
         return 1;
      } else {
         for(int i = 1; i < this.pos; ++i) {
            if ((this.readAhead[i] & 192) != 128 || (this.readAhead[0] & 128 >>> i) == 0) {
               return i;
            }
         }

         return this.pos;
      }
   }
}
