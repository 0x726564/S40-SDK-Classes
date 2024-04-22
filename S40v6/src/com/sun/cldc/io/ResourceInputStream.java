package com.sun.cldc.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

public class ResourceInputStream extends InputStream {
   private Object handle;
   private int pos;
   private int size;
   private int mark;

   private static String fixResourceName(String name) throws IOException {
      Vector dirVector = new Vector();
      int startIdx = 0;
      boolean var3 = false;

      int endIdx;
      while((endIdx = name.indexOf(47, startIdx)) != -1) {
         if (endIdx == startIdx) {
            ++startIdx;
         } else {
            String curDir = name.substring(startIdx, endIdx);
            startIdx = endIdx + 1;
            if (!curDir.equals(".")) {
               if (curDir.equals("..")) {
                  try {
                     dirVector.removeElementAt(dirVector.size() - 1);
                  } catch (ArrayIndexOutOfBoundsException var8) {
                     throw new IOException();
                  }
               } else {
                  dirVector.addElement(curDir);
               }
            }
         }
      }

      StringBuffer dirName = new StringBuffer();
      int nelements = dirVector.size();

      for(int i = 0; i < nelements; ++i) {
         dirName.append((String)dirVector.elementAt(i));
         dirName.append("/");
      }

      if (startIdx < name.length()) {
         String filename = name.substring(startIdx);
         if (filename.endsWith(".class") && !".class".equals(filename)) {
            throw new IOException();
         }

         dirName.append(name.substring(startIdx));
      }

      return dirName.toString();
   }

   public ResourceInputStream(String name) throws IOException {
      String fixedName = fixResourceName(name);
      this.handle = open(fixedName);
      if (this.handle == null) {
         throw new IOException();
      } else {
         this.size = size(this.handle);
         this.pos = 0;
         this.mark = 0;
      }
   }

   public int read() throws IOException {
      int result;
      if ((result = read(this.handle)) != -1) {
         ++this.pos;
      }

      return result;
   }

   public synchronized void close() throws IOException {
      close(this.handle);
      this.handle = null;
   }

   public int available() throws IOException {
      return this.size - this.pos;
   }

   public int read(byte[] b, int off, int len) throws IOException {
      if (b == null) {
         throw new NullPointerException();
      } else if (off >= 0 && off <= b.length && len >= 0 && off + len <= b.length && off + len >= 0) {
         if (this.pos >= this.size) {
            return -1;
         } else {
            if (this.pos + len > this.size) {
               len = this.size - this.pos;
            }

            if (len <= 0) {
               return 0;
            } else {
               int readLength;
               if ((readLength = readBytes(this.handle, b, off, this.pos, len)) != -1) {
                  this.pos += readLength;
               }

               return readLength;
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public synchronized void mark(int readlimit) {
      this.mark = this.pos;
   }

   public boolean markSupported() {
      return true;
   }

   public synchronized void reset() throws IOException {
      resetToPos(this.handle, this.mark);
      this.pos = this.mark;
   }

   public long skip(long n) throws IOException {
      int result = skipN(this.handle, (int)n);
      this.pos += result;
      return (long)result;
   }

   private static native Object open(String var0) throws IOException;

   private static native void close(Object var0) throws IOException;

   private static native int size(Object var0) throws IOException;

   private static native int read(Object var0) throws IOException;

   private static native int readBytes(Object var0, byte[] var1, int var2, int var3, int var4) throws IOException;

   private static native void resetToPos(Object var0, int var1);

   private static native int skipN(Object var0, int var1);
}
