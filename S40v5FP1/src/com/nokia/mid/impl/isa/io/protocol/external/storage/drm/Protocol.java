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
   private static final int DRM_DEFAULT = 0;
   private static final int DRM_PLAY = 1;
   private static final int DRM_DISPLAY = 2;
   private static final int DRM_EXECUTE = 3;
   private static final int DRM_PRINT = 4;
   private static final int DRM_ENC = 5;
   private static final int DRM_CREATE = 6;
   private int drmOperation = -1;
   private int drmType = 0;
   private int source;
   private int midlet_controller = 0;
   private boolean isPreview = false;
   private static Object lock = SharedObjects.getLock("javax.microedition.io.FileConnection.drm");

   public Connection openPrim(String name, int mode, boolean timeouts) throws IOException, IllegalArgumentException {
      String[] urlComponents = FileUrlParser.getUriComponents(name);
      String drmMode = urlComponents[4];
      String purpose = urlComponents[5];
      String drmSType = urlComponents[7];
      String drmPreview = urlComponents[6];
      this.isPreview = drmPreview != null;
      if (drmMode != null) {
         drmMode = drmMode.toLowerCase();
         if (drmMode.equals("enc")) {
            if (purpose == null) {
               if (mode == 1) {
                  this.drmOperation = 5;
               } else if (mode == 2) {
                  this.drmOperation = 6;
               }
            }
         } else if (drmMode.equals("dec")) {
            this.drmOperation = this.getOperation(purpose);
         }
      }

      if (this.drmOperation == -1) {
         throw new IllegalArgumentException("Invalid URL");
      } else {
         if (drmSType != null) {
            drmSType.toLowerCase();
            if (drmSType.equals("dcfv1")) {
               this.drmType = 3;
            } else if (drmSType.equals("dcfv2")) {
               this.drmType = 4;
            } else if (drmSType.equals("fl")) {
               this.drmType = 1;
            } else if (drmSType.equals("cd")) {
               this.drmType = 2;
            } else if (drmSType.startsWith("dcf")) {
               this.drmType = 4;
            }
         }

         return super.openPrim("//" + urlComponents[2], mode, timeouts);
      }
   }

   protected OutputStream createOutputStream() throws IOException {
      return new Protocol.DRMOutputStream();
   }

   protected boolean filesys_access_allowed(String path, int mode) {
      boolean allowed = false;
      if (this.isMidletAllowed()) {
         if (mode == 1 && this.drmOperation != 6) {
            this.source = this.createSource(path, this.drmOperation, this.isPreview);
            allowed = this.source != 0;
         } else {
            allowed = mode == 2;
         }
      }

      return allowed;
   }

   protected native int filesys_fileSize();

   protected boolean filesys_open() {
      this.fileHandle = this.source;
      return this.source != 0;
   }

   protected native boolean filesys_close();

   protected native int filesys_read(byte[] var1, int var2, int var3, int var4);

   private int getOperation(String purpose) {
      byte var2;
      if (purpose == null) {
         var2 = 0;
      } else {
         purpose = purpose.toLowerCase();
         if (purpose.equals("play")) {
            var2 = 1;
         } else if (purpose.equals("display")) {
            var2 = 2;
         } else if (purpose.equals("execute")) {
            var2 = 3;
         } else {
            if (!purpose.equals("print")) {
               throw new IllegalArgumentException("Invalid URL");
            }

            var2 = 4;
         }
      }

      return var2;
   }

   private native boolean isMidletAllowed();

   private native int createSource(String var1, int var2, boolean var3);

   private native int filedrmsys_savecontents(byte[] var1, int var2, String var3, String var4);

   private native void filedrmsys_write(int var1, byte[] var2, int var3);

   private native void filedrmsys_startDRM(int var1, int var2);

   private native void filedrmsys_eof(int var1);

   private native boolean filedrmsys_hasDrmStarted(int var1);

   private native void filedrmsys_resumeWriteThread(int var1);

   protected boolean private_exists() {
      return true;
   }

   public void create() throws IOException {
   }

   public OutputStream openOutputStream(long byteOffset) throws IOException {
      throw new IOException("DRM specialisation does not support data append operation.");
   }

   public void setHidden(boolean hidden) throws IOException {
      throw new IOException("DRM specialisation does not support a change of hidden attribute.");
   }

   public void setWritable(boolean writable) throws IOException {
      throw new IOException("DRM specialisation does not support a change of writable attribute.");
   }

   public void truncate(long byteOffset) throws IOException {
      throw new IOException("DRM specialisation does not support truncate of botton file content.");
   }

   private class DRMOutputStream extends OutputStream {
      private ByteArrayOutputStream output = new ByteArrayOutputStream();
      private Thread drmThread;
      private Exception exceptionThrown = null;
      private static final int drm_buffer_size = 2048;
      private int handle = 0;

      DRMOutputStream() throws IOException {
      }

      private void checkForException() throws IOException {
         if (this.exceptionThrown != null) {
            if (this.exceptionThrown instanceof IOException) {
               IOException e = (IOException)this.exceptionThrown;
               throw e;
            } else {
               throw new IOException("UnexpectedException: " + this.exceptionThrown.toString());
            }
         }
      }

      public void write(byte[] b, int off, int len) throws IOException {
         this.checkForException();
         this.output.write(b, off, len);
         if (this.output.size() > 2048) {
            this.flush();
         }

      }

      public void write(int b) throws IOException {
         this.checkForException();
         this.output.write(b);
         if (this.output.size() > 2048) {
            this.flush();
         }

      }

      private native void registerNativeCleanup();

      public void flush() throws IOException {
         if (!Protocol.this.out_closed) {
            if (this.output.size() != 0) {
               byte[] buf = this.output.toByteArray();
               this.output = new ByteArrayOutputStream();
               String path = Protocol.this.getPath();
               path = path.substring(1, path.length() - 1);
               synchronized(Protocol.lock) {
                  if (this.handle == 0) {
                     this.handle = Protocol.this.filedrmsys_savecontents(buf, buf.length, path, Protocol.this.getName());
                     this.registerNativeCleanup();
                     this.drmThread = new Thread() {
                        public void run() {
                           try {
                              Protocol.this.filedrmsys_startDRM(DRMOutputStream.this.handle, Protocol.this.drmType);
                           } catch (Exception var2) {
                              DRMOutputStream.this.exceptionThrown = var2;
                              if (Protocol.this.midlet_controller != 0) {
                                 Protocol.this.filedrmsys_resumeWriteThread(Protocol.this.midlet_controller);
                              }
                           }

                           DRMOutputStream.this.drmThread = null;
                        }
                     };
                     this.drmThread.start();

                     do {
                        Thread.yield();
                     } while(!Protocol.this.filedrmsys_hasDrmStarted(this.handle));
                  } else {
                     Protocol.this.filedrmsys_write(this.handle, buf, buf.length);
                  }

               }
            }
         }
      }

      public synchronized void close() throws IOException {
         this.flush();
         Protocol.this.filedrmsys_eof(this.handle);

         try {
            if (this.drmThread != null) {
               this.drmThread.join();
            }
         } catch (InterruptedException var2) {
         }

         Protocol.this.out_closed = true;
         Protocol.this.out = null;
         this.handle = 0;
         super.close();
         this.checkForException();
      }
   }
}
