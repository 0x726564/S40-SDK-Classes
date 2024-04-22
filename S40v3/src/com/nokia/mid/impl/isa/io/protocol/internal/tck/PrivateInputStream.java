package com.nokia.mid.impl.isa.io.protocol.internal.tck;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

class PrivateInputStream extends InputStream {
   private Protocol parent;
   boolean eof = false;

   public PrivateInputStream(Protocol var1) throws IOException {
      this.parent = var1;
   }

   void ensureOpen() throws IOException {
      if (this.parent == null) {
         throw new IOException("Stream closed");
      }
   }

   public synchronized int read() throws IOException {
      this.ensureOpen();
      if (this.eof) {
         return -1;
      } else {
         while(true) {
            int var1 = this.parent.read0((byte[])null, 0, 1);
            if (var1 != -2) {
               if (var1 == -1) {
                  this.eof = true;
               }

               if (this.parent == null) {
                  throw new InterruptedIOException();
               } else {
                  return var1;
               }
            }

            Thread.yield();
         }
      }
   }

   public synchronized int read(byte[] var1, int var2, int var3) throws IOException {
      this.ensureOpen();
      if (this.eof) {
         return -1;
      } else if (var1 == null) {
         throw new NullPointerException();
      } else if (var3 == 0) {
         return 0;
      } else {
         int var4 = 0;

         while(var4 < var3) {
            int var5 = this.parent.read0(var1, var2 + var4, var3 - var4);
            if (var5 == -1) {
               this.eof = true;
               if (var4 == 0) {
                  var4 = -1;
               }
               break;
            }

            var4 += var5;
            if (var4 == var3) {
               break;
            }

            Thread.yield();
         }

         if (this.parent == null) {
            throw new InterruptedIOException();
         } else {
            return var4;
         }
      }
   }

   public synchronized int available() throws IOException {
      this.ensureOpen();
      return this.parent.available0();
   }

   public void close() throws IOException {
      if (this.parent != null) {
         this.ensureOpen();
         this.parent.realClose();
         this.parent.isopen = false;
         this.parent = null;
      }

   }
}
