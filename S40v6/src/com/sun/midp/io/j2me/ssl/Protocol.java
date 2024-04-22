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
   public Connection openPrim(String name, int mode, boolean timeouts) throws IOException {
      switch(mode) {
      case 1:
      case 2:
      case 3:
         if (name.charAt(0) == '/' && name.charAt(1) == '/') {
            HttpUrl url = new HttpUrl("ssl", name);
            if (url.path == null && url.query == null && url.fragment == null && url.host != null) {
               this.host = url.host;
               this.port = url.port;
               if (this.port < 0) {
                  throw new IllegalArgumentException("Missing port number");
               } else {
                  synchronized(GeneralSharedIO.networkPermissionLock) {
                     this.checkPermission0();
                  }

                  synchronized(SocketCommon.socketLock) {
                     int newHandle = this.open0(this.host, this.port);
                     if (newHandle <= 0) {
                        if (newHandle == -23) {
                           throw new InterruptedIOException("timed out");
                        }

                        if (newHandle <= -47 && newHandle >= -54) {
                           byte errorReason;
                           switch(newHandle) {
                           case -54:
                              errorReason = 7;
                              break;
                           case -53:
                           default:
                              errorReason = 14;
                              break;
                           case -52:
                              errorReason = 9;
                              break;
                           case -51:
                              errorReason = 10;
                              break;
                           case -50:
                              errorReason = 6;
                              break;
                           case -49:
                              errorReason = 8;
                              break;
                           case -48:
                              errorReason = 3;
                              break;
                           case -47:
                              errorReason = 14;
                           }

                           throw new CertificateException(new NetworkCertificate(this.getBadCertificateID0()), errorReason);
                        }

                        throw new IOException("Error occured whilst opening connection");
                     }

                     this.handle = newHandle;
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

   protected int nonBufferedRead(byte[] b, int off, int len) throws IOException {
      while(true) {
         int bytesRead;
         try {
            bytesRead = this.read0(b, off, len);
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

   public int writeBytes(byte[] b, int off, int len) throws IOException {
      int bytesWritten = 0;

      while(bytesWritten <= 0) {
         bytesWritten = this.write0(b, off, len);
         if (bytesWritten <= -1) {
            if (bytesWritten == -23) {
               throw new InterruptedIOException("Secure write timed out");
            }

            if (bytesWritten != -45) {
               throw new IOException("Secure write failed");
            }

            try {
               Thread.sleep(100L);
            } catch (InterruptedException var6) {
               throw new IOException("I/O failure");
            }
         }
      }

      return bytesWritten;
   }

   private native int read0(byte[] var1, int var2, int var3) throws IOException;

   private native int write0(byte[] var1, int var2, int var3) throws IOException;

   private native void close0() throws IOException;

   private native int open0(String var1, int var2) throws IOException;

   private native void checkPermission0() throws IOException;

   private native int getBadCertificateID0();
}
