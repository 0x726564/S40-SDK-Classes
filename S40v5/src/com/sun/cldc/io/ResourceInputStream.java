package com.sun.cldc.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

public class ResourceInputStream extends InputStream {
   private Object dz;
   private int pos;
   private int size;
   private int mark;

   private static String v(String var0) throws IOException {
      Vector var1 = new Vector();
      int var2 = 0;
      boolean var3 = false;

      int var7;
      while((var7 = var0.indexOf(47, var2)) != -1) {
         if (var7 == var2) {
            ++var2;
         } else {
            String var4 = var0.substring(var2, var7);
            var2 = var7 + 1;
            if (!var4.equals(".")) {
               if (var4.equals("..")) {
                  try {
                     var1.removeElementAt(var1.size() - 1);
                  } catch (ArrayIndexOutOfBoundsException var6) {
                     throw new IOException();
                  }
               } else {
                  var1.addElement(var4);
               }
            }
         }
      }

      StringBuffer var8 = new StringBuffer();
      int var9 = var1.size();

      for(int var5 = 0; var5 < var9; ++var5) {
         var8.append((String)var1.elementAt(var5));
         var8.append("/");
      }

      if (var2 < var0.length()) {
         String var10;
         if ((var10 = var0.substring(var2)).endsWith(".class") && !".class".equals(var10)) {
            throw new IOException();
         }

         var8.append(var0.substring(var2));
      }

      return var8.toString();
   }

   public ResourceInputStream(String var1) throws IOException {
      var1 = v(var1);
      this.dz = open(var1);
      if (this.dz == null) {
         throw new IOException();
      } else {
         this.size = size(this.dz);
         this.pos = 0;
         this.mark = 0;
      }
   }

   public int read() throws IOException {
      int var1;
      if ((var1 = read(this.dz)) != -1) {
         ++this.pos;
      }

      return var1;
   }

   public synchronized void close() throws IOException {
      close(this.dz);
      this.dz = null;
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
               if ((var4 = readBytes(this.dz, var1, var2, this.pos, var3)) != -1) {
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
      resetToPos(this.dz, this.mark);
      this.pos = this.mark;
   }

   public long skip(long var1) throws IOException {
      int var3 = skipN(this.dz, (int)var1);
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
