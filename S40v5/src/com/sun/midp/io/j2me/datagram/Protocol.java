package com.sun.midp.io.j2me.datagram;

import com.nokia.mid.impl.isa.io.GeneralSharedIO;
import com.sun.cldc.io.ConnectionBaseInterface;
import com.sun.midp.io.HttpUrl;
import java.io.IOException;
import java.io.InterruptedIOException;
import javax.microedition.io.Connection;
import javax.microedition.io.Datagram;
import javax.microedition.io.UDPDatagramConnection;

public class Protocol implements ConnectionBaseInterface, UDPDatagramConnection {
   private int handle = -1;
   private boolean dU = false;
   private String dV;
   private String host;
   private int port;
   private int dW;
   private String dX = null;
   private static boolean dY = false;

   public void open(String var1, int var2, boolean var3) throws IOException {
      throw new RuntimeException("Should not be called");
   }

   public Connection openPrim(String var1, int var2, boolean var3) throws IOException {
      if (var2 != 1 && var2 != 2 && var2 != 3) {
         throw new IllegalArgumentException("invalid mode");
      } else {
         this.dV = "datagram:" + var1;
         (new StringBuffer()).append("Datagram openPrim: address=").append(this.dV).toString();
         this.host = null;
         this.port = -1;
         if (var1.charAt(0) == '/' && var1.charAt(1) == '/') {
            var1 = ":" + var1;
            HttpUrl var7 = new HttpUrl(var1);
            this.host = var7.host;
            this.port = var7.port;
            if (this.host != null && this.port == -1) {
               throw new IllegalArgumentException("Missing port number");
            } else {
               synchronized(GeneralSharedIO.networkPermissionLock) {
                  this.checkPermission0(this.host == null);
               }

               synchronized(SocketCommon.socketLock) {
                  this.handle = this.open0(this.host, this.port, var2, var3);
               }

               (new StringBuffer()).append("open0 returned: ").append(this.handle).toString();
               if (this.handle > 0) {
                  this.dU = true;
                  return this;
               } else if (this.handle == -23 && var3) {
                  throw new InterruptedIOException("timed out");
               } else if (this.handle == -22) {
                  throw new IOException("Maximum number of sockets reached");
               } else {
                  throw new IOException("Error occured whilst opening socket");
               }
            }
         } else {
            throw new IllegalArgumentException("Protocol must start with \"//\" " + var1);
         }
      }
   }

   private void ensureOpen() throws IOException {
      if (!this.dU) {
         throw new IOException("connection closed");
      }
   }

   public void close() throws IOException {
      if (this.dU) {
         this.dU = false;
         Protocol var4;
         synchronized((var4 = this).getClass()) {
            var4.close0();
         }

         var4.handle = -1;
      }

   }

   public int getMaximumLength() throws IOException {
      this.ensureOpen();
      return 1450;
   }

   public int getNominalLength() throws IOException {
      this.ensureOpen();
      return 1450;
   }

   public void send(Datagram var1) throws IOException {
      this.ensureOpen();
      int var2;
      if ((var2 = var1.getLength()) < 0) {
         throw new IOException("Bad datagram length");
      } else {
         String var3;
         if ((var3 = var1.getAddress()) == null) {
            throw new IOException("No address in datagram");
         } else {
            HttpUrl var11;
            String var4 = (var11 = new HttpUrl(var3)).host;
            int var12 = var11.port;
            if (var4 == null) {
               throw new IOException("Missing host");
            } else if (var12 == -1) {
               throw new IOException("Missing port");
            } else {
               while(true) {
                  int var5;
                  try {
                     var5 = this.write0(var4, var12, var1.getData(), var1.getOffset(), var2);
                  } finally {
                     if (!this.dU) {
                        throw new InterruptedIOException("Socket closed");
                     }

                  }

                  if (var5 == var1.getLength()) {
                     return;
                  }

                  if (var5 != 0) {
                     throw new IOException("Failed to send datagram");
                  }

                  try {
                     Thread.sleep(100L);
                  } catch (InterruptedException var9) {
                     throw new InterruptedIOException("Socket closed");
                  }
               }
            }
         }
      }
   }

   public void receive(Datagram var1) throws IOException {
      this.ensureOpen();
      if (var1.getLength() <= 1450 && var1.getLength() != 0) {
         this.dX = null;
         this.dW = this.read0(var1.getData(), var1.getOffset(), var1.getLength());
         (new StringBuffer()).append("receive: error: ").append(this.dW).toString();
         if (this.dW == -23) {
            throw new InterruptedIOException("timed out");
         } else {
            throw new IOException("receive failed");
         }
      } else {
         throw new IllegalArgumentException("Bad datagram length");
      }
   }

   public Datagram newDatagram(int var1) throws IOException {
      (new StringBuffer()).append("newDatagram size = ").append(var1).toString();
      return this.a(true, (byte[])null, var1, true, (String)null);
   }

   public Datagram newDatagram(int var1, String var2) throws IOException {
      (new StringBuffer()).append("newDatagram size = ").append(var1).append(",address = ").append(var2).toString();
      return this.a(true, (byte[])null, var1, false, var2);
   }

   public Datagram newDatagram(byte[] var1, int var2) throws IOException {
      if (var1 != null) {
         (new StringBuffer()).append("newDatagram size = ").append(var2).append(",buffer len = ").append(var1.length).toString();
      }

      return this.a(false, var1, var2, true, (String)null);
   }

   public Datagram newDatagram(byte[] var1, int var2, String var3) throws IOException {
      (new StringBuffer()).append("newDatagram size = ").append(var2).append(",buffer len = ").append(var1.length).append("addr = ").append(var3).toString();
      return this.a(false, var1, var2, false, var3);
   }

   public String getLocalAddress() throws IOException {
      String var1 = null;
      boolean var2 = false;
      this.ensureOpen();

      while(!var2) {
         if ((var1 = this.getLocalHost0()).equals("retry")) {
            try {
               Thread.sleep(100L);
            } catch (InterruptedException var3) {
               throw new IOException();
            }
         } else {
            if (var1.equals("")) {
               throw new IOException();
            }

            var2 = true;
         }
      }

      return var1;
   }

   public int getLocalPort() throws IOException {
      int var1 = -2;
      this.ensureOpen();

      while(var1 <= 0) {
         if ((var1 = this.getLocalPort0()) <= 0) {
            if (var1 != -45) {
               throw new IOException();
            }

            try {
               Thread.sleep(100L);
            } catch (InterruptedException var2) {
               throw new IOException();
            }
         }
      }

      return var1;
   }

   private Datagram a(boolean var1, byte[] var2, int var3, boolean var4, String var5) throws IOException {
      this.ensureOpen();
      if (var3 < 0) {
         throw new IllegalArgumentException("Negative size");
      } else {
         byte[] var7;
         if (var1) {
            var7 = new byte[var3];
         } else {
            if (var2 == null) {
               throw new IllegalArgumentException("Invalid buffer");
            }

            if (var3 > var2.length) {
               throw new IllegalArgumentException("Size is larger than the buffer");
            }

            var7 = var2;
         }

         DatagramObject var8 = new DatagramObject(var7, var3);
         if (var4) {
            if (this.host != null) {
               try {
                  var8.setAddress("datagram://" + this.host + ":" + this.port);
               } catch (IllegalArgumentException var6) {
               }
            }
         } else {
            var8.setAddress(var5);
         }

         return var8;
      }
   }

   private native int open0(String var1, int var2, int var3, boolean var4) throws IOException;

   private native int read0(byte[] var1, int var2, int var3) throws IOException;

   private native int write0(String var1, int var2, byte[] var3, int var4, int var5) throws IOException;

   private native void close0() throws IOException;

   private native void checkPermission0(boolean var1) throws IOException;

   private native String getLocalHost0();

   private native int getLocalPort0();
}
