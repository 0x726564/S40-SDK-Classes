package com.sun.midp.io.j2me.socket;

import com.sun.midp.io.HttpUrl;
import com.sun.midp.io.NetworkConnectionBase;
import com.sun.midp.io.j2me.datagram.SocketCommon;
import com.sun.midp.io.j2me.serversocket.Socket;
import java.io.IOException;
import java.io.InterruptedIOException;
import javax.microedition.io.Connection;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.SocketConnection;

public class Protocol extends NetworkConnectionBase implements SocketConnection {
   protected static int bufferSize;
   protected String host;
   protected int port;

   public Protocol() {
      super(bufferSize);
      this.protocol = "javax.microedition.io.Connector.socket";
   }

   public Connection openPrim(String name, int mode, boolean timeouts) throws IOException {
      if (name.charAt(0) == '/' && name.charAt(1) == '/') {
         HttpUrl url = new HttpUrl("socket", name);
         if (url.path == null && url.query == null && url.fragment == null) {
            this.host = url.host;
            this.port = url.port;
            if (this.host != null) {
               return super.openPrim(name, mode, timeouts);
            } else {
               Socket con = new Socket();
               con.open(this.port);
               return con;
            }
         } else {
            throw new IllegalArgumentException("Malformed address");
         }
      } else {
         throw new IllegalArgumentException("Protocol must start with \"//\"");
      }
   }

   public void connect(String name, int mode, boolean timeouts) throws IOException {
      int newHandle = false;
      this.verifyPermissionCheck();
      if (this.port >= 1 && this.port <= 65535) {
         int newHandle;
         synchronized(SocketCommon.socketLock) {
            newHandle = this.open0(this.host, this.port);
         }

         if (newHandle > 0) {
            this.handle = newHandle;
         } else if (System.getProperty("sprintpcs.profiles") == null || !System.getProperty("sprintpcs.profiles").equals("SPRINTPCS-1.0") || newHandle != -23 && newHandle != -33 && newHandle != -34) {
            if (newHandle == -23) {
               throw new InterruptedIOException("timed out");
            } else if (newHandle == -33) {
               throw new ConnectionNotFoundException("Connection refused");
            } else if (newHandle != -18 && newHandle != -60) {
               throw new IOException("Error occured whilst opening connection");
            } else {
               throw new ConnectionNotFoundException("Bad host name");
            }
         } else {
            throw new IOException("Error Code #4000 - Network timeout");
         }
      } else {
         throw new IllegalArgumentException("Missing or invalid port number");
      }
   }

   public void open(int handle) {
      this.handle = handle;

      try {
         this.connectionOpen = true;
         this.checkForPermission("");
      } catch (Exception var5) {
         Exception e = var5;
         this.connectionOpen = false;
         if (var5 instanceof IOException) {
            e = new SecurityException("Unknown TCP client");
         }

         try {
            this.close0();
         } catch (IOException var4) {
         }

         throw (RuntimeException)e;
      }

      this.registerCleanup0();
   }

   public void disconnect() throws IOException {
      this.close0();
   }

   protected int nonBufferedRead(byte[] b, int off, int len) throws IOException {
      int bytesRead = 0;
      this.ensureIStreamOpen();
      if (b == null) {
         throw new NullPointerException("read failed");
      } else if (off >= 0 && len >= 0 && b.length >= off + len) {
         if (len == 0) {
            return 0;
         } else {
            while(true) {
               try {
                  do {
                     int numBytesRead;
                     if (len > 1400) {
                        numBytesRead = this.read0(b, off, 1400, false);
                     } else {
                        numBytesRead = this.read0(b, off, len, false);
                     }

                     if (numBytesRead <= 0) {
                        if (bytesRead == 0) {
                           bytesRead = numBytesRead;
                        }
                        break;
                     }

                     bytesRead += numBytesRead;
                     off += numBytesRead;
                     len -= numBytesRead;
                  } while(len > 0);
               } finally {
                  if (this.iStreams == 0) {
                     throw new InterruptedIOException("Stream closed");
                  }

               }

               if (bytesRead == -1) {
                  this.eof = true;
                  return -1;
               }

               if (bytesRead > 0) {
                  return bytesRead;
               }

               if (bytesRead <= -2) {
                  if (bytesRead == -23) {
                     throw new InterruptedIOException("timed out");
                  }

                  throw new IOException("read failed");
               }

               try {
                  Thread.sleep(100L);
               } catch (InterruptedException var9) {
                  throw new IOException("I/O failure");
               }
            }
         }
      } else {
         throw new ArrayIndexOutOfBoundsException("read failed");
      }
   }

   public int available() throws IOException {
      if (this.count > 0) {
         return this.count;
      } else {
         int bytesAvailable = false;
         this.ensureIStreamOpen();
         int bytesAvailable = this.read0((byte[])null, 0, 0, true);
         if (bytesAvailable <= -2) {
            if (bytesAvailable == -23) {
               throw new InterruptedIOException("timed out");
            } else {
               throw new IOException("available failed");
            }
         } else {
            if (bytesAvailable == 0) {
               try {
                  Thread.sleep(100L);
               } catch (InterruptedException var3) {
               }
            }

            return bytesAvailable;
         }
      }
   }

   public int writeBytes(byte[] b, int off, int len) throws IOException {
      int bytesWritten = 0;
      int returnCode = 0;
      this.ensureOStreamOpen();
      if (b == null) {
         throw new NullPointerException("write failed");
      } else if (off >= 0 && len >= 0 && b.length >= off + len) {
         if (len == 0) {
            return 0;
         } else {
            while(returnCode >= 0 && len > 0) {
               if (len > 1400) {
                  returnCode = this.write0(b, off, 1400);
               } else {
                  returnCode = this.write0(b, off, len);
               }

               if (returnCode > 0) {
                  len -= returnCode;
                  off += returnCode;
                  bytesWritten += returnCode;
               }
            }

            if (returnCode <= -1) {
               if (returnCode == -23) {
                  throw new InterruptedIOException("timed out");
               }

               if (returnCode != -45) {
                  throw new IOException("write failed");
               }

               try {
                  Thread.sleep(100L);
               } catch (InterruptedException var7) {
                  throw new IOException("I/O failure");
               }
            }

            return bytesWritten;
         }
      } else {
         throw new ArrayIndexOutOfBoundsException("write failed");
      }
   }

   private void checkOption(byte option) throws IllegalArgumentException {
      if (option != 2 && option != 1 && option != 4 && option != 3 && option != 0) {
         throw new IllegalArgumentException("Unsupported Socket Option");
      }
   }

   public synchronized void setSocketOption(byte option, int value) throws IllegalArgumentException, IOException {
      int retVal = -2;
      this.checkOption(option);
      if (value < 0) {
         throw new IllegalArgumentException("Unsupported Socket Option");
      } else {
         this.ensureOpen();

         while(retVal <= -2) {
            if ((retVal = this.setSockOpt0(option, value)) <= -2) {
               if (retVal != -45) {
                  return;
               }

               try {
                  Thread.sleep(100L);
               } catch (InterruptedException var5) {
                  return;
               }
            }
         }

      }
   }

   public synchronized int getSocketOption(byte option) throws IllegalArgumentException, IOException {
      int retVal = -2;
      this.checkOption(option);
      this.ensureOpen();

      while(retVal < 0) {
         if ((retVal = this.getSockOpt0(option)) < 0) {
            if (retVal != -45) {
               return -1;
            }

            try {
               Thread.sleep(100L);
            } catch (InterruptedException var4) {
               return -1;
            }
         }
      }

      return retVal;
   }

   public String getLocalAddress() throws IOException {
      String address = "";
      boolean done = false;
      this.ensureOpen();

      while(!done) {
         address = this.getLocalAddress0();
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

   public String getAddress() throws IOException {
      this.ensureOpen();
      return this.host;
   }

   public int getPort() throws IOException {
      this.ensureOpen();
      return this.port;
   }

   private void ensureIStreamOpen() throws IOException {
      if (!this.connectionOpen && this.iStreams == 0) {
         throw new IOException("IStream closed");
      }
   }

   private void ensureOStreamOpen() throws IOException {
      if (!this.connectionOpen && this.oStreams == 0) {
         throw new IOException("OStream closed");
      }
   }

   private native int open0(String var1, int var2) throws IOException;

   private native int read0(byte[] var1, int var2, int var3, boolean var4) throws IOException;

   private native int write0(byte[] var1, int var2, int var3) throws IOException;

   private native void close0() throws IOException;

   private native void registerCleanup0();

   private native String getLocalAddress0();

   private native int getLocalPort0();

   private native int getSockOpt0(int var1);

   private native int setSockOpt0(int var1, int var2);
}
