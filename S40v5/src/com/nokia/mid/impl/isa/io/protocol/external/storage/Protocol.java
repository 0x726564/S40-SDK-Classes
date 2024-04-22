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

public class Protocol implements ConnectionBaseInterface, FileConnection {
   private int mode;
   private String url;
   private String fullpath;
   private String i;
   private String name;
   private String path;
   private boolean isClosed = true;
   private boolean exists;
   private boolean j;
   protected int fileHandle = -1;
   protected boolean out_closed = true;
   private boolean k = true;
   private InputStream in = null;
   protected OutputStream out = null;
   private int errorCode = 0;
   private static final String[] l = new String[]{".jar", ".jad", ".rms"};
   private static final boolean m = PriAccess.getInt(5) == 1;
   private String n;
   private String o;
   private String p;
   private String q;
   private boolean r = false;

   public Connection openPrim(String var1, int var2, boolean var3) throws IOException, IllegalArgumentException {
      if (var2 != 1 && var2 != 2 && var2 != 3) {
         throw new IllegalArgumentException("illegal mode: " + var2);
      } else {
         var1 = b(var1);
         if (m) {
            System.getProperty("fileconn.dir.private");
            String var4 = this.getPrivateDir();
            this.n = var4 + '/';
            if (var1.startsWith("////") | var1.startsWith("//localhost//") | var1.startsWith("///MemoryCard/")) {
               var4 = var1;
               if (var1.startsWith("////")) {
                  var4 = var1.substring("////".length());
                  var4 = "///" + this.n + var4;
               } else if (var1.startsWith("//localhost//")) {
                  var4 = var1.substring("//localhost//".length());
                  this.r = true;
                  var4 = "///" + this.n + var4;
               } else if (var1.startsWith("///MemoryCard/")) {
                  var4 = var1.substring("///MemoryCard/".length());
                  var4 = "///E:" + var4;
               }

               var1 = var4;
               this.p = this.n.substring("C:".length());
               this.q = this.p.substring(0, this.p.substring(0, this.p.length() - 1).lastIndexOf(47)) + '/';
               this.o = this.p.substring(this.q.length(), this.p.length());
            }
         }

         this.url = "file:" + escape(var1);
         if (var1.indexOf(92) != -1) {
            throw new IllegalArgumentException();
         } else if (var1.indexOf("/.") != -1) {
            throw new IllegalArgumentException("Malformed URL");
         } else {
            int var5;
            if ((var5 = var1.indexOf(58)) == -1) {
               var5 = var1.indexOf(124);
            }

            if (var5 == -1) {
               throw new ConnectionNotFoundException("Only absolute file URL's of type <drive>:<path> are supported");
            } else {
               this.fullpath = var1.substring(var5 - 1);
               this.mode = var2;
               this.c();
               if (!SecurityPermission.isPermitted(var2, this.fullpath, false)) {
                  throw new SecurityException("Access denied");
               } else {
                  this.parseURL();
                  if (!this.filesys_access_allowed(this.fullpath, var2)) {
                     throw new SecurityException("Access denied");
                  } else if (!this.a(this.path, this.name)) {
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

   private int c() throws IllegalArgumentException {
      int var1;
      if ((var1 = this.fullpath.indexOf(58)) == -1 && (var1 = this.fullpath.indexOf(124)) != -1) {
         try {
            this.fullpath = this.fullpath.substring(0, var1) + ":" + this.fullpath.substring(var1 + 1);
         } catch (StringIndexOutOfBoundsException var2) {
            throw new IllegalArgumentException("| in invalid location");
         }
      }

      return var1;
   }

   private void parseURL() throws IOException {
      this.filesys_attributes_get();

      try {
         this.i = this.fullpath.substring(0, 2).toUpperCase();
         this.fullpath = this.i + this.fullpath.substring(2);
         if (this.exists) {
            if (this.j) {
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

         int var1;
         if ((var1 = this.fullpath.lastIndexOf(47)) != -1) {
            if (var1 == this.fullpath.length() - 1) {
               this.j = false;
               var1 = this.fullpath.lastIndexOf(47, var1 - 1);
            } else {
               this.j = true;
            }

            if (var1 == -1) {
               this.path = "/";
               if (this.j) {
                  this.name = this.fullpath.substring(4);
                  return;
               }
            } else {
               this.path = this.fullpath.substring(this.c() + 1, var1 + 1);
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
         return false;
      }
   }

   public void close() throws IOException {
      this.isClosed = true;
   }

   public void create() throws IOException {
      if (this.isClosed) {
         throw new ConnectionClosedException("connection is closed");
      } else if (!this.j) {
         throw new IOException("create called for a directory");
      } else if (this.private_exists()) {
         throw new IOException("already exists");
      } else if (this.mode == 1) {
         throw new IllegalModeException("Connection opened in READ mode");
      } else if (!this.filesys_create()) {
         throw new IOException("Failed to create file");
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

         if (!this.k && this.in != null) {
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
      } else if (this.j) {
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

   protected boolean private_exists() {
      this.filesys_attributes_get();
      return this.exists;
   }

   public long fileSize() throws IOException {
      if (this.isClosed) {
         throw new ConnectionClosedException("connection is closed");
      } else if (!this.j) {
         throw new IOException("fileSize called on a directory.");
      } else if (this.mode == 2) {
         throw new IllegalModeException("Connection opened in WRITE mode");
      } else {
         return !this.private_exists() ? -1L : (long)this.filesys_fileSize();
      }
   }

   public String getName() {
      if (this.name != null) {
         if (m && this.o != null && this.name.equals(this.o)) {
            return "";
         } else {
            if (!this.j && !this.name.endsWith("/")) {
               this.name = this.name + "/";
            }

            return this.name;
         }
      } else {
         return "";
      }
   }

   public String getPath() {
      if (m && this.p != null) {
         if (this.path.equals(this.q)) {
            return "//";
         }

         if (this.path.startsWith(this.p)) {
            return b(escape("//" + this.path.substring(this.p.length())));
         }

         if (this.i.equals("E:")) {
            return "/MemoryCard" + this.path;
         }
      }

      return "/" + this.i + this.path;
   }

   public String getURL() {
      if (m && this.p != null) {
         String var1 = "";
         if (this.r) {
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
            return !this.j;
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
         return false;
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
         long var4 = (long)TimeZone.getDefault().getRawOffset();
         long var1;
         if ((var1 = this.filesys_lastModified()) != 0L) {
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
      } else if (this.j) {
         throw new IOException("list called on a file");
      } else if (!this.private_exists()) {
         throw new IOException("directory doesn't exist");
      } else if (this.mode == 2) {
         throw new IllegalModeException("Connection opened in WRITE mode");
      } else {
         var1 = b(var1);
         int var3 = "\\/:?\"<>|\u0000�".length();

         for(int var4 = 0; var4 < var3; ++var4) {
            if (var1.indexOf("\\/:?\"<>|\u0000�".charAt(var4)) != -1) {
               throw new IllegalArgumentException("invalid character in filter");
            }
         }

         if (this.path.length() + var1.length() > 258) {
            throw new IllegalArgumentException("filter too long");
         } else {
            return new Protocol.OpenDirEnumeration(this, var1, this.fullpath, var2);
         }
      }
   }

   public void mkdir() throws IOException {
      if (this.isClosed) {
         throw new ConnectionClosedException("connection is closed");
      } else if (this.private_exists()) {
         throw new IOException("already exists");
      } else if (this.j) {
         throw new IOException("mkdir called for a file");
      } else if (this.mode == 1) {
         throw new IllegalModeException("Connection opened in READ mode");
      } else if (!this.filesys_mkdir()) {
         throw new IOException("Failed to create directory");
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
      } else if (!this.j) {
         throw new IOException("cannot open input streams on directories");
      } else if (this.in != null) {
         throw new IOException("InputStream is already open");
      } else if (!this.private_exists()) {
         throw new IOException("file does not exist");
      } else if (this.mode == 2) {
         throw new IllegalModeException("Connection opened in WRITE mode");
      } else {
         this.in = new Protocol.FileConnectionInputStream(this, this);
         this.k = false;
         return this.in;
      }
   }

   public OutputStream openOutputStream() throws IOException {
      if (this.isClosed) {
         throw new IOException("connection is closed");
      } else if (!this.j) {
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
      return new Protocol.FileConnectionOutputStream(this);
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
      } else if ((var1 = b(var1)).indexOf(47) != -1) {
         throw new IllegalArgumentException();
      } else if (!this.a(this.path, var1)) {
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

         if (!this.k && this.in != null) {
            this.in.close();
         }

         if (!this.filesys_rename(var1)) {
            throw new IOException("Failed to rename file");
         } else {
            this.name = var1;
            this.fullpath = this.i + this.path + this.name;
            this.url = "file:///" + escape(this.fullpath);
         }
      }
   }

   public void setFileConnection(String var1) throws IOException {
      if (this.isClosed) {
         throw new ConnectionClosedException("connection is closed");
      } else if (var1 == null) {
         throw new NullPointerException();
      } else if (this.j) {
         throw new IOException("setFileConnection can only be called on directories");
      } else if (!this.private_exists()) {
         throw new IOException("directory does not exist");
      } else {
         int var2;
         if ((var2 = var1.indexOf(47)) != -1 && var2 != var1.length() - 1) {
            throw new IllegalArgumentException();
         } else {
            String var3 = null;
            if (var1.equals("..")) {
               if (m && this.path.equals(this.q)) {
                  throw new SecurityException("Access denied");
               }

               var3 = this.i + this.path;
            } else {
               if (!this.a(this.path, var1)) {
                  throw new IOException("invalid filename");
               }

               var3 = this.fullpath + var1;
            }

            if (!SecurityPermission.isPermitted(this.mode, var3, false)) {
               throw new SecurityException("Access denied");
            } else {
               var1 = this.fullpath;
               this.fullpath = var3;
               if (!this.private_exists()) {
                  this.fullpath = var1;
                  throw new IllegalArgumentException("setFileConnection called with target that doesn't exist");
               } else {
                  this.url = "file:///" + escape(this.fullpath);
                  this.c();
                  this.parseURL();
                  if (!this.filesys_access_allowed(this.fullpath, this.mode)) {
                     this.fullpath = var1;
                     this.url = "file:///" + escape(this.fullpath);
                     this.c();
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
      } else if (!this.j) {
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

            if (!this.k && this.in != null) {
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

   private boolean a(String var1, String var2) {
      if (var2 != null && var2.length() != 0) {
         String var5 = " .";
         if (" .".indexOf(var2.charAt(0)) != -1) {
            return false;
         } else if (!a(var2)) {
            return false;
         } else {
            var5 = "\\:*?\"<>|\u0000�";

            int var3;
            for(var3 = 0; var3 < var5.length(); ++var3) {
               if (var2.indexOf(var5.charAt(var3)) != -1) {
                  return false;
               }
            }

            if ((var3 = var2.indexOf(47)) != -1 && var3 != var2.length() - 1) {
               return false;
            } else {
               int var4 = 255;
               if (var3 == var2.length() - 1) {
                  ++var4;
               }

               if (var2.length() > var4) {
                  return false;
               } else {
                  for(var4 = 0; var4 < var5.length(); ++var4) {
                     if (var1.indexOf(var5.charAt(var4)) != -1) {
                        return false;
                     }
                  }

                  var4 = 258;
                  if (var3 == var2.length() - 1) {
                     ++var4;
                  }

                  if (var1.length() + var2.length() <= var4) {
                     return true;
                  } else {
                     return false;
                  }
               }
            }
         }
      } else {
         return true;
      }
   }

   private static boolean a(String var0) {
      for(int var1 = 0; var1 < l.length; ++var1) {
         int var2 = l[var1].length();
         if (var0.regionMatches(true, var0.length() - var2, l[var1], 0, var2)) {
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

   private static String b(String var0) throws IllegalArgumentException {
      if (var0.indexOf(37) == -1) {
         return var0;
      } else {
         try {
            byte[] var1;
            byte[] var7 = new byte[(var1 = var0.getBytes("UTF-8")).length];
            int var2 = 0;

            for(int var3 = 0; var3 < var1.length; ++var2) {
               switch(var1[var3]) {
               case 37:
                  try {
                     if ((char)var1[var3 + 1] == '-') {
                        throw new IllegalArgumentException("malformed %xx escape sequence");
                     }

                     var7[var2] = (byte)Integer.parseInt((char)var1[var3 + 1] + "" + (char)var1[var3 + 2], 16);
                     var3 += 2;
                     break;
                  } catch (ArrayIndexOutOfBoundsException var4) {
                     throw new IllegalArgumentException("malformed %xx escape sequence");
                  } catch (NumberFormatException var5) {
                     throw new IllegalArgumentException("malformed %xx escape sequence");
                  }
               case 43:
                  var7[var2] = 32;
                  break;
               default:
                  var7[var2] = var1[var3];
               }

               ++var3;
            }

            String var8;
            if ((var8 = new String(var7, 0, var2, "UTF-8")).length() != 0 && var8.indexOf(65533) == -1) {
               return var8;
            } else {
               throw new IllegalArgumentException("malformed UTF-8 encoding, or replacement character used");
            }
         } catch (UnsupportedEncodingException var6) {
            throw new Error("Either ASCII or UTF-8 is not supported");
         }
      }
   }

   private static String escape(String var0) throws IllegalArgumentException {
      StringBuffer var1 = new StringBuffer();
      int var2 = var0.length();
      boolean var3 = false;

      for(int var4 = 0; var4 < var2; ++var4) {
         char var7;
         if ((var7 = var0.charAt(var4)) == ' ') {
            var1.append("%20");
         } else if (var7 == '^') {
            var1.append("%5e");
         } else if (var7 == '%') {
            var1.append("%25");
         } else if (var7 <= 127) {
            var1.append((char)var7);
         } else {
            int var5;
            int var6;
            if (var7 <= 2047) {
               var5 = 192 | (var7 & 1984) >> 6;
               var6 = 128 | var7 & 63;
               var1.append("%" + Integer.toHexString(var5) + "%" + Integer.toHexString(var6));
            } else {
               if (var7 > '\uffff') {
                  throw new IllegalArgumentException("bad string");
               }

               var5 = 224 | (var7 & '\uf000') >> 12;
               var6 = 128 | (var7 & 4032) >> 6;
               int var8 = 128 | var7 & 63;
               var1.append("%" + Integer.toHexString(var5) + "%" + Integer.toHexString(var6) + "%" + Integer.toHexString(var8));
            }
         }
      }

      return var1.toString();
   }

   static boolean a(Protocol var0) {
      return var0.k;
   }

   static InputStream a(Protocol var0, InputStream var1) {
      return var0.in = null;
   }

   static boolean a(Protocol var0, boolean var1) {
      return var0.k = true;
   }

   static InputStream b(Protocol var0) {
      return var0.in;
   }

   static boolean a(Protocol var0, String var1) {
      return a(var1);
   }

   private class OpenDirEnumeration implements Enumeration {
      private String fw;
      private final Protocol cz;

      OpenDirEnumeration(Protocol var1, String var2, String var3, boolean var4) {
         this.cz = var1;
         this.fw = null;
         this.filesys_opendir(var2);
         this.O();
      }

      public boolean hasMoreElements() {
         return this.fw != null;
      }

      public Object nextElement() {
         if (this.fw == null) {
            return null;
         } else {
            String var1 = this.fw;
            this.O();
            return var1;
         }
      }

      private void O() {
         while((this.fw = this.filesys_readdir()) != null && !Protocol.a(this.cz, this.fw)) {
         }

      }

      native void filesys_opendir(String var1);

      native String filesys_readdir();
   }

   private class FileConnectionOutputStream extends OutputStream {
      private int cy;
      private final Protocol cz;

      FileConnectionOutputStream(Protocol var1) throws IOException {
         this.cz = var1;
         this.cy = 0;
         if (var1.fileHandle == -1) {
            var1.filesys_open();
            if (var1.fileHandle == -1) {
               throw new IOException("failed to open file for writing");
            }
         }

      }

      void setFilepos(long var1) {
         this.cy = (int)var1;
      }

      final void truncate(long var1) {
         if ((long)this.cy > var1) {
            this.cy = (int)var1;
         }

      }

      public void close() throws IOException {
         if (!this.cz.out_closed) {
            this.cz.out = null;
            this.cz.out_closed = true;
            if (Protocol.b(this.cz) == null && Protocol.a(this.cz)) {
               if (!this.cz.filesys_close()) {
                  throw new IOException("failed to close file");
               }

               this.cz.fileHandle = -1;
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
         } else if (this.cz.out_closed) {
            throw new IOException("OutputStream is closed");
         } else if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length) {
            int var4;
            if ((var4 = this.cz.filesys_write(var1, this.cy, var2, var3)) <= 0) {
               throw new IOException("failed to write to file");
            } else if (var4 < var3) {
               throw new IOException("only managed to write " + var4 + " of " + var3 + " bytes to file");
            } else {
               this.cy += var4;
            }
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public void write(int var1) throws IOException {
         byte[] var2;
         (var2 = new byte[1])[0] = (byte)var1;
         this.write(var2, 0, var2.length);
      }
   }

   private class FileConnectionInputStream extends InputStream {
      private int cy;
      private int ma;
      private final Protocol cz;

      FileConnectionInputStream(Protocol var1, FileConnection var2) throws IOException {
         this.cz = var1;
         this.cy = 0;
         this.ma = -1;
         if (var1.fileHandle == -1) {
            var1.filesys_open();
            if (var1.fileHandle == -1) {
               throw new IOException("failed to open file for reading");
            }
         }

      }

      final void truncate(long var1) {
         if ((long)this.cy > var1) {
            this.cy = (int)var1;
         }

         if ((long)this.ma > var1) {
            this.ma = -1;
         }

      }

      public int available() throws IOException {
         return this.cz.filesys_fileSize() - this.cy;
      }

      public void close() throws IOException {
         if (!Protocol.a(this.cz)) {
            Protocol.a((Protocol)this.cz, (InputStream)null);
            Protocol.a(this.cz, true);
            if (this.cz.out == null && this.cz.out_closed) {
               if (!this.cz.filesys_close()) {
                  throw new IOException("failed to close file");
               }

               this.cz.fileHandle = -1;
            }

         }
      }

      public void mark(int var1) {
         this.ma = this.cy;
      }

      public boolean markSupported() {
         return true;
      }

      public int read() throws IOException {
         if (Protocol.a(this.cz)) {
            throw new IOException("InputStream is closed");
         } else if (this.cz.filesys_fileSize() == this.cy) {
            return -1;
         } else {
            byte[] var1 = new byte[1];
            if (this.cz.filesys_read(var1, this.cy, 0, 1) > 0) {
               ++this.cy;
               return var1[0] & 255;
            } else {
               throw new IOException("error reading from file");
            }
         }
      }

      public int read(byte[] var1) throws IOException {
         if (var1 == null) {
            throw new NullPointerException();
         } else if (Protocol.a(this.cz)) {
            throw new IOException("InputStream is closed");
         } else if (this.cz.filesys_fileSize() == this.cy) {
            return -1;
         } else {
            int var2;
            if ((var2 = this.cz.filesys_read(var1, this.cy, 0, var1.length)) < 0) {
               throw new IOException("error reading from file");
            } else {
               this.cy += var2;
               return var2;
            }
         }
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         if (var1 == null) {
            throw new NullPointerException();
         } else if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length) {
            if (Protocol.a(this.cz)) {
               throw new IOException("InputStream is closed");
            } else if (this.cz.filesys_fileSize() == this.cy) {
               return -1;
            } else {
               int var4;
               if ((var4 = this.cz.filesys_read(var1, this.cy, var2, var3)) < 0) {
                  throw new IOException("error reading from file");
               } else {
                  this.cy += var4;
                  return var4;
               }
            }
         } else {
            throw new IndexOutOfBoundsException();
         }
      }

      public void reset() throws IOException {
         if (this.ma == -1) {
            throw new IOException("reset called before call to mark");
         } else {
            this.cy = this.ma;
         }
      }

      public long skip(long var1) throws IOException {
         if (var1 < 0L) {
            return 0L;
         } else {
            int var3;
            if ((long)(var3 = this.available()) > var1) {
               this.cy = (int)((long)this.cy + var1);
               return var1;
            } else {
               this.cy += var3;
               return (long)var3;
            }
         }
      }
   }
}
