package com.sun.midp.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

class BaseInputStream extends InputStream {
   private ConnectionBaseAdapter parent;
   byte[] buf = new byte[1];

   BaseInputStream(ConnectionBaseAdapter parent) throws IOException {
      this.parent = parent;
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

   public int read(byte[] b, int off, int len) throws IOException {
      this.ensureOpen();
      if (len == 0) {
         return 0;
      } else if (off >= 0 && len >= 0 && off + len <= b.length) {
         return this.parent.readBytes(b, off, len);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void close() throws IOException {
      if (this.parent != null) {
         this.parent.closeInputStream();
         this.parent = null;
      }

   }
}
