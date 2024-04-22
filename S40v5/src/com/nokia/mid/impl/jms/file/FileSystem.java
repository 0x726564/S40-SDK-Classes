package com.nokia.mid.impl.jms.file;

import java.io.IOException;

public class FileSystem {
   private static FileSystem la = null;
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
   public static final byte FILESYSTEM_JAVA_DOWNLOAD_DATA_FILE = 50;
   public static final String FILESYSTEM_PATH_SEPARATOR = "\\";
   public static final String FILESYSTEM_DRIVE_SEPARATOR = "\\\\";
   private static final String[] lb = new String[]{getFileSystem().getSystemFilePath(11) + "\\", getFileSystem().getSystemFilePath(8) + "\\", getFileSystem().getSystemFilePath(1) + "\\", getFileSystem().getSystemFilePath(14) + "\\"};
   private static final String lc = getFileSystem().getSystemFilePath(50);

   private FileSystem() {
   }

   public static FileSystem getFileSystem() {
      return la != null ? la : (la = new FileSystem());
   }

   public native String getSystemFilePath(int var1);

   /** @deprecated */
   public native long getFreeSpaceAvailable();

   /** @deprecated */
   public native long getTotalSpace();

   static void P(String var0) throws SecurityException {
      if ((var0 = var0.replace('/', '\\')).indexOf("..\\") > -1) {
         throw new SecurityException();
      } else if (!var0.equals(lc)) {
         for(int var1 = 0; var1 < lb.length; ++var1) {
            String var2;
            if ((var2 = lb[var1]) != null && var0.length() >= var2.length() && var0.startsWith(var2)) {
               return;
            }
         }

         throw new SecurityException();
      }
   }

   /** @deprecated */
   public boolean createFile(String var1, byte[] var2, int var3, int var4) throws NullPointerException, IOException {
      P(var1);
      if (var1.equals("")) {
         return false;
      } else if ((new File(var1)).exists()) {
         return false;
      } else if (var2 != null && var3 + var4 > var2.length) {
         throw new IndexOutOfBoundsException();
      } else {
         FileOutputStream var7 = null;

         try {
            var7 = new FileOutputStream(var1, true, false);
         } catch (IOException var6) {
            return false;
         }

         try {
            if (var2 != null) {
               var7.write(var2, var3, var4);
            }

            var7.close();
            return true;
         } catch (IOException var5) {
            var7.close();
            throw var5;
         }
      }
   }

   /** @deprecated */
   public boolean delete(String var1, boolean var2) throws NullPointerException {
      P(var1);
      return this.delete0(var1, var2);
   }

   /** @deprecated */
   public int rename(String var1, String var2) throws NullPointerException {
      P(var1);
      P(var2);
      return this.rename0(var1, var2);
   }

   public boolean copy(String var1, String var2) throws NullPointerException {
      P(var1);
      P(var2);
      boolean var3 = false;
      String var7 = new String(var2);
      if (var1 != null && var2 != null) {
         try {
            File var4 = new File(var1);
            File var5 = new File(var2);
            if (!var4.exists()) {
               return false;
            }

            if (!var4.isDirectory()) {
               if (var5.exists()) {
                  if (!var5.isDirectory()) {
                     return false;
                  }

                  if (this.pathEndsWithSeparator(var2)) {
                     var7 = var2 + var4.getName();
                  } else {
                     var7 = var2 + "\\" + var4.getName();
                  }
               }

               var3 = this.copy0(var1, var7);
            } else {
               if (var5.exists() && !var5.isDirectory()) {
                  return false;
               }

               var3 = this.copy0(var1, var2);
            }
         } catch (Exception var6) {
            var3 = false;
         }

         return var3;
      } else {
         throw new NullPointerException();
      }
   }

   /** @deprecated */
   public boolean mkdir(String var1) throws NullPointerException {
      P(var1);
      return this.mkdir0(var1);
   }

   /** @deprecated */
   public boolean mkdirs(String var1) throws NullPointerException {
      P(var1);
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

            for(int var2 = 0; var2 < var3; ++var2) {
               if (var1.indexOf("\\/:*?\"<>|".charAt(var2)) != -1) {
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

      for(int var3 = 0; var3 < var2; ++var3) {
         char var4 = var0.charAt(var3);
         if (var3 == 0 && var4 == ' ' || var3 == 0 && var4 == '.') {
            var1.append('_');
         } else if ("\\/:*?\"<>|".indexOf(var4) != -1) {
            var1.append('_');
         } else {
            var1.append(var4);
         }
      }

      if (var0.length() > 255) {
         var1.setLength(255);
      }

      return var1.toString();
   }

   public byte[] getFileContentFromJar(String var1, String var2) {
      P(var1);
      return this.getFileContentFromJar0(var1, var2);
   }

   public boolean pathEndsWithSeparator(String var1) {
      int var2;
      if ((var2 = var1.lastIndexOf("\\".charAt(0))) == -1) {
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
