package com.nokia.mid.impl.isa.mmedia;

import com.nokia.mid.impl.isa.mmedia.control.RateOut;
import com.nokia.mid.impl.isa.mmedia.control.Switchable;
import javax.microedition.media.MediaException;
import javax.microedition.media.control.RateControl;

public class RateCtrlImpl extends Switchable implements RateControl {
   private static int maxRate;
   private static int minRate;
   private static final int defaultRate = 100000;
   private int curRate = 100000;
   protected RateOut rateOut;

   public RateCtrlImpl(BasicPlayer var1, RateOut var2) {
      this.player = var1;
      this.rateOut = var2;
      synchronized(var1) {
         minRate = var2.getDefaultMinRate();
         maxRate = var2.getDefaultMaxRate();
      }
   }

   public boolean isValid() {
      return minRate != maxRate;
   }

   public void activate() {
      minRate = this.rateOut.getMinRate();
      maxRate = this.rateOut.getMaxRate();
      if (this.curRate != 100000) {
         try {
            this.rateOut.setRate(this.curRate);
         } catch (MediaException var2) {
         }
      }

   }

   public int getMaxRate() {
      return maxRate;
   }

   public int getMinRate() {
      return minRate;
   }

   public int getRate() {
      synchronized(this.player) {
         if (this.player.isActive()) {
            try {
               this.curRate = this.rateOut.getRate();
            } catch (MediaException var4) {
            }
         }

         return this.curRate;
      }
   }

   public int setRate(int var1) {
      var1 = MediaPrefs.boundInt(var1, minRate, maxRate);
      synchronized(this.player) {
         if (this.player.isActive()) {
            try {
               this.curRate = this.rateOut.setRate(var1);
            } catch (MediaException var5) {
            }
         } else {
            this.curRate = var1;
         }

         return this.curRate;
      }
   }
}
