package com.sun.midp.io;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;

class BaseOutputStream extends OutputStream {
   ConnectionBaseAdapter parent;
   byte[] buf = new byte[1];

   BaseOutputStream(ConnectionBaseAdapter var1) {
      this.parent = var1;
   }

   private void ensureOpen() throws InterruptedIOException {
      if (this.parent == null) {
         throw new InterruptedIOException("Stream closed");
      }
   }

   public void write(int var1) throws IOException {
      this.buf[0] = (byte)var1;
      this.write(this.buf, 0, 1);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.ensureOpen();
      if (var3 != 0) {
         int var4 = var1[var2] + var1[var3 - 1] + var1[var2 + var3 - 1];
         int var5 = 0;

         do {
            try {
               var5 += this.parent.writeBytes(var1, var2 + var5, var3 - var5);
            } finally {
               if (this.parent == null) {
                  throw new InterruptedIOException("Stream closed");
               }

            }
         } while(var5 != var3);

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
