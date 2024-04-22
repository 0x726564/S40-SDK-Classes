package com.nokia.mid.impl.isa.source_handling;

import com.nokia.mid.impl.isa.util.SharedObjects;
import com.sun.midp.io.j2me.http.Protocol;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

public class JavaProducerSource implements Runnable {
   private int eL = 262144;
   private InputStream eM = null;
   private String eN;
   private int eO = 131072;
   private byte[] buffer = null;
   private int eP = -1;
   private Object eQ = null;
   private int eR = 0;
   private boolean connected = false;
   private boolean eS = false;
   protected Vector listeners = new Vector(2);
   public Thread thread;
   public boolean useActiveSource = false;
   private static final Object eT;

   public JavaProducerSource() {
      this.eR = 1;
   }

   public JavaProducerSource(InputStream var1) {
      this.eM = var1;
      this.eR = 1;
      this.connected = true;
   }

   public JavaProducerSource(InputStream var1, int var2) {
      this.eM = var1;
      this.eO = var2;
      this.eR = 1;
      this.connected = true;
   }

   public JavaProducerSource(String var1, boolean var2) throws IOException, SourceHandlingException {
      this.useActiveSource = var2;
      this.F(var1);
   }

   public JavaProducerSource(String var1, int var2, boolean var3) throws IOException, SourceHandlingException {
      this.useActiveSource = var3;
      this.eO = var2;
      this.F(var1);
   }

   public void setHighLowWaterMarks(int var1, int var2) {
      if (var1 > 131072) {
         this.eL = var1;
      }

   }

   private void F(String var1) throws IOException, SourceHandlingException {
      this.eN = var1;
      String var2;
      if ((var2 = var1.toLowerCase()).startsWith("http://")) {
         this.eR = 1;
         HttpConnection var3 = (HttpConnection)Connector.open(var1);
         int var4;
         if (!this.useActiveSource) {
            if ((var4 = var3.getResponseCode()) != 200) {
               var3.close();
               throw new SourceHandlingException("Invalid resource: " + var1 + " caused error: " + var4);
            }

            this.eM = var3.openInputStream();
            long var5;
            if ((var5 = var3.getLength()) != -1L && var5 < (long)this.eO) {
               this.eO = (int)var5;
            }

            var3.close();
            this.connected = true;
            return;
         }

         this.eQ = ((Protocol)var3).prepareCMSourceId();
         if ((var4 = var3.getResponseCode()) != 200) {
            var3.close();
            throw new SourceHandlingException("Invalid resource: " + var1 + " caused error: " + var4);
         }

         this.eR = 4;
      } else {
         if (!var2.startsWith("file://")) {
            throw new SourceHandlingException("URI protocol not supported");
         }

         this.eR = 3;
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
      switch(this.eR) {
      case 0:
      default:
         throw new SourceHandlingException();
      case 1:
         synchronized(eT) {
            var1 = this.createRAMSource(this.buffer, this.eP);
            break;
         }
      case 2:
         synchronized(eT) {
            var1 = this.createActiveSource();
            this.writeData(this.buffer, this.eP);
            break;
         }
      case 3:
         synchronized(eT) {
            var1 = this.createFileSource(this.eN.substring("file://".length()));
            break;
         }
      case 4:
         synchronized(eT) {
            var1 = this.obtainHTTPSource(this.eQ);
         }
      }

      this.eS = true;
      return var1;
   }

   public void setData(byte[] var1) {
      this.buffer = var1;
      this.eP = this.buffer.length;
   }

   public void fetchData() throws IOException {
      if (this.eM != null && this.eO != 0) {
         int var1;
         if (this.useActiveSource) {
            this.buffer = new byte[this.eO];
            this.eP = this.eM.read(this.buffer, 0, this.eO - 1);
            if ((var1 = this.eM.read()) != -1) {
               this.buffer[this.eP] = (byte)var1;
               ++this.eP;
               this.eR = 2;
               return;
            }

            this.eM.close();
            this.connected = false;
         } else {
            boolean var3 = false;
            Vector var4 = new Vector(5);

            byte[] var2;
            int var6;
            do {
               var2 = new byte[512];
               boolean var9 = true;
               var6 = 0;
               byte[] var5 = var2;
               JavaProducerSource var10 = this;
               int var7 = 512;

               int var8;
               while((var8 = var10.eM.read(var5, var6, var7)) != -1) {
                  var6 += var8;
                  if ((var7 -= var8) != 0) {
                     Thread.yield();
                  }

                  if (var7 == 0) {
                     break;
                  }
               }

               int var10000 = var8 == -1 ? -(512 - var7) : 512 - var7;
               var1 = var8 == -1 ? -(512 - var7) : 512 - var7;
               if (var10000 != 0) {
                  if (var1 > 0) {
                     var4.addElement(var2);
                  }

                  var3 = true;
               }
            } while(var1 > 0);

            if (var3) {
               int var11 = 0;
               this.eP = (var4.size() << 9) - var1;
               this.buffer = new byte[this.eP];

               for(var6 = 0; var6 < var4.size(); ++var6) {
                  System.arraycopy((byte[])var4.elementAt(var6), 0, this.buffer, var11, 512);
                  var11 += 512;
               }

               if (var1 < 0) {
                  System.arraycopy(var2, 0, this.buffer, var11, -var1);
               }
            }

            this.eM.close();
            this.connected = false;
         }
      }

   }

   public void start() {
      if (!this.eS) {
         this.a(1, new SourceHandlingException("Failed to called generateSourceID"));
      } else if ((this.eR == 2 || this.eR == 4) && this.thread == null) {
         this.thread = new Thread(this);
         this.thread.start();
      } else {
         this.a(0, (SourceHandlingException)null);
      }
   }

   public void disconnect() {
      if (this.eR == 2) {
         if (this.thread != null) {
            synchronized(eT) {
               this.nAbortSuspendedThread();
               return;
            }
         }
      } else if (this.connected) {
         try {
            this.eM.close();
            this.connected = false;
            return;
         } catch (IOException var4) {
         }
      }

   }

   public void run() {
      boolean var1;
      if (this.eR == 4) {
         var1 = true;

         while(var1) {
            synchronized(eT) {
               var1 = this.processHTTPdata();
            }
         }
      } else {
         try {
            var1 = false;
            boolean var2 = true;

            int var9;
            do {
               if ((var9 = this.eM.read(this.buffer)) > 0) {
                  synchronized(eT) {
                     var2 = this.writeData(this.buffer, var9);
                  }
               }
            } while(var9 != -1 && var2);

            this.eM.close();
            this.connected = false;
         } catch (IOException var8) {
            this.a(1, new SourceHandlingException(var8.toString()));
         }
      }

      synchronized(eT) {
         this.setEOF();
      }

      this.a(0, (SourceHandlingException)null);
      this.thread = null;
   }

   private void a(int var1, SourceHandlingException var2) {
      for(int var3 = 0; var3 < this.listeners.size(); ++var3) {
         ((SourceHandlingListener)this.listeners.elementAt(var3)).sourceHandlingEvent(var1, var2);
      }

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
      synchronized(eT = SharedObjects.getLock("com.nokia.mid.impl.isa.source_handling.JavaProducerSource")) {
         init();
      }
   }
}
