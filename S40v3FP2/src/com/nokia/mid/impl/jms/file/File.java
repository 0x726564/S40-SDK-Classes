package com.nokia.mid.impl.jms.file;

import java.io.IOException;

public class File {
   public static final int FILE_ATTR_NORMAL = 0;
   public static final int FILE_ATTR_READ_ONLY = 1;
   public static final int FILE_ATTR_HIDDEN = 2;
   public static final int FILE_ATTR_SYSTEM = 4;
   public static final int FILE_ATTR_VOLUME = 8;
   public static final int FILE_ATTR_DIRECTORY = 16;
   public static final int FILE_ATTR_ARCHIVE = 32;
   public static final int FILE_ATTR_COPYRIGHTED = 64;
   public static final int FILE_TYPE_UNKNOWN = 0;
   public static final int FILE_TYPE_JAD = 1;
   public static final int FILE_TYPE_JAR = 2;
   public static final int FILE_TYPE_RMS = 3;
   public static final int FILE_TYPE_AMR = 4;
   public static final int FILE_TYPE_AMR_WB = 5;
   public static final int FILE_TYPE_BMP = 6;
   public static final int FILE_TYPE_GIF = 7;
   public static final int FILE_TYPE_JPG = 8;
   public static final int FILE_TYPE_M4A = 9;
   public static final int FILE_TYPE_MIDI = 10;
   public static final int FILE_TYPE_MP3 = 11;
   public static final int FILE_TYPE_OTABMP = 12;
   public static final int FILE_TYPE_PNG = 13;
   public static final int FILE_TYPE_QCELP = 14;
   public static final int FILE_TYPE_WBMP = 15;
   private String m_Path;

   public static File getFile(String var0) throws NullPointerException, IllegalArgumentException {
      return new File(var0);
   }

   public static File getFile(File var0, String var1) throws NullPointerException, IllegalArgumentException {
      return new File(var0, var1);
   }

   public static File getFile(String var0, String var1) throws NullPointerException, IllegalArgumentException {
      return new File(var0, var1);
   }

   protected File(String var1) throws NullPointerException, IllegalArgumentException {
      this.init((String)null, var1);
   }

   protected File(File var1, String var2) throws NullPointerException, IllegalArgumentException {
      this.init(var1.getPath(), var2);
   }

   protected File(String var1, String var2) throws NullPointerException, IllegalArgumentException {
      this.init(var1, var2);
   }

   private void init(String var1, String var2) throws NullPointerException, IllegalArgumentException {
      if (var2 == null) {
         throw new NullPointerException();
      } else if (var2.equals("")) {
         throw new IllegalArgumentException();
      } else {
         if (var1 != null) {
            FileSystem.checkPath(var1 + var2);
         } else {
            FileSystem.checkPath(var2);
         }

         try {
            if (var1 != null) {
               File var3 = new File(var1);
               if (!var3.exists() || !var3.isDirectory()) {
                  throw new IllegalArgumentException("Bad parent");
               }
            }
         } catch (IOException var4) {
            throw new IllegalArgumentException("Bad parent");
         }

         if (var1 != null) {
            if (FileSystem.getFileSystem().pathEndsWithSeparator(var1)) {
               this.m_Path = var1 + var2;
            } else {
               this.m_Path = var1 + "\\" + var2;
            }
         } else {
            this.m_Path = var2;
         }

         if (this.m_Path.equals("")) {
            throw new IllegalArgumentException();
         } else {
            if (FileSystem.getFileSystem().pathEndsWithSeparator(this.m_Path)) {
               this.m_Path = this.m_Path.substring(0, this.m_Path.length() - 1);
            }

         }
      }
   }

   public native boolean exists();

   public String getPath() {
      return this.m_Path;
   }

   public String getName() {
      int var1 = this.m_Path.lastIndexOf("\\".charAt(0));
      return var1 == -1 ? this.m_Path : this.m_Path.substring(var1 + 1);
   }

   public String getParent() {
      int var1 = this.m_Path.lastIndexOf("\\".charAt(0));
      return var1 == -1 ? this.m_Path : this.m_Path.substring(0, var1);
   }

   public short getFileType(boolean var1) {
      if (!var1) {
         short var2 = this.getFileType0();
         if (var2 != 0) {
            return var2;
         }
      }

      int var4 = this.m_Path.lastIndexOf(46);
      if (var4 == -1) {
         return 0;
      } else {
         String var3 = this.m_Path.substring(var4 + 1).toUpperCase();
         if (var3.equals("JAD")) {
            return 1;
         } else if (var3.equals("JAR")) {
            return 2;
         } else {
            return (short)(var3.equals("RMS") ? 3 : 0);
         }
      }
   }

   public native int getAttributes() throws IOException;

   public native void setAttributes(int var1) throws IOException;

   public long getSize() throws IOException {
      return this.getSize(false);
   }

   public native long getSize(boolean var1) throws IOException;

   public boolean delete() {
      return FileSystem.getFileSystem().delete(this.m_Path, true);
   }

   public boolean delete(boolean var1) {
      return FileSystem.getFileSystem().delete(this.m_Path, var1);
   }

   public boolean rename(String var1) {
      String var2 = this.getParent() + "\\" + var1;
      FileSystem.checkPath(var2);
      int var3 = FileSystem.getFileSystem().rename(this.m_Path, var2);
      return var3 == 0;
   }

   public boolean moveTo(File var1) {
      try {
         if (var1.exists() && var1.isDirectory()) {
            String var2 = var1.getPath() + "\\" + this.getName();
            int var3 = FileSystem.getFileSystem().rename(this.m_Path, var2);
            if (var3 == 0) {
               this.m_Path = var2;
            }

            return var3 == 0;
         } else {
            throw new IllegalArgumentException("The destination is not a directory or does not exist.");
         }
      } catch (IOException var4) {
         var4.printStackTrace();
         return false;
      }
   }

   public boolean copyTo(File var1) {
      String var2 = var1.getPath() + "\\" + this.getName();
      return FileSystem.getFileSystem().copy(this.m_Path, var2);
   }

   public native boolean isDirectory() throws IOException;

   public File[] listContents(boolean var1, String var2) throws NullPointerException, IllegalArgumentException, IOException {
      if (var2 == null) {
         throw new NullPointerException();
      } else if (var2.equals("")) {
         throw new IllegalArgumentException("Empty Filter");
      } else if (var2.indexOf(47) == -1 && var2.indexOf(58) == -1) {
         if (!this.exists()) {
            throw new IOException("File/Folder does not exist");
         } else if (!this.isDirectory()) {
            return null;
         } else {
            String[] var3 = this.getChildren0(var1, var2);
            if (var3 != null && var3.length != 0) {
               File[] var4 = new File[var3.length];

               try {
                  for(int var5 = 0; var5 < var3.length; ++var5) {
                     var4[var5] = new File(var3[var5]);
                  }

                  return var4;
               } catch (Exception var6) {
                  return null;
               }
            } else {
               return null;
            }
         }
      } else {
         throw new IllegalArgumentException("invalid character in filter");
      }
   }

   private native String[] getChildren0(boolean var1, String var2);

   private native short getFileType0();
}