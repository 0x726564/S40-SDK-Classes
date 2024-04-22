package com.sun.ukit.jaxp;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class ReaderUTF16 extends Reader {
   private InputStream is;
   private char bo;

   public ReaderUTF16(InputStream is, char bo) {
      switch(bo) {
      case 'b':
      case 'l':
         this.bo = bo;
         this.is = is;
         return;
      default:
         throw new IllegalArgumentException("");
      }
   }

   public int read(char[] cbuf, int off, int len) throws IOException {
      int num = 0;
      int val;
      if (this.bo == 'b') {
         while(num < len) {
            if ((val = this.is.read()) < 0) {
               return num != 0 ? num : -1;
            }

            cbuf[off++] = (char)(val << 8 | this.is.read() & 255);
            ++num;
         }
      } else {
         while(num < len) {
            if ((val = this.is.read()) < 0) {
               return num != 0 ? num : -1;
            }

            cbuf[off++] = (char)(this.is.read() << 8 | val & 255);
            ++num;
         }
      }

      return num;
   }

   public int read() throws IOException {
      int val;
      if ((val = this.is.read()) < 0) {
         return -1;
      } else {
         char val;
         if (this.bo == 'b') {
            val = (char)(val << 8 | this.is.read() & 255);
         } else {
            val = (char)(this.is.read() << 8 | val & 255);
         }

         return val;
      }
   }

   public void close() throws IOException {
      this.is.close();
   }
}
