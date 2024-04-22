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

   public Connection openPrim(String var1, int var2, boolean var3) throws IOException {
      if (var1.charAt(0) == '/' && var1.charAt(1) == '/') {
         HttpUrl var4;
         if ((var4 = new HttpUrl("socket", var1)).path == null && var4.query == null && var4.fragment == null) {
            this.host = var4.host;
            this.port = var4.port;
            if (this.host != null) {
               return super.openPrim(var1, var2, var3);
            } else {
               Socket var5;
               (var5 = new Socket()).open(this.port);
               return var5;
            }
         } else {
            throw new IllegalArgumentException("Malformed address");
         }
      } else {
         throw new IllegalArgumentException("Protocol must start with \"//\"");
      }
   }

   public void connect(String var1, int var2, boolean var3) throws IOException {
      boolean var5 = false;
      this.verifyPermissionCheck();
      if (this.port >= 1 && this.port <= 65535) {
         int var6;
         synchronized(SocketCommon.socketLock) {
            var6 = this.open0(this.host, this.port);
         }

         if (var6 > 0) {
            this.handle = var6;
         } else if (System.getProperty("sprintpcs.profiles") == null || !System.getProperty("sprintpcs.profiles").equals("SPRINTPCS-1.0") || var6 != -23 && var6 != -33 && var6 != -34) {
            if (var6 == -23) {
               throw new InterruptedIOException("timed out");
            } else if (var6 == -33) {
               throw new ConnectionNotFoundException("Connection refused");
            } else if (var6 != -18 && var6 != -60) {
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

   public void open(int var1) {
      this.handle = var1;

      try {
         this.connectionOpen = true;
         this.checkForPermission("");
      } catch (Exception var3) {
         Object var4 = var3;
         this.connectionOpen = false;
         if (var3 instanceof IOException) {
            var4 = new SecurityException("Unknown TCP client");
         }

         try {
            this.close0();
         } catch (IOException var2) {
         }

         throw (RuntimeException)var4;
      }

      this.registerCleanup0();
   }

   public void disconnect() throws IOException {
      this.close0();
   }

   protected int nonBufferedRead(byte[] var1, int var2, int var3) throws IOException {
      int var4 = 0;
      this.d();
      if (var1 == null) {
         throw new NullPointerException("read failed");
      } else if (var2 >= 0 && var3 >= 0 && var1.length >= var2 + var3) {
         if (var3 == 0) {
            return 0;
         } else {
            while(true) {
               try {
                  do {
                     int var5;
                     if (var3 > 1400) {
                        var5 = this.read0(var1, var2, 1400, false);
                     } else {
                        var5 = this.read0(var1, var2, var3, false);
                     }

                     if (var5 <= 0) {
                        if (var4 == 0) {
                           var4 = var5;
                        }
                        break;
                     }

                     var4 += var5;
                     var2 += var5;
                     var3 -= var5;
                  } while(var3 > 0);
               } finally {
                  if (this.iStreams == 0) {
                     throw new InterruptedIOException("Stream closed");
                  }

               }

               if (var4 == -1) {
                  this.eof = true;
                  return -1;
               }

               if (var4 > 0) {
                  return var4;
               }

               if (var4 <= -2) {
                  if (var4 == -23) {
                     throw new InterruptedIOException("timed out");
                  }

                  throw new IOException("read failed");
               }

               try {
                  Thread.sleep(100L);
               } catch (InterruptedException var8) {
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
         boolean var1 = false;
         this.d();
         int var3;
         if ((var3 = this.read0((byte[])null, 0, 0, true)) <= -2) {
            if (var3 == -23) {
               throw new InterruptedIOException("timed out");
            } else {
               throw new IOException("available failed");
            }
         } else {
            if (var3 == 0) {
               try {
                  Thread.sleep(100L);
               } catch (InterruptedException var2) {
               }
            }

            return var3;
         }
      }
   }

   public int writeBytes(byte[] var1, int var2, int var3) throws IOException {
      int var4 = 0;
      int var5 = 0;
      if (!this.connectionOpen && this.oStreams == 0) {
         throw new IOException("OStream closed");
      } else if (var1 == null) {
         throw new NullPointerException("write failed");
      } else if (var2 >= 0 && var3 >= 0 && var1.length >= var2 + var3) {
         if (var3 == 0) {
            return 0;
         } else {
            while(var5 >= 0 && var3 > 0) {
               if (var3 > 1400) {
                  var5 = this.write0(var1, var2, 1400);
               } else {
                  var5 = this.write0(var1, var2, var3);
               }

               if (var5 > 0) {
                  var3 -= var5;
                  var2 += var5;
                  var4 += var5;
               }
            }

            if (var5 <= -1) {
               if (var5 == -23) {
                  throw new InterruptedIOException("timed out");
               }

               if (var5 != -45) {
                  throw new IOException("write failed");
               }

               try {
                  Thread.sleep(100L);
               } catch (InterruptedException var7) {
                  throw new IOException("I/O failure");
               }
            }

            return var4;
         }
      } else {
         throw new ArrayIndexOutOfBoundsException("write failed");
      }
   }

   private static void a(byte var0) throws IllegalArgumentException {
      if (var0 != 2 && var0 != 1 && var0 != 4 && var0 != 3 && var0 != 0) {
         throw new IllegalArgumentException("Unsupported Socket Option");
      }
   }

   public synchronized void setSocketOption(byte var1, int var2) throws IllegalArgumentException, IOException {
      int var3 = -2;
      a(var1);
      if (var2 < 0) {
         throw new IllegalArgumentException("Unsupported Socket Option");
      } else {
         this.ensureOpen();

         while(var3 <= -2) {
            if ((var3 = this.setSockOpt0(var1, var2)) <= -2) {
               if (var3 != -45) {
                  return;
               }

               try {
                  Thread.sleep(100L);
               } catch (InterruptedException var4) {
                  return;
               }
            }
         }

      }
   }

   public synchronized int getSocketOption(byte var1) throws IllegalArgumentException, IOException {
      int var2 = -2;
      a(var1);
      this.ensureOpen();

      while(var2 < 0) {
         if ((var2 = this.getSockOpt0(var1)) < 0) {
            if (var2 != -45) {
               return -1;
            }

            try {
               Thread.sleep(100L);
            } catch (InterruptedException var3) {
               return -1;
            }
         }
      }

      return var2;
   }

   public String getLocalAddress() throws IOException {
      String var1 = null;
      boolean var2 = false;
      this.ensureOpen();

      while(!var2) {
         if ((var1 = this.getLocalAddress0()).equals("retry")) {
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

   public String getAddress() throws IOException {
      this.ensureOpen();
      return this.host;
   }

   public int getPort() throws IOException {
      this.ensureOpen();
      return this.port;
   }

   private void d() throws IOException {
      if (!this.connectionOpen && this.iStreams == 0) {
         throw new IOException("IStream closed");
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
