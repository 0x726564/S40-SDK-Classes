package com.nokia.mid.impl.isa.obex;

import java.io.IOException;
import javax.microedition.io.StreamConnection;
import javax.obex.Authenticator;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;

public class ClientSessionImpl extends AbstractObexConnection implements ClientSession {
   private boolean connected = false;

   public ClientSessionImpl(StreamConnection var1) {
      super(var1, (Authenticator)null);
   }

   public HeaderSet createHeaderSet() {
      return new HeaderSetImpl();
   }

   public HeaderSet connect(HeaderSet var1) throws IOException {
      this.a(true, var1);

      HeaderSetImpl var5;
      try {
         this.setOutgoingHeaders((HeaderSetImpl)var1);
         Packet var4;
         (var4 = new Packet()).packetSort = 128;
         this.sendPacket(var4);
         this.getPacket();
         if (this.getIncomingHeaders().getResponseCode() == 160) {
            this.setConnected(true);
         }

         var5 = this.getIncomingHeaders();
      } finally {
         this.setInOperation(false);
      }

      return var5;
   }

   public HeaderSet disconnect(HeaderSet var1) throws IOException {
      this.a(false, var1);

      HeaderSetImpl var5;
      try {
         this.setOutgoingHeaders((HeaderSetImpl)var1);
         Packet var4;
         (var4 = new Packet()).packetSort = 129;
         this.sendPacket(var4);
         this.getPacket();
         var5 = this.getIncomingHeaders();
      } finally {
         this.setConnected(false);
         this.setInOperation(false);
      }

      return var5;
   }

   public boolean isClient() {
      return true;
   }

   public HeaderSet setPath(HeaderSet var1, boolean var2, boolean var3) throws IOException {
      this.a(false, var1);

      HeaderSetImpl var7;
      try {
         this.setOutgoingHeaders((HeaderSetImpl)var1);
         Packet var6;
         (var6 = new Packet()).packetSort = 133;
         var6.isSetPathBackup = var2;
         var6.isSetPathCreate = var3;
         this.sendPacket(var6);
         this.getPacket();
         var7 = this.getIncomingHeaders();
      } finally {
         this.setInOperation(false);
      }

      return var7;
   }

   public HeaderSet delete(HeaderSet var1) throws IOException {
      HeaderSetImpl var4;
      try {
         this.put(var1).close();
         var4 = this.getIncomingHeaders();
      } finally {
         this.setInOperation(false);
      }

      return var4;
   }

   public Operation get(HeaderSet var1) throws IOException {
      this.a(false, var1);
      Packet var2;
      (var2 = new Packet()).packetSort = 3;
      this.setOutgoingHeaders((HeaderSetImpl)var1);
      var2.isFinal = true;
      this.sendPacket(var2);
      Packet var3;
      OperationImpl var4;
      if ((var3 = this.getPacket()).packetSort == 3 && (var3.respCode == 144 || !var3.isFinal || var3.respCode == 160 && var3.isFinal)) {
         var4 = new OperationImpl(this, true);
      } else {
         var4 = new OperationImpl(this);
      }

      return var4;
   }

   public Operation put(HeaderSet var1) throws IOException {
      this.a(false, var1);
      OperationImpl var2 = new OperationImpl(this, false);
      if (var1 != null) {
         var2.sendHeaders(var1);
      }

      return var2;
   }

   private synchronized void a(boolean var1, HeaderSet var2) throws IOException {
      if (this.isClosed()) {
         throw new IOException("Connection closed");
      } else if (this.isInOperation()) {
         throw new IOException("Client is already in an operation");
      } else if (var1 && this.connected) {
         throw new IOException("Already connected");
      } else if (!var1 && !this.connected) {
         throw new IOException("Not connected");
      } else {
         HeaderSetImpl var4;
         try {
            var4 = (HeaderSetImpl)var2;
         } catch (ClassCastException var3) {
            throw new IllegalArgumentException("HeaderSet not created by a call to createHeaderSet");
         }

         if (var4 != null && var4.isReceivedHeaderSet()) {
            throw new IllegalArgumentException("HeaderSet not created by a call to createHeaderSet");
         } else {
            this.setInOperation(true);
         }
      }
   }

   private synchronized void setConnected(boolean var1) {
      this.connected = var1;
   }
}
