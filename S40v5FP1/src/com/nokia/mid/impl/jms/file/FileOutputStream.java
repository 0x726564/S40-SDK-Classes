package com.nokia.mid.impl.jms.file;

import java.io.IOException;
import java.io.OutputStream;

/** @deprecated */
public class FileOutputStream extends OutputStream {
   int m_FileDesc;
   String m_Path;
   int m_FilePos;

   public FileOutputStream(File file, boolean create, boolean append) throws NullPointerException, IOException {
      this(file.getPath(), create, append);
   }

   public FileOutputStream(String filepath, boolean create, boolean append) throws NullPointerException, IOException {
      this.m_FilePos = 0;
      FileSystem.checkPath(filepath);
      if (filepath == null) {
         throw new NullPointerException();
      } else {
         this.m_Path = filepath;
         this.m_FileDesc = this.open0(filepath, create, append);
         if (this.m_FileDesc == -1) {
            throw new IOException("File not found");
         }
      }
   }

   public void write(int b) throws IOException {
      byte[] buffer = new byte[]{(byte)b};
      this.write(buffer, 0, buffer.length);
   }

   public void write(byte[] b, int off, int len) throws IOException {
      if (this.m_FileDesc == -1) {
         throw new IOException("Stream Closed");
      } else if (b == null) {
         throw new NullPointerException();
      } else if (off >= 0 && off <= b.length && len >= 0 && off + len <= b.length && off + len >= 0) {
         if (len != 0) {
            int bytesWritten = this.writeBytes0(this.m_FileDesc, this.m_FilePos, b, off, len);
            if (bytesWritten > 0) {
               this.m_FilePos += bytesWritten;
            }

         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void flush() throws IOException {
   }

   public native void close() throws IOException;

   public void seek(int pos) {
      this.m_FilePos = pos;
   }

   public void truncate(int length) throws IOException {
      if (this.m_FileDesc == -1) {
         throw new IOException("Stream Closed");
      } else {
         int fileSize = (int)(new File(this.m_Path)).getSize(false);
         if (length > fileSize) {
            this.m_FilePos = fileSize;
         } else {
            if (this.truncate0(length)) {
               this.m_FilePos = length;
            }

         }
      }
   }

   private native int open0(String var1, boolean var2, boolean var3);

   private native int writeBytes0(int var1, int var2, byte[] var3, int var4, int var5);

   private native boolean truncate0(int var1);
}
