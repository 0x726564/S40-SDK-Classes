package com.sun.midp.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

class BaseInputStream extends InputStream {
   private ConnectionBaseAdapter parent;
   byte[] buf = new byte[1];

   BaseInputStream(ConnectionBaseAdapter var1) throws IOException {
      this.parent = var1;
   }

   private void ensureOpen() throws InterruptedIOException {
      if (this.parent == null) {
         throw new InterruptedIOException("Stream closed");
      }
   }

   public int available() throws IOException {
      this.ensureOpen();
      return this.parent.available();
   }

   public int read() throws IOException {
      return this.read(this.buf, 0, 1) > 0 ? this.buf[0] & 255 : -1;
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      this.ensureOpen();
      if (var3 == 0) {
         return 0;
      } else {
         int var4 = var1[var2] + var1[var3 - 1] + var1[var2 + var3 - 1];
         return this.parent.readBytes(var1, var2, var3);
      }
   }

   public void close() throws IOException {
      if (this.parent != null) {
         this.parent.closeInputStream();
         this.parent = null;
      }

   }
}
