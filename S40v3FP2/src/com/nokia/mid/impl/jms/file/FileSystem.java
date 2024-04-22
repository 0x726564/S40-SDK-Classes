package com.nokia.mid.impl.jms.file;

import java.io.IOException;

public class FileSystem {
   private static FileSystem m_fs = null;
   public static final int FILESYSTEM_JMS_JAVA_ROOT = 1;
   public static final int FILESYSTEM_JMS_JAVA_SYSTEM_ROOT = 2;
   public static final int FILESYSTEM_JMS_JAVA_SYSTEM_PERMANENT_ROOT = 3;
   public static final int FILESYSTEM_JMS_JAVA_SYSTEM_CORE_ROOT = 4;
   public static final int FILESYSTEM_JMS_JAVA_SYSTEM_UPGRADABLE_ROOT = 5;
   /** @deprecated */
   public static final int FILESYSTEM_JMS_JAVA_CARRIER_ROOT = 6;
   public static final int FILESYSTEM_JMS_JAVA_TEMP_ROOT = 7;
   public static final int FILESYSTEM_GALLERY_ROOT = 8;
   public static final int FILESYSTEM_GALLERY_PHOTOS = 9;
   public static final int FILESYSTEM_GALLERY_VIDEOS = 10;
   public static final int FILESYSTEM_JAVA_ROOT = 11;
   public static final int FILESYSTEM_JAVA_APPS_ROOT = 12;
   public static final int FILESYSTEM_JAVA_GAMES_ROOT = 13;
   public static final int FILESYSTEM_MEMORY_CARD_ROOT = 14;
   static final int FILESYS_EACCES = 1;
   static final int FILESYS_EBADF = 2;
   static final int FILESYS_EEXIST = 3;
   static final int FILESYS_EINVAL = 4;
   static final int FILESYS_EMFILE = 5;
   static final int FILESYS_ENOENT = 6;
   static final int FILESYS_ESHARE = 7;
   static final int FILESYS_ENOSPC = 8;
   static final int FILESYS_EDEVICE = 9;
   static final int FILESYS_EBADDIR = 10;
   static final int FILESYS_ENOTREADY = 11;
   static final int FILESYS_EFAIL = 12;
   static final int FILESYS_EIO = 13;
   static final int FILESYS_EINVALIDPATH = 14;
   static final int FILESYS_EACCESSDENIED = 15;
   public static final byte FILESYSTEM_JAVA_DOWNLOAD_DATA_FILE = 50;
   public static final String FILESYSTEM_PATH_SEPARATOR = "\\";
   public static final String FILESYSTEM_DRIVE_SEPARATOR = "\\\\";
   private static final String[] allowedPaths = new String[]{getFileSystem().getSystemFilePath(11), getFileSystem().getSystemFilePath(8), getFileSystem().getSystemFilePath(1), getFileSystem().getSystemFilePath(14), getFileSystem().getSystemFilePath(50)};

   private FileSystem() {
   }

   public static FileSystem getFileSystem() {
      if (m_fs != null) {
         return m_fs;
      } else {
         m_fs = new FileSystem();
         return m_fs;
      }
   }

   public native String getSystemFilePath(int var1);

   /** @deprecated */
   public native long getFreeSpaceAvailable();

   /** @deprecated */
   public native long getTotalSpace();

   static void checkPath(String var0) throws SecurityException {
      synchronized(allowedPaths) {
         if (var0.indexOf("../") <= -1 && var0.indexOf("..\\") <= -1) {
            for(int var2 = 0; var2 < allowedPaths.length; ++var2) {
               int var3 = allowedPaths[var2].length();
               if (var0.length() >= var3) {
                  for(int var4 = 0; var4 < var3; ++var4) {
                     char var5 = allowedPaths[var2].charAt(var4);
                     char var6 = var0.charAt(var4);
                     if ((var6 != '/' && var6 != '\\' || var5 != '/' && var5 != '\\') && var5 != var6) {
                        break;
                     }

                     if (var4 == var3 - 1) {
                        return;
                     }
                  }
               }
            }

            throw new SecurityException();
         } else {
            throw new SecurityException();
         }
      }
   }

   /** @deprecated */
   public boolean createFile(String var1, byte[] var2, int var3, int var4) throws NullPointerException, IOException {
      checkPath(var1);
      if (var1.equals("")) {
         return false;
      } else if ((new File(var1)).exists()) {
         return false;
      } else if (var2 != null && var3 + var4 > var2.length) {
         throw new IndexOutOfBoundsException();
      } else {
         FileOutputStream var5 = null;

         try {
            var5 = new FileOutputStream(var1, true, false);
         } catch (IOException var8) {
            return false;
         }

         try {
            if (var2 != null) {
               var5.write(var2, var3, var4);
            }

            var5.close();
            return true;
         } catch (IOException var7) {
            var5.close();
            throw var7;
         }
      }
   }

   /** @deprecated */
   public boolean delete(String var1, boolean var2) throws NullPointerException {
      checkPath(var1);
      return this.delete0(var1, var2);
   }

   /** @deprecated */
   public int rename(String var1, String var2) throws NullPointerException {
      checkPath(var1);
      checkPath(var2);
      return this.rename0(var1, var2);
   }

   public boolean copy(String var1, String var2) throws NullPointerException {
      checkPath(var1);
      checkPath(var2);
      boolean var3 = false;
      String var4 = new String(var2);
      if (var1 != null && var2 != null) {
         try {
            File var5 = new File(var1);
            File var6 = new File(var2);
            if (!var5.exists()) {
               return false;
            }

            if (!var5.isDirectory()) {
               if (var6.exists()) {
                  if (!var6.isDirectory()) {
                     return false;
                  }

                  if (this.pathEndsWithSeparator(var2)) {
                     var4 = var2 + var5.getName();
                  } else {
                     var4 = var2 + "\\" + var5.getName();
                  }
               }

               var3 = this.copy0(var1, var4);
            } else {
               if (var6.exists() && !var6.isDirectory()) {
                  return false;
               }

               var3 = this.copy0(var1, var2);
            }
         } catch (Exception var7) {
            var3 = false;
         }

         return var3;
      } else {
         throw new NullPointerException();
      }
   }

   /** @deprecated */
   public boolean mkdir(String var1) throws NullPointerException {
      checkPath(var1);
      return this.mkdir0(var1);
   }

   /** @deprecated */
   public boolean mkdirs(String var1) throws NullPointerException {
      checkPath(var1);
      return this.mkdirs0(var1);
   }

   public boolean isAValidFileName(String var1) {
      if (var1 != null && var1.length() != 0) {
         if (var1.charAt(0) == '.') {
            return false;
         } else if (var1.charAt(0) == ' ') {
            return false;
         } else {
            int var3 = "\\/:*?\"<>|".length();

            for(int var4 = 0; var4 < var3; ++var4) {
               if (var1.indexOf("\\/:*?\"<>|".charAt(var4)) != -1) {
                  return false;
               }
            }

            if (var1.length() > 255) {
               return false;
            } else {
               return true;
            }
         }
      } else {
         return false;
      }
   }

   public static String makeAValidFileName(String var0) {
      StringBuffer var1 = new StringBuffer();
      int var2 = var0.length();

      for(int var4 = 0; var4 < var2; ++var4) {
         char var5 = var0.charAt(var4);
         if (var4 == 0 && var5 == ' ' || var4 == 0 && var5 == '.') {
            var1.append('_');
         } else if ("\\/:*?\"<>|".indexOf(var5) != -1) {
            var1.append('_');
         } else {
            var1.append(var5);
         }
      }

      if (var0.length() > 255) {
         var1.setLength(255);
      }

      return var1.toString();
   }

   public byte[] getFileContentFromJar(String var1, String var2) {
      checkPath(var1);
      return this.getFileContentFromJar0(var1, var2);
   }

   public boolean pathEndsWithSeparator(String var1) {
      int var2 = var1.lastIndexOf("\\".charAt(0));
      if (var2 == -1) {
         return false;
      } else {
         return var2 == var1.length() - 1;
      }
   }

   private native boolean delete0(String var1, boolean var2) throws NullPointerException;

   private native boolean copy0(String var1, String var2);

   private native byte[] getFileContentFromJar0(String var1, String var2);

   private native boolean mkdir0(String var1) throws NullPointerException;

   private native boolean mkdirs0(String var1) throws NullPointerException;

   private native int rename0(String var1, String var2) throws NullPointerException;
}
