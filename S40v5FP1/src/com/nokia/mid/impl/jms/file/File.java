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
   private String m_Path;

   public static File getFile(String path) throws NullPointerException, IllegalArgumentException {
      return new File(path);
   }

   public static File getFile(File parent, String child) throws NullPointerException, IllegalArgumentException {
      return new File(parent, child);
   }

   public static File getFile(String parent, String child) throws NullPointerException, IllegalArgumentException {
      return new File(parent, child);
   }

   protected File(String path) throws NullPointerException, IllegalArgumentException {
      this.init((String)null, path);
   }

   protected File(File parent, String child) throws NullPointerException, IllegalArgumentException {
      this.init(parent.getPath(), child);
   }

   protected File(String parent, String child) throws NullPointerException, IllegalArgumentException {
      this.init(parent, child);
   }

   private void init(String parent, String child) throws NullPointerException, IllegalArgumentException {
      if (child == null) {
         throw new NullPointerException();
      } else if (child.equals("")) {
         throw new IllegalArgumentException();
      } else {
         if (parent != null) {
            FileSystem.checkPath(parent + child);
         } else {
            FileSystem.checkPath(child);
         }

         try {
            if (parent != null) {
               File parentFile = new File(parent);
               if (!parentFile.exists() || !parentFile.isDirectory()) {
                  throw new IllegalArgumentException("Bad parent");
               }
            }
         } catch (IOException var4) {
            throw new IllegalArgumentException("Bad parent");
         }

         if (parent != null) {
            if (FileSystem.getFileSystem().pathEndsWithSeparator(parent)) {
               this.m_Path = parent + child;
            } else {
               this.m_Path = parent + "\\" + child;
            }
         } else {
            this.m_Path = child;
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
      int index = this.m_Path.lastIndexOf("\\".charAt(0));
      return index == -1 ? this.m_Path : this.m_Path.substring(index + 1);
   }

   public String getParent() {
      int index = this.m_Path.lastIndexOf("\\".charAt(0));
      return index == -1 ? this.m_Path : this.m_Path.substring(0, index);
   }

   public short getFileType(boolean useExtOnly) {
      if (!useExtOnly) {
         short fileType = this.getFileType0();
         if (fileType != 0) {
            return fileType;
         }
      }

      int index = this.m_Path.lastIndexOf(46);
      if (index == -1) {
         return 0;
      } else {
         String extension = this.m_Path.substring(index + 1).toUpperCase();
         if (extension.equals("JAD")) {
            return 1;
         } else if (extension.equals("JAR")) {
            return 2;
         } else {
            return (short)(extension.equals("RMS") ? 3 : 0);
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

   public boolean delete(boolean failIfNotEmpty) {
      return FileSystem.getFileSystem().delete(this.m_Path, failIfNotEmpty);
   }

   public boolean rename(String newName) {
      String newPath = this.getParent() + "\\" + newName;
      FileSystem.checkPath(newPath);
      int result = FileSystem.getFileSystem().rename(this.m_Path, newPath);
      return result == 0;
   }

   public boolean moveTo(File destination) {
      try {
         if (destination.exists() && destination.isDirectory()) {
            String newPath = destination.getPath() + "\\" + this.getName();
            int retVal = FileSystem.getFileSystem().rename(this.m_Path, newPath);
            if (retVal == 0) {
               this.m_Path = newPath;
            }

            return retVal == 0;
         } else {
            throw new IllegalArgumentException("The destination is not a directory or does not exist.");
         }
      } catch (IOException var4) {
         var4.printStackTrace();
         return false;
      }
   }

   public boolean copyTo(File destination) {
      String newPath = destination.getPath() + "\\" + this.getName();
      return FileSystem.getFileSystem().copy(this.m_Path, newPath);
   }

   public native boolean isDirectory() throws IOException;

   public File[] listContents(boolean filesOnly, String filter) throws NullPointerException, IllegalArgumentException, IOException {
      if (filter == null) {
         throw new NullPointerException();
      } else if (filter.equals("")) {
         throw new IllegalArgumentException("Empty Filter");
      } else if (filter.indexOf(47) == -1 && filter.indexOf(58) == -1) {
         if (!this.exists()) {
            throw new IOException("File/Folder does not exist");
         } else if (!this.isDirectory()) {
            return null;
         } else {
            String[] filePaths = this.getChildren0(filesOnly, filter);
            if (filePaths != null && filePaths.length != 0) {
               File[] files = new File[filePaths.length];

               try {
                  for(int i = 0; i < filePaths.length; ++i) {
                     files[i] = new File(filePaths[i]);
                  }

                  return files;
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

   public int getDRMType() {
      return this.getDRMType0(this.m_Path);
   }

   private native int getDRMType0(String var1);

   private native String[] getChildren0(boolean var1, String var2);

   private native short getFileType0();
}
