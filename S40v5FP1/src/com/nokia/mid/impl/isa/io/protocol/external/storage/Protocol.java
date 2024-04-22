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
   protected boolean out_closed = true;
   private boolean in_closed = true;
   private InputStream in = null;
   protected OutputStream out = null;
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

   public Connection openPrim(String _name, int mode, boolean timeouts) throws IOException, IllegalArgumentException {
      if (mode != 1 && mode != 2 && mode != 3) {
         throw new IllegalArgumentException("illegal mode: " + mode);
      } else {
         String name = unescape(_name);
         if (SPRINT_SUPPORTED) {
            System.getProperty("fileconn.dir.private");
            String midletDir = this.getPrivateDir();
            this.privateDir = midletDir + '/';
            if (name.startsWith("////") | name.startsWith("//localhost//") | name.startsWith("///MemoryCard/")) {
               name = this.parseSprintPath(name);
               this.privateDirPath = this.privateDir.substring("C:".length());
               this.privateParentDir = this.privateDirPath.substring(0, this.privateDirPath.substring(0, this.privateDirPath.length() - 1).lastIndexOf(47)) + '/';
               this.privateName = this.privateDirPath.substring(this.privateParentDir.length(), this.privateDirPath.length());
            }
         }

         this.url = "file:" + escape(name);
         if (name.indexOf(92) != -1) {
            throw new IllegalArgumentException();
         } else if (name.indexOf("/.") != -1) {
            throw new IllegalArgumentException("Malformed URL");
         } else {
            int n = name.indexOf(58);
            if (n == -1) {
               n = name.indexOf(124);
            }

            if (n == -1) {
               throw new ConnectionNotFoundException("Only absolute file URL's of type <drive>:<path> are supported");
            } else {
               this.fullpath = name.substring(n - 1);
               this.mode = mode;
               this.parseSeparator();
               if (!SecurityPermission.isPermitted(mode, this.fullpath, false)) {
                  throw new SecurityException("Access denied");
               } else {
                  this.parseURL();
                  if (!this.filesys_access_allowed(this.fullpath, mode)) {
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

   private String parseSprintPath(String name) {
      String nameEnd;
      if (name.startsWith("////")) {
         nameEnd = name.substring("////".length());
         name = "///" + this.privateDir + nameEnd;
      } else if (name.startsWith("//localhost//")) {
         nameEnd = name.substring("//localhost//".length());
         this.privateWithHost = true;
         name = "///" + this.privateDir + nameEnd;
      } else if (name.startsWith("///MemoryCard/")) {
         nameEnd = name.substring("///MemoryCard/".length());
         name = "///E:" + nameEnd;
      }

      return name;
   }

   private int parseSeparator() throws IllegalArgumentException {
      int n = this.fullpath.indexOf(58);
      if (n == -1) {
         n = this.fullpath.indexOf(124);
         if (n != -1) {
            try {
               this.fullpath = this.fullpath.substring(0, n) + ":" + this.fullpath.substring(n + 1);
            } catch (StringIndexOutOfBoundsException var3) {
               throw new IllegalArgumentException("| in invalid location");
            }
         }
      }

      return n;
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

         int lastSlash = this.fullpath.lastIndexOf(47);
         if (lastSlash != -1) {
            if (lastSlash == this.fullpath.length() - 1) {
               this.isFile = false;
               lastSlash = this.fullpath.lastIndexOf(47, lastSlash - 1);
            } else {
               this.isFile = true;
            }

            if (lastSlash == -1) {
               this.path = "/";
               if (this.isFile) {
                  this.name = this.fullpath.substring(4);
               }
            } else {
               this.path = this.fullpath.substring(this.parseSeparator() + 1, lastSlash + 1);
               this.name = this.fullpath.substring(lastSlash + 1);
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
         long[] drivestat = new long[3];
         this.filesys_drivestat(drivestat);
         return drivestat[1] * drivestat[2];
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

   public long directorySize(boolean includeSubDirs) throws IOException {
      if (this.isClosed) {
         throw new ConnectionClosedException("connection is closed");
      } else if (this.isFile) {
         throw new IOException("directorySize called on a file.");
      } else if (this.mode == 2) {
         throw new IllegalModeException("Connection opened in WRITE mode");
      } else {
         return !this.private_exists() ? -1L : this.filesys_directorySize(includeSubDirs);
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

   protected boolean private_exists() {
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
            String newPath = escape("//" + this.path.substring(this.privateDirPath.length()));
            return unescape(newPath);
         }

         if (this.drive.equals("E:")) {
            return "/MemoryCard" + this.path;
         }
      }

      return "/" + this.drive + this.path;
   }

   public String getURL() {
      if (SPRINT_SUPPORTED && this.privateDirPath != null) {
         String host = "";
         if (this.privateWithHost) {
            host = "localhost";
         }

         return escape("file://" + host + this.getPath() + this.getName());
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
         TimeZone tzDefault = TimeZone.getDefault();
         long tzDelta = (long)tzDefault.getRawOffset();
         long modifyTime = this.filesys_lastModified();
         if (modifyTime != 0L) {
            modifyTime -= tzDelta;
         }

         return modifyTime;
      }
   }

   public Enumeration list() throws IOException {
      return this.list("*", false);
   }

   public Enumeration list(String filter, boolean includeHidden) throws IOException {
      if (filter == null) {
         throw new NullPointerException();
      } else if (filter.equals("")) {
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
         filter = unescape(filter);
         String illegalFilterChars = "\\/:?\"<>|\u0000�";
         int len = "\\/:?\"<>|\u0000�".length();

         for(int i = 0; i < len; ++i) {
            if (filter.indexOf("\\/:?\"<>|\u0000�".charAt(i)) != -1) {
               throw new IllegalArgumentException("invalid character in filter");
            }
         }

         if (this.path.length() + filter.length() > 258) {
            throw new IllegalArgumentException("filter too long");
         } else {
            return new Protocol.OpenDirEnumeration(filter, this.fullpath, includeHidden);
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
      InputStream in = this.openInputStream();
      return new DataInputStream(in);
   }

   public DataOutputStream openDataOutputStream() throws IOException {
      OutputStream out = this.openOutputStream();
      return new DataOutputStream(out);
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
         this.out = this.createOutputStream();
         this.out_closed = false;
         return this.out;
      }
   }

   protected OutputStream createOutputStream() throws IOException {
      Protocol.FileConnectionOutputStream out = new Protocol.FileConnectionOutputStream();
      return out;
   }

   public OutputStream openOutputStream(long byteOffset) throws IOException {
      OutputStream out = this.openOutputStream();
      long fileSize = (long)this.filesys_fileSize();
      if (byteOffset > fileSize) {
         byteOffset = fileSize;
      }

      ((Protocol.FileConnectionOutputStream)out).setFilepos(byteOffset);
      return out;
   }

   public void rename(String newName) throws IOException {
      if (this.isClosed) {
         throw new ConnectionClosedException("connection is closed");
      } else if (newName == null) {
         throw new NullPointerException();
      } else if (newName.equals("")) {
         throw new IOException("Invalid filename");
      } else if (!this.private_exists()) {
         throw new IOException("file or directory doesn't exist");
      } else {
         newName = unescape(newName);
         if (newName.indexOf(47) != -1) {
            throw new IllegalArgumentException();
         } else if (!this.isValidPathAndFilename(this.path, newName)) {
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

            if (!this.filesys_rename(newName)) {
               switch(this.errorCode) {
               case 8:
                  throw new IOException("No space on drive");
               case 16:
                  throw new IOException("Root directory full");
               default:
                  throw new IOException("Failed to rename file");
               }
            } else {
               this.name = newName;
               this.fullpath = this.drive + this.path + this.name;
               this.url = "file:///" + escape(this.fullpath);
            }
         }
      }
   }

   public void setFileConnection(String fileName) throws IOException {
      if (this.isClosed) {
         throw new ConnectionClosedException("connection is closed");
      } else if (fileName == null) {
         throw new NullPointerException();
      } else if (this.isFile) {
         throw new IOException("setFileConnection can only be called on directories");
      } else if (!this.private_exists()) {
         throw new IOException("directory does not exist");
      } else {
         int slashIdx = fileName.indexOf(47);
         if (slashIdx != -1 && slashIdx != fileName.length() - 1) {
            throw new IllegalArgumentException();
         } else {
            String newFullpath = null;
            if (fileName.equals("..")) {
               if (SPRINT_SUPPORTED && this.path.equals(this.privateParentDir)) {
                  throw new SecurityException("Access denied");
               }

               if (this.name == null || this.name.length() == 0) {
                  throw new IOException("Cannot go upper than the root directory");
               }

               newFullpath = this.drive + this.path;
            } else {
               if (!this.isValidPathAndFilename(this.path, fileName)) {
                  throw new IOException("invalid filename");
               }

               newFullpath = this.fullpath + fileName;
            }

            if (!SecurityPermission.isPermitted(this.mode, newFullpath, false)) {
               throw new SecurityException("Access denied");
            } else {
               String originalFullpath = this.fullpath;
               this.fullpath = newFullpath;
               if (!this.private_exists()) {
                  this.fullpath = originalFullpath;
                  throw new IllegalArgumentException("setFileConnection called with target that doesn't exist");
               } else {
                  this.url = "file:///" + escape(this.fullpath);
                  this.parseSeparator();
                  this.parseURL();
                  if (!this.filesys_access_allowed(this.fullpath, this.mode)) {
                     this.fullpath = originalFullpath;
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

   public void setHidden(boolean hidden) throws IOException {
      if (this.isClosed) {
         throw new ConnectionClosedException("connection is closed");
      } else if (this.mode == 1) {
         throw new IllegalModeException("Connection opened in READ mode");
      } else if (!this.private_exists()) {
         throw new IOException("file does not exist");
      } else if (!this.filesys_setHidden(hidden)) {
         throw new IOException("failed to set hidden");
      }
   }

   public void setReadable(boolean readable) throws IOException {
      if (this.isClosed) {
         throw new ConnectionClosedException("connection is closed");
      } else if (this.mode == 1) {
         throw new IllegalModeException("Connection opened in READ mode");
      } else if (!this.private_exists()) {
         throw new IOException("file does not exist");
      }
   }

   public void setWritable(boolean writable) throws IOException {
      if (this.isClosed) {
         throw new ConnectionClosedException("connection is closed");
      } else if (this.mode == 1) {
         throw new IllegalModeException("Connection opened in READ mode");
      } else if (!this.private_exists()) {
         throw new IOException("file does not exist");
      } else if (!this.filesys_setWritable(writable)) {
         throw new IOException("failed to set writable");
      }
   }

   public long totalSize() {
      if (this.isClosed) {
         throw new ConnectionClosedException("connection is closed");
      } else if (this.mode == 2) {
         throw new IllegalModeException("Connection opened in WRITE mode");
      } else {
         long[] drivestat = new long[3];
         this.filesys_drivestat(drivestat);
         return drivestat[0] * drivestat[2];
      }
   }

   public void truncate(long byteOffset) throws IOException {
      if (this.isClosed) {
         throw new ConnectionClosedException("connection is closed");
      } else if (this.mode == 1) {
         throw new IllegalModeException("Connection opened in READ mode");
      } else if (byteOffset < 0L) {
         throw new IllegalArgumentException("invalid byteOffset value");
      } else if (!this.isFile) {
         throw new IOException("truncate called on a directory");
      } else if (!this.private_exists()) {
         throw new IOException("truncate called on a non existant file");
      } else {
         if (!this.out_closed && this.out != null) {
            this.out.flush();
         }

         if (!this.filesys_truncate(byteOffset)) {
            throw new IOException("failed to truncate");
         } else {
            if (!this.out_closed && this.out != null) {
               ((Protocol.FileConnectionOutputStream)this.out).truncate(byteOffset);
            }

            if (!this.in_closed && this.in != null) {
               ((Protocol.FileConnectionInputStream)this.in).truncate(byteOffset);
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
         long[] drivestat = new long[3];
         this.filesys_drivestat(drivestat);
         return (drivestat[0] - drivestat[1]) * drivestat[2];
      }
   }

   private boolean isValidPathAndFilename(String path, String name) {
      if (name != null && name.length() != 0) {
         String illegalStartChars = " .";
         if (illegalStartChars.indexOf(name.charAt(0)) != -1) {
            return false;
         } else if (!this.isValidExtension(name)) {
            return false;
         } else {
            String illegalChars = "\\:*?\"<>|\u0000�";

            int slash_idx;
            for(slash_idx = 0; slash_idx < illegalChars.length(); ++slash_idx) {
               if (name.indexOf(illegalChars.charAt(slash_idx)) != -1) {
                  return false;
               }
            }

            slash_idx = name.indexOf(47);
            if (slash_idx != -1 && slash_idx != name.length() - 1) {
               return false;
            } else {
               int maxLength = 255;
               if (slash_idx == name.length() - 1) {
                  ++maxLength;
               }

               if (name.length() > maxLength) {
                  return false;
               } else {
                  for(int i = 0; i < illegalChars.length(); ++i) {
                     if (path.indexOf(illegalChars.charAt(i)) != -1) {
                        return false;
                     }
                  }

                  maxLength = 258;
                  if (slash_idx == name.length() - 1) {
                     ++maxLength;
                  }

                  return path.length() + name.length() <= maxLength;
               }
            }
         }
      } else {
         return true;
      }
   }

   private boolean isValidExtension(String filename) {
      for(int i = 0; i < ILLEGAL_EXTENSIONS.length; ++i) {
         int ext_len = ILLEGAL_EXTENSIONS[i].length();
         if (filename.regionMatches(true, filename.length() - ext_len, ILLEGAL_EXTENSIONS[i], 0, ext_len)) {
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

   private static String unescape(String s) throws IllegalArgumentException {
      if (s.indexOf(37) == -1) {
         return s;
      } else {
         try {
            byte[] escaped = s.getBytes("UTF-8");
            byte[] unescaped = new byte[escaped.length];
            int charactersUnescaped = 0;

            for(int i = 0; i < escaped.length; ++charactersUnescaped) {
               switch(escaped[i]) {
               case 37:
                  try {
                     if ((char)escaped[i + 1] == '-') {
                        throw new IllegalArgumentException("malformed %xx escape sequence");
                     }

                     unescaped[charactersUnescaped] = (byte)Integer.parseInt((char)escaped[i + 1] + "" + (char)escaped[i + 2], 16);
                     i += 2;
                     break;
                  } catch (ArrayIndexOutOfBoundsException var6) {
                     throw new IllegalArgumentException("malformed %xx escape sequence");
                  } catch (NumberFormatException var7) {
                     throw new IllegalArgumentException("malformed %xx escape sequence");
                  }
               case 43:
                  unescaped[charactersUnescaped] = 32;
                  break;
               default:
                  unescaped[charactersUnescaped] = escaped[i];
               }

               ++i;
            }

            String decoded = new String(unescaped, 0, charactersUnescaped, "UTF-8");
            if (decoded.length() != 0 && decoded.indexOf(65533) == -1) {
               return decoded;
            } else {
               throw new IllegalArgumentException("malformed UTF-8 encoding, or replacement character used");
            }
         } catch (UnsupportedEncodingException var8) {
            throw new Error("Either ASCII or UTF-8 is not supported");
         }
      }
   }

   private static String escape(String s) throws IllegalArgumentException {
      StringBuffer sbuf = new StringBuffer();
      int l = s.length();
      int ch = true;
      int sumb = false;
      int i = 0;

      for(boolean var7 = true; i < l; ++i) {
         int ch = s.charAt(i);
         if (ch == ' ') {
            sbuf.append("%20");
         } else if (ch == '^') {
            sbuf.append("%5e");
         } else if (ch == '%') {
            sbuf.append("%25");
         } else if (ch <= 127) {
            sbuf.append((char)ch);
         } else {
            int b1;
            int b2;
            if (ch <= 2047) {
               b1 = 192 | (ch & 1984) >> 6;
               b2 = 128 | ch & 63;
               sbuf.append("%" + Integer.toHexString(b1) + "%" + Integer.toHexString(b2));
            } else {
               if (ch > '\uffff') {
                  throw new IllegalArgumentException("bad string");
               }

               b1 = 224 | (ch & '\uf000') >> 12;
               b2 = 128 | (ch & 4032) >> 6;
               int b3 = 128 | ch & 63;
               sbuf.append("%" + Integer.toHexString(b1) + "%" + Integer.toHexString(b2) + "%" + Integer.toHexString(b3));
            }
         }
      }

      return sbuf.toString();
   }

   private class OpenDirEnumeration implements Enumeration {
      private String filter;
      private int dir_handle;
      private String next_string = null;
      private String fullpath;
      private boolean includeHidden;

      OpenDirEnumeration(String filter, String fullpath, boolean includeHidden) {
         this.fullpath = fullpath;
         this.includeHidden = includeHidden;
         this.filesys_opendir(filter);
         this.getNextEntry();
      }

      public boolean hasMoreElements() {
         return this.next_string != null;
      }

      public Object nextElement() {
         if (this.next_string == null) {
            return null;
         } else {
            String result = this.next_string;
            this.getNextEntry();
            return result;
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

      FileConnectionOutputStream() throws IOException {
         if (Protocol.this.fileHandle == -1) {
            Protocol.this.filesys_open();
            if (Protocol.this.fileHandle == -1) {
               throw new IOException("failed to open file for writing");
            }
         }

      }

      void setFilepos(long newFilepos) {
         this.filepos = (int)newFilepos;
      }

      void truncate(long byteOffset) {
         if ((long)this.filepos > byteOffset) {
            this.filepos = (int)byteOffset;
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

      public void write(byte[] b) throws IOException {
         this.write(b, 0, b.length);
      }

      public void write(byte[] b, int off, int len) throws IOException {
         if (b == null) {
            throw new NullPointerException();
         } else if (Protocol.this.out_closed) {
            throw new IOException("OutputStream is closed");
         } else if (off >= 0 && len >= 0 && off + len <= b.length) {
            int bytesWritten = Protocol.this.filesys_write(b, this.filepos, off, len);
            if (bytesWritten <= 0) {
               throw new IOException("failed to write to file");
            } else if (bytesWritten < len) {
               throw new IOException("only managed to write " + bytesWritten + " of " + len + " bytes to file");
            } else {
               this.filepos += bytesWritten;
            }
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public void write(int b) throws IOException {
         byte[] ba = new byte[]{(byte)b};
         this.write(ba, 0, ba.length);
      }
   }

   private class FileConnectionInputStream extends InputStream {
      private int filepos = 0;
      private int marked_filepos = -1;
      private FileConnection fc;

      FileConnectionInputStream(FileConnection fc) throws IOException {
         this.fc = fc;
         if (Protocol.this.fileHandle == -1) {
            Protocol.this.filesys_open();
            if (Protocol.this.fileHandle == -1) {
               throw new IOException("failed to open file for reading");
            }
         }

      }

      void truncate(long byteOffset) {
         if ((long)this.filepos > byteOffset) {
            this.filepos = (int)byteOffset;
         }

         if ((long)this.marked_filepos > byteOffset) {
            this.marked_filepos = -1;
         }

      }

      public int available() throws IOException {
         int filesize = Protocol.this.filesys_fileSize();
         return filesize - this.filepos;
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

      public void mark(int readlimit) {
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
            byte[] buf = new byte[1];
            if (Protocol.this.filesys_read(buf, this.filepos, 0, 1) > 0) {
               ++this.filepos;
               return buf[0] & 255;
            } else {
               throw new IOException("error reading from file");
            }
         }
      }

      public int read(byte[] b) throws IOException {
         if (b == null) {
            throw new NullPointerException();
         } else if (Protocol.this.in_closed) {
            throw new IOException("InputStream is closed");
         } else if (Protocol.this.filesys_fileSize() == this.filepos) {
            return -1;
         } else {
            int amount_read = Protocol.this.filesys_read(b, this.filepos, 0, b.length);
            if (amount_read < 0) {
               throw new IOException("error reading from file");
            } else {
               this.filepos += amount_read;
               return amount_read;
            }
         }
      }

      public int read(byte[] b, int off, int len) throws IOException {
         if (b == null) {
            throw new NullPointerException();
         } else if (off >= 0 && len >= 0 && off + len <= b.length) {
            if (Protocol.this.in_closed) {
               throw new IOException("InputStream is closed");
            } else if (Protocol.this.filesys_fileSize() == this.filepos) {
               return -1;
            } else {
               int amount_read = Protocol.this.filesys_read(b, this.filepos, off, len);
               if (amount_read < 0) {
                  throw new IOException("error reading from file");
               } else {
                  this.filepos += amount_read;
                  return amount_read;
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

      public long skip(long n) throws IOException {
         if (n < 0L) {
            return 0L;
         } else {
            int bytesLeft = this.available();
            if ((long)bytesLeft > n) {
               this.filepos = (int)((long)this.filepos + n);
               return n;
            } else {
               this.filepos += bytesLeft;
               return (long)bytesLeft;
            }
         }
      }
   }
}
