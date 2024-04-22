package com.nokia.mid.impl.isa.io.protocol.internal.wap;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

class PrivateInputStream extends InputStream {
   private Object handle;
   private Protocol parent;
   boolean eof = false;
   static Object readLock = new Object();

   public PrivateInputStream(Protocol parent) throws IOException {
      this.parent = parent;
   }

   void ensureOpen() throws IOException {
      if (this.parent == null) {
         throw new IOException("Stream closed");
      }
   }

   public int read() throws IOException {
      this.ensureOpen();
      if (this.eof) {
         return -1;
      } else {
         int res;
         synchronized(readLock) {
            do {
               res = this.parent.read0((byte[])null, 0, 1);
            } while(res == -2);
         }

         if (res == -1) {
            this.eof = true;
         }

         if (this.parent == null) {
            throw new InterruptedIOException();
         } else {
            return res;
         }
      }
   }

   public int read(byte[] b, int off, int len) throws IOException {
      this.ensureOpen();
      if (this.eof) {
         return -1;
      } else if (b == null) {
         throw new NullPointerException();
      } else if (len == 0) {
         return 0;
      } else {
         int n = 0;
         synchronized(readLock) {
            for(; n < len; Thread.yield()) {
               int count = this.parent.read0(b, off + n, len - n);
               if (count == -1) {
                  this.eof = true;
                  if (n == 0) {
                     n = -1;
                  }
                  break;
               }

               if (count > 0) {
                  n += count;
                  if (n == len) {
                     break;
                  }
               }
            }
         }

         if (this.parent == null) {
            throw new InterruptedIOException();
         } else {
            return n;
         }
      }
   }

   public void close() throws IOException {
      if (this.parent != null) {
         this.ensureOpen();
         this.parent.realClose();
         this.parent.isopen = false;
         this.parent = null;
      }

   }

   public int available() throws IOException {
      return this.parent.available0();
   }
}
