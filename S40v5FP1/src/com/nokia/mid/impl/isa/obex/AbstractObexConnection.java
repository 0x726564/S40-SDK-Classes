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
   private Authenticator authenticator;
   private ServerRequestHandler requestHandler;
   private PasswordAuthentication passwordAuthentication;
   private byte[] myNonce;
   private byte[] itsAuthChallengeNonce;
   private boolean closed;
   private boolean inOperation;
   private long connectionID = -1L;
   private HeaderSetImpl inHeaders;
   private HeaderSetImpl outHeaders;
   private boolean expectingAuthResp = false;
   private Packet lastPacket;
   private Packet incomingPacket;
   public StreamConnection conn;
   private InputStream is;
   private OutputStream os;
   private boolean connIdInNextPacket = false;
   private int lastPacketSort;

   public long getConnectionID() {
      return this.connectionID;
   }

   public void setConnectionID(long id) {
      if (id >= 0L && id <= 4294967295L) {
         this.connectionID = id;
      } else {
         throw new IllegalArgumentException("Invalid connection ID (" + id + ")");
      }
   }

   protected HeaderSetImpl getIncomingHeaders() {
      return this.inHeaders;
   }

   protected void setIncomingHeaders(HeaderSetImpl headers) {
      this.inHeaders = headers;
   }

   protected void setOutgoingHeaders(HeaderSetImpl headers) {
      this.outHeaders = headers;
   }

   protected HeaderSetImpl getOutgoingHeaders() {
      if (this.outHeaders == null) {
         this.outHeaders = new HeaderSetImpl();
      }

      return this.outHeaders;
   }

   protected AbstractObexConnection(StreamConnection conn, Authenticator authenticator) {
      this.conn = conn;

      try {
         this.is = conn.openInputStream();
         this.os = conn.openOutputStream();
      } catch (IOException var4) {
      }

      if (authenticator == null) {
         this.authenticator = null;
      } else {
         this.authenticator = authenticator;
      }

   }

   public void close() {
      if (!this.closed) {
         try {
            this.is.close();
            this.os.close();
            this.conn.close();
         } catch (IOException var2) {
         }

         this.closed = true;
      }
   }

   public boolean isClosed() {
      return this.closed;
   }

   public void setAuthenticator(Authenticator auth) {
      if (auth == null) {
         throw new NullPointerException("authenticator cannot be null");
      } else {
         this.authenticator = auth;
      }
   }

   ServerRequestHandler getServerRequestHandler() {
      if (this.isClient()) {
         throw new IllegalStateException("This is a client connection");
      } else {
         return this.requestHandler;
      }
   }

   void setServerRequestHandler(ServerRequestHandler requestHandler) {
      if (this.isClient()) {
         throw new IllegalStateException("This is a client connection");
      } else {
         this.requestHandler = requestHandler;
      }
   }

   private void processIncomingPacket() throws IOException {
      Packet p = this.incomingPacket;
      this.inHeaders = new HeaderSetImpl(p.serializedHeaders);
      Long connID = (Long)this.inHeaders.getHeaderPrivate(203);
      if (connID != null) {
         this.setConnectionID(connID);
      }

      if (this.expectingAuthResp) {
         if (p.authenticationResponse) {
            byte[] password = this.authenticator.onAuthenticationResponse(p.userName);
            if (!Packet.IsItsDigestValid(password, this.myNonce, p.itsAuthResponseNonce, p.digest)) {
               if (this.isClient()) {
                  throw new IOException("Server failed authentication");
               }

               this.requestHandler.onAuthenticationFailure(p.userName);
            }
         } else if (this.isClient()) {
            if (!p.authenticationChallenge) {
               throw new IOException("Server failed authentication");
            }

            if (this.authenticator != null) {
               this.passwordAuthentication = this.authenticator.onAuthenticationChallenge(p.realm, p.userIdRequired, p.isFullAccess);
            }
         } else {
            this.requestHandler.onAuthenticationFailure(p.userName);
         }

         this.expectingAuthResp = false;
      } else if (p.authenticationChallenge && this.authenticator != null) {
         this.passwordAuthentication = this.authenticator.onAuthenticationChallenge(p.realm, p.userIdRequired, p.isFullAccess);
         this.itsAuthChallengeNonce = p.itsAuthChallengeNonce;
         if (this.isClient()) {
            Packet outP = this.lastPacket;
            outP.authenticationResponse = true;
            outP.itsAuthChallengeNonce = this.itsAuthChallengeNonce;
            outP.userName = this.passwordAuthentication.getUserName();
            outP.password = this.passwordAuthentication.getPassword();
            this.sendPacket(outP);
            this.incomingPacket = this.getPacket();
         }
      }

      if (this.incomingPacket.packetSort == 128 && p.maxPacketLength < 65535) {
         this.REMOTE_MAX_OBEX_PACKET_SIZE = p.maxPacketLength;
      }

   }

   protected synchronized Packet getPacket() throws IOException {
      boolean received = false;
      boolean assembling = false;
      byte[] incoming = new byte[this.REMOTE_MAX_OBEX_PACKET_SIZE];

      for(int count = 0; !received; Thread.yield()) {
         int read = this.readProtocol(incoming, count, incoming.length - count);
         if (read > 0) {
            if (!assembling) {
               assembling = true;
            }

            count += read;
            Packet p = Packet.decode(incoming, count);
            if (p != null) {
               if (this.isClient()) {
                  p.packetSort = this.lastPacketSort;
               }

               this.incomingPacket = p;
               this.processIncomingPacket();
               return this.incomingPacket;
            }
         } else if (assembling) {
            throw new IOException("Error receiving packet");
         }
      }

      return null;
   }

   protected synchronized void sendPacket(Packet p) throws IOException {
      if (!this.isClient()) {
         this.connectionID = this.requestHandler.getConnectionID();
         p.respCode = this.getOutgoingHeaders().getResponseCode();
      }

      if (this.connIdInNextPacket && this.connectionID != -1L) {
         this.getOutgoingHeaders().setHeaderPrivate(203, new Long(this.connectionID));
         this.connIdInNextPacket = false;
      }

      if (this.passwordAuthentication != null) {
         p.authenticationResponse = true;
         p.userName = this.passwordAuthentication.getUserName();
         p.password = this.passwordAuthentication.getPassword();
         p.itsAuthChallengeNonce = this.itsAuthChallengeNonce;
      }

      if (this.getOutgoingHeaders().containsAuthenticationChallenge()) {
         this.expectingAuthResp = true;
         p.authenticationChallenge = true;
         p.userIdRequired = this.getOutgoingHeaders().isUserId();
         p.realm = this.getOutgoingHeaders().getRealm();
         p.isFullAccess = this.getOutgoingHeaders().isFullAccess();
      }

      p.serializedHeaders = this.getOutgoingHeaders().serializeHeaders();
      p.isResp = !this.isClient();
      this.lastPacketSort = p.packetSort;
      p.maxPacketLength = this.REMOTE_MAX_OBEX_PACKET_SIZE;
      if (p.serializedHeaders != null && p.serializedHeaders.length > this.REMOTE_MAX_OBEX_PACKET_SIZE) {
         throw new IOException("Error packet too large to send.");
      } else {
         byte[] bytes = Packet.encode(p);
         if (bytes.length > this.REMOTE_MAX_OBEX_PACKET_SIZE) {
            throw new IOException("Error sending packet - length " + bytes.length);
         } else {
            if (p.authenticationChallenge) {
               this.myNonce = p.myNonce;
            }

            this.writeProtocol(bytes, 0, bytes.length);
            this.lastPacket = p;
            this.passwordAuthentication = null;
            this.outHeaders = null;
         }
      }
   }

   public abstract boolean isClient();

   protected boolean isInOperation() {
      return this.inOperation;
   }

   protected void setInOperation(boolean b) {
      if (b) {
         this.connIdInNextPacket = true;
      }

      this.inOperation = b;
   }

   private int readProtocol(byte[] b, int off, int len) throws IOException {
      return this.is.read(b, off, len);
   }

   private void writeProtocol(byte[] b, int off, int len) throws IOException {
      this.os.write(b, off, len);
      this.os.flush();
   }
}
