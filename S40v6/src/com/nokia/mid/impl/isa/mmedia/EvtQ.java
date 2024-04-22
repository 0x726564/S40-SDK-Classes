package com.nokia.mid.impl.isa.mmedia;

import javax.microedition.media.PlayerListener;

class EvtQ extends Thread {
   private BasicPlayer p;
   private String[] evtQ;
   private Object[] evtDataQ;
   private int head = 0;
   private int tail = 0;
   private static final int size = 12;

   EvtQ(BasicPlayer p) {
      this.p = p;
      this.evtQ = new String[12];
      this.evtDataQ = new Object[12];
      this.start();
   }

   synchronized void sendEvent(String evt, Object evtData) {
      while((this.head + 1) % 12 == this.tail) {
         try {
            this.wait();
         } catch (Exception var4) {
         }
      }

      this.evtQ[this.head] = evt;
      this.evtDataQ[this.head] = evtData;
      if (++this.head == 12) {
         this.head = 0;
      }

      this.notify();
   }

   public void run() {
      String evt = "";
      Object evtData = null;
      boolean evtToGo = false;
      boolean evtSent = false;

      do {
         synchronized(this) {
            if (this.head == this.tail) {
               try {
                  this.wait(5000L);
               } catch (Exception var13) {
               }
            }

            if (this.head != this.tail) {
               evt = this.evtQ[this.tail];
               evtData = this.evtDataQ[this.tail];
               this.evtDataQ[this.tail] = null;
               evtToGo = true;
               if (++this.tail == 12) {
                  this.tail = 0;
               }

               this.notify();
            } else {
               evtToGo = false;
            }
         }

         if (evtToGo) {
            synchronized(this.p.listeners) {
               int i = 0;

               while(true) {
                  if (i >= this.p.listeners.size()) {
                     break;
                  }

                  try {
                     PlayerListener l = (PlayerListener)this.p.listeners.elementAt(i);
                     l.playerUpdate(this.p, evt, evtData);
                  } catch (Exception var12) {
                  }

                  ++i;
               }
            }

            evtSent = true;
         }
      } while((evtToGo || !evtSent) && evt != "closed");

      synchronized(this.p.evtLock) {
         if (evt == "closed") {
            this.p.listeners.removeAllElements();
         }

         this.p.evtQ = null;
      }
   }
}
