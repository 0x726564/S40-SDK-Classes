package com.sun.ukit.jaxp;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

public class ReaderUTF8 extends Reader {
   private InputStream is;

   public ReaderUTF8(InputStream var1) {
      this.is = var1;
   }

   public int read(char[] var1, int var2, int var3) throws IOException {
      int var4;
      for(var4 = 0; var4 < var3; ++var4) {
         int var5;
         if ((var5 = this.is.read()) < 0) {
            return var4 != 0 ? var4 : -1;
         }

         switch(var5 & 240) {
         case 192:
         case 208:
            var1[var2++] = (char)((var5 & 31) << 6 | this.is.read() & 63);
            break;
         case 224:
            var1[var2++] = (char)((var5 & 15) << 12 | (this.is.read() & 63) << 6 | this.is.read() & 63);
            break;
         case 240:
            throw new UnsupportedEncodingException();
         default:
            var1[var2++] = (char)var5;
         }
      }

      return var4;
   }

   public int read() throws IOException {
      int var1;
      if ((var1 = this.is.read()) < 0) {
         return -1;
      } else {
         switch(var1 & 240) {
         case 192:
         case 208:
            var1 = (var1 & 31) << 6 | this.is.read() & 63;
            break;
         case 224:
            var1 = (var1 & 15) << 12 | (this.is.read() & 63) << 6 | this.is.read() & 63;
            break;
         case 240:
            throw new UnsupportedEncodingException();
         }

         return var1;
      }
   }

   public void close() throws IOException {
      this.is.close();
   }
}
