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
   private static final String[] allowedPaths = new String[]{getFileSystem().getSystemFilePath(11) + "\\", getFileSystem().getSystemFilePath(8) + "\\", getFileSystem().getSystemFilePath(1) + "\\", getFileSystem().getSystemFilePath(14) + "\\"};
   private static final String DOWNLOAD_DATA_FILE_PATH = getFileSystem().getSystemFilePath(50);

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

   static void checkPath(String path) throws SecurityException {
      path = path.replace('/', '\\');
      if (path.indexOf("..\\") > -1) {
         throw new SecurityException();
      } else if (!path.equals(DOWNLOAD_DATA_FILE_PATH)) {
         for(int i = 0; i < allowedPaths.length; ++i) {
            String allowedPath = allowedPaths[i];
            if (allowedPath != null && path.length() >= allowedPath.length() && path.startsWith(allowedPath)) {
               return;
            }
         }

         throw new SecurityException();
      }
   }

   /** @deprecated */
   public boolean createFile(String path, byte[] content, int offset, int len) throws NullPointerException, IOException {
      checkPath(path);
      if (path.equals("")) {
         return false;
      } else if ((new File(path)).exists()) {
         return false;
      } else if (content != null && offset + len > content.length) {
         throw new IndexOutOfBoundsException();
      } else {
         FileOutputStream file = null;

         try {
            file = new FileOutputStream(path, true, false);
         } catch (IOException var8) {
            return false;
         }

         try {
            if (content != null) {
               file.write(content, offset, len);
            }

            file.close();
            return true;
         } catch (IOException var7) {
            file.close();
            throw var7;
         }
      }
   }

   /** @deprecated */
   public boolean delete(String path, boolean failIfNotEmpty) throws NullPointerException {
      checkPath(path);
      return this.delete0(path, failIfNotEmpty);
   }

   /** @deprecated */
   public int rename(String oldPath, String newPath) throws NullPointerException {
      checkPath(oldPath);
      checkPath(newPath);
      return this.rename0(oldPath, newPath);
   }

   public boolean copy(String oldPath, String newPath) throws NullPointerException {
      checkPath(oldPath);
      checkPath(newPath);
      boolean retVal = false;
      String tempPath = new String(newPath);
      if (oldPath != null && newPath != null) {
         try {
            File oldFile = new File(oldPath);
            File newFile = new File(newPath);
            if (!oldFile.exists()) {
               return false;
            }

            if (!oldFile.isDirectory()) {
               if (newFile.exists()) {
                  if (!newFile.isDirectory()) {
                     return false;
                  }

                  if (this.pathEndsWithSeparator(newPath)) {
                     tempPath = newPath + oldFile.getName();
                  } else {
                     tempPath = newPath + "\\" + oldFile.getName();
                  }
               }

               retVal = this.copy0(oldPath, tempPath);
            } else {
               if (newFile.exists() && !newFile.isDirectory()) {
                  return false;
               }

               retVal = this.copy0(oldPath, newPath);
            }
         } catch (Exception var7) {
            retVal = false;
         }

         return retVal;
      } else {
         throw new NullPointerException();
      }
   }

   /** @deprecated */
   public boolean mkdir(String dirPath) throws NullPointerException {
      checkPath(dirPath);
      return this.mkdir0(dirPath);
   }

   /** @deprecated */
   public boolean mkdirs(String dirPath) throws NullPointerException {
      checkPath(dirPath);
      return this.mkdirs0(dirPath);
   }

   public boolean isAValidFileName(String filename) {
      if (filename != null && filename.length() != 0) {
         if (filename.charAt(0) == '.') {
            return false;
         } else if (filename.charAt(0) == ' ') {
            return false;
         } else {
            String illegalChars = "\\/:*?\"<>|";
            int length = "\\/:*?\"<>|".length();

            for(int i = 0; i < length; ++i) {
               if (filename.indexOf("\\/:*?\"<>|".charAt(i)) != -1) {
                  return false;
               }
            }

            if (filename.length() > 255) {
               return false;
            } else {
               return true;
            }
         }
      } else {
         return false;
      }
   }

   public static String makeAValidFileName(String filename) {
      StringBuffer retVal = new StringBuffer();
      int filenameLength = filename.length();
      String illegalChars = "\\/:*?\"<>|";

      for(int i = 0; i < filenameLength; ++i) {
         char c = filename.charAt(i);
         if (i == 0 && c == ' ' || i == 0 && c == '.') {
            retVal.append('_');
         } else if ("\\/:*?\"<>|".indexOf(c) != -1) {
            retVal.append('_');
         } else {
            retVal.append(c);
         }
      }

      if (filename.length() > 255) {
         retVal.setLength(255);
      }

      return retVal.toString();
   }

   public byte[] getFileContentFromJar(String jarPath, String fileName) {
      checkPath(jarPath);
      return this.getFileContentFromJar0(jarPath, fileName);
   }

   public boolean pathEndsWithSeparator(String path) {
      int index = path.lastIndexOf("\\".charAt(0));
      if (index == -1) {
         return false;
      } else {
         return index == path.length() - 1;
      }
   }

   private native boolean delete0(String var1, boolean var2) throws NullPointerException;

   private native boolean copy0(String var1, String var2);

   private native byte[] getFileContentFromJar0(String var1, String var2);

   private native boolean mkdir0(String var1) throws NullPointerException;

   private native boolean mkdirs0(String var1) throws NullPointerException;

   private native int rename0(String var1, String var2) throws NullPointerException;
}
