package com.nokia.mid.impl.isa.io.protocol.external.btspp;

import com.nokia.mid.impl.isa.bluetooth.CommonBluetooth;
import com.nokia.mid.impl.isa.bluetooth.RemoteDeviceAccessor;
import com.nokia.mid.impl.isa.bluetooth.Tracer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.StreamConnection;

public class ProtocolConnection extends Protocol implements StreamConnection, RemoteDeviceAccessor {
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

   ProtocolConnection(boolean isClient, int mode) {
      Tracer.println("ProtocolConnection(isClient=" + String.valueOf(isClient) + "mode=" + mode + ")");
      this.isClient = isClient;
      this.mode = mode;
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
               ProtocolConnection.PrivateInputStream inputStream = new ProtocolConnection.PrivateInputStream(this);
               this.inputStreamOpen = true;
               return inputStream;
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
                  ProtocolConnection.PrivateOutputStream outputStream = new ProtocolConnection.PrivateOutputStream(this);
                  this.outputStreamOpen = true;
                  return outputStream;
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

   public String getRemoteDeviceAddress() throws IOException {
      if (this.isClosed() && !this.isPushConnection()) {
         throw new IOException("Connection is closed");
      } else {
         Tracer.println("getRemoteDeviceAddress");
         String addr = CommonBluetooth.getStringAddress(this.remoteAddress);
         Tracer.println("returns " + addr);
         return addr;
      }
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

      PrivateOutputStream(ProtocolConnection parent) {
         this.cache = new byte[ProtocolConnection.RFCOMM_MAX_MSG_SIZE - 1];
         this.lenCacheUsed = 0;
         this.parent = parent;
         Tracer.println("PrivateOutputStream()");
      }

      public void write(int b) throws IOException {
         byte[] data_byte = new byte[]{(byte)b};
         this.write(data_byte, 0, 1);
      }

      public void write(byte[] b, int off, int len) throws IOException {
         if (b == null) {
            Tracer.println("write throws NullPointerException");
            throw new NullPointerException("Null b");
         } else if (off >= 0 && len >= 0 && off + len <= b.length) {
            if (len != 0) {
               synchronized(this.writeLock) {
                  for(int rem_len_cache = this.cache.length - this.lenCacheUsed; len >= rem_len_cache; this.lenCacheUsed = 0) {
                     int len_snd = len + this.lenCacheUsed < ProtocolConnection.RFCOMM_MAX_MSG_SIZE ? len + this.lenCacheUsed : ProtocolConnection.RFCOMM_MAX_MSG_SIZE;
                     int len_b = len_snd - this.lenCacheUsed;
                     byte[] data;
                     int off_data;
                     if (this.lenCacheUsed <= 0 && len <= ProtocolConnection.RFCOMM_MAX_MSG_SIZE) {
                        data = b;
                        off_data = off;
                     } else {
                        data = new byte[len_snd];
                        System.arraycopy(this.cache, 0, data, 0, this.lenCacheUsed);
                        System.arraycopy(b, off, data, this.lenCacheUsed, len_b);
                        off_data = 0;
                     }

                     int retval;
                     do {
                        synchronized(this.closeLock) {
                           this.ensureOpen();
                           retval = this.parent.write0(this.parent.pepHandle, data, off_data, len_snd);
                        }

                        Thread.yield();
                     } while(1 == retval);

                     off += len_b;
                     len -= len_b;
                     rem_len_cache = this.cache.length;
                  }

                  if (len > 0) {
                     System.arraycopy(b, off, this.cache, this.lenCacheUsed, len);
                     this.lenCacheUsed += len;
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
            int retval;
            if (this.lenCacheUsed > 0) {
               do {
                  synchronized(this.closeLock) {
                     this.ensureOpen();
                     retval = this.parent.write0(this.parent.pepHandle, this.cache, 0, this.lenCacheUsed);
                  }

                  Thread.yield();
               } while(1 == retval);

               this.lenCacheUsed = 0;
            }

            do {
               synchronized(this.closeLock) {
                  this.ensureOpen();
                  retval = ProtocolConnection.this.flush0(this.parent.pepHandle);
               }

               Thread.yield();
            } while(1 == retval);

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

      PrivateInputStream(ProtocolConnection parent) {
         this.cache = new byte[ProtocolConnection.RFCOMM_MAX_MSG_SIZE];
         this.cache_index = 0;
         this.available_data = 0;
         this.parent = parent;
         Tracer.println("PrivateInputStream()");
      }

      public int read() throws IOException {
         byte[] data_byte = new byte[]{0};
         return -1 == this.read(data_byte) ? -1 : data_byte[0] & 255;
      }

      public int read(byte[] b) throws IOException {
         if (b == null) {
            Tracer.println("read throws NullPointerException");
            throw new NullPointerException();
         } else if (b.length == 0) {
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
                              int retval = this.parent.read0(this.parent.pepHandle, this.cache, 0, this.cache.length);
                              if (retval <= 0) {
                                 if (retval == -1) {
                                    Tracer.println("read throws IOException read0 FAILURE");
                                    throw new IOException("error in read");
                                 }
                                 break label68;
                              }

                              this.available_data = retval;
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

                     int length;
                     if (b.length >= this.available_data) {
                        length = this.available_data;
                        System.arraycopy(this.cache, this.cache_index, b, 0, length);
                        this.available_data = 0;
                        this.cache_index = 0;
                     } else {
                        length = b.length;
                        System.arraycopy(this.cache, this.cache_index, b, 0, length);
                        this.available_data -= length;
                        this.cache_index += length;
                     }

                     return length;
                  }

                  Thread.yield();
               }
            }
         }
      }

      public int read(byte[] b, int off, int len) throws IOException {
         if (b == null) {
            Tracer.println("read throws NullPointerException");
            throw new NullPointerException();
         } else if (off >= 0 && len >= 0 && off + len <= b.length) {
            if (len == 0) {
               Tracer.println("read got len=0");
               return 0;
            } else {
               synchronized(this.readLock) {
                  int length_written = 0;

                  while(len != 0) {
                     while(true) {
                        label114: {
                           if (this.available_data == 0) {
                              label107: {
                                 byte var10000;
                                 synchronized(this.closeLock) {
                                    this.ensureOpen();
                                    if (!this.parent.isClosed()) {
                                       int retval = this.parent.read0(this.parent.pepHandle, this.cache, 0, this.cache.length);
                                       if (retval <= 0) {
                                          if (retval == 0) {
                                             if (length_written > 0) {
                                                int var12 = length_written;
                                                return var12;
                                             }
                                          } else if (retval == -1) {
                                             Tracer.println("read throws IOException read0 FAILURE");
                                             throw new IOException("error in read");
                                          }
                                          break label114;
                                       }

                                       this.available_data = retval;
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

                           if (len < this.available_data) {
                              System.arraycopy(this.cache, this.cache_index, b, off, len);
                              length_written += len;
                              this.available_data -= len;
                              this.cache_index += len;
                              return length_written;
                           }

                           System.arraycopy(this.cache, this.cache_index, b, off, this.available_data);
                           length_written += this.available_data;
                           off += this.available_data;
                           len -= this.available_data;
                           this.available_data = 0;
                           this.cache_index = 0;
                           continue;
                        }

                        Thread.yield();
                     }
                  }

                  return length_written;
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
                  int retval = this.parent.available0(this.parent.pepHandle);
                  if (retval == -1) {
                     Tracer.println("available throws IOException");
                     throw new IOException("error in available");
                  }

                  var10000 = retval;
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
