package com.nokia.mid.impl.isa.io.protocol.external.btl2cap;

import com.nokia.mid.impl.isa.bluetooth.CommonBluetooth;
import java.io.IOException;
import javax.bluetooth.L2CAPConnection;

public class ProtocolConnection extends Protocol implements L2CAPConnection {
   private static final int SUCCESS = 0;
   private static final int BUSY = 1;
   private static final int FAILURE = -1;
   private byte conHandle;
   private int pepHandle;
   private byte[] remoteAddress = new byte[6];
   private byte[] nativeInBuf = null;
   private int nativeInBufIndex = 0;
   private int nativeInBufLength = 0;
   private final boolean isClient;
   private boolean isPushConnection = false;
   private final Object sndLock = new Object();
   private final Object rcvLock = new Object();
   private final Object closeLock = new Object();

   ProtocolConnection(boolean var1, int var2) {
      this.isClient = var1;
      this.mode = var2;
   }

   public int getReceiveMTU() throws IOException {
      this.ensureOpen();
      return this.receiveMTU;
   }

   public int getTransmitMTU() throws IOException {
      this.ensureOpen();
      return this.transmitMTU;
   }

   public boolean ready() throws IOException {
      if (this.mode != 1 && this.mode != 3) {
         return false;
      } else {
         synchronized(this.rcvLock) {
            boolean var10000;
            synchronized(this.closeLock) {
               if (this.isClosed) {
                  if (this.nativeInBuf != null && this.nativeInBufLength > 0) {
                     var10000 = true;
                     return var10000;
                  }

                  throw new IOException("connection is closed");
               }

               int var3 = this.ready0(this.pepHandle);
               if (var3 == -1) {
                  throw new IOException("error in ready");
               }

               var10000 = var3 > 0;
            }

            return var10000;
         }
      }
   }

   public int receive(byte[] var1) throws IOException {
      if (this.mode != 1 && this.mode != 3) {
         throw new IOException("write-only connection");
      } else if (var1 == null) {
         throw new NullPointerException("Null inBuf");
      } else {
         synchronized(this.rcvLock) {
            boolean var5 = false;

            while(true) {
               synchronized(this.closeLock) {
                  int var10000;
                  if (this.isClosed) {
                     if (this.nativeInBuf != null && this.nativeInBufIndex < this.nativeInBufLength) {
                        int var11 = this.nativeInBuf[this.nativeInBufIndex++] << 8 & '\uff00' | this.nativeInBuf[this.nativeInBufIndex++] & 255;
                        int var4 = var1.length > var11 ? var11 : var1.length;
                        System.arraycopy(this.nativeInBuf, this.nativeInBufIndex, var1, 0, var4);
                        this.nativeInBufIndex += var11;
                        var10000 = var4;
                        return var10000;
                     }

                     throw new IOException("connection is closed");
                  }

                  int var3 = this.receive0(this.pepHandle, var1, var1.length);
                  if (var3 > 0 || var1.length == 0) {
                     var10000 = var3;
                     return var10000;
                  }

                  if (var3 == -1) {
                     throw new IOException("error in receive");
                  }
               }

               Thread.yield();
            }
         }
      }
   }

   public void send(byte[] var1) throws IOException {
      if (this.mode != 2 && this.mode != 3) {
         throw new IOException("read-only connection");
      } else if (var1 == null) {
         throw new NullPointerException("Null data");
      } else {
         synchronized(this.sndLock) {
            int var4 = var1.length > this.transmitMTU ? this.transmitMTU : var1.length;

            int var3;
            do {
               synchronized(this.closeLock) {
                  this.ensureOpen();
                  var3 = this.send0(this.pepHandle, var1, var4);
                  if (var3 == -1) {
                     throw new IOException("error in send");
                  }
               }

               Thread.yield();
            } while(var3 == 1);

         }
      }
   }

   public void close() throws IOException {
      synchronized(this.closeLock) {
         if (!this.isClosed) {
            try {
               Thread.sleep(1000L);
            } catch (InterruptedException var4) {
            }

            if (!this.isClosed) {
               this.close0(this.conHandle);
               this.isClosed = true;
            }

            this.pepHandle = 0;
         }

      }
   }

   public boolean isClient() {
      return this.isClient;
   }

   public String getRemoteDeviceAddress() {
      return CommonBluetooth.getStringAddress(this.remoteAddress);
   }

   public boolean isPushConnection() {
      return this.isPushConnection;
   }

   private void ensureOpen() throws IOException {
      if (this.isClosed) {
         throw new IOException("connection is closed");
      }
   }

   protected void setPushConnection() {
      this.isPushConnection = true;
   }

   private native int send0(int var1, byte[] var2, int var3);

   private native int receive0(int var1, byte[] var2, int var3) throws IOException;

   private native int ready0(int var1) throws IOException;

   private native void close0(byte var1);
}
