package com.nokia.mid.impl.isa.obex;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.StreamConnection;
import javax.obex.Authenticator;
import javax.obex.PasswordAuthentication;
import javax.obex.ServerRequestHandler;

public abstract class AbstractObexConnection {
   public static final int MAX_OBEX_PACKET_SIZE = 65535;
   public int REMOTE_MAX_OBEX_PACKET_SIZE = 65535;
   private Authenticator D;
   private ServerRequestHandler F;
   private PasswordAuthentication G;
   private byte[] myNonce;
   private byte[] itsAuthChallengeNonce;
   private boolean closed;
   private boolean H;
   private long I = -1L;
   private HeaderSetImpl J;
   private HeaderSetImpl K;
   private boolean L = false;
   private Packet M;
   private Packet N;
   private StreamConnection O;
   private InputStream is;
   private OutputStream os;
   private boolean P = false;
   private int Q;

   public long getConnectionID() {
      return this.I;
   }

   public void setConnectionID(long var1) {
      if (var1 >= 0L && var1 <= 4294967295L) {
         this.I = var1;
      } else {
         throw new IllegalArgumentException("Invalid connection ID (" + var1 + ")");
      }
   }

   protected HeaderSetImpl getIncomingHeaders() {
      return this.J;
   }

   protected void setIncomingHeaders(HeaderSetImpl var1) {
      this.J = var1;
   }

   protected void setOutgoingHeaders(HeaderSetImpl var1) {
      this.K = var1;
   }

   protected HeaderSetImpl getOutgoingHeaders() {
      if (this.K == null) {
         this.K = new HeaderSetImpl();
      }

      return this.K;
   }

   protected AbstractObexConnection(StreamConnection var1, Authenticator var2) {
      this.O = var1;

      try {
         this.is = var1.openInputStream();
         this.os = var1.openOutputStream();
      } catch (IOException var3) {
      }

      if (var2 == null) {
         this.D = null;
      } else {
         this.D = var2;
      }
   }

   public void close() {
      if (!this.closed) {
         try {
            this.is.close();
            this.os.close();
            this.O.close();
         } catch (IOException var1) {
         }

         this.closed = true;
      }
   }

   public boolean isClosed() {
      return this.closed;
   }

   public void setAuthenticator(Authenticator var1) {
      if (var1 == null) {
         throw new NullPointerException("authenticator cannot be null");
      } else {
         this.D = var1;
      }
   }

   ServerRequestHandler getServerRequestHandler() {
      if (this.isClient()) {
         throw new IllegalStateException("This is a client connection");
      } else {
         return this.F;
      }
   }

   void setServerRequestHandler(ServerRequestHandler var1) {
      if (this.isClient()) {
         throw new IllegalStateException("This is a client connection");
      } else {
         this.F = var1;
      }
   }

   protected synchronized Packet getPacket() throws IOException {
      boolean var1 = false;
      byte[] var2 = new byte[this.REMOTE_MAX_OBEX_PACKET_SIZE];
      int var3 = 0;

      while(true) {
         int var7 = var2.length - var3;
         int var4;
         if ((var4 = this.is.read(var2, var3, var7)) > 0) {
            if (!var1) {
               var1 = true;
            }

            var3 += var4;
            Packet var8;
            if ((var8 = Packet.decode(var2, var3)) != null) {
               if (this.isClient()) {
                  var8.packetSort = this.Q;
               }

               this.N = var8;
               Packet var5 = this.N;
               this.J = new HeaderSetImpl(var5.serializedHeaders);
               Long var6;
               if ((var6 = (Long)this.J.getHeaderPrivate(203)) != null) {
                  this.setConnectionID(var6);
               }

               if (this.L) {
                  if (var5.authenticationResponse) {
                     if (!Packet.IsItsDigestValid(this.D.onAuthenticationResponse(var5.userName), this.myNonce, var5.itsAuthResponseNonce, var5.digest)) {
                        if (this.isClient()) {
                           throw new IOException("Server failed authentication");
                        }

                        this.F.onAuthenticationFailure(var5.userName);
                     }
                  } else if (this.isClient()) {
                     if (!var5.authenticationChallenge) {
                        throw new IOException("Server failed authentication");
                     }

                     if (this.D != null) {
                        this.G = this.D.onAuthenticationChallenge(var5.realm, var5.userIdRequired, var5.isFullAccess);
                     }
                  } else {
                     this.F.onAuthenticationFailure(var5.userName);
                  }

                  this.L = false;
               } else if (var5.authenticationChallenge && this.D != null) {
                  this.G = this.D.onAuthenticationChallenge(var5.realm, var5.userIdRequired, var5.isFullAccess);
                  this.itsAuthChallengeNonce = var5.itsAuthChallengeNonce;
                  if (this.isClient()) {
                     Packet var9;
                     (var9 = this.M).authenticationResponse = true;
                     var9.itsAuthChallengeNonce = this.itsAuthChallengeNonce;
                     var9.userName = this.G.getUserName();
                     var9.password = this.G.getPassword();
                     this.sendPacket(var9);
                     this.N = this.getPacket();
                  }
               }

               if (this.N.packetSort == 128 && var5.maxPacketLength < 65535) {
                  this.REMOTE_MAX_OBEX_PACKET_SIZE = var5.maxPacketLength;
               }

               return this.N;
            }
         } else if (var1) {
            throw new IOException("Error receiving packet");
         }

         Thread.yield();
      }
   }

   protected synchronized void sendPacket(Packet var1) throws IOException {
      if (!this.isClient()) {
         this.I = this.F.getConnectionID();
         var1.respCode = this.getOutgoingHeaders().getResponseCode();
      }

      if (this.P && this.I != -1L) {
         this.getOutgoingHeaders().setHeaderPrivate(203, new Long(this.I));
         this.P = false;
      }

      if (this.G != null) {
         var1.authenticationResponse = true;
         var1.userName = this.G.getUserName();
         var1.password = this.G.getPassword();
         var1.itsAuthChallengeNonce = this.itsAuthChallengeNonce;
      }

      if (this.getOutgoingHeaders().containsAuthenticationChallenge()) {
         this.L = true;
         var1.authenticationChallenge = true;
         var1.userIdRequired = this.getOutgoingHeaders().isUserId();
         var1.realm = this.getOutgoingHeaders().getRealm();
         var1.isFullAccess = this.getOutgoingHeaders().isFullAccess();
      }

      var1.serializedHeaders = this.getOutgoingHeaders().serializeHeaders();
      var1.isResp = !this.isClient();
      this.Q = var1.packetSort;
      var1.maxPacketLength = this.REMOTE_MAX_OBEX_PACKET_SIZE;
      byte[] var2;
      if ((var2 = Packet.encode(var1)).length > this.REMOTE_MAX_OBEX_PACKET_SIZE) {
         throw new IOException("Error sending packet - length " + var2.length);
      } else {
         if (var1.authenticationChallenge) {
            this.myNonce = var1.myNonce;
         }

         byte[] var10001 = var2;
         int var4 = var2.length;
         boolean var5 = false;
         byte[] var3 = var10001;
         this.os.write(var3, 0, var4);
         this.os.flush();
         this.M = var1;
         this.G = null;
         this.K = null;
      }
   }

   public abstract boolean isClient();

   protected boolean isInOperation() {
      return this.H;
   }

   protected void setInOperation(boolean var1) {
      if (var1) {
         this.P = true;
      }

      this.H = var1;
   }
}
