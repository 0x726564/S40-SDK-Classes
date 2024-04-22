package com.nokia.mid.impl.isa.source_handling;

import com.sun.midp.io.j2me.http.Protocol;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

public class JavaProducerSource implements Runnable {
   static final int JAVA_RAM_BUFFER_SIZE = 131072;
   private int activeSourceHigh = 262144;
   private int activeSourceLow = 16384;
   private InputStream inputStream = null;
   private String URI;
   private int buffer_size = 131072;
   private byte[] buffer = null;
   private int CM_Source;
   private int CM_HTTP_Source = 0;
   private Object httpToken = null;
   private static final int SOURCE_UNDEFINED = 0;
   private static final int SOURCE_RAM = 1;
   private static final int SOURCE_ACTIVE = 2;
   private static final int SOURCE_FILE = 3;
   private static final int SOURCE_HTTP = 4;
   private int SOURCE_CATEGORY = 0;
   private static final String HTTP_PREFIX = "http://";
   private static final String FILE_PREFIX = "file://";
   private boolean connected = false;
   private boolean generatedSourceID = false;
   protected Vector listeners = new Vector(2);
   public Thread thread;
   public boolean useActiveSource = false;
   private static final int CHUNK_SIZE = 512;

   public JavaProducerSource() {
      this.SOURCE_CATEGORY = 1;
   }

   public JavaProducerSource(InputStream var1) {
      this.inputStream = var1;
      this.SOURCE_CATEGORY = 1;
      this.connected = true;
   }

   public JavaProducerSource(InputStream var1, int var2) {
      this.inputStream = var1;
      this.buffer_size = var2;
      this.SOURCE_CATEGORY = 1;
      this.connected = true;
   }

   public JavaProducerSource(String var1, boolean var2) throws IOException, SourceHandlingException {
      this.useActiveSource = var2;
      this.commonURLcode(var1);
   }

   public JavaProducerSource(String var1, int var2, boolean var3) throws IOException, SourceHandlingException {
      this.useActiveSource = var3;
      this.buffer_size = var2;
      this.commonURLcode(var1);
   }

   public void setHighLowWaterMarks(int var1, int var2) {
      if (var1 > 131072) {
         this.activeSourceHigh = var1;
      }

      if (var2 < this.activeSourceHigh) {
         this.activeSourceLow = var2;
      }

   }

   private void commonURLcode(String var1) throws IOException, SourceHandlingException {
      this.URI = var1;
      String var2 = var1.toLowerCase();
      if (var2.startsWith("http://")) {
         this.SOURCE_CATEGORY = 1;
         HttpConnection var3 = (HttpConnection)Connector.open(var1);
         int var4;
         if (this.useActiveSource) {
            this.httpToken = ((Protocol)var3).prepareCMSourceId();
            if ((var4 = var3.getResponseCode()) != 200) {
               var3.close();
               throw new SourceHandlingException("Invalid resource: " + var1 + " caused error: " + var4);
            }

            this.SOURCE_CATEGORY = 4;
         } else {
            if ((var4 = var3.getResponseCode()) != 200) {
               var3.close();
               throw new SourceHandlingException("Invalid resource: " + var1 + " caused error: " + var4);
            }

            this.inputStream = var3.openInputStream();
            long var5 = var3.getLength();
            if (var5 != -1L && var5 < (long)this.buffer_size) {
               this.buffer_size = (int)var5;
            }

            var3.close();
            this.connected = true;
         }
      } else {
         if (!var2.startsWith("file://")) {
            throw new SourceHandlingException("URI protocol not supported");
         }

         this.SOURCE_CATEGORY = 3;
         if (!nFileIsValid(var1.substring("file://".length()))) {
            throw new SourceHandlingException("Invalid resource: " + var1);
         }
      }

   }

   public void addSourceHandlingListener(SourceHandlingListener var1) {
      if (var1 != null) {
         this.listeners.addElement(var1);
      }

   }

   public void removePlayerListener(SourceHandlingListener var1) {
      this.listeners.removeElement(var1);
   }

   public byte[] generateSourceId() throws SourceHandlingException {
      byte[] var1;
      switch(this.SOURCE_CATEGORY) {
      case 0:
      default:
         throw new SourceHandlingException();
      case 1:
         var1 = this.createRAMSource(this.buffer, this.buffer.length);
         break;
      case 2:
         var1 = this.createActiveSource();
         this.writeData(this.buffer, this.buffer.length);
         break;
      case 3:
         var1 = this.createFileSource(this.URI.substring("file://".length()));
         break;
      case 4:
         var1 = this.obtainHTTPSource(this.httpToken);
      }

      this.generatedSourceID = true;
      return var1;
   }

   public void setData(byte[] var1) {
      this.buffer = var1;
   }

   public void fetchData() throws IOException {
      if (this.inputStream != null && this.buffer_size != 0) {
         int var1;
         if (this.useActiveSource) {
            this.buffer = new byte[this.buffer_size];
            var1 = this.inputStream.read(this.buffer, 0, this.buffer_size - 1);
            int var2 = this.inputStream.read();
            if (var2 == -1) {
               this.inputStream.close();
               this.connected = false;
            } else {
               this.buffer[this.buffer_size - 1] = (byte)var2;
               this.SOURCE_CATEGORY = 2;
            }
         } else {
            boolean var3 = false;
            Vector var4 = new Vector(5);

            byte[] var9;
            do {
               var9 = new byte[512];
               if ((var1 = this.readData(var9, 0, 512)) != 0) {
                  if (var1 > 0) {
                     var4.addElement(var9);
                  }

                  var3 = true;
               }
            } while(var1 > 0);

            if (var3) {
               int var5 = 0;
               int var7 = var4.size() * 512 + -var1;
               this.buffer = new byte[var7];

               for(int var8 = 0; var8 < var4.size(); ++var8) {
                  byte[] var6 = (byte[])var4.elementAt(var8);
                  System.arraycopy(var6, 0, this.buffer, var5, 512);
                  var5 += 512;
               }

               if (var1 < 0) {
                  System.arraycopy(var9, 0, this.buffer, var5, -var1);
               }
            }

            this.inputStream.close();
            this.connected = false;
         }
      }

   }

   public void start() {
      if (!this.generatedSourceID) {
         this.notifyListeners(1, new SourceHandlingException("Failed to called generateSourceID"));
      } else {
         if ((this.SOURCE_CATEGORY == 2 || this.SOURCE_CATEGORY == 4) && this.thread == null) {
            this.thread = new Thread(this);
            this.thread.start();
         } else {
            this.notifyListeners(0, (SourceHandlingException)null);
         }

      }
   }

   public void disconnect() {
      if (this.SOURCE_CATEGORY == 2) {
         if (this.thread != null) {
            this.nAbortSuspendedThread();
         }
      } else if (this.connected) {
         try {
            this.inputStream.close();
            this.connected = false;
         } catch (IOException var2) {
         }
      }

   }

   public void run() {
      if (this.SOURCE_CATEGORY == 4) {
         while(true) {
            if (this.processHTTPdata()) {
               continue;
            }
         }
      } else {
         try {
            boolean var1 = false;
            boolean var2 = true;

            int var4;
            do {
               var4 = this.inputStream.read(this.buffer);
               if (var4 > 0) {
                  var2 = this.writeData(this.buffer, var4);
               }
            } while(var4 != -1 && var2);

            this.inputStream.close();
            this.connected = false;
         } catch (IOException var3) {
            this.notifyListeners(1, new SourceHandlingException(var3.toString()));
         }
      }

      this.setEOF();
      this.notifyListeners(0, (SourceHandlingException)null);
      this.thread = null;
   }

   private void notifyListeners(int var1, SourceHandlingException var2) {
      for(int var3 = 0; var3 < this.listeners.size(); ++var3) {
         SourceHandlingListener var4 = (SourceHandlingListener)this.listeners.elementAt(var3);
         var4.sourceHandlingEvent(var1, var2);
      }

   }

   private int readData(byte[] var1, int var2, int var3) throws IOException {
      int var4 = var3;

      int var5;
      do {
         var5 = this.inputStream.read(var1, var2, var4);
         if (var5 == -1) {
            break;
         }

         var2 += var5;
         var4 -= var5;
         if (var4 != 0) {
            Thread.yield();
         }
      } while(var4 != 0);

      return var5 == -1 ? -(var3 - var4) : var3 - var4;
   }

   private native byte[] createRAMSource(byte[] var1, int var2) throws SourceHandlingException;

   private native byte[] createFileSource(String var1);

   private native byte[] createActiveSource();

   private native byte[] obtainHTTPSource(Object var1);

   private native boolean processHTTPdata();

   private native boolean writeData(byte[] var1, int var2);

   private native void setEOF();

   private native void nAbortSuspendedThread();

   private static native void init();

   private static native boolean nFileIsValid(String var0);

   static {
      init();
   }
}
