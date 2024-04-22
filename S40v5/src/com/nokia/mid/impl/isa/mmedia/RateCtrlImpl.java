package com.nokia.mid.impl.isa.mmedia;

import com.nokia.mid.impl.isa.mmedia.control.RateOut;
import com.nokia.mid.impl.isa.mmedia.control.Switchable;
import javax.microedition.media.MediaException;
import javax.microedition.media.control.RateControl;

public class RateCtrlImpl extends Switchable implements RateControl {
   private static int hc;
   private static int hd;
   private int he = 100000;
   protected RateOut rateOut;

   public RateCtrlImpl(BasicPlayer var1, RateOut var2) {
      this.player = var1;
      this.rateOut = var2;
      synchronized(var1) {
         hd = var2.getDefaultMinRate();
         hc = var2.getDefaultMaxRate();
      }
   }

   public boolean isValid() {
      return hd != hc;
   }

   public void activate() {
      hd = this.rateOut.getMinRate();
      hc = this.rateOut.getMaxRate();
      if (this.he != 100000) {
         try {
            this.rateOut.setRate(this.he);
            return;
         } catch (MediaException var1) {
         }
      }

   }

   public int getMaxRate() {
      return hc;
   }

   public int getMinRate() {
      return hd;
   }

   public int getRate() {
      synchronized(this.player) {
         if (this.player.isActive()) {
            try {
               this.he = this.rateOut.getRate();
            } catch (MediaException var2) {
            }
         }

         return this.he;
      }
   }

   public int setRate(int var1) {
      var1 = MediaPrefs.boundInt(var1, hd, hc);
      synchronized(this.player) {
         if (this.player.isActive()) {
            try {
               this.he = this.rateOut.setRate(var1);
            } catch (MediaException var3) {
            }
         } else {
            this.he = var1;
         }

         return this.he;
      }
   }
}
