package com.sun.ukit.jaxp;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class ReaderUTF16 extends Reader {
   private InputStream is;
   private char bo;

   public ReaderUTF16(InputStream var1, char var2) {
      switch(var2) {
      case 'b':
      case 'l':
         this.bo = var2;
         this.is = var1;
         return;
      default:
         throw new IllegalArgumentException("");
      }
   }

   public int read(char[] var1, int var2, int var3) throws IOException {
      int var4 = 0;
      int var5;
      if (this.bo == 'b') {
         while(var4 < var3) {
            if ((var5 = this.is.read()) < 0) {
               return var4 != 0 ? var4 : -1;
            }

            var1[var2++] = (char)(var5 << 8 | this.is.read() & 255);
            ++var4;
         }
      } else {
         while(var4 < var3) {
            if ((var5 = this.is.read()) < 0) {
               return var4 != 0 ? var4 : -1;
            }

            var1[var2++] = (char)(this.is.read() << 8 | var5 & 255);
            ++var4;
         }
      }

      return var4;
   }

   public int read() throws IOException {
      int var1;
      if ((var1 = this.is.read()) < 0) {
         return -1;
      } else {
         char var2;
         if (this.bo == 'b') {
            var2 = (char)(var1 << 8 | this.is.read() & 255);
         } else {
            var2 = (char)(this.is.read() << 8 | var1 & 255);
         }

         return var2;
      }
   }

   public void close() throws IOException {
      this.is.close();
   }
}
