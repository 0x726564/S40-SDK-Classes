package com.nokia.mid.impl.isa.mmedia;

import javax.microedition.media.PlayerListener;

class EvtQ extends Thread {
   private BasicPlayer p;
   private String[] evtQ;
   private Object[] evtDataQ;
   private int head = 0;
   private int tail = 0;
   private static final int size = 12;

   EvtQ(BasicPlayer var1) {
      this.p = var1;
      this.evtQ = new String[12];
      this.evtDataQ = new Object[12];
      this.start();
   }

   synchronized void sendEvent(String var1, Object var2) {
      while((this.head + 1) % 12 == this.tail) {
         try {
            this.wait();
         } catch (Exception var4) {
         }
      }

      this.evtQ[this.head] = var1;
      this.evtDataQ[this.head] = var2;
      if (++this.head == 12) {
         this.head = 0;
      }

      this.notify();
   }

   public void run() {
      String var1 = "";
      Object var2 = null;
      boolean var3 = false;
      boolean var4 = false;

      do {
         synchronized(this) {
            if (this.head == this.tail) {
               try {
                  this.wait(5000L);
               } catch (Exception var13) {
               }
            }

            if (this.head != this.tail) {
               var1 = this.evtQ[this.tail];
               var2 = this.evtDataQ[this.tail];
               this.evtDataQ[this.tail] = null;
               var3 = true;
               if (++this.tail == 12) {
                  this.tail = 0;
               }

               this.notify();
            } else {
               var3 = false;
            }
         }

         if (var3) {
            synchronized(this.p.listeners) {
               int var7 = 0;

               while(true) {
                  if (var7 >= this.p.listeners.size()) {
                     break;
                  }

                  try {
                     PlayerListener var6 = (PlayerListener)this.p.listeners.elementAt(var7);
                     var6.playerUpdate(this.p, var1, var2);
                  } catch (Exception var12) {
                  }

                  ++var7;
               }
            }

            var4 = true;
         }
      } while((var3 || !var4) && var1 != "closed");

      synchronized(this.p.evtLock) {
         if (var1 == "closed") {
            this.p.listeners.removeAllElements();
         }

         this.p.evtQ = null;
      }
   }
}
