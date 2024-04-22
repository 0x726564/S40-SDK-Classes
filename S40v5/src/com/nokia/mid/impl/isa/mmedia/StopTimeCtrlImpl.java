package com.nokia.mid.impl.isa.mmedia;

import com.nokia.mid.impl.isa.mmedia.control.StopTimeOut;
import com.nokia.mid.impl.isa.mmedia.control.Switchable;
import javax.microedition.media.MediaException;
import javax.microedition.media.control.StopTimeControl;

public class StopTimeCtrlImpl extends Switchable implements StopTimeControl {
   private long ke = Long.MAX_VALUE;
   private StopTimeOut kf;

   public StopTimeCtrlImpl(BasicPlayer var1, StopTimeOut var2) {
      this.player = var1;
      this.kf = var2;
   }

   public void activate() {
      if (this.ke != Long.MAX_VALUE) {
         try {
            this.kf.setStopTime(this.ke);
            return;
         } catch (MediaException var1) {
         }
      }

   }

   public long getStopTime() {
      synchronized(this.player) {
         if (this.player.isActive() && this.ke != Long.MAX_VALUE) {
            try {
               this.ke = this.kf.getStopTime();
            } catch (MediaException var2) {
            }
         }

         return this.ke;
      }
   }

   public void setStopTime(long var1) {
      if (this.player.getState() == 400 && this.ke != Long.MAX_VALUE) {
         throw new IllegalStateException("StopTime already set");
      } else {
         synchronized(this.player) {
            if (this.player.isActive()) {
               try {
                  this.ke = this.kf.setStopTime(var1);
                  return;
               } catch (MediaException var4) {
               }
            }

            this.ke = var1;
         }
      }
   }

   public void resetStopTime() {
      this.ke = Long.MAX_VALUE;
   }
}
