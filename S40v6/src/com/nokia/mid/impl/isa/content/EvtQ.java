package com.nokia.mid.impl.isa.content;

import javax.microedition.content.Registry;
import javax.microedition.content.RequestListener;
import javax.microedition.content.ResponseListener;

class EvtQ extends Thread {
   private CHAPIConsumer consumer;
   private Object[] destination;
   private Object[] target;
   private boolean[] type;
   private int headIdx = 0;
   private int tailIdx = 0;
   private static final int size = 12;

   EvtQ(CHAPIConsumer c) {
      this.consumer = c;
      this.destination = new Object[12];
      this.target = new Object[12];
      this.type = new boolean[12];
      this.start();
   }

   synchronized void sendRequestEvent(RequestListener rl, ContentHandlerServerImpl server) {
      this.waitForQueueToBecomeAvailable();
      this.destination[this.headIdx] = rl;
      this.target[this.headIdx] = server;
      this.type[this.headIdx] = true;
      this.notifyEventAvailable();
   }

   synchronized void sendResponseEvent(ResponseListener rl, RegistryImpl reg) {
      this.waitForQueueToBecomeAvailable();
      this.destination[this.headIdx] = rl;
      this.target[this.headIdx] = reg;
      this.type[this.headIdx] = false;
      this.notifyEventAvailable();
   }

   private void waitForQueueToBecomeAvailable() {
      while((this.headIdx + 1) % 12 == this.tailIdx) {
         try {
            this.wait();
         } catch (Exception var2) {
         }
      }

   }

   private void notifyEventAvailable() {
      if (++this.headIdx == 12) {
         this.headIdx = 0;
      }

      this.notify();
   }

   public void run() {
      Object evt = null;
      Object evtData = null;
      boolean evtToGo = false;
      boolean evtSent = false;
      boolean evtReq = false;

      do {
         synchronized(this) {
            if (this.headIdx == this.tailIdx) {
               try {
                  this.wait(5000L);
               } catch (Exception var11) {
               }
            }

            if (this.headIdx != this.tailIdx) {
               evt = this.destination[this.tailIdx];
               evtData = this.target[this.tailIdx];
               evtReq = this.type[this.tailIdx];
               this.target[this.tailIdx] = null;
               evtToGo = true;
               if (++this.tailIdx == 12) {
                  this.tailIdx = 0;
               }

               this.notify();
            } else {
               evtToGo = false;
            }
         }

         if (evtToGo) {
            try {
               if (evtReq) {
                  ContentHandlerServerImpl server = (ContentHandlerServerImpl)evtData;
                  server.unblockGetRequest();
                  ((RequestListener)evt).invocationRequestNotify(Registry.getServer(server.getClassName()));
               } else {
                  RegistryImpl reg = (RegistryImpl)evtData;
                  reg.unblockGetResponse();
                  ((ResponseListener)evt).invocationResponseNotify(Registry.getRegistry(reg.getClassName()));
               }
            } catch (Exception var12) {
            }

            evtSent = true;
         }
      } while(evtToGo || !evtSent);

      synchronized(this.consumer.evtLock) {
         this.consumer.evtQ = null;
      }
   }
}
