package com.nokia.mid.impl.isa.io.protocol.external.btspp;

import com.nokia.mid.impl.isa.bluetooth.CommonBluetooth;
import com.nokia.mid.impl.isa.bluetooth.Tracer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.StreamConnection;

public class ProtocolConnection extends Protocol implements StreamConnection {
   private static final int SUCCESS = 0;
   private static final int BUSY = 1;
   private static final int FAILURE = -1;
   private static final int RFCOMM_MAX_MSG_SIZE = maxPacketSize0();
   private byte conHandle;
   private int pepHandle;
   private byte[] remoteAddress = new byte[6];
   private byte[] nativeInBuf = null;
   private int nativeInBufLength = 0;
   private final boolean isClient;
   private boolean inputStreamOpen = false;
   private boolean outputStreamOpen = false;
   private boolean connectionOpen = false;
   private boolean isPushConnection = false;
   private final Object openCloseLock = new Object();

   ProtocolConnection(boolean var1, int var2) {
      Tracer.println("ProtocolConnection(isClient=" + String.valueOf(var1) + "mode=" + var2 + ")");
      this.isClient = var1;
      this.mode = var2;
      this.connectionOpen = true;
   }

   public InputStream openInputStream() throws IOException {
      Tracer.println("openInputStream");
      if (this.mode != 1 && this.mode != 3) {
         Tracer.println("IOException WO");
         throw new IOException("write-only connection");
      } else {
         synchronized(this.openCloseLock) {
            if (!this.connectionOpen) {
               Tracer.println("IOException closed");
               throw new IOException("connection already closed");
            } else if (this.inputStreamOpen) {
               Tracer.println("IOException already open");
               throw new IOException("input stream already opened");
            } else {
               ProtocolConnection.PrivateInputStream var2 = new ProtocolConnection.PrivateInputStream(this);
               this.inputStreamOpen = true;
               return var2;
            }
         }
      }
   }

   public OutputStream openOutputStream() throws IOException {
      Tracer.println("openOutputStream");
      if (this.mode != 2 && this.mode != 3) {
         Tracer.println("IOException RO");
         throw new IOException("read-only connection");
      } else {
         synchronized(this.openCloseLock) {
            if (!this.isClosed && this.connectionOpen) {
               if (this.outputStreamOpen) {
                  Tracer.println("IOException already open");
                  throw new IOException("output stream already opened");
               } else {
                  ProtocolConnection.PrivateOutputStream var2 = new ProtocolConnection.PrivateOutputStream(this);
                  this.outputStreamOpen = true;
                  return var2;
               }
            } else {
               Tracer.println("IOException closed");
               throw new IOException("connection already closed");
            }
         }
      }
   }

   public DataInputStream openDataInputStream() throws IOException {
      Tracer.println("openDataInputStream");
      return new DataInputStream(this.openInputStream());
   }

   public DataOutputStream openDataOutputStream() throws IOException {
      Tracer.println("openDataOutputStream");
      return new DataOutputStream(this.openOutputStream());
   }

   public void close() throws IOException {
      synchronized(this.openCloseLock) {
         Tracer.println("Connection.close");
         if (this.connectionOpen) {
            this.connectionOpen = false;
            if (!this.isClosed) {
               this.closeServiceConnection();
            }
         }

      }
   }

   public boolean isClient() {
      Tracer.println("isClient()=" + String.valueOf(this.isClient));
      return this.isClient;
   }

   public String getRemoteDeviceAddress() {
      Tracer.println("getRemoteDeviceAddress");
      String var1 = CommonBluetooth.getStringAddress(this.remoteAddress);
      Tracer.println("returns " + var1);
      return var1;
   }

   private void closeServiceConnection() throws IOException {
      Tracer.println("closeServiceConnection");
      if (!this.connectionOpen && !this.inputStreamOpen && !this.outputStreamOpen) {
         try {
            Tracer.println("sleep(1000)");
            Thread.sleep(1000L);
         } catch (InterruptedException var2) {
         }

         if (!this.isClosed) {
            Tracer.println("close0(conHandle=" + String.valueOf((int)this.conHandle) + ")");
            this.close0(this.conHandle);
            this.isClosed = true;
         }

         this.pepHandle = 0;
      }

   }

   public boolean isPushConnection() {
      return this.isPushConnection;
   }

   protected void setPushConnection() {
      this.isPushConnection = true;
   }

   private native int read0(int var1, byte[] var2, int var3, int var4);

   private native int write0(int var1, byte[] var2, int var3, int var4);

   private native int available0(int var1);

   private native int flush0(int var1);

   private native void close0(byte var1);

   private static native int maxPacketSize0();

   private class PrivateOutputStream extends OutputStream {
      private ProtocolConnection parent;
      private final Object writeLock = new Object();
      private final Object closeLock = new Object();
      private byte[] cache;
      private int lenCacheUsed;

      PrivateOutputStream(ProtocolConnection var2) {
         this.cache = new byte[ProtocolConnection.RFCOMM_MAX_MSG_SIZE - 1];
         this.lenCacheUsed = 0;
         this.parent = var2;
         Tracer.println("PrivateOutputStream()");
      }

      public void write(int var1) throws IOException {
         byte[] var2 = new byte[]{(byte)var1};
         this.write(var2, 0, 1);
      }

      public void write(byte[] var1, int var2, int var3) throws IOException {
         if (var1 == null) {
            Tracer.println("write throws NullPointerException");
            throw new NullPointerException("Null b");
         } else if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length) {
            if (var3 != 0) {
               synchronized(this.writeLock) {
                  for(int var8 = this.cache.length - this.lenCacheUsed; var3 >= var8; this.lenCacheUsed = 0) {
                     int var9 = var3 + this.lenCacheUsed < ProtocolConnection.RFCOMM_MAX_MSG_SIZE ? var3 + this.lenCacheUsed : ProtocolConnection.RFCOMM_MAX_MSG_SIZE;
                     int var10 = var9 - this.lenCacheUsed;
                     byte[] var5;
                     int var7;
                     if (this.lenCacheUsed <= 0 && var3 <= ProtocolConnection.RFCOMM_MAX_MSG_SIZE) {
                        var5 = var1;
                        var7 = var2;
                     } else {
                        var5 = new byte[var9];
                        System.arraycopy(this.cache, 0, var5, 0, this.lenCacheUsed);
                        System.arraycopy(var1, var2, var5, this.lenCacheUsed, var10);
                        var7 = 0;
                     }

                     int var6;
                     do {
                        synchronized(this.closeLock) {
                           this.ensureOpen();
                           var6 = this.parent.write0(this.parent.pepHandle, var5, var7, var9);
                        }

                        Thread.yield();
                     } while(1 == var6);

                     var2 += var10;
                     var3 -= var10;
                     var8 = this.cache.length;
                  }

                  if (var3 > 0) {
                     System.arraycopy(var1, var2, this.cache, this.lenCacheUsed, var3);
                     this.lenCacheUsed += var3;
                  }

               }
            }
         } else {
            Tracer.println("write throws IndexOutOfBoundsException");
            throw new IndexOutOfBoundsException();
         }
      }

      public void flush() throws IOException {
         synchronized(this.writeLock) {
            int var2;
            if (this.lenCacheUsed > 0) {
               do {
                  synchronized(this.closeLock) {
                     this.ensureOpen();
                     var2 = this.parent.write0(this.parent.pepHandle, this.cache, 0, this.lenCacheUsed);
                  }

                  Thread.yield();
               } while(1 == var2);

               this.lenCacheUsed = 0;
            }

            do {
               synchronized(this.closeLock) {
                  this.ensureOpen();
                  var2 = ProtocolConnection.this.flush0(this.parent.pepHandle);
               }

               Thread.yield();
            } while(1 == var2);

         }
      }

      public void close() throws IOException {
         Tracer.println("OutputStream.close");
         this.flush();
         this.streamClose();
      }

      private void ensureOpen() throws IOException {
         if (this.parent == null || this.parent.isClosed()) {
            Tracer.println("ensureOpen throws IOException");
            throw new IOException("output stream is closed");
         }
      }

      private void streamClose() throws IOException {
         synchronized(this.closeLock) {
            if (this.parent != null) {
               synchronized(this.parent.openCloseLock) {
                  Tracer.println("streamClose");
                  this.parent.outputStreamOpen = false;
                  this.parent.closeServiceConnection();
                  this.parent = null;
               }
            }

         }
      }
   }

   private class PrivateInputStream extends InputStream {
      private ProtocolConnection parent;
      private final Object readLock = new Object();
      private final Object closeLock = new Object();
      private byte[] cache;
      private int cache_index;
      private int available_data;

      PrivateInputStream(ProtocolConnection var2) {
         this.cache = new byte[ProtocolConnection.RFCOMM_MAX_MSG_SIZE];
         this.cache_index = 0;
         this.available_data = 0;
         this.parent = var2;
         Tracer.println("PrivateInputStream()");
      }

      public int read() throws IOException {
         byte[] var1 = new byte[]{0};
         return -1 == this.read(var1) ? -1 : var1[0] & 255;
      }

      public int read(byte[] var1) throws IOException {
         if (var1 == null) {
            Tracer.println("read throws NullPointerException");
            throw new NullPointerException();
         } else if (var1.length == 0) {
            Tracer.println("read got 0 array");
            return 0;
         } else {
            synchronized(this.readLock) {
               while(true) {
                  label68: {
                     if (this.available_data == 0) {
                        synchronized(this.closeLock) {
                           this.ensureOpen();
                           if (!this.parent.isClosed()) {
                              int var3 = this.parent.read0(this.parent.pepHandle, this.cache, 0, this.cache.length);
                              if (var3 <= 0) {
                                 if (var3 == -1) {
                                    Tracer.println("read throws IOException read0 FAILURE");
                                    throw new IOException("error in read");
                                 }
                                 break label68;
                              }

                              this.available_data = var3;
                           } else {
                              if (this.parent.nativeInBuf == null) {
                                 this.parent = null;
                                 byte var10000 = -1;
                                 return var10000;
                              }

                              this.cache_index = 0;
                              this.cache = this.parent.nativeInBuf;
                              this.available_data = this.parent.nativeInBufLength;
                              this.parent.nativeInBufLength = 0;
                              this.parent.nativeInBuf = null;
                           }
                        }
                     }

                     int var4;
                     if (var1.length >= this.available_data) {
                        var4 = this.available_data;
                        System.arraycopy(this.cache, this.cache_index, var1, 0, var4);
                        this.available_data = 0;
                        this.cache_index = 0;
                     } else {
                        var4 = var1.length;
                        System.arraycopy(this.cache, this.cache_index, var1, 0, var4);
                        this.available_data -= var4;
                        this.cache_index += var4;
                     }

                     return var4;
                  }

                  Thread.yield();
               }
            }
         }
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         if (var1 == null) {
            Tracer.println("read throws NullPointerException");
            throw new NullPointerException();
         } else if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length) {
            if (var3 == 0) {
               Tracer.println("read got len=0");
               return 0;
            } else {
               synchronized(this.readLock) {
                  int var6 = 0;

                  while(var3 != 0) {
                     while(true) {
                        label114: {
                           if (this.available_data == 0) {
                              label107: {
                                 byte var10000;
                                 synchronized(this.closeLock) {
                                    this.ensureOpen();
                                    if (!this.parent.isClosed()) {
                                       int var5 = this.parent.read0(this.parent.pepHandle, this.cache, 0, this.cache.length);
                                       if (var5 <= 0) {
                                          if (var5 == 0) {
                                             if (var6 > 0) {
                                                int var12 = var6;
                                                return var12;
                                             }
                                          } else if (var5 == -1) {
                                             Tracer.println("read throws IOException read0 FAILURE");
                                             throw new IOException("error in read");
                                          }
                                          break label114;
                                       }

                                       this.available_data = var5;
                                       break label107;
                                    }

                                    if (this.parent.nativeInBuf != null) {
                                       this.cache_index = 0;
                                       this.cache = this.parent.nativeInBuf;
                                       this.available_data = this.parent.nativeInBufLength;
                                       this.parent.nativeInBufLength = 0;
                                       this.parent.nativeInBuf = null;
                                       break label107;
                                    }

                                    this.parent = null;
                                    var10000 = -1;
                                 }

                                 return var10000;
                              }
                           }

                           if (var3 < this.available_data) {
                              System.arraycopy(this.cache, this.cache_index, var1, var2, var3);
                              var6 += var3;
                              this.available_data -= var3;
                              this.cache_index += var3;
                              return var6;
                           }

                           System.arraycopy(this.cache, this.cache_index, var1, var2, this.available_data);
                           var6 += this.available_data;
                           var2 += this.available_data;
                           var3 -= this.available_data;
                           this.available_data = 0;
                           this.cache_index = 0;
                           continue;
                        }

                        Thread.yield();
                     }
                  }

                  return var6;
               }
            }
         } else {
            Tracer.println("read throws IndexOutOfBoundsException");
            throw new IndexOutOfBoundsException();
         }
      }

      public int available() throws IOException {
         synchronized(this.readLock) {
            int var10000;
            synchronized(this.closeLock) {
               this.ensureOpen();
               if (!this.parent.isClosed()) {
                  int var3 = this.parent.available0(this.parent.pepHandle);
                  if (var3 == -1) {
                     Tracer.println("available throws IOException");
                     throw new IOException("error in available");
                  }

                  var10000 = var3;
                  return var10000;
               }

               if (this.available_data <= 0 && this.parent.nativeInBufLength <= 0) {
                  this.parent = null;
                  Tracer.println("available throws IOException");
                  throw new IOException("input stream is closed");
               }

               var10000 = this.available_data + this.parent.nativeInBufLength;
            }

            return var10000;
         }
      }

      public void close() throws IOException {
         synchronized(this.closeLock) {
            if (this.parent != null) {
               synchronized(this.parent.openCloseLock) {
                  Tracer.println("InputStream.close");
                  this.parent.inputStreamOpen = false;
                  this.parent.closeServiceConnection();
                  this.parent = null;
               }
            }

         }
      }

      private void ensureOpen() throws IOException {
         if (this.parent == null) {
            Tracer.println("ensureOpen throws IOException");
            throw new IOException("input stream is closed");
         }
      }
   }
}
