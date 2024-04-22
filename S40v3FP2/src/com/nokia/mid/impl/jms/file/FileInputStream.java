package com.nokia.mid.impl.jms.file;

import java.io.IOException;
import java.io.InputStream;

/** @deprecated */
public class FileInputStream extends InputStream {
   int m_FileDesc;
   String m_Path;
   int m_FilePos;
   int m_MarkedFilePos;

   public FileInputStream(File var1) throws NullPointerException, IOException {
      this(var1.getPath());
   }

   public FileInputStream(String var1) throws NullPointerException, IOException {
      this.m_FileDesc = -1;
      this.m_FilePos = 0;
      this.m_MarkedFilePos = -1;
      FileSystem.checkPath(var1);
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.m_Path = var1;
         this.m_FileDesc = this.open0(var1);
         if (this.m_FileDesc == -1) {
            throw new IOException("File not found");
         }
      }
   }

   public synchronized int read() throws IOException {
      byte[] var1 = new byte[1];
      return this.read(var1, 0, 1) > 0 ? var1[0] & 255 : -1;
   }

   public synchronized int read(byte[] var1, int var2, int var3) throws IOException, IndexOutOfBoundsException {
      if (this.m_FileDesc == -1) {
         throw new IOException("Stream Closed");
      } else if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         if (var3 == 0) {
            return 0;
         } else {
            int var4 = this.readBytes0(this.m_FileDesc, this.m_FilePos, var1, var2, var3);
            if (var4 > 0) {
               this.m_FilePos += var4;
            }

            return var4;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public synchronized native void close() throws IOException;

   public synchronized void mark(int var1) {
      this.m_MarkedFilePos = this.m_FilePos;
   }

   public synchronized void reset() throws IOException {
      if (this.m_MarkedFilePos != -1) {
         this.m_FilePos = this.m_MarkedFilePos;
      } else {
         this.m_FilePos = 0;
      }

   }

   public synchronized boolean markSupported() {
      return true;
   }

   public int available() throws IOException {
      int var1 = (int)(new File(this.m_Path)).getSize(false);
      return var1 - this.m_FilePos;
   }

   public synchronized long skip(long var1) throws IOException {
      if (var1 < 0L) {
         return 0L;
      } else {
         int var3 = this.available();
         if ((long)var3 > var1) {
            this.m_FilePos = (int)((long)this.m_FilePos + var1);
            return var1;
         } else {
            this.m_FilePos += var3;
            return (long)var3;
         }
      }
   }

   private native int open0(String var1);

   private native int readBytes0(int var1, int var2, byte[] var3, int var4, int var5);
}
