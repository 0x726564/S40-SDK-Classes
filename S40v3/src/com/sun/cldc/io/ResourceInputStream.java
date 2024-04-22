package com.sun.cldc.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

public class ResourceInputStream extends InputStream {
   private Object handle;
   private int pos;
   private int size;
   private int mark;

   private static String fixResourceName(String var0) throws IOException {
      Vector var1 = new Vector();
      int var2 = 0;
      boolean var3 = false;

      int var9;
      while((var9 = var0.indexOf(47, var2)) != -1) {
         if (var9 == var2) {
            ++var2;
         } else {
            String var4 = var0.substring(var2, var9);
            var2 = var9 + 1;
            if (!var4.equals(".")) {
               if (var4.equals("..")) {
                  try {
                     var1.removeElementAt(var1.size() - 1);
                  } catch (ArrayIndexOutOfBoundsException var8) {
                     throw new IOException();
                  }
               } else {
                  var1.addElement(var4);
               }
            }
         }
      }

      StringBuffer var5 = new StringBuffer();
      int var6 = var1.size();

      for(int var7 = 0; var7 < var6; ++var7) {
         var5.append((String)var1.elementAt(var7));
         var5.append("/");
      }

      if (var2 < var0.length()) {
         String var10 = var0.substring(var2);
         if (var10.endsWith(".class") && !".class".equals(var10)) {
            throw new IOException();
         }

         var5.append(var0.substring(var2));
      }

      return var5.toString();
   }

   public ResourceInputStream(String var1) throws IOException {
      String var2 = fixResourceName(var1);
      this.handle = open(var2);
      if (this.handle == null) {
         throw new IOException();
      } else {
         this.size = size(this.handle);
         this.pos = 0;
         this.mark = 0;
      }
   }

   public int read() throws IOException {
      int var1;
      if ((var1 = read(this.handle)) != -1) {
         ++this.pos;
      }

      return var1;
   }

   public synchronized void close() throws IOException {
      close(this.handle);
      this.handle = null;
   }

   public int available() throws IOException {
      return this.size - this.pos;
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         if (this.pos >= this.size) {
            return -1;
         } else {
            if (this.pos + var3 > this.size) {
               var3 = this.size - this.pos;
            }

            if (var3 <= 0) {
               return 0;
            } else {
               int var4;
               if ((var4 = readBytes(this.handle, var1, var2, this.pos, var3)) != -1) {
                  this.pos += var4;
               }

               return var4;
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public synchronized void mark(int var1) {
      this.mark = this.pos;
   }

   public boolean markSupported() {
      return true;
   }

   public synchronized void reset() throws IOException {
      resetToPos(this.handle, this.mark);
      this.pos = this.mark;
   }

   public long skip(long var1) throws IOException {
      int var3 = skipN(this.handle, (int)var1);
      this.pos += var3;
      return (long)var3;
   }

   private static native Object open(String var0) throws IOException;

   private static native void close(Object var0) throws IOException;

   private static native int size(Object var0) throws IOException;

   private static native int read(Object var0) throws IOException;

   private static native int readBytes(Object var0, byte[] var1, int var2, int var3, int var4) throws IOException;

   private static native void resetToPos(Object var0, int var1);

   private static native int skipN(Object var0, int var1);
}
