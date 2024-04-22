package com.nokia.mid.impl.jms.file;

import java.io.IOException;
import java.io.OutputStream;

/** @deprecated */
public class FileOutputStream extends OutputStream {
   private int dr;
   private String ds;
   private int dt;

   public FileOutputStream(File var1, boolean var2, boolean var3) throws NullPointerException, IOException {
      this(var1.getPath(), var2, var3);
   }

   public FileOutputStream(String var1, boolean var2, boolean var3) throws NullPointerException, IOException {
      this.dt = 0;
      FileSystem.P(var1);
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.ds = var1;
         this.dr = this.open0(var1, var2, var3);
         if (this.dr == -1) {
            throw new IOException("File not found");
         }
      }
   }

   public void write(int var1) throws IOException {
      byte[] var2;
      (var2 = new byte[1])[0] = (byte)var1;
      this.write(var2, 0, var2.length);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      if (this.dr == -1) {
         throw new IOException("Stream Closed");
      } else if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         if (var3 != 0) {
            int var4;
            if ((var4 = this.writeBytes0(this.dr, this.dt, var1, var2, var3)) > 0) {
               this.dt += var4;
            }

         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void flush() throws IOException {
   }

   public native void close() throws IOException;

   public void seek(int var1) {
      this.dt = var1;
   }

   public void truncate(int var1) throws IOException {
      if (this.dr == -1) {
         throw new IOException("Stream Closed");
      } else {
         int var2 = (int)(new File(this.ds)).getSize(false);
         if (var1 > var2) {
            this.dt = var2;
         } else {
            if (this.truncate0(var1)) {
               this.dt = var1;
            }

         }
      }
   }

   private native int open0(String var1, boolean var2, boolean var3);

   private native int writeBytes0(int var1, int var2, byte[] var3, int var4, int var5);

   private native boolean truncate0(int var1);
}
