package com.nokia.mid.impl.isa.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class VariableWidthReader extends ReaderImpl {
   private byte[] bl;
   private int pos;
   private boolean bm;

   VariableWidthReader() {
   }

   public VariableWidthReader(InputStream var1, byte[] var2) throws UnsupportedEncodingException {
      super(var1, var2);
      String var3;
      if ((var3 = (new String(var2, 0, var2.length - 1, "ASCII")).toUpperCase().replace('_', '-')).equals("UTF-8") || var3.equals("UTF8")) {
         this.bm = true;
         this.maxByteLen = 3;
      }

      this.bl = new byte[this.maxByteLen];
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

      label97:
      while(var4 < var3) {
         boolean var7 = var4 == 0;
         int var10000 = !var7 && this.in.available() <= 0 ? -2 : this.in.read();
         int var5 = var10000;
         if (var10000 == -1) {
            var6 = true;
            if (this.pos == 0) {
               break;
            }
         } else {
            if (var5 == -2) {
               return var4;
            }

            this.bl[this.pos++] = (byte)var5;
         }

         while(var4 < var3 && this.pos > 0) {
            switch(var5 = CharsetConv.byteToChar(this.nativeConvInfo, this.bl, 0, this.pos, var1, var4 + var2)) {
            case -2:
               if (!var6 && this.pos < this.maxByteLen) {
                  continue label97;
               }

               var5 = this.pos;
               var1[var4 + var2] = '�';
               ++var4;
               break;
            case -1:
               if (!this.bm) {
                  var5 = this.pos;
               } else {
                  VariableWidthReader var8 = this;
                  if ((this.bl[0] & 128) == 0) {
                     var10000 = 1;
                  } else {
                     int var9 = 1;

                     while(true) {
                        if (var9 >= var8.pos) {
                           var10000 = var8.pos;
                           break;
                        }

                        if ((var8.bl[var9] & 192) != 128 || (var8.bl[0] & 128 >>> var9) == 0) {
                           var10000 = var9;
                           break;
                        }

                        ++var9;
                     }
                  }

                  var5 = var10000;
               }

               var1[var4 + var2] = '�';
               ++var4;
               break;
            case 0:
               this.pos = 0;
               continue label97;
            default:
               if (var5 <= 0) {
                  throw new IOException("Problem during character conversion");
               }

               ++var4;
            }

            this.pos -= var5;
            System.arraycopy(this.bl, var5, this.bl, 0, this.pos);
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
}
