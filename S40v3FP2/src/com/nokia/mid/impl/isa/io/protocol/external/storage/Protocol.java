package com.nokia.mid.impl.isa.io.protocol.external.storage;

import com.nokia.mid.impl.isa.jsr75.SecurityPermission;
import com.nokia.mid.pri.PriAccess;
import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.TimeZone;
import javax.microedition.io.Connection;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.file.ConnectionClosedException;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.IllegalModeException;

public class Protocol implements FileConnection, ConnectionBaseInterface {
   private int mode;
   private String url;
   private String fullpath;
   private String drive;
   private String name;
   private String path;
   private boolean isClosed = true;
   private boolean exists;
   private boolean isFile;
   private boolean isWritable;
   private boolean isHidden;
   protected int fileHandle = -1;
   private boolean out_closed = true;
   private boolean in_closed = true;
   private InputStream in = null;
   private OutputStream out = null;
   private static final int MAX_LENGTH_OF_PATH = 258;
   private static final int MAX_LENGTH_OF_NAME = 255;
   private static final char UNICODE_REPLACEMENT_CHAR = '�';
   private int errorCode = 0;
   private static final int FILESYS_EROOTFULL = 16;
   private static final int FILESYS_ENOSPC = 8;
   private static final int ONE_SLASH_LENGTH = 1;
   private static final char SEPARATOR = '/';
   private static final String PARENT_DIR = "..";
   private static final String[] ILLEGAL_EXTENSIONS = new String[]{".jar", ".jad", ".rms"};
   private static final boolean SPRINT_SUPPORTED = PriAccess.getInt(5) == 1;
   private static final String FILE_SCHEME = "file://";
   private static final String SPRINT_ROOT_LOCALHOST_DRIVE = "//localhost//";
   private static final String ROOT_HOST_SLASHES = "///";
   private static final String SPRINT_ROOT_MEMORYCARD = "///MemoryCard/";
   private static final String SPRINT_ROOT_HOST_DRIVE = "////";
   private static final String MEMORYCARD_DRIVE_E = "E:";
   private static final String DRIVE_NAME_C = "C:";
   private String privateDir;
   private String privateName;
   private String privateDirPath;
   private String privateParentDir;
   private boolean privateWithHost = false;

   public Connection openPrim(String var1, int var2, boolean var3) throws IOException, IllegalArgumentException {
      if (var2 != 1 && var2 != 2 && var2 != 3) {
         throw new IllegalArgumentException("illegal mode: " + var2);
      } else {
         String var4 = unescape(var1);
         if (SPRINT_SUPPORTED) {
            System.getProperty("fileconn.dir.private");
            String var5 = this.getPrivateDir();
            this.privateDir = var5 + '/';
            if (var4.startsWith("////") | var4.startsWith("//localhost//") | var4.startsWith("///MemoryCard/")) {
               var4 = this.parseSprintPath(var4);
               this.privateDirPath = this.privateDir.substring("C:".length());
               this.privateParentDir = this.privateDirPath.substring(0, this.privateDirPath.substring(0, this.privateDirPath.length() - 1).lastIndexOf(47)) + '/';
               this.privateName = this.privateDirPath.substring(this.privateParentDir.length(), this.privateDirPath.length());
            }
         }

         this.url = "file:" + escape(var4);
         if (var4.indexOf(92) != -1) {
            throw new IllegalArgumentException();
         } else if (var4.indexOf("/.") != -1) {
            throw new IllegalArgumentException("Malformed URL");
         } else {
            int var6 = var4.indexOf(58);
            if (var6 == -1) {
               var6 = var4.indexOf(124);
            }

            if (var6 == -1) {
               throw new ConnectionNotFoundException("Only absolute file URL's of type <drive>:<path> are supported");
            } else {
               this.fullpath = var4.substring(var6 - 1);
               this.mode = var2;
               this.parseSeparator();
               if (!SecurityPermission.isPermitted(var2, this.fullpath, false)) {
                  throw new SecurityException("Access denied");
               } else {
                  this.parseURL();
                  if (!this.filesys_access_allowed(this.fullpath, var2)) {
                     throw new SecurityException("Access denied");
                  } else if (!this.isValidPathAndFilename(this.path, this.name)) {
                     throw new IllegalArgumentException("Invalid name");
                  } else {
                     this.isClosed = false;
                     return this;
                  }
               }
            }
         }
      }
   }

   private String parseSprintPath(String var1) {
      String var2;
      if (var1.startsWith("////")) {
         var2 = var1.substring("////".length());
         var1 = "///" + this.privateDir + var2;
      } else if (var1.startsWith("//localhost//")) {
         var2 = var1.substring("//localhost//".length());
         this.privateWithHost = true;
         var1 = "///" + this.privateDir + var2;
      } else if (var1.startsWith("///MemoryCard/")) {
         var2 = var1.substring("///MemoryCard/".length());
         var1 = "///E:" + var2;
      }

      return var1;
   }

   private int parseSeparator() throws IllegalArgumentException {
      int var1 = this.fullpath.indexOf(58);
      if (var1 == -1) {
         var1 = this.fullpath.indexOf(124);
         if (var1 != -1) {
            try {
               this.fullpath = this.fullpath.substring(0, var1) + ":" + this.fullpath.substring(var1 + 1);
            } catch (StringIndexOutOfBoundsException var3) {
               throw new IllegalArgumentException("| in invalid location");
            }
         }
      }

      return var1;
   }

   private void parseURL() throws IOException {
      this.filesys_attributes_get();

      try {
         this.drive = this.fullpath.substring(0, 2).toUpperCase();
         this.fullpath = this.drive + this.fullpath.substring(2);
         if (this.exists) {
            if (this.isFile) {
               if (this.fullpath.endsWith("/")) {
                  this.debug("remove the trailing slash");
                  this.fullpath = this.fullpath.substring(0, this.fullpath.length() - 1);
                  this.url = this.url.substring(0, this.url.length() - 1);
               }
            } else if (!this.fullpath.endsWith("/")) {
               this.debug("add a trailing slash");
               this.fullpath = this.fullpath + '/';
               this.url = this.url + '/';
            }
         }

         int var1 = this.fullpath.lastIndexOf(47);
         if (var1 != -1) {
            if (var1 == this.fullpath.length() - 1) {
               this.isFile = false;
               var1 = this.fullpath.lastIndexOf(47, var1 - 1);
            } else {
               this.isFile = true;
            }

            if (var1 == -1) {
               this.path = "/";
               if (this.isFile) {
                  this.name = this.fullpath.substring(4);
               }
            } else {
               this.path = this.fullpath.substring(this.parseSeparator() + 1, var1 + 1);
               this.name = this.fullpath.substring(var1 + 1);
            }

         }
      } catch (StringIndexOutOfBoundsException var2) {
         throw new IllegalArgumentException("Invalid path");
      }
   }

   public long availableSize() {
      if (this.isClosed) {
         throw new ConnectionClosedException();
      } else if (this.mode == 2) {
         throw new IllegalModeException("Connection opened in WRITE mode");
      } else {
         long[] var1 = new long[3];
         this.filesys_drivestat(var1);
         return var1[1] * var1[2];
      }
   }

   public boolean canRead() {
      if (this.isClosed) {
         throw new ConnectionClosedException();
      } else if (this.mode == 2) {
         throw new IllegalModeException("Connection opened in WRITE mode");
      } else {
         return this.private_exists();
      }
   }

   public boolean canWrite() {
      if (this.isClosed) {
         throw new ConnectionClosedException();
      } else if (this.mode == 2) {
         throw new IllegalModeException("Connection opened in WRITE mode");
      } else if (!this.private_exists()) {
         return false;
      } else {
         this.filesys_attributes_get();
         return this.isWritable;
      }
   }

   public void close() throws IOException {
      this.isClosed = true;
   }

   public void create() throws IOException {
      if (this.isClosed) {
         throw new ConnectionClosedException("connection is closed");
      } else if (!this.isFile) {
         throw new IOException("create called for a directory");
      } else if (this.private_exists()) {
         throw new IOException("already exists");
      } else if (this.mode == 1) {
         throw new IllegalModeException("Connection opened in READ mode");
      } else if (!this.filesys_create()) {
         switch(this.errorCode) {
         case 8:
            throw new IOException("No space on drive");
         case 16:
            throw new IOException("Root directory full");
         default:
            throw new IOException("Failed to create file");
         }
      }
   }

   public void delete() throws IOException {
      if (this.isClosed) {
         throw new ConnectionClosedException("connection is closed");
      } else if (this.mode == 1) {
         throw new IllegalModeException("Connection opened in READ mode");
      } else if (!this.private_exists()) {
         throw new IOException("connection target does not exist");
      } else {
         if (!this.out_closed && this.out != null) {
            this.out.flush();
            this.out.close();
         }

         if (!this.in_closed && this.in != null) {
            this.in.close();
         }

         if (!this.filesys_delete()) {
            throw new IOException("Failed to delete");
         } else {
            this.exists = false;
         }
      }
   }

   public long directorySize(boolean var1) throws IOException {
      if (this.isClosed) {
         throw new ConnectionClosedException("connection is closed");
      } else if (this.isFile) {
         throw new IOException("directorySize called on a file.");
      } else if (this.mode == 2) {
         throw new IllegalModeException("Connection opened in WRITE mode");
      } else {
         return !this.private_exists() ? -1L : this.filesys_directorySize(var1);
      }
   }

   public boolean exists() {
      if (this.isClosed) {
         throw new ConnectionClosedException("connection is closed");
      } else if (this.mode == 2) {
         throw new IllegalModeException("Connection opened in WRITE mode");
      } else {
         return this.private_exists();
      }
   }

   private boolean private_exists() {
      this.filesys_attributes_get();
      return this.exists;
   }

   public long fileSize() throws IOException {
      if (this.isClosed) {
         throw new ConnectionClosedException("connection is closed");
      } else if (!this.isFile) {
         throw new IOException("fileSize called on a directory.");
      } else if (this.mode == 2) {
         throw new IllegalModeException("Connection opened in WRITE mode");
      } else {
         return !this.private_exists() ? -1L : (long)this.filesys_fileSize();
      }
   }

   public String getName() {
      if (this.name != null) {
         if (SPRINT_SUPPORTED && this.privateName != null && this.name.equals(this.privateName)) {
            return "";
         } else {
            if (!this.isFile && !this.name.endsWith("/")) {
               this.name = this.name + "/";
            }

            return this.name;
         }
      } else {
         return "";
      }
   }

   public String getPath() {
      if (SPRINT_SUPPORTED && this.privateDirPath != null) {
         if (this.path.equals(this.privateParentDir)) {
            return "//";
         }

         if (this.path.startsWith(this.privateDirPath)) {
            String var1 = escape("//" + this.path.substring(this.privateDirPath.length()));
            return unescape(var1);
         }

         if (this.drive.equals("E:")) {
            return "/MemoryCard" + this.path;
         }
      }

      return "/" + this.drive + this.path;
   }

   public String getURL() {
      if (SPRINT_SUPPORTED && this.privateDirPath != null) {
         String var1 = "";
         if (this.privateWithHost) {
            var1 = "localhost";
         }

         return escape("file://" + var1 + this.getPath() + this.getName());
      } else {
         return this.url;
      }
   }

   public boolean isDirectory() {
      if (this.isClosed) {
         throw new ConnectionClosedException();
      } else if (this.mode == 2) {
         throw new IllegalModeException("Connection opened in WRITE mode");
      } else {
         this.private_exists();
         if (!this.exists) {
            return false;
         } else {
            return !this.isFile;
         }
      }
   }

   public boolean isHidden() {
      if (this.isClosed) {
         throw new ConnectionClosedException();
      } else if (this.mode == 2) {
         throw new IllegalModeException("Connection opened in WRITE mode");
      } else if (!this.private_exists()) {
         return false;
      } else {
         this.filesys_attributes_get();
         return this.isHidden;
      }
   }

   public boolean isOpen() {
      return !this.isClosed;
   }

   public long lastModified() {
      if (this.isClosed) {
         throw new ConnectionClosedException();
      } else if (this.mode == 2) {
         throw new IllegalModeException("Connection opened in WRITE mode");
      } else {
         TimeZone var3 = TimeZone.getDefault();
         long var4 = (long)var3.getRawOffset();
         long var1 = this.filesys_lastModified();
         if (var1 != 0L) {
            var1 -= var4;
         }

         return var1;
      }
   }

   public Enumeration list() throws IOException {
      return this.list("*", false);
   }

   public Enumeration list(String var1, boolean var2) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var1.equals("")) {
         throw new IllegalArgumentException("invalid character in filter");
      } else if (this.isClosed) {
         throw new ConnectionClosedException("connection is closed");
      } else if (this.isFile) {
         throw new IOException("list called on a file");
      } else if (!this.private_exists()) {
         throw new IOException("directory doesn't exist");
      } else if (this.mode == 2) {
         throw new IllegalModeException("Connection opened in WRITE mode");
      } else {
         var1 = unescape(var1);
         int var4 = "\\/:?\"<>|\u0000�".length();

         for(int var5 = 0; var5 < var4; ++var5) {
            if (var1.indexOf("\\/:?\"<>|\u0000�".charAt(var5)) != -1) {
               throw new IllegalArgumentException("invalid character in filter");
            }
         }

         if (this.path.length() + var1.length() > 258) {
            throw new IllegalArgumentException("filter too long");
         } else {
            return new Protocol.OpenDirEnumeration(var1, this.fullpath, var2);
         }
      }
   }

   public void mkdir() throws IOException {
      if (this.isClosed) {
         throw new ConnectionClosedException("connection is closed");
      } else if (this.private_exists()) {
         throw new IOException("already exists");
      } else if (this.isFile) {
         throw new IOException("mkdir called for a file");
      } else if (this.mode == 1) {
         throw new IllegalModeException("Connection opened in READ mode");
      } else if (!this.filesys_mkdir()) {
         switch(this.errorCode) {
         case 8:
            throw new IOException("No space on drive");
         case 16:
            throw new IOException("Root directory full");
         default:
            throw new IOException("Failed to create directory");
         }
      } else {
         this.exists = true;
      }
   }

   public DataInputStream openDataInputStream() throws IOException {
      InputStream var1 = this.openInputStream();
      return new DataInputStream(var1);
   }

   public DataOutputStream openDataOutputStream() throws IOException {
      OutputStream var1 = this.openOutputStream();
      return new DataOutputStream(var1);
   }

   public InputStream openInputStream() throws IOException {
      if (this.isClosed) {
         throw new IOException("connection is closed");
      } else if (!this.isFile) {
         throw new IOException("cannot open input streams on directories");
      } else if (this.in != null) {
         throw new IOException("InputStream is already open");
      } else if (!this.private_exists()) {
         throw new IOException("file does not exist");
      } else if (this.mode == 2) {
         throw new IllegalModeException("Connection opened in WRITE mode");
      } else {
         this.in = new Protocol.FileConnectionInputStream(this);
         this.in_closed = false;
         return this.in;
      }
   }

   public OutputStream openOutputStream() throws IOException {
      if (this.isClosed) {
         throw new IOException("connection is closed");
      } else if (!this.isFile) {
         throw new IOException("cannot open output stream on a directory");
      } else if (this.out != null) {
         throw new IOException("OutputStream is already open");
      } else if (!this.private_exists()) {
         throw new IOException("File does not exist");
      } else if (this.mode == 1) {
         throw new IllegalModeException("Connection opened in READ mode");
      } else {
         this.out = new Protocol.FileConnectionOutputStream(this);
         this.out_closed = false;
         return this.out;
      }
   }

   public OutputStream openOutputStream(long var1) throws IOException {
      OutputStream var3 = this.openOutputStream();
      long var4 = (long)this.filesys_fileSize();
      if (var1 > var4) {
         var1 = var4;
      }

      ((Protocol.FileConnectionOutputStream)var3).setFilepos(var1);
      return var3;
   }

   public void rename(String var1) throws IOException {
      if (this.isClosed) {
         throw new ConnectionClosedException("connection is closed");
      } else if (var1 == null) {
         throw new NullPointerException();
      } else if (var1.equals("")) {
         throw new IOException("Invalid filename");
      } else if (!this.private_exists()) {
         throw new IOException("file or directory doesn't exist");
      } else {
         var1 = unescape(var1);
         if (var1.indexOf(47) != -1) {
            throw new IllegalArgumentException();
         } else if (!this.isValidPathAndFilename(this.path, var1)) {
            throw new IOException("New name is invalid");
         } else if (this.mode == 1) {
            throw new IllegalModeException("Connection opened in READ mode");
         } else if (this.name == null) {
            throw new IOException("cannot rename root directory");
         } else {
            if (!this.out_closed && this.out != null) {
               this.out.flush();
               this.out.close();
            }

            if (!this.in_closed && this.in != null) {
               this.in.close();
            }

            if (!this.filesys_rename(var1)) {
               switch(this.errorCode) {
               case 8:
                  throw new IOException("No space on drive");
               case 16:
                  throw new IOException("Root directory full");
               default:
                  throw new IOException("Failed to rename file");
               }
            } else {
               this.name = var1;
               this.fullpath = this.drive + this.path + this.name;
               this.url = "file:///" + escape(this.fullpath);
            }
         }
      }
   }

   public void setFileConnection(String var1) throws IOException {
      if (this.isClosed) {
         throw new ConnectionClosedException("connection is closed");
      } else if (var1 == null) {
         throw new NullPointerException();
      } else if (this.isFile) {
         throw new IOException("setFileConnection can only be called on directories");
      } else if (!this.private_exists()) {
         throw new IOException("directory does not exist");
      } else {
         int var2 = var1.indexOf(47);
         if (var2 != -1 && var2 != var1.length() - 1) {
            throw new IllegalArgumentException();
         } else {
            String var3 = null;
            if (var1.equals("..")) {
               if (SPRINT_SUPPORTED && this.path.equals(this.privateParentDir)) {
                  throw new SecurityException("Access denied");
               }

               var3 = this.drive + this.path;
            } else {
               if (!this.isValidPathAndFilename(this.path, var1)) {
                  throw new IOException("invalid filename");
               }

               var3 = this.fullpath + var1;
            }

            if (!SecurityPermission.isPermitted(this.mode, var3, false)) {
               throw new SecurityException("Access denied");
            } else {
               String var4 = this.fullpath;
               this.fullpath = var3;
               if (!this.private_exists()) {
                  this.fullpath = var4;
                  throw new IllegalArgumentException("setFileConnection called with target that doesn't exist");
               } else {
                  this.url = "file:///" + escape(this.fullpath);
                  this.parseSeparator();
                  this.parseURL();
                  if (!this.filesys_access_allowed(this.fullpath, this.mode)) {
                     this.fullpath = var4;
                     this.url = "file:///" + escape(this.fullpath);
                     this.parseSeparator();
                     this.parseURL();
                     throw new SecurityException("Access denied");
                  }
               }
            }
         }
      }
   }

   public void setHidden(boolean var1) throws IOException {
      if (this.isClosed) {
         throw new ConnectionClosedException("connection is closed");
      } else if (this.mode == 1) {
         throw new IllegalModeException("Connection opened in READ mode");
      } else if (!this.private_exists()) {
         throw new IOException("file does not exist");
      } else if (!this.filesys_setHidden(var1)) {
         throw new IOException("failed to set hidden");
      }
   }

   public void setReadable(boolean var1) throws IOException {
      if (this.isClosed) {
         throw new ConnectionClosedException("connection is closed");
      } else if (this.mode == 1) {
         throw new IllegalModeException("Connection opened in READ mode");
      } else if (!this.private_exists()) {
         throw new IOException("file does not exist");
      }
   }

   public void setWritable(boolean var1) throws IOException {
      if (this.isClosed) {
         throw new ConnectionClosedException("connection is closed");
      } else if (this.mode == 1) {
         throw new IllegalModeException("Connection opened in READ mode");
      } else if (!this.private_exists()) {
         throw new IOException("file does not exist");
      } else if (!this.filesys_setWritable(var1)) {
         throw new IOException("failed to set writable");
      }
   }

   public long totalSize() {
      if (this.isClosed) {
         throw new ConnectionClosedException("connection is closed");
      } else if (this.mode == 2) {
         throw new IllegalModeException("Connection opened in WRITE mode");
      } else {
         long[] var1 = new long[3];
         this.filesys_drivestat(var1);
         return var1[0] * var1[2];
      }
   }

   public void truncate(long var1) throws IOException {
      if (this.isClosed) {
         throw new ConnectionClosedException("connection is closed");
      } else if (this.mode == 1) {
         throw new IllegalModeException("Connection opened in READ mode");
      } else if (var1 < 0L) {
         throw new IllegalArgumentException("invalid byteOffset value");
      } else if (!this.isFile) {
         throw new IOException("truncate called on a directory");
      } else if (!this.private_exists()) {
         throw new IOException("truncate called on a non existant file");
      } else {
         if (!this.out_closed && this.out != null) {
            this.out.flush();
         }

         if (!this.filesys_truncate(var1)) {
            throw new IOException("failed to truncate");
         } else {
            if (!this.out_closed && this.out != null) {
               ((Protocol.FileConnectionOutputStream)this.out).truncate(var1);
            }

            if (!this.in_closed && this.in != null) {
               ((Protocol.FileConnectionInputStream)this.in).truncate(var1);
            }

         }
      }
   }

   public long usedSize() {
      if (this.isClosed) {
         throw new ConnectionClosedException("connection is closed");
      } else if (this.mode == 2) {
         throw new IllegalModeException("Connection opened in WRITE mode");
      } else {
         long[] var1 = new long[3];
         this.filesys_drivestat(var1);
         return (var1[0] - var1[1]) * var1[2];
      }
   }

   private boolean isValidPathAndFilename(String var1, String var2) {
      if (var2 != null && var2.length() != 0) {
         String var3 = " .";
         if (var3.indexOf(var2.charAt(0)) != -1) {
            return false;
         } else if (!this.isValidExtension(var2)) {
            return false;
         } else {
            String var4 = "\\:*?\"<>|\u0000�";

            int var5;
            for(var5 = 0; var5 < var4.length(); ++var5) {
               if (var2.indexOf(var4.charAt(var5)) != -1) {
                  return false;
               }
            }

            var5 = var2.indexOf(47);
            if (var5 != -1 && var5 != var2.length() - 1) {
               return false;
            } else {
               int var6 = 255;
               if (var5 == var2.length() - 1) {
                  ++var6;
               }

               if (var2.length() > var6) {
                  return false;
               } else {
                  for(int var7 = 0; var7 < var4.length(); ++var7) {
                     if (var1.indexOf(var4.charAt(var7)) != -1) {
                        return false;
                     }
                  }

                  var6 = 258;
                  if (var5 == var2.length() - 1) {
                     ++var6;
                  }

                  return var1.length() + var2.length() <= var6;
               }
            }
         }
      } else {
         return true;
      }
   }

   private boolean isValidExtension(String var1) {
      for(int var2 = 0; var2 < ILLEGAL_EXTENSIONS.length; ++var2) {
         int var3 = ILLEGAL_EXTENSIONS[var2].length();
         if (var1.regionMatches(true, var1.length() - var3, ILLEGAL_EXTENSIONS[var2], 0, var3)) {
            return false;
         }
      }

      return true;
   }

   protected native String getPrivateDir();

   protected native boolean filesys_access_allowed(String var1, int var2);

   native void filesys_attributes_get();

   native void filesys_drivestat(long[] var1);

   native boolean filesys_create();

   native boolean filesys_delete();

   native long filesys_directorySize(boolean var1);

   protected native int filesys_fileSize();

   native long filesys_lastModified();

   native boolean filesys_mkdir();

   native boolean filesys_rename(String var1);

   native boolean filesys_setHidden(boolean var1);

   native boolean filesys_setWritable(boolean var1);

   native boolean filesys_truncate(long var1);

   protected native boolean filesys_open();

   protected native boolean filesys_close();

   protected native int filesys_read(byte[] var1, int var2, int var3, int var4);

   native int filesys_write(byte[] var1, int var2, int var3, int var4);

   native void debug(String var1);

   private static String unescape(String var0) throws IllegalArgumentException {
      if (var0.indexOf(37) == -1) {
         return var0;
      } else {
         try {
            byte[] var1 = var0.getBytes("UTF-8");
            byte[] var2 = new byte[var1.length];
            int var3 = 0;

            for(int var4 = 0; var4 < var1.length; ++var3) {
               switch(var1[var4]) {
               case 37:
                  try {
                     if ((char)var1[var4 + 1] == '-') {
                        throw new IllegalArgumentException("malformed %xx escape sequence");
                     }

                     var2[var3] = (byte)Integer.parseInt((char)var1[var4 + 1] + "" + (char)var1[var4 + 2], 16);
                     var4 += 2;
                     break;
                  } catch (ArrayIndexOutOfBoundsException var6) {
                     throw new IllegalArgumentException("malformed %xx escape sequence");
                  } catch (NumberFormatException var7) {
                     throw new IllegalArgumentException("malformed %xx escape sequence");
                  }
               case 43:
                  var2[var3] = 32;
                  break;
               default:
                  var2[var3] = var1[var4];
               }

               ++var4;
            }

            String var9 = new String(var2, 0, var3, "UTF-8");
            if (var9.length() != 0 && var9.indexOf(65533) == -1) {
               return var9;
            } else {
               throw new IllegalArgumentException("malformed UTF-8 encoding, or replacement character used");
            }
         } catch (UnsupportedEncodingException var8) {
            throw new Error("Either ASCII or UTF-8 is not supported");
         }
      }
   }

   private static String escape(String var0) throws IllegalArgumentException {
      StringBuffer var1 = new StringBuffer();
      int var2 = var0.length();
      boolean var3 = true;
      boolean var5 = false;
      int var6 = 0;

      for(boolean var7 = true; var6 < var2; ++var6) {
         char var11 = var0.charAt(var6);
         if (var11 == ' ') {
            var1.append("%20");
         } else if (var11 == '^') {
            var1.append("%5e");
         } else if (var11 == '%') {
            var1.append("%25");
         } else if (var11 <= 127) {
            var1.append((char)var11);
         } else {
            int var8;
            int var9;
            if (var11 <= 2047) {
               var8 = 192 | (var11 & 1984) >> 6;
               var9 = 128 | var11 & 63;
               var1.append("%" + Integer.toHexString(var8) + "%" + Integer.toHexString(var9));
            } else {
               if (var11 > '\uffff') {
                  throw new IllegalArgumentException("bad string");
               }

               var8 = 224 | (var11 & '\uf000') >> 12;
               var9 = 128 | (var11 & 4032) >> 6;
               int var10 = 128 | var11 & 63;
               var1.append("%" + Integer.toHexString(var8) + "%" + Integer.toHexString(var9) + "%" + Integer.toHexString(var10));
            }
         }
      }

      return var1.toString();
   }

   private class OpenDirEnumeration implements Enumeration {
      private String filter;
      private int dir_handle;
      private String next_string = null;
      private String fullpath;
      private boolean includeHidden;

      OpenDirEnumeration(String var2, String var3, boolean var4) {
         this.fullpath = var3;
         this.includeHidden = var4;
         this.filesys_opendir(var2);
         this.getNextEntry();
      }

      public boolean hasMoreElements() {
         return this.next_string != null;
      }

      public Object nextElement() {
         if (this.next_string == null) {
            return null;
         } else {
            String var1 = this.next_string;
            this.getNextEntry();
            return var1;
         }
      }

      private void getNextEntry() {
         while((this.next_string = this.filesys_readdir()) != null && !Protocol.this.isValidExtension(this.next_string)) {
         }

      }

      native void filesys_opendir(String var1);

      native String filesys_readdir();
   }

   private class FileConnectionOutputStream extends OutputStream {
      private int filepos = 0;
      private FileConnection fc;

      FileConnectionOutputStream(FileConnection var2) throws IOException {
         this.fc = var2;
         if (Protocol.this.fileHandle == -1) {
            Protocol.this.filesys_open();
            if (Protocol.this.fileHandle == -1) {
               throw new IOException("failed to open file for writing");
            }
         }

      }

      void setFilepos(long var1) {
         this.filepos = (int)var1;
      }

      void truncate(long var1) {
         if ((long)this.filepos > var1) {
            this.filepos = (int)var1;
         }

      }

      public void close() throws IOException {
         if (!Protocol.this.out_closed) {
            Protocol.this.out = null;
            Protocol.this.out_closed = true;
            if (Protocol.this.in == null && Protocol.this.in_closed) {
               if (!Protocol.this.filesys_close()) {
                  throw new IOException("failed to close file");
               }

               Protocol.this.fileHandle = -1;
            }

         }
      }

      public void flush() throws IOException {
      }

      public void write(byte[] var1) throws IOException {
         this.write(var1, 0, var1.length);
      }

      public void write(byte[] var1, int var2, int var3) throws IOException {
         if (var1 == null) {
            throw new NullPointerException();
         } else if (Protocol.this.out_closed) {
            throw new IOException("OutputStream is closed");
         } else if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length) {
            int var4 = Protocol.this.filesys_write(var1, this.filepos, var2, var3);
            if (var4 <= 0) {
               throw new IOException("failed to write to file");
            } else if (var4 < var3) {
               throw new IOException("only managed to write " + var4 + " of " + var3 + " bytes to file");
            } else {
               this.filepos += var4;
            }
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public void write(int var1) throws IOException {
         byte[] var2 = new byte[]{(byte)var1};
         this.write(var2, 0, var2.length);
      }
   }

   private class FileConnectionInputStream extends InputStream {
      private int filepos = 0;
      private int marked_filepos = -1;
      private FileConnection fc;

      FileConnectionInputStream(FileConnection var2) throws IOException {
         this.fc = var2;
         if (Protocol.this.fileHandle == -1) {
            Protocol.this.filesys_open();
            if (Protocol.this.fileHandle == -1) {
               throw new IOException("failed to open file for reading");
            }
         }

      }

      void truncate(long var1) {
         if ((long)this.filepos > var1) {
            this.filepos = (int)var1;
         }

         if ((long)this.marked_filepos > var1) {
            this.marked_filepos = -1;
         }

      }

      public int available() throws IOException {
         int var1 = Protocol.this.filesys_fileSize();
         return var1 - this.filepos;
      }

      public void close() throws IOException {
         if (!Protocol.this.in_closed) {
            Protocol.this.in = null;
            Protocol.this.in_closed = true;
            if (Protocol.this.out == null && Protocol.this.out_closed) {
               if (!Protocol.this.filesys_close()) {
                  throw new IOException("failed to close file");
               }

               Protocol.this.fileHandle = -1;
            }

         }
      }

      public void mark(int var1) {
         this.marked_filepos = this.filepos;
      }

      public boolean markSupported() {
         return true;
      }

      public int read() throws IOException {
         if (Protocol.this.in_closed) {
            throw new IOException("InputStream is closed");
         } else if (Protocol.this.filesys_fileSize() == this.filepos) {
            return -1;
         } else {
            byte[] var1 = new byte[1];
            if (Protocol.this.filesys_read(var1, this.filepos, 0, 1) > 0) {
               ++this.filepos;
               return var1[0] & 255;
            } else {
               throw new IOException("error reading from file");
            }
         }
      }

      public int read(byte[] var1) throws IOException {
         if (var1 == null) {
            throw new NullPointerException();
         } else if (Protocol.this.in_closed) {
            throw new IOException("InputStream is closed");
         } else if (Protocol.this.filesys_fileSize() == this.filepos) {
            return -1;
         } else {
            int var2 = Protocol.this.filesys_read(var1, this.filepos, 0, var1.length);
            if (var2 < 0) {
               throw new IOException("error reading from file");
            } else {
               this.filepos += var2;
               return var2;
            }
         }
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         if (var1 == null) {
            throw new NullPointerException();
         } else if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length) {
            if (Protocol.this.in_closed) {
               throw new IOException("InputStream is closed");
            } else if (Protocol.this.filesys_fileSize() == this.filepos) {
               return -1;
            } else {
               int var4 = Protocol.this.filesys_read(var1, this.filepos, var2, var3);
               if (var4 < 0) {
                  throw new IOException("error reading from file");
               } else {
                  this.filepos += var4;
                  return var4;
               }
            }
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public void reset() throws IOException {
         if (this.marked_filepos == -1) {
            throw new IOException("reset called before call to mark");
         } else {
            this.filepos = this.marked_filepos;
         }
      }

      public long skip(long var1) throws IOException {
         if (var1 < 0L) {
            return 0L;
         } else {
            int var3 = this.available();
            if ((long)var3 > var1) {
               this.filepos = (int)((long)this.filepos + var1);
               return var1;
            } else {
               this.filepos += var3;
               return (long)var3;
            }
         }
      }
   }
}
