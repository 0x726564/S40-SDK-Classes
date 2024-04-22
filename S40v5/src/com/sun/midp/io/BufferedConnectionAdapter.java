package com.sun.midp.io;

import java.io.IOException;

public abstract class BufferedConnectionAdapter extends ConnectionBaseAdapter {
   protected boolean eof;
   protected byte[] buf;
   protected int count;
   protected int pos;

   protected BufferedConnectionAdapter(int var1) {
      if (var1 > 0) {
         this.buf = new byte[var1];
      }

   }

   public int readBytes(byte[] var1, int var2, int var3) throws IOException {
      if (this.count == 0) {
         if (this.eof) {
            return -1;
         }

         if (this.buf == null || var3 >= this.buf.length) {
            return this.nonBufferedRead(var1, var2, var3);
         }

         int var4 = this.nonBufferedRead(this.buf, 0, this.buf.length);
         this.pos = 0;
         if (var4 <= 0) {
            return var4;
         }

         this.count = var4;
      }

      if (var3 > this.count) {
         var3 = this.count;
      }

      System.arraycopy(this.buf, this.pos, var1, var2, var3);
      this.count -= var3;
      this.pos += var3;
      return var3;
   }

   protected int readBytesNonBlocking(byte[] var1, int var2, int var3) throws IOException {
      return 0;
   }

   public int available() throws IOException {
      if (this.buf == null) {
         return 0;
      } else if (this.count > 0) {
         return this.count;
      } else {
         int var1;
         if ((var1 = this.readBytesNonBlocking(this.buf, 0, this.buf.length)) == -1) {
            return 0;
         } else {
            this.pos = 0;
            this.count = var1;
            return this.count;
         }
      }
   }

   protected abstract int nonBufferedRead(byte[] var1, int var2, int var3) throws IOException;
}
