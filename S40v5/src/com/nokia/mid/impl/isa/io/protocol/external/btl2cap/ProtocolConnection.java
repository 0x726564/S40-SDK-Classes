package com.nokia.mid.impl.isa.io.protocol.external.btl2cap;

import com.nokia.mid.impl.isa.bluetooth.CommonBluetooth;
import com.nokia.mid.impl.isa.bluetooth.RemoteDeviceAccessor;
import java.io.IOException;
import javax.bluetooth.L2CAPConnection;

public class ProtocolConnection extends Protocol implements RemoteDeviceAccessor, L2CAPConnection {
   private int gG;
   private byte[] gH = new byte[6];
   private byte[] gI = null;
   private int gJ = 0;
   private int gK = 0;
   private final boolean gL;
   private boolean gM = false;
   private final Object gN = new Object();
   private final Object gO = new Object();
   private final Object ag = new Object();

   ProtocolConnection(boolean var1, int var2) {
      this.gL = var1;
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
         synchronized(this.gO) {
            boolean var10000;
            synchronized(this.ag) {
               if (this.isClosed) {
                  throw new IOException("connection is closed");
               }

               int var5;
               if ((var5 = this.ready0(0)) == -1) {
                  throw new IOException("error in ready");
               }

               var10000 = var5 > 0;
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
         synchronized(this.gO) {
            while(true) {
               label49: {
                  int var10000;
                  synchronized(this.ag) {
                     if (this.isClosed) {
                        throw new IOException("connection is closed");
                     }

                     int var3;
                     if ((var3 = this.receive0(0, var1, var1.length)) <= 0 && var1.length != 0) {
                        if (var3 == -1) {
                           throw new IOException("error in receive");
                        }
                        break label49;
                     }

                     var10000 = var3;
                  }

                  return var10000;
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
         synchronized(this.gN) {
            int var4 = var1.length > this.transmitMTU ? this.transmitMTU : var1.length;

            int var3;
            do {
               synchronized(this.ag) {
                  this.ensureOpen();
                  if ((var3 = this.send0(0, var1, var4)) == -1) {
                     throw new IOException("error in send");
                  }
               }

               Thread.yield();
            } while(var3 == 1);

         }
      }
   }

   public void close() throws IOException {
      synchronized(this.ag) {
         if (!this.isClosed) {
            try {
               Thread.sleep(1000L);
            } catch (InterruptedException var2) {
            }

            if (!this.isClosed) {
               this.close0((byte)0);
               this.isClosed = true;
            }

            this.gG = 0;
         }

      }
   }

   public boolean isClient() {
      return this.gL;
   }

   public String getRemoteDeviceAddress() throws IOException {
      if (this.isClosed() && !this.isPushConnection()) {
         throw new IOException("Connection is closed");
      } else {
         return CommonBluetooth.getStringAddress(this.gH);
      }
   }

   public boolean isPushConnection() {
      return this.gM;
   }

   private void ensureOpen() throws IOException {
      if (this.isClosed) {
         throw new IOException("connection is closed");
      }
   }

   protected void setPushConnection() {
      this.gM = true;
   }

   private native int send0(int var1, byte[] var2, int var3);

   private native int receive0(int var1, byte[] var2, int var3) throws IOException;

   private native int ready0(int var1) throws IOException;

   private native void close0(byte var1);
}
