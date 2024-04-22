package com.sun.midp.io;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;

class BaseOutputStream extends OutputStream {
   ConnectionBaseAdapter parent;
   byte[] buf = new byte[1];

   BaseOutputStream(ConnectionBaseAdapter p) {
      this.parent = p;
   }

   private void ensureOpen() throws InterruptedIOException {
      if (this.parent == null) {
         throw new InterruptedIOException("Stream closed");
      }
   }

   public void write(int b) throws IOException {
      this.buf[0] = (byte)b;
      this.write(this.buf, 0, 1);
   }

   public void write(byte[] b, int off, int len) throws IOException {
      this.ensureOpen();
      if (len != 0) {
         if (off >= 0 && len >= 0 && off + len <= b.length) {
            int bytesWritten = 0;

            do {
               try {
                  bytesWritten += this.parent.writeBytes(b, off + bytesWritten, len - bytesWritten);
               } finally {
                  if (this.parent == null) {
                     throw new InterruptedIOException("Stream closed");
                  }

               }
            } while(bytesWritten != len);

         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public void flush() throws IOException {
      this.ensureOpen();
      this.parent.flush();
   }

   public void close() throws IOException {
      if (this.parent != null) {
         this.parent.closeOutputStream();
         this.parent = null;
      }

   }
}
