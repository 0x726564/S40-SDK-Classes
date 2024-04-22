package com.nokia.mid.impl.isa.mmedia;

import javax.microedition.media.PlayerListener;

class EvtQ extends Thread {
   private BasicPlayer ak;
   private String[] bs;
   private Object[] bt;
   private int head = 0;
   private int tail = 0;

   EvtQ(BasicPlayer var1) {
      this.ak = var1;
      this.bs = new String[12];
      this.bt = new Object[12];
      this.start();
   }

   final synchronized void a(String var1, Object var2) {
      while((this.head + 1) % 12 == this.tail) {
         try {
            this.wait();
         } catch (Exception var3) {
         }
      }

      this.bs[this.head] = var1;
      this.bt[this.head] = var2;
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
               } catch (Exception var9) {
               }
            }

            if (this.head != this.tail) {
               var1 = this.bs[this.tail];
               var2 = this.bt[this.tail];
               this.bt[this.tail] = null;
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
            synchronized(this.ak.listeners) {
               int var6 = 0;

               while(true) {
                  if (var6 >= this.ak.listeners.size()) {
                     break;
                  }

                  try {
                     ((PlayerListener)this.ak.listeners.elementAt(var6)).playerUpdate(this.ak, var1, var2);
                  } catch (Exception var8) {
                  }

                  ++var6;
               }
            }

            var4 = true;
         }
      } while((var3 || !var4) && var1 != "closed");

      synchronized(this.ak.fL) {
         if (var1 == "closed") {
            this.ak.listeners.removeAllElements();
         }

         this.ak.fK = null;
      }
   }
}
