package com.nokia.mid.impl.isa.obex;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.io.Connection;
import javax.microedition.io.StreamConnection;
import javax.obex.Authenticator;
import javax.obex.ServerRequestHandler;

public class ClientConnectionHandler extends AbstractObexConnection implements Runnable, Connection {
   public ClientConnectionHandler(StreamConnection var1, Authenticator var2) {
      super(var1, var2);
   }

   public void startThread(ServerRequestHandler var1) {
      this.setServerRequestHandler(var1);
      (new Thread(this)).start();
   }

   public boolean isClient() {
      return false;
   }

   public void run() {
      for(; !this.isClosed(); Thread.yield()) {
         try {
            Packet var1;
            if ((var1 = this.getPacket()) != null) {
               this.setInOperation(true);
               switch(var1.packetSort) {
               case 2:
               case 3:
                  this.b(var1);
                  break;
               case 128:
               case 129:
               case 133:
                  this.a(var1);
                  break;
               default:
                  Packet var3;
                  (var3 = new Packet()).packetSort = var1.packetSort;
                  var3.isFinal = true;
                  this.getOutgoingHeaders().setResponseCode(192);
                  this.sendPacket(var3);
               }

               this.setInOperation(false);
            }
         } catch (Exception var4) {
         }
      }

   }

   private void a(Packet var1) {
      try {
         HeaderSetImpl var2 = this.getIncomingHeaders();
         HeaderSetImpl var3 = this.getOutgoingHeaders();
         ServerRequestHandler var5 = this.getServerRequestHandler();
         int var4;
         switch(var1.packetSort) {
         case 128:
            var4 = var5.onConnect(var2, var3);
            var3.setResponseCode(var4);
            break;
         case 129:
            var5.onDisconnect(var2, var3);
            var3.setResponseCode(160);
            break;
         case 133:
            var4 = var5.onSetPath(var2, var3, var1.isSetPathBackup, var1.isSetPathCreate);
            var3.setResponseCode(var4);
         }

         Packet var7;
         (var7 = new Packet()).packetSort = var1.packetSort;
         var7.isFinal = true;
         this.sendPacket(var7);
      } catch (IOException var6) {
      }
   }

   private void b(Packet var1) {
      Vector var2 = null;

      HeaderSetImpl var3;
      while(true) {
         var3 = this.getIncomingHeaders();
         Packet var4;
         if (var1.isFinal) {
            if (var1.packetSort == 255) {
               (var4 = new Packet()).isFinal = true;
               var4.packetSort = 255;
               this.getOutgoingHeaders().setResponseCode(160);

               try {
                  this.sendPacket(var4);
                  return;
               } catch (IOException var6) {
                  return;
               }
            }
            break;
         }

         if (var1.packetSort == 2) {
            if (var3.getHeaderPrivate(72) != null || var3.getHeaderPrivate(73) != null) {
               break;
            }
         } else if (var1.packetSort != 3) {
            (var4 = new Packet()).packetSort = 2;
            var4.isFinal = true;
            this.getOutgoingHeaders().setResponseCode(192);

            try {
               this.sendPacket(var4);
               return;
            } catch (IOException var9) {
               return;
            }
         }

         if (var2 == null) {
            var2 = new Vector();
         }

         var2.addElement(var3);
         (var4 = new Packet()).packetSort = var1.packetSort;
         var4.isFinal = true;
         this.getOutgoingHeaders().setResponseCodePrivate(144);

         try {
            this.sendPacket(var4);
            var1 = this.getPacket();
         } catch (IOException var10) {
            return;
         }
      }

      if (var2 == null) {
         var3 = this.getIncomingHeaders();
      } else {
         var3 = new HeaderSetImpl();
         Enumeration var14 = var2.elements();

         while(var14.hasMoreElements()) {
            var3.includeHeaders((HeaderSetImpl)var14.nextElement());
         }

         var3.includeHeaders(this.getIncomingHeaders());
         this.setIncomingHeaders(var3);
      }

      ServerRequestHandler var12 = this.getServerRequestHandler();
      int var15;
      if (var1.packetSort == 2 && var1.isFinal) {
         try {
            if (var3.getHeaderPrivate(72) == null && var3.getHeaderPrivate(73) == null && var3.getHeader(1) != null) {
               HeaderSetImpl var5 = this.getOutgoingHeaders();
               var15 = var12.onDelete(var3, var5);
               Packet var13;
               (var13 = new Packet()).packetSort = 2;
               var13.isFinal = true;
               var5.setResponseCode(var15);

               try {
                  this.sendPacket(var13);
                  return;
               } catch (IOException var7) {
                  return;
               }
            }
         } catch (IOException var8) {
         }
      }

      OperationImpl var11;
      if (var1.packetSort == 2) {
         var11 = new OperationImpl(this, false);
         var15 = var12.onPut(var11);
      } else {
         var11 = new OperationImpl(this, true);
         var15 = var12.onGet(var11);
      }

      var11.finish(var15);
   }
}
