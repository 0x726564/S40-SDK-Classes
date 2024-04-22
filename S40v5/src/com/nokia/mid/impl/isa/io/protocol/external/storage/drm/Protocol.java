package com.nokia.mid.impl.isa.io.protocol.external.storage.drm;

import com.nokia.mid.impl.isa.util.SharedObjects;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.microedition.io.Connection;

public class Protocol extends com.nokia.mid.impl.isa.io.protocol.external.storage.Protocol {
   public static final byte DRM_TYPE_UNKNOWN = 0;
   public static final byte DRM_TYPE_FL = 1;
   public static final byte DRM_TYPE_CD = 2;
   public static final byte DRM_TYPE_DCFv1 = 3;
   public static final byte DRM_TYPE_DCFv2 = 4;
   private int lH = -1;
   private int lI = 0;
   private int lJ;
   private int lK = 0;
   private static Object lock = SharedObjects.getLock("javax.microedition.io.FileConnection.drm");

   public Connection openPrim(String var1, int var2, boolean var3) throws IOException, IllegalArgumentException {
      String[] var8;
      String var4 = (var8 = FileUrlParser.getUriComponents(var1))[4];
      String var5 = var8[5];
      String var6 = var8[7];
      if (var4 != null) {
         if ((var4 = var4.toLowerCase()).equals("enc")) {
            if (var5 == null) {
               if (var2 == 1) {
                  this.lH = 5;
               } else if (var2 == 2) {
                  this.lH = 6;
               }
            }
         } else if (var4.equals("dec")) {
            byte var7;
            if (var5 == null) {
               var7 = 0;
            } else if ((var4 = var5.toLowerCase()).equals("play")) {
               var7 = 1;
            } else if (var4.equals("display")) {
               var7 = 2;
            } else if (var4.equals("execute")) {
               var7 = 3;
            } else {
               if (!var4.equals("print")) {
                  throw new IllegalArgumentException("Invalid URL");
               }

               var7 = 4;
            }

            this.lH = var7;
         }
      }

      if (this.lH == -1) {
         throw new IllegalArgumentException("Invalid URL");
      } else {
         if (var6 != null) {
            var6.toLowerCase();
            if (var6.equals("dcfv1")) {
               this.lI = 3;
            } else if (var6.equals("dcfv2")) {
               this.lI = 4;
            } else if (var6.equals("fl")) {
               this.lI = 1;
            } else if (var6.equals("cd")) {
               this.lI = 2;
            } else if (var6.startsWith("dcf")) {
               this.lI = 4;
            }
         }

         return super.openPrim("//" + var8[2], var2, var3);
      }
   }

   protected OutputStream createOutputStream() throws IOException {
      return new Protocol.DRMOutputStream(this);
   }

   protected boolean filesys_access_allowed(String var1, int var2) {
      boolean var3 = false;
      if (this.isMidletAllowed()) {
         if (var2 == 1 && this.lH != 6) {
            this.lJ = this.createSource(var1, this.lH);
            var3 = this.lJ != 0;
         } else {
            var3 = var2 == 2;
         }
      }

      return var3;
   }

   protected native int filesys_fileSize();

   protected boolean filesys_open() {
      this.fileHandle = this.lJ;
      return this.lJ != 0;
   }

   protected native boolean filesys_close();

   protected native int filesys_read(byte[] var1, int var2, int var3, int var4);

   private native boolean isMidletAllowed();

   private native int createSource(String var1, int var2);

   private native int filedrmsys_savecontents(byte[] var1, int var2, String var3, String var4);

   private native void filedrmsys_write(int var1, byte[] var2, int var3);

   private native void filedrmsys_startDRM(int var1, int var2);

   private native void filedrmsys_eof(int var1);

   private native boolean filedrmsys_hasDrmStarted(int var1);

   protected boolean private_exists() {
      return true;
   }

   public void create() throws IOException {
   }

   public OutputStream openOutputStream(long var1) throws IOException {
      throw new IOException("DRM specialisation does not support data append operation.");
   }

   public void setHidden(boolean var1) throws IOException {
      throw new IOException("DRM specialisation does not support a change of hidden attribute.");
   }

   public void setWritable(boolean var1) throws IOException {
      throw new IOException("DRM specialisation does not support a change of writable attribute.");
   }

   public void truncate(long var1) throws IOException {
      throw new IOException("DRM specialisation does not support truncate of botton file content.");
   }

   static boolean a(Protocol var0) {
      return var0.out_closed;
   }

   static Object access$100() {
      return lock;
   }

   static int a(Protocol var0, byte[] var1, int var2, String var3, String var4) {
      return var0.filedrmsys_savecontents(var1, var2, var3, var4);
   }

   static int b(Protocol var0) {
      return var0.lI;
   }

   static void a(Protocol var0, int var1, int var2) {
      var0.filedrmsys_startDRM(var1, var2);
   }

   static boolean a(Protocol var0, int var1) {
      return var0.filedrmsys_hasDrmStarted(var1);
   }

   static void a(Protocol var0, int var1, byte[] var2, int var3) {
      var0.filedrmsys_write(var1, var2, var3);
   }

   static void b(Protocol var0, int var1) {
      var0.filedrmsys_eof(var1);
   }

   static boolean a(Protocol var0, boolean var1) {
      return var0.out_closed = true;
   }

   static OutputStream a(Protocol var0, OutputStream var1) {
      return var0.out = null;
   }

   private class DRMOutputStream extends OutputStream {
      private ByteArrayOutputStream ed;
      private Thread jt;
      private Exception ju;
      private int handle;
      private final Protocol jv;

      DRMOutputStream(Protocol var1) throws IOException {
         this.jv = var1;
         this.ju = null;
         this.ed = new ByteArrayOutputStream();
         this.handle = 0;
      }

      public void write(byte[] var1) throws IOException {
         if (this.ju == null) {
            this.ed.write(var1);
            if (this.ed.size() > 2048) {
               this.flush();
            }
         } else if (this.ju instanceof IOException) {
            throw (IOException)this.ju;
         } else {
            throw new IOException("UnexpectedException: " + this.ju.toString());
         }
      }

      public void write(byte[] var1, int var2, int var3) throws IOException {
         if (this.ju == null) {
            this.ed.write(var1, var2, var3);
            if (this.ed.size() > 2048) {
               this.flush();
            }
         } else if (this.ju instanceof IOException) {
            throw (IOException)this.ju;
         } else {
            throw new IOException("UnexpectedException: " + this.ju.toString());
         }
      }

      public void write(int var1) throws IOException {
         if (this.ju == null) {
            this.ed.write(var1);
            if (this.ed.size() > 2048) {
               this.flush();
            }
         } else if (this.ju instanceof IOException) {
            throw (IOException)this.ju;
         } else {
            throw new IOException("UnexpectedException: " + this.ju.toString());
         }
      }

      private native void registerNativeCleanup();

      public void flush() throws IOException {
         if (!Protocol.a(this.jv)) {
            if (this.ed.size() != 0) {
               byte[] var1 = this.ed.toByteArray();
               this.ed = new ByteArrayOutputStream();
               String var2 = (var2 = this.jv.getPath()).substring(1, var2.length() - 1);
               synchronized(Protocol.access$100()) {
                  if (this.handle == 0) {
                     this.handle = Protocol.a(this.jv, var1, var1.length, var2, this.jv.getName());
                     this.registerNativeCleanup();
                     this.jt = new Thread(this) {
                        private final Protocol.DRMOutputStream hh;

                        {
                           this.hh = var1;
                        }

                        public void run() {
                           try {
                              Protocol.a(Protocol.DRMOutputStream.b(this.hh), Protocol.DRMOutputStream.a(this.hh), Protocol.b(Protocol.DRMOutputStream.b(this.hh)));
                           } catch (Exception var2) {
                              Protocol.DRMOutputStream.a(this.hh, var2);
                              Protocol var1 = Protocol.DRMOutputStream.b(this.hh);
                           }

                           Protocol.DRMOutputStream.a(this.hh, (Thread)null);
                        }
                     };
                     this.jt.start();

                     do {
                        Thread.yield();
                     } while(!Protocol.a(this.jv, this.handle));
                  } else {
                     Protocol.a(this.jv, this.handle, var1, var1.length);
                  }

               }
            }
         }
      }

      public synchronized void close() throws IOException {
         this.flush();
         Protocol.b(this.jv, this.handle);

         try {
            if (this.jt != null) {
               this.jt.join();
            }
         } catch (InterruptedException var2) {
         }

         Protocol.a(this.jv, true);
         Protocol.a(this.jv, (OutputStream)null);
         this.handle = 0;
         super.close();
         if (this.ju != null) {
            if (this.ju instanceof IOException) {
               throw (IOException)this.ju;
            } else {
               throw new IOException("UnexpectedException: " + this.ju.toString());
            }
         }
      }

      static int a(Protocol.DRMOutputStream var0) {
         return var0.handle;
      }

      static Protocol b(Protocol.DRMOutputStream var0) {
         return var0.jv;
      }

      static Exception a(Protocol.DRMOutputStream var0, Exception var1) {
         return var0.ju = var1;
      }

      static Thread a(Protocol.DRMOutputStream var0, Thread var1) {
         return var0.jt = null;
      }
   }
}
