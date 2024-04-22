package com.nokia.mid.impl.isa.obex;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.io.Connection;
import javax.microedition.io.StreamConnection;
import javax.obex.Authenticator;
import javax.obex.ServerRequestHandler;

public class ClientConnectionHandler extends AbstractObexConnection implements Connection, Runnable {
   public ClientConnectionHandler(StreamConnection conn, Authenticator authenticator) {
      super(conn, authenticator);
   }

   public void startThread(ServerRequestHandler requestHandler) {
      this.setServerRequestHandler(requestHandler);
      (new Thread(this)).start();
   }

   public boolean isClient() {
      return false;
   }

   public void run() {
      for(; !this.isClosed(); Thread.yield()) {
         try {
            Packet p = this.getPacket();
            if (p != null) {
               this.setInOperation(true);
               this.processPacket(p);
               this.setInOperation(false);
            }
         } catch (Exception var2) {
         }
      }

   }

   private void processPacket(Packet p) throws IOException {
      switch(p.packetSort) {
      case 2:
      case 3:
         this.processMultiPacketOperation(p);
         break;
      case 128:
      case 129:
      case 133:
         this.processSinglePacketOperation(p);
         break;
      default:
         Packet respP = new Packet();
         respP.packetSort = p.packetSort;
         respP.isFinal = true;
         this.getOutgoingHeaders().setResponseCode(192);
         this.sendPacket(respP);
      }

   }

   private void processSinglePacketOperation(Packet reqP) {
      try {
         HeaderSetImpl reqHS = this.getIncomingHeaders();
         HeaderSetImpl respHS = this.getOutgoingHeaders();
         ServerRequestHandler requestHandler = this.getServerRequestHandler();
         int response;
         switch(reqP.packetSort) {
         case 128:
            response = requestHandler.onConnect(reqHS, respHS);
            respHS.setResponseCode(response);
            break;
         case 129:
            requestHandler.onDisconnect(reqHS, respHS);
            respHS.setResponseCode(160);
            break;
         case 133:
            response = requestHandler.onSetPath(reqHS, respHS, reqP.isSetPathBackup, reqP.isSetPathCreate);
            respHS.setResponseCode(response);
         }

         Packet respP = new Packet();
         respP.packetSort = reqP.packetSort;
         respP.isFinal = true;
         this.sendPacket(respP);
      } catch (IOException var7) {
      }

   }

   private void processMultiPacketOperation(Packet reqP) {
      Vector packets = null;

      HeaderSetImpl allHeaders;
      while(true) {
         allHeaders = this.getIncomingHeaders();
         Packet respP;
         if (reqP.isFinal) {
            if (reqP.packetSort == 255) {
               respP = new Packet();
               respP.isFinal = true;
               respP.packetSort = 255;
               this.getOutgoingHeaders().setResponseCode(160);

               try {
                  this.sendPacket(respP);
               } catch (IOException var10) {
               }

               return;
            }
            break;
         }

         if (reqP.packetSort == 2) {
            if (allHeaders.getHeaderPrivate(72) != null || allHeaders.getHeaderPrivate(73) != null) {
               break;
            }
         } else if (reqP.packetSort != 3) {
            respP = new Packet();
            respP.packetSort = 2;
            respP.isFinal = true;
            this.getOutgoingHeaders().setResponseCode(192);

            try {
               this.sendPacket(respP);
            } catch (IOException var13) {
            }

            return;
         }

         if (packets == null) {
            packets = new Vector();
         }

         packets.addElement(allHeaders);
         respP = new Packet();
         respP.packetSort = reqP.packetSort;
         respP.isFinal = true;
         this.getOutgoingHeaders().setResponseCodePrivate(144);

         try {
            this.sendPacket(respP);
            reqP = this.getPacket();
         } catch (IOException var14) {
            return;
         }
      }

      if (packets == null) {
         allHeaders = this.getIncomingHeaders();
      } else {
         allHeaders = new HeaderSetImpl();
         Enumeration e = packets.elements();

         while(e.hasMoreElements()) {
            allHeaders.includeHeaders((HeaderSetImpl)e.nextElement());
         }

         allHeaders.includeHeaders(this.getIncomingHeaders());
         this.setIncomingHeaders(allHeaders);
      }

      ServerRequestHandler requestHandler = this.getServerRequestHandler();
      int response;
      if (reqP.packetSort == 2 && reqP.isFinal) {
         try {
            if (allHeaders.getHeaderPrivate(72) == null && allHeaders.getHeaderPrivate(73) == null && allHeaders.getHeader(1) != null) {
               HeaderSetImpl respHS = this.getOutgoingHeaders();
               response = requestHandler.onDelete(allHeaders, respHS);
               Packet respP = new Packet();
               respP.packetSort = 2;
               respP.isFinal = true;
               respHS.setResponseCode(response);

               try {
                  this.sendPacket(respP);
               } catch (IOException var11) {
               }

               return;
            }
         } catch (IOException var12) {
         }
      }

      OperationImpl operation;
      if (reqP.packetSort == 2) {
         operation = new OperationImpl(this, false);
         response = requestHandler.onPut(operation);
      } else {
         operation = new OperationImpl(this, true);
         response = requestHandler.onGet(operation);
      }

      operation.finish(response);
   }
}
