package com.nokia.mid.impl.isa.content;

import com.nokia.mid.impl.isa.ui.EventConsumer;
import com.nokia.mid.impl.isa.ui.EventProducer;
import com.nokia.mid.impl.isa.ui.InitJALM;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.content.RequestListener;
import javax.microedition.content.ResponseListener;

public class CHAPIConsumer implements EventConsumer {
   public static final byte EVENT_INCOMING_INV_REQ = 1;
   public static final byte EVENT_INCOMING_INV_RESP = 2;
   Hashtable requestListeners = new Hashtable();
   Hashtable responseListeners = new Hashtable();
   Object evtLock = new Object();
   EvtQ evtQ;
   private static CHAPIConsumer instance;

   private CHAPIConsumer() {
   }

   public static synchronized CHAPIConsumer getInstance() {
      if (instance == null) {
         instance = new CHAPIConsumer();
         EventProducer evtProd = InitJALM.s_getEventProducer();
         evtProd.attachEventConsumer(10, instance);
      }

      return instance;
   }

   public void setResponseListener(ResponseListener rl, RegistryImpl reg) {
      boolean firstTime = false;
      synchronized(this.evtLock) {
         if (this.responseListeners.remove(reg) == null) {
            firstTime = true;
         }

         if (rl != null) {
            this.responseListeners.put(reg, rl);
         }
      }

      if (firstTime) {
         this.consumeEvent(10, 2, 0);
      }

   }

   public void setRequestListener(RequestListener rl, ContentHandlerServerImpl server) {
      boolean firstTime = false;
      synchronized(this.evtLock) {
         if (this.requestListeners.remove(server) == null) {
            firstTime = true;
         }

         if (rl != null) {
            this.requestListeners.put(server, rl);
         }
      }

      if (firstTime) {
         this.consumeEvent(10, 1, 0);
      }

   }

   public synchronized void consumeEvent(int category, int type, int param) {
      try {
         switch(type) {
         case 1:
            Enumeration eReq = this.requestListeners.keys();

            while(eReq.hasMoreElements()) {
               ContentHandlerServerImpl server = (ContentHandlerServerImpl)eReq.nextElement();
               if (CHAPIQueueManager.getInstance().isInvocationRequestAvailable(server.getClassName())) {
                  synchronized(this.evtLock) {
                     if (this.evtQ == null) {
                        this.evtQ = new EvtQ(this);
                     }

                     this.evtQ.sendRequestEvent((RequestListener)this.requestListeners.get(server), server);
                  }
               }
            }

            return;
         case 2:
            Enumeration eResp = this.responseListeners.keys();

            while(eResp.hasMoreElements()) {
               RegistryImpl reg = (RegistryImpl)eResp.nextElement();
               if (CHAPIQueueManager.getInstance().isInvocationResponseAvailable(reg.getClassName())) {
                  synchronized(this.evtLock) {
                     if (this.evtQ == null) {
                        this.evtQ = new EvtQ(this);
                     }

                     this.evtQ.sendResponseEvent((ResponseListener)this.responseListeners.get(reg), reg);
                  }
               }
            }
         }
      } catch (Exception var11) {
      }

   }
}
