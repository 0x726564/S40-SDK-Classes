package com.nokia.mid.impl.jms.file;

import java.io.IOException;
import java.io.InputStream;

/** @deprecated */
public class FileInputStream extends InputStream {
   int m_FileDesc;
   String m_Path;
   int m_FilePos;
   int m_MarkedFilePos;

   public FileInputStream(File file) throws NullPointerException, IOException {
      this(file.getPath());
   }

   public FileInputStream(String filepath) throws NullPointerException, IOException {
      this.m_FileDesc = -1;
      this.m_FilePos = 0;
      this.m_MarkedFilePos = -1;
      FileSystem.checkPath(filepath);
      if (filepath == null) {
         throw new NullPointerException();
      } else {
         this.m_Path = filepath;
         this.m_FileDesc = this.open0(filepath);
         if (this.m_FileDesc == -1) {
            throw new IOException("File not found");
         }
      }
   }

   public synchronized int read() throws IOException {
      byte[] buf = new byte[1];
      return this.read(buf, 0, 1) > 0 ? buf[0] & 255 : -1;
   }

   public synchronized int read(byte[] b, int off, int len) throws IOException, IndexOutOfBoundsException {
      if (this.m_FileDesc == -1) {
         throw new IOException("Stream Closed");
      } else if (b == null) {
         throw new NullPointerException();
      } else if (off >= 0 && off <= b.length && len >= 0 && off + len <= b.length && off + len >= 0) {
         if (len == 0) {
            return 0;
         } else {
            int bytesRead = this.readBytes0(this.m_FileDesc, this.m_FilePos, b, off, len);
            if (bytesRead > 0) {
               this.m_FilePos += bytesRead;
            }

            return bytesRead;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public synchronized native void close() throws IOException;

   public synchronized void mark(int readlimit) {
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
      int filesize = (int)(new File(this.m_Path)).getSize(false);
      return filesize - this.m_FilePos;
   }

   public synchronized long skip(long n) throws IOException {
      if (n < 0L) {
         return 0L;
      } else {
         int bytesLeft = this.available();
         if ((long)bytesLeft > n) {
            this.m_FilePos = (int)((long)this.m_FilePos + n);
            return n;
         } else {
            this.m_FilePos += bytesLeft;
            return (long)bytesLeft;
         }
      }
   }

   private native int open0(String var1);

   private native int readBytes0(int var1, int var2, byte[] var3, int var4, int var5);
}
