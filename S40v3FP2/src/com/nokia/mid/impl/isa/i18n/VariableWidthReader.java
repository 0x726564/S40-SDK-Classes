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

   public VariableWidthReader(InputStream var1, byte[] var2) throws UnsupportedEncodingException {
      super(var1, var2);
      String var3 = new String(var2, 0, var2.length - 1, "ASCII");
      var3 = var3.toUpperCase().replace('_', '-');
      if (var3.equals("UTF-8") || var3.equals("UTF8")) {
         this.utf8 = true;
         this.maxByteLen = 3;
      }

      this.readAhead = new byte[this.maxByteLen];
      this.pos = 0;
   }

   public void mark(int var1) throws IOException {
      throw new IOException("mark() not supported");
   }

   public boolean markSupported() {
      return false;
   }

   public synchronized int read(char[] var1, int var2, int var3) throws IOException {
      int var4 = 0;
      boolean var6 = false;

      label60:
      while(var4 < var3) {
         int var7 = this.get(var4 == 0);
         if (var7 == -1) {
            var6 = true;
            if (this.pos == 0) {
               break;
            }
         } else {
            if (var7 == -2) {
               return var4;
            }

            this.readAhead[this.pos++] = (byte)(var7 & 255);
         }

         while(var4 < var3 && this.pos > 0) {
            int var5 = CharsetConv.byteToChar(this.nativeConvInfo, this.readAhead, 0, this.pos, var1, var4 + var2);
            switch(var5) {
            case -2:
               if (!var6 && this.pos < this.maxByteLen) {
                  continue label60;
               }

               var5 = this.pos;
               var1[var4 + var2] = '�';
               ++var4;
               break;
            case -1:
               if (this.utf8) {
                  var5 = this.lengthOfInvalidUTF8Sequence();
               } else {
                  var5 = this.pos;
               }

               var1[var4 + var2] = '�';
               ++var4;
               break;
            case 0:
               this.pos = 0;
               continue label60;
            default:
               if (var5 <= 0) {
                  throw new IOException("Problem during character conversion");
               }

               ++var4;
            }

            this.pos -= var5;
            System.arraycopy(this.readAhead, var5, this.readAhead, 0, this.pos);
         }
      }

      return var4 == 0 ? -1 : var4;
   }

   public void reset() throws IOException {
      this.pos = 0;
      super.reset();
   }

   public void close() throws IOException {
      super.close();
   }

   private int get(boolean var1) throws IOException {
      return !var1 && this.in.available() <= 0 ? -2 : this.in.read();
   }

   private int lengthOfInvalidUTF8Sequence() {
      if ((this.readAhead[0] & 128) == 0) {
         return 1;
      } else {
         for(int var1 = 1; var1 < this.pos; ++var1) {
            if ((this.readAhead[var1] & 192) != 128 || (this.readAhead[0] & 128 >>> var1) == 0) {
               return var1;
            }
         }

         return this.pos;
      }
   }
}
