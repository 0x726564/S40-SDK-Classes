package com.sun.ukit.jaxp;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

public class ReaderUTF8 extends Reader {
   private InputStream is;

   public ReaderUTF8(InputStream is) {
      this.is = is;
   }

   public int read(char[] cbuf, int off, int len) throws IOException {
      int num;
      for(num = 0; num < len; ++num) {
         int val;
         if ((val = this.is.read()) < 0) {
            return num != 0 ? num : -1;
         }

         switch(val & 240) {
         case 192:
         case 208:
            cbuf[off++] = (char)((val & 31) << 6 | this.is.read() & 63);
            break;
         case 224:
            cbuf[off++] = (char)((val & 15) << 12 | (this.is.read() & 63) << 6 | this.is.read() & 63);
            break;
         case 240:
            throw new UnsupportedEncodingException();
         default:
            cbuf[off++] = (char)val;
         }
      }

      return num;
   }

   public int read() throws IOException {
      int val;
      if ((val = this.is.read()) < 0) {
         return -1;
      } else {
         switch(val & 240) {
         case 192:
         case 208:
            val = (val & 31) << 6 | this.is.read() & 63;
            break;
         case 224:
            val = (val & 15) << 12 | (this.is.read() & 63) << 6 | this.is.read() & 63;
            break;
         case 240:
            throw new UnsupportedEncodingException();
         }

         return val;
      }
   }

   public void close() throws IOException {
      this.is.close();
   }
}
