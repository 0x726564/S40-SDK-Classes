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

   public RateCtrlImpl(BasicPlayer player, RateOut rateOut) {
      this.player = player;
      this.rateOut = rateOut;
      synchronized(player) {
         minRate = rateOut.getDefaultMinRate();
         maxRate = rateOut.getDefaultMaxRate();
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

   public int setRate(int milliRate) {
      milliRate = MediaPrefs.boundInt(milliRate, minRate, maxRate);
      synchronized(this.player) {
         if (this.player.isActive()) {
            try {
               this.curRate = this.rateOut.setRate(milliRate);
            } catch (MediaException var5) {
            }
         } else {
            this.curRate = milliRate;
         }

         return this.curRate;
      }
   }
}
