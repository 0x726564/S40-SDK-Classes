package com.nokia.mid.impl.isa.io.protocol.internal.wap;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

class PrivateInputStream extends InputStream {
   private Protocol bw;
   private boolean eof = false;
   private static Object af = new Object();

   public PrivateInputStream(Protocol var1) throws IOException {
      this.bw = var1;
   }

   private void ensureOpen() throws IOException {
      if (this.bw == null) {
         throw new IOException("Stream closed");
      }
   }

   public int read() throws IOException {
      this.ensureOpen();
      if (this.eof) {
         return -1;
      } else {
         int var1;
         synchronized(af) {
            while((var1 = this.bw.read0((byte[])null, 0, 1)) == -2) {
            }
         }

         if (var1 == -1) {
            this.eof = true;
         }

         if (this.bw == null) {
            throw new InterruptedIOException();
         } else {
            return var1;
         }
      }
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      this.ensureOpen();
      if (this.eof) {
         return -1;
      } else if (var1 == null) {
         throw new NullPointerException();
      } else if (var3 == 0) {
         return 0;
      } else {
         int var4 = 0;
         synchronized(af) {
            while(var4 < var3) {
               int var6;
               if ((var6 = this.bw.read0(var1, var2 + var4, var3 - var4)) == -1) {
                  this.eof = true;
                  if (var4 == 0) {
                     var4 = -1;
                  }
                  break;
               }

               if (var6 > 0 && (var4 += var6) == var3) {
                  break;
               }

               Thread.yield();
            }
         }

         if (this.bw == null) {
            throw new InterruptedIOException();
         } else {
            return var4;
         }
      }
   }

   public void close() throws IOException {
      if (this.bw != null) {
         this.ensureOpen();
         this.bw.I();
         this.bw.el = false;
         this.bw = null;
      }

   }

   public int available() throws IOException {
      return this.bw.available0();
   }
}
