package com.nokia.mid.impl.isa.io.protocol.internal.wap;

import java.io.IOException;
import java.io.OutputStream;

class PrivateOutputStream extends OutputStream {
   private Protocol bw;

   public PrivateOutputStream(Protocol var1) throws IOException {
      this.bw = var1;
   }

   private void ensureOpen() throws IOException {
      if (this.bw == null) {
         throw new IOException("Stream closed");
      }
   }

   public void write(int var1) throws IOException {
      this.ensureOpen();

      while(this.bw.write0((byte[])null, var1, 1) == 0) {
         Thread.yield();
      }

   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.ensureOpen();
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var3 != 0) {
         int var4 = 0;

         while((var4 += this.bw.write0(var1, var2 + var4, var3 - var4)) != var3) {
            Thread.yield();
         }

      }
   }

   public void close() throws IOException {
      if (this.bw != null) {
         this.ensureOpen();
         this.bw.I();
         this.bw.em = false;
         this.bw = null;
      }

   }
}
