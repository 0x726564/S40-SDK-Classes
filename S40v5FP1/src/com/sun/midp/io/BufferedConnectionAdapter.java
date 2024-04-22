package com.sun.midp.io;

import java.io.IOException;

public abstract class BufferedConnectionAdapter extends ConnectionBaseAdapter {
   protected boolean eof;
   protected byte[] buf;
   protected int count;
   protected int pos;

   protected BufferedConnectionAdapter(int sizeOfBuffer) {
      if (sizeOfBuffer > 0) {
         this.buf = new byte[sizeOfBuffer];
      }

   }

   public int readBytes(byte[] b, int off, int len) throws IOException {
      if (this.count == 0) {
         if (this.eof) {
            return -1;
         }

         if (this.buf == null || len >= this.buf.length) {
            return this.nonBufferedRead(b, off, len);
         }

         int res = this.nonBufferedRead(this.buf, 0, this.buf.length);
         this.pos = 0;
         if (res <= 0) {
            return res;
         }

         this.count = res;
      }

      if (len > this.count) {
         len = this.count;
      }

      System.arraycopy(this.buf, this.pos, b, off, len);
      this.count -= len;
      this.pos += len;
      return len;
   }

   protected int readBytesNonBlocking(byte[] b, int off, int len) throws IOException {
      return 0;
   }

   public int available() throws IOException {
      if (this.buf == null) {
         return 0;
      } else if (this.count > 0) {
         return this.count;
      } else {
         int bytesRead = this.readBytesNonBlocking(this.buf, 0, this.buf.length);
         if (bytesRead == -1) {
            return 0;
         } else {
            this.pos = 0;
            this.count = bytesRead;
            return this.count;
         }
      }
   }

   protected abstract int nonBufferedRead(byte[] var1, int var2, int var3) throws IOException;
}
