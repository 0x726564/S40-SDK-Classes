package com.sun.midp.io.j2me.ssl;

import com.nokia.mid.impl.isa.io.GeneralSharedIO;
import com.nokia.mid.impl.isa.io.SslSecurityInfo;
import com.nokia.mid.impl.isa.pki.NetworkCertificate;
import com.sun.midp.io.HttpUrl;
import com.sun.midp.io.j2me.datagram.SocketCommon;
import java.io.IOException;
import java.io.InterruptedIOException;
import javax.microedition.io.Connection;
import javax.microedition.io.SecureConnection;
import javax.microedition.io.SecurityInfo;
import javax.microedition.pki.CertificateException;

public class Protocol extends com.sun.midp.io.j2me.socket.Protocol implements SecureConnection {
   public Connection openPrim(String var1, int var2, boolean var3) throws IOException {
      switch(var2) {
      case 1:
      case 2:
      case 3:
         if (var1.charAt(0) == '/' && var1.charAt(1) == '/') {
            HttpUrl var6;
            if ((var6 = new HttpUrl("ssl", var1)).path == null && var6.query == null && var6.fragment == null && var6.host != null) {
               this.host = var6.host;
               this.port = var6.port;
               if (this.port < 0) {
                  throw new IllegalArgumentException("Missing port number");
               } else {
                  synchronized(GeneralSharedIO.networkPermissionLock) {
                     this.checkPermission0();
                  }

                  synchronized(SocketCommon.socketLock) {
                     int var7;
                     if ((var7 = this.open0(this.host, this.port)) <= 0) {
                        if (var7 == -23) {
                           throw new InterruptedIOException("timed out");
                        }

                        if (var7 <= -47 && var7 >= -54) {
                           byte var8;
                           switch(var7) {
                           case -54:
                              var8 = 7;
                              break;
                           case -53:
                           default:
                              var8 = 14;
                              break;
                           case -52:
                              var8 = 9;
                              break;
                           case -51:
                              var8 = 10;
                              break;
                           case -50:
                              var8 = 6;
                              break;
                           case -49:
                              var8 = 8;
                              break;
                           case -48:
                              var8 = 3;
                              break;
                           case -47:
                              var8 = 14;
                           }

                           throw new CertificateException(new NetworkCertificate(this.getBadCertificateID0()), var8);
                        }

                        throw new IOException("Error occured whilst opening connection");
                     }

                     this.handle = var7;
                  }

                  this.connectionOpen = true;
                  return this;
               }
            } else {
               throw new IllegalArgumentException("Malformed address");
            }
         } else {
            throw new IllegalArgumentException("Protocol must start with \"//\"");
         }
      default:
         throw new IllegalArgumentException("Illegal mode");
      }
   }

   public void disconnect() throws IOException {
      this.close0();
   }

   public SecurityInfo getSecurityInfo() throws IOException {
      if (!this.connectionOpen) {
         throw new IOException("getSecurityInfo : Connection not open");
      } else {
         return new SslSecurityInfo(this);
      }
   }

   protected int nonBufferedRead(byte[] var1, int var2, int var3) throws IOException {
      while(true) {
         int var4;
         try {
            var4 = this.read0(var1, var2, var3);
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
               throw new InterruptedIOException("Secure read timed out");
            }

            throw new IOException("Secure read failed");
         }

         try {
            Thread.sleep(100L);
         } catch (InterruptedException var8) {
            throw new IOException("I/O failure");
         }
      }
   }

   public int writeBytes(byte[] var1, int var2, int var3) throws IOException {
      int var4 = 0;

      while(var4 <= 0) {
         if ((var4 = this.write0(var1, var2, var3)) <= -1) {
            if (var4 == -23) {
               throw new InterruptedIOException("Secure write timed out");
            }

            if (var4 != -45) {
               throw new IOException("Secure write failed");
            }

            try {
               Thread.sleep(100L);
            } catch (InterruptedException var5) {
               throw new IOException("I/O failure");
            }
         }
      }

      return var4;
   }

   private native int read0(byte[] var1, int var2, int var3) throws IOException;

   private native int write0(byte[] var1, int var2, int var3) throws IOException;

   private native void close0() throws IOException;

   private native int open0(String var1, int var2) throws IOException;

   private native void checkPermission0() throws IOException;

   private native int getBadCertificateID0();
}
