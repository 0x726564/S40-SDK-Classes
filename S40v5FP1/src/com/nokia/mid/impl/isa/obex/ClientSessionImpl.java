package com.nokia.mid.impl.isa.obex;

import java.io.IOException;
import javax.microedition.io.StreamConnection;
import javax.obex.Authenticator;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;

public class ClientSessionImpl extends AbstractObexConnection implements ClientSession {
   private boolean connected = false;

   public ClientSessionImpl(StreamConnection conn) {
      super(conn, (Authenticator)null);
   }

   public HeaderSet createHeaderSet() {
      return new HeaderSetImpl();
   }

   public HeaderSet connect(HeaderSet headers) throws IOException {
      this.checkReadyForOperation(true, headers);

      HeaderSetImpl var3;
      try {
         this.setOutgoingHeaders((HeaderSetImpl)headers);
         Packet p = new Packet();
         p.packetSort = 128;
         this.sendPacket(p);
         p = this.getPacket();
         if (this.getIncomingHeaders().getResponseCode() == 160) {
            this.setConnected(true);
         }

         var3 = this.getIncomingHeaders();
      } finally {
         this.setInOperation(false);
      }

      return var3;
   }

   public HeaderSet disconnect(HeaderSet headers) throws IOException {
      this.checkReadyForOperation(false, headers);

      HeaderSetImpl var3;
      try {
         this.setOutgoingHeaders((HeaderSetImpl)headers);
         Packet p = new Packet();
         p.packetSort = 129;
         this.sendPacket(p);
         p = this.getPacket();
         var3 = this.getIncomingHeaders();
      } finally {
         this.setConnected(false);
         this.setInOperation(false);
      }

      return var3;
   }

   public boolean isClient() {
      return true;
   }

   public HeaderSet setPath(HeaderSet headers, boolean backup, boolean create) throws IOException {
      this.checkReadyForOperation(false, headers);

      HeaderSetImpl var5;
      try {
         this.setOutgoingHeaders((HeaderSetImpl)headers);
         Packet p = new Packet();
         p.packetSort = 133;
         p.isSetPathBackup = backup;
         p.isSetPathCreate = create;
         this.sendPacket(p);
         p = this.getPacket();
         var5 = this.getIncomingHeaders();
      } finally {
         this.setInOperation(false);
      }

      return var5;
   }

   public HeaderSet delete(HeaderSet headers) throws IOException {
      HeaderSetImpl var3;
      try {
         Operation operation = this.put(headers);
         operation.close();
         var3 = this.getIncomingHeaders();
      } finally {
         this.setInOperation(false);
      }

      return var3;
   }

   public Operation get(HeaderSet headers) throws IOException {
      this.checkReadyForOperation(false, headers);
      Packet reqP = new Packet();
      reqP.packetSort = 3;
      this.setOutgoingHeaders((HeaderSetImpl)headers);
      reqP.isFinal = true;
      this.sendPacket(reqP);
      Packet respP = this.getPacket();
      OperationImpl oper;
      if (respP.packetSort == 3 && (respP.respCode == 144 || !respP.isFinal || respP.respCode == 160 && respP.isFinal)) {
         oper = new OperationImpl(this, true);
      } else {
         oper = new OperationImpl(this);
      }

      return oper;
   }

   public Operation put(HeaderSet headers) throws IOException {
      this.checkReadyForOperation(false, headers);
      OperationImpl oper = new OperationImpl(this, false);
      if (headers != null) {
         oper.sendHeaders(headers);
      }

      return oper;
   }

   private synchronized void checkReadyForOperation(boolean connect, HeaderSet headers) throws IOException {
      if (this.isClosed()) {
         throw new IOException("Connection closed");
      } else if (this.isInOperation()) {
         throw new IOException("Client is already in an operation");
      } else if (connect && this.connected) {
         throw new IOException("Already connected");
      } else if (!connect && !this.connected) {
         throw new IOException("Not connected");
      } else {
         HeaderSetImpl hsimpl;
         try {
            hsimpl = (HeaderSetImpl)headers;
         } catch (ClassCastException var5) {
            throw new IllegalArgumentException("HeaderSet not created by a call to createHeaderSet");
         }

         if (hsimpl != null && hsimpl.isReceivedHeaderSet()) {
            throw new IllegalArgumentException("HeaderSet not created by a call to createHeaderSet");
         } else {
            this.setInOperation(true);
         }
      }
   }

   private synchronized void setConnected(boolean connected) {
      this.connected = connected;
   }
}
