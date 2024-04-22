package com.nokia.mid.impl.isa.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class FixedWidthReader extends ReaderImpl {
   private byte[] encoding;

   FixedWidthReader() {
   }

   public FixedWidthReader(InputStream var1, byte[] var2) throws UnsupportedEncodingException {
      super(var1, var2);
      this.encoding = var2;
   }

   public void mark(int var1) throws IOException {
      if (this.in.markSupported()) {
         this.in.mark(var1 * this.maxByteLen);
      } else {
         throw new IOException("mark() not supported");
      }
   }

   public boolean markSupported() {
      return this.in.markSupported();
   }

   public synchronized int read() throws IOException {
      char[] var1 = new char[1];
      byte[] var2 = new byte[this.maxByteLen];

      int var3;
      for(var3 = 0; var3 == 0; var3 = CharsetConv.byteToChar(this.nativeConvInfo, var2, 0, this.maxByteLen, var1, 0)) {
         if (this.in.read(var2) == -1) {
            return -1;
         }
      }

      return var3 > 0 ? var1[0] : '�';
   }

   public synchronized int read(char[] var1, int var2, int var3) throws IOException {
      byte[] var4 = new byte[this.maxByteLen * var3];
      int var5 = this.in.read(var4);
      if (var5 == -1) {
         return -1;
      } else {
         int var6 = CharsetConv.byteToCharArray(this.encoding, var4, 0, var5, var1, var2, var3);
         if (var6 < 0) {
            throw new IOException("Problem during buffer conversion");
         } else {
            return var6;
         }
      }
   }

   public long skip(long var1) throws IOException {
      if (var1 < 0L) {
         throw new IllegalArgumentException("skip value is negative");
      } else {
         var1 = Math.min(var1, 8192L);
         long var3 = this.in.skip(var1 * (long)this.maxByteLen);
         return var3 / (long)this.maxByteLen;
      }
   }
}
