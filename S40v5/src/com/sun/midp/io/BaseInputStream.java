package com.sun.midp.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

class BaseInputStream extends InputStream {
   private ConnectionBaseAdapter cG;
   private byte[] buf = new byte[1];

   BaseInputStream(ConnectionBaseAdapter var1) throws IOException {
      this.cG = var1;
   }

   private void ensureOpen() throws InterruptedIOException {
      if (this.cG == null) {
         throw new InterruptedIOException("Stream closed");
      }
   }

   public int available() throws IOException {
      this.ensureOpen();
      return this.cG.available();
   }

   public int read() throws IOException {
      return this.read(this.buf, 0, 1) > 0 ? this.buf[0] & 255 : -1;
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      this.ensureOpen();
      if (var3 == 0) {
         return 0;
      } else if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length) {
         return this.cG.readBytes(var1, var2, var3);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void close() throws IOException {
      if (this.cG != null) {
         this.cG.closeInputStream();
         this.cG = null;
      }

   }
}
