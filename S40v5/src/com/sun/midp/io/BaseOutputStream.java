package com.sun.midp.io;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;

class BaseOutputStream extends OutputStream {
   private ConnectionBaseAdapter cG;
   private byte[] buf = new byte[1];

   BaseOutputStream(ConnectionBaseAdapter var1) {
      this.cG = var1;
   }

   private void ensureOpen() throws InterruptedIOException {
      if (this.cG == null) {
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
         if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length) {
            int var4 = 0;

            do {
               try {
                  var4 += this.cG.writeBytes(var1, var2 + var4, var3 - var4);
               } finally {
                  if (this.cG == null) {
                     throw new InterruptedIOException("Stream closed");
                  }

               }
            } while(var4 != var3);

         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public void flush() throws IOException {
      this.ensureOpen();
      this.cG.flush();
   }

   public void close() throws IOException {
      if (this.cG != null) {
         this.cG.closeOutputStream();
         this.cG = null;
      }

   }
}
