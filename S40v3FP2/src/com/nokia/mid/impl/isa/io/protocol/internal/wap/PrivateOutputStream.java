package com.nokia.mid.impl.isa.io.protocol.internal.wap;

import java.io.IOException;
import java.io.OutputStream;

class PrivateOutputStream extends OutputStream {
   private Protocol parent;

   public PrivateOutputStream(Protocol var1) throws IOException {
      this.parent = var1;
   }

   void ensureOpen() throws IOException {
      if (this.parent == null) {
         throw new IOException("Stream closed");
      }
   }

   public void write(int var1) throws IOException {
      this.ensureOpen();

      while(true) {
         int var2 = this.parent.write0((byte[])null, var1, 1);
         if (var2 != 0) {
            return;
         }

         Thread.yield();
      }
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.ensureOpen();
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var3 != 0) {
         int var4 = 0;

         while(true) {
            var4 += this.parent.write0(var1, var2 + var4, var3 - var4);
            if (var4 == var3) {
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
