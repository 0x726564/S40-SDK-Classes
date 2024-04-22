package com.nokia.mid.impl.isa.io.protocol.internal.wap;

import java.io.IOException;
import java.io.OutputStream;

class PrivateOutputStream extends OutputStream {
   private Protocol parent;

   public PrivateOutputStream(Protocol parent) throws IOException {
      this.parent = parent;
   }

   void ensureOpen() throws IOException {
      if (this.parent == null) {
         throw new IOException("Stream closed");
      }
   }

   public void write(int b) throws IOException {
      this.ensureOpen();

      while(true) {
         int res = this.parent.write0((byte[])null, b, 1);
         if (res != 0) {
            return;
         }

         Thread.yield();
      }
   }

   public void write(byte[] b, int off, int len) throws IOException {
      this.ensureOpen();
      if (b == null) {
         throw new NullPointerException();
      } else if (len != 0) {
         int n = 0;

         while(true) {
            n += this.parent.write0(b, off + n, len - n);
            if (n == len) {
               return;
            }

            Thread.yield();
         }
      }
   }

   public void close() throws IOException {
      if (this.parent != null) {
         this.ensureOpen();
         this.parent.realClose();
         this.parent.osopen = false;
         this.parent = null;
      }

   }
}
