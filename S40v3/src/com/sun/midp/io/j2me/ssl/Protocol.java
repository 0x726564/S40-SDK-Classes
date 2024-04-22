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
            HttpUrl var4 = new HttpUrl("ssl", var1);
            if (var4.path == null && var4.query == null && var4.fragment == null && var4.host != null) {
               this.host = var4.host;
               this.port = var4.port;
               if (this.port < 0) {
                  throw new IllegalArgumentException("Missing port number");
               }

               synchronized(GeneralSharedIO.networkPermissionLock) {
                  this.checkPermission0();
               }

               int var5;
               synchronized(SocketCommon.socketLock) {
                  var5 = this.open0(this.host, this.port);
               }

               if (var5 <= 0) {
                  if (var5 == -23) {
                     throw new InterruptedIOException("timed out");
                  }

                  if (var5 <= -47 && var5 >= -54) {
                     byte var6;
                     switch(var5) {
                     case -54:
                        var6 = 7;
                        break;
                     case -53:
                     default:
                        var6 = 14;
                        break;
                     case -52:
                        var6 = 9;
                        break;
                     case -51:
                        var6 = 10;
                        break;
                     case -50:
                        var6 = 6;
                        break;
                     case -49:
                        var6 = 8;
                        break;
                     case -48:
                        var6 = 3;
                        break;
                     case -47:
                        var6 = 14;
                     }

                     throw new CertificateException(new NetworkCertificate(this.getBadCertificateID0()), var6);
                  }

                  throw new IOException("Error occured whilst opening connection");
               }

               this.handle = var5;
               this.connectionOpen = true;
               return this;
            }

            throw new IllegalArgumentException("Malformed address");
         }

         throw new IllegalArgumentException("Protocol must start with \"//\"");
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
         return new SslSecurityInfo(this.handle);
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
         var4 = this.write0(var1, var2, var3);
         if (var4 <= -1) {
            if (var4 == -23) {
               throw new InterruptedIOException("Secure write timed out");
            }

            if (var4 != -45) {
               throw new IOException("Secure write failed");
            }

            try {
               Thread.sleep(100L);
            } catch (InterruptedException var6) {
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
