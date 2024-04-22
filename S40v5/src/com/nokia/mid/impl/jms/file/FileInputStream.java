package com.nokia.mid.impl.jms.file;

import java.io.IOException;
import java.io.InputStream;

/** @deprecated */
public class FileInputStream extends InputStream {
   private int dr;
   private String ds;
   private int dt;
   private int kU;

   public FileInputStream(File var1) throws NullPointerException, IOException {
      this(var1.getPath());
   }

   public FileInputStream(String var1) throws NullPointerException, IOException {
      this.dr = -1;
      this.dt = 0;
      this.kU = -1;
      FileSystem.P(var1);
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.ds = var1;
         this.dr = this.open0(var1);
         if (this.dr == -1) {
            throw new IOException("File not found");
         }
      }
   }

   public synchronized int read() throws IOException {
      byte[] var1 = new byte[1];
      return this.read(var1, 0, 1) > 0 ? var1[0] & 255 : -1;
   }

   public synchronized int read(byte[] var1, int var2, int var3) throws IOException, IndexOutOfBoundsException {
      if (this.dr == -1) {
         throw new IOException("Stream Closed");
      } else if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         if (var3 == 0) {
            return 0;
         } else {
            int var4;
            if ((var4 = this.readBytes0(this.dr, this.dt, var1, var2, var3)) > 0) {
               this.dt += var4;
            }

            return var4;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public synchronized native void close() throws IOException;

   public synchronized void mark(int var1) {
      this.kU = this.dt;
   }

   public synchronized void reset() throws IOException {
      if (this.kU != -1) {
         this.dt = this.kU;
      } else {
         this.dt = 0;
      }
   }

   public synchronized boolean markSupported() {
      return true;
   }

   public int available() throws IOException {
      return (int)(new File(this.ds)).getSize(false) - this.dt;
   }

   public synchronized long skip(long var1) throws IOException {
      if (var1 < 0L) {
         return 0L;
      } else {
         int var3;
         if ((long)(var3 = this.available()) > var1) {
            this.dt = (int)((long)this.dt + var1);
            return var1;
         } else {
            this.dt += var3;
            return (long)var3;
         }
      }
   }

   private native int open0(String var1);

   private native int readBytes0(int var1, int var2, byte[] var3, int var4, int var5);
}
