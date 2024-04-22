package com.sun.midp.io.j2me.datagram;

import com.nokia.mid.impl.isa.io.GeneralSharedIO;
import com.sun.cldc.io.ConnectionBaseInterface;
import com.sun.midp.io.HttpUrl;
import java.io.IOException;
import java.io.InterruptedIOException;
import javax.microedition.io.Connection;
import javax.microedition.io.Datagram;
import javax.microedition.io.UDPDatagramConnection;

public class Protocol implements UDPDatagramConnection, ConnectionBaseInterface {
   private int handle = -1;
   private int mode;
   private boolean timeouts;
   private boolean copen = false;
   private String address;
   private String host;
   private int port;
   private int sendAddress;
   private int receivePort;
   private int receiveBytesRead;
   private String receiveAddr = null;
   private static final int MAX_ADDR_LEN = 256;
   private static final int MAX_LENGTH = 1450;
   private static final int MAX_NOMINAL_LENGTH = 1450;
   private static final int NETWORK_ERROR_CODE__FAILURE_ALLOCATING_NEW_MESSAGE_BLOCK = -2;
   private static final int NETWORK_ERROR_CODE__EMPTY_SCKT_NETWORKSTATUS_IND_MESSAGE = -5;
   private static final int NETWORK_ERROR_CODE__MAX_CONNECTION_ATTEMPTS_EXCEEDED = -7;
   private static final int NETWORK_ERROR_CODE__FAILURE_OPENING_NETWORK_LINK = -11;
   private static final int NETWORK_ERROR_CODE__FAILURE_UNPACKING_MESSAGE = -15;
   private static final int NETWORK_ERROR_CODE__PPP_NEGOTIATION_FAILED = -16;
   private static final int NETWORK_ERROR_CODE__MAX_NUM_SOCKETS_REACHED = -22;
   private static final int NETWORK_ERROR_CODE__OPERATION_TIMED_OUT = -23;
   private static final int NETWORK_ERROR_CODE__BEARER_NOT_AVAILABLE = -25;
   private static final int NETWORK_ERROR_CODE__NETWORK_DISCONNECTED = -27;
   private static final int NETWORK_ERROR_CODE__CONT_ACT_FAIL = -28;
   private static final int NETWORK_ERROR_CODE__CSD_NO_ANSWER = -29;
   private static final int NETWORK_ERROR_CODE__CSD_BUSY = -30;
   private static final int NETWORK_ERROR_CODE__BCS_BLOCKED_AND_CLOSED = -31;
   private static final int NETWORK_ERROR_CODE__NETWORK_UNREACHABLE = -34;
   private static final int NETWORK_ERROR_CODE__BEARER_NOT_AVAILABLE_SUSPENDED = -36;
   private static final int NETWORK_ERROR_CODE__NETWORK_CLOSING = -37;
   private static boolean tracing = false;

   public void open(String var1, int var2, boolean var3) throws IOException {
      throw new RuntimeException("Should not be called");
   }

   public Connection openPrim(String var1, int var2, boolean var3) throws IOException {
      if (var2 != 1 && var2 != 2 && var2 != 3) {
         throw new IllegalArgumentException("invalid mode");
      } else {
         this.mode = var2;
         this.timeouts = var3;
         this.address = "datagram:" + var1;
         Trace("Datagram openPrim: address=" + this.address);
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

               Trace("open0 returned: " + this.handle);
               if (this.handle > 0) {
                  this.copen = true;
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
      if (!this.copen) {
         throw new IOException("connection closed");
      }
   }

   public void close() throws IOException {
      if (this.copen) {
         this.copen = false;
         this.realClose();
      }

   }

   void realClose() throws IOException {
      synchronized(this.getClass()) {
         this.close0();
      }

      this.handle = -1;
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
      int var2 = var1.getLength();
      if (var2 < 0) {
         throw new IOException("Bad datagram length");
      } else {
         String var5 = var1.getAddress();
         if (var5 == null) {
            throw new IOException("No address in datagram");
         } else {
            HttpUrl var6 = new HttpUrl(var5);
            String var4 = var6.host;
            int var3 = var6.port;
            if (var4 == null) {
               throw new IOException("Missing host");
            } else if (var3 == -1) {
               throw new IOException("Missing port");
            } else {
               while(true) {
                  int var7;
                  try {
                     var7 = this.write0(var4, var3, var1.getData(), var1.getOffset(), var2);
                  } finally {
                     if (!this.copen) {
                        throw new InterruptedIOException("Socket closed");
                     }

                  }

                  if (var7 == var1.getLength()) {
                     return;
                  }

                  if (var7 != 0) {
                     throw new IOException("Failed to send datagram");
                  }

                  try {
                     Thread.sleep(100L);
                  } catch (InterruptedException var11) {
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
         this.receiveAddr = null;
         this.receiveBytesRead = this.read0(var1.getData(), var1.getOffset(), var1.getLength());
         if (this.receiveAddr != null && !this.receiveAddr.equals("") && this.receiveBytesRead >= -1) {
            String var2 = "datagram://" + this.receiveAddr + ":" + this.receivePort;
            var1.setAddress(var2);
            int var3 = var1.getOffset();
            var1.reset();
            var1.setData(var1.getData(), var3, this.receiveBytesRead);
            Trace("datagram receive, packet's src addres: " + this.receiveAddr + "\n");
         } else {
            Trace("receive: error: " + this.receiveBytesRead);
            if (this.receiveBytesRead == -23) {
               throw new InterruptedIOException("timed out");
            } else {
               throw new IOException("receive failed");
            }
         }
      } else {
         throw new IllegalArgumentException("Bad datagram length");
      }
   }

   public Datagram newDatagram(int var1) throws IOException {
      Trace("newDatagram size = " + var1);
      return this.createDatagram(true, (byte[])null, var1, true, (String)null);
   }

   public Datagram newDatagram(int var1, String var2) throws IOException {
      Trace("newDatagram size = " + var1 + ",address = " + var2);
      return this.createDatagram(true, (byte[])null, var1, false, var2);
   }

   public Datagram newDatagram(byte[] var1, int var2) throws IOException {
      if (var1 != null) {
         Trace("newDatagram size = " + var2 + ",buffer len = " + var1.length);
      }

      return this.createDatagram(false, var1, var2, true, (String)null);
   }

   public Datagram newDatagram(byte[] var1, int var2, String var3) throws IOException {
      Trace("newDatagram size = " + var2 + ",buffer len = " + var1.length + "addr = " + var3);
      return this.createDatagram(false, var1, var2, false, var3);
   }

   public String getLocalAddress() throws IOException {
      String var1 = "";
      boolean var2 = false;
      this.ensureOpen();

      while(!var2) {
         var1 = this.getLocalHost0();
         if (var1.equals("retry")) {
            try {
               Thread.sleep(100L);
            } catch (InterruptedException var4) {
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
            } catch (InterruptedException var3) {
               throw new IOException();
            }
         }
      }

      return var1;
   }

   private Datagram createDatagram(boolean var1, byte[] var2, int var3, boolean var4, String var5) throws IOException {
      this.ensureOpen();
      if (var3 < 0) {
         throw new IllegalArgumentException("Negative size");
      } else {
         byte[] var6;
         if (var1) {
            var6 = new byte[var3];
         } else {
            if (var2 == null) {
               throw new IllegalArgumentException("Invalid buffer");
            }

            if (var3 > var2.length) {
               throw new IllegalArgumentException("Size is larger than the buffer");
            }

            var6 = var2;
         }

         DatagramObject var7 = new DatagramObject(var6, var3);
         if (var4) {
            if (this.host != null) {
               try {
                  var7.setAddress("datagram://" + this.host + ":" + this.port);
               } catch (IllegalArgumentException var9) {
               }
            }
         } else {
            var7.setAddress(var5);
         }

         return var7;
      }
   }

   static void Trace(String var0) {
      if (tracing) {
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
