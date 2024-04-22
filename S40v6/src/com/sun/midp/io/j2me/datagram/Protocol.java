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

   public void open(String name, int mode, boolean timeouts) throws IOException {
      throw new RuntimeException("Should not be called");
   }

   public Connection openPrim(String name, int mode, boolean timeouts) throws IOException {
      if (mode != 1 && mode != 2 && mode != 3) {
         throw new IllegalArgumentException("invalid mode");
      } else {
         this.mode = mode;
         this.timeouts = timeouts;
         this.address = "datagram:" + name;
         Trace("Datagram openPrim: address=" + this.address);
         this.host = null;
         this.port = -1;
         if (name.charAt(0) == '/' && name.charAt(1) == '/') {
            name = ":" + name;
            HttpUrl url = new HttpUrl(name);
            this.host = url.host;
            this.port = url.port;
            if (this.host != null && this.port == -1) {
               throw new IllegalArgumentException("Missing port number");
            } else {
               synchronized(GeneralSharedIO.networkPermissionLock) {
                  this.checkPermission0(this.host == null);
               }

               synchronized(SocketCommon.socketLock) {
                  this.handle = this.open0(this.host, this.port, mode, timeouts);
               }

               Trace("open0 returned: " + this.handle);
               if (this.handle > 0) {
                  this.copen = true;
                  return this;
               } else if (this.handle == -23 && timeouts) {
                  throw new InterruptedIOException("timed out");
               } else if (this.handle == -22) {
                  throw new IOException("Maximum number of sockets reached");
               } else {
                  throw new IOException("Error occured whilst opening socket");
               }
            }
         } else {
            throw new IllegalArgumentException("Protocol must start with \"//\" " + name);
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

   public void send(Datagram dgram) throws IOException {
      this.ensureOpen();
      int length = dgram.getLength();
      if (length < 0) {
         throw new IOException("Bad datagram length");
      } else {
         String addr = dgram.getAddress();
         if (addr == null) {
            throw new IOException("No address in datagram");
         } else {
            HttpUrl url = new HttpUrl(addr);
            String host = url.host;
            int port = url.port;
            if (host == null) {
               throw new IOException("Missing host");
            } else if (port == -1) {
               throw new IOException("Missing port");
            } else {
               while(true) {
                  int res;
                  try {
                     res = this.write0(host, port, dgram.getData(), dgram.getOffset(), length);
                  } finally {
                     if (!this.copen) {
                        throw new InterruptedIOException("Socket closed");
                     }

                  }

                  if (res == dgram.getLength()) {
                     return;
                  }

                  if (res != 0) {
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

   public void receive(Datagram dgram) throws IOException {
      this.ensureOpen();
      if (dgram.getLength() <= 1450 && dgram.getLength() != 0) {
         this.receiveAddr = null;
         this.receiveBytesRead = this.read0(dgram.getData(), dgram.getOffset(), dgram.getLength());
         if (this.receiveAddr != null && !this.receiveAddr.equals("") && this.receiveBytesRead >= -1) {
            String addr = "datagram://" + this.receiveAddr + ":" + this.receivePort;
            dgram.setAddress(addr);
            int offset = dgram.getOffset();
            dgram.reset();
            dgram.setData(dgram.getData(), offset, this.receiveBytesRead);
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

   public Datagram newDatagram(int size) throws IOException {
      Trace("newDatagram size = " + size);
      return this.createDatagram(true, (byte[])null, size, true, (String)null);
   }

   public Datagram newDatagram(int size, String addr) throws IOException {
      Trace("newDatagram size = " + size + ",address = " + addr);
      return this.createDatagram(true, (byte[])null, size, false, addr);
   }

   public Datagram newDatagram(byte[] buf, int size) throws IOException {
      if (buf != null) {
         Trace("newDatagram size = " + size + ",buffer len = " + buf.length);
      }

      return this.createDatagram(false, buf, size, true, (String)null);
   }

   public Datagram newDatagram(byte[] buf, int size, String addr) throws IOException {
      Trace("newDatagram size = " + size + ",buffer len = " + buf.length + "addr = " + addr);
      return this.createDatagram(false, buf, size, false, addr);
   }

   public String getLocalAddress() throws IOException {
      String address = "";
      boolean done = false;
      this.ensureOpen();

      while(!done) {
         address = this.getLocalHost0();
         if (address.equals("retry")) {
            try {
               Thread.sleep(100L);
            } catch (InterruptedException var4) {
               throw new IOException();
            }
         } else {
            if (address.equals("")) {
               throw new IOException();
            }

            done = true;
         }
      }

      return address;
   }

   public int getLocalPort() throws IOException {
      int retVal = -2;
      this.ensureOpen();

      while(retVal <= 0) {
         if ((retVal = this.getLocalPort0()) <= 0) {
            if (retVal != -45) {
               throw new IOException();
            }

            try {
               Thread.sleep(100L);
            } catch (InterruptedException var3) {
               throw new IOException();
            }
         }
      }

      return retVal;
   }

   private Datagram createDatagram(boolean createBuffer, byte[] buf, int size, boolean defaultAddress, String addr) throws IOException {
      this.ensureOpen();
      if (size < 0) {
         throw new IllegalArgumentException("Negative size");
      } else {
         byte[] buffer;
         if (createBuffer) {
            buffer = new byte[size];
         } else {
            if (buf == null) {
               throw new IllegalArgumentException("Invalid buffer");
            }

            if (size > buf.length) {
               throw new IllegalArgumentException("Size is larger than the buffer");
            }

            buffer = buf;
         }

         Datagram newDatagram = new DatagramObject(buffer, size);
         if (defaultAddress) {
            if (this.host != null) {
               try {
                  newDatagram.setAddress("datagram://" + this.host + ":" + this.port);
               } catch (IllegalArgumentException var9) {
               }
            }
         } else {
            newDatagram.setAddress(addr);
         }

         return newDatagram;
      }
   }

   static void Trace(String trace) {
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
