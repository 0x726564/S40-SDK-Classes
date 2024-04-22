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
   public static final int FILE_DRM_TYPE_NONE = 0;
   public static final int FILE_DRM_TYPE_FORWARD_LOCK = 1;
   public static final int FILE_DRM_TYPE_COMBINED_DELIVERY = 2;
   public static final int FILE_DRM_TYPE_SEPARATE_DELIVERY = 3;
   private String ds;

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
      this.f((String)null, var1);
   }

   protected File(File var1, String var2) throws NullPointerException, IllegalArgumentException {
      this.f(var1.getPath(), var2);
   }

   protected File(String var1, String var2) throws NullPointerException, IllegalArgumentException {
      this.f(var1, var2);
   }

   private void f(String var1, String var2) throws NullPointerException, IllegalArgumentException {
      if (var2 == null) {
         throw new NullPointerException();
      } else if (var2.equals("")) {
         throw new IllegalArgumentException();
      } else {
         if (var1 != null) {
            FileSystem.P(var1 + var2);
         } else {
            FileSystem.P(var2);
         }

         try {
            File var3;
            if (var1 != null && (!(var3 = new File(var1)).exists() || !var3.isDirectory())) {
               throw new IllegalArgumentException("Bad parent");
            }
         } catch (IOException var4) {
            throw new IllegalArgumentException("Bad parent");
         }

         if (var1 != null) {
            if (FileSystem.getFileSystem().pathEndsWithSeparator(var1)) {
               this.ds = var1 + var2;
            } else {
               this.ds = var1 + "\\" + var2;
            }
         } else {
            this.ds = var2;
         }

         if (this.ds.equals("")) {
            throw new IllegalArgumentException();
         } else {
            if (FileSystem.getFileSystem().pathEndsWithSeparator(this.ds)) {
               this.ds = this.ds.substring(0, this.ds.length() - 1);
            }

         }
      }
   }

   public native boolean exists();

   public String getPath() {
      return this.ds;
   }

   public String getName() {
      int var1;
      return (var1 = this.ds.lastIndexOf("\\".charAt(0))) == -1 ? this.ds : this.ds.substring(var1 + 1);
   }

   public String getParent() {
      int var1;
      return (var1 = this.ds.lastIndexOf("\\".charAt(0))) == -1 ? this.ds : this.ds.substring(0, var1);
   }

   public short getFileType(boolean var1) {
      short var3;
      if (!var1 && (var3 = this.getFileType0()) != 0) {
         return var3;
      } else {
         int var4;
         if ((var4 = this.ds.lastIndexOf(46)) == -1) {
            return 0;
         } else {
            String var2;
            if ((var2 = this.ds.substring(var4 + 1).toUpperCase()).equals("JAD")) {
               return 1;
            } else if (var2.equals("JAR")) {
               return 2;
            } else {
               return (short)(var2.equals("RMS") ? 3 : 0);
            }
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
      return FileSystem.getFileSystem().delete(this.ds, true);
   }

   public boolean delete(boolean var1) {
      return FileSystem.getFileSystem().delete(this.ds, var1);
   }

   public boolean rename(String var1) {
      FileSystem.P(var1 = this.getParent() + "\\" + var1);
      return FileSystem.getFileSystem().rename(this.ds, var1) == 0;
   }

   public boolean moveTo(File var1) {
      try {
         if (var1.exists() && var1.isDirectory()) {
            String var4 = var1.getPath() + "\\" + this.getName();
            int var2;
            if ((var2 = FileSystem.getFileSystem().rename(this.ds, var4)) == 0) {
               this.ds = var4;
            }

            return var2 == 0;
         } else {
            throw new IllegalArgumentException("The destination is not a directory or does not exist.");
         }
      } catch (IOException var3) {
         var3.printStackTrace();
         return false;
      }
   }

   public boolean copyTo(File var1) {
      String var2 = var1.getPath() + "\\" + this.getName();
      return FileSystem.getFileSystem().copy(this.ds, var2);
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
            String[] var4;
            if ((var4 = this.getChildren0(var1, var2)) != null && var4.length != 0) {
               File[] var5 = new File[var4.length];

               try {
                  for(int var6 = 0; var6 < var4.length; ++var6) {
                     var5[var6] = new File(var4[var6]);
                  }

                  return var5;
               } catch (Exception var3) {
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

   public int getDRMType() {
      return this.getDRMType0(this.ds);
   }

   private native int getDRMType0(String var1);

   private native String[] getChildren0(boolean var1, String var2);

   private native short getFileType0();
}
