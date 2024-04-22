package com.nokia.mid.impl.isa.mmedia;

import com.nokia.mid.impl.isa.mmedia.control.StopTimeOut;
import com.nokia.mid.impl.isa.mmedia.control.Switchable;
import javax.microedition.media.MediaException;
import javax.microedition.media.control.StopTimeControl;

public class StopTimeCtrlImpl extends Switchable implements StopTimeControl {
   private long currentStopTime = Long.MAX_VALUE;
   private StopTimeOut stopTimeOut;

   public StopTimeCtrlImpl(BasicPlayer player, StopTimeOut stopTimeOut) {
      this.player = player;
      this.stopTimeOut = stopTimeOut;
   }

   public void activate() {
      if (this.currentStopTime != Long.MAX_VALUE) {
         try {
            this.stopTimeOut.setStopTime(this.currentStopTime);
         } catch (MediaException var2) {
         }
      }

   }

   public long getStopTime() {
      synchronized(this.player) {
         if (this.player.isActive() && this.currentStopTime != Long.MAX_VALUE) {
            try {
               this.currentStopTime = this.stopTimeOut.getStopTime();
            } catch (MediaException var4) {
            }
         }

         return this.currentStopTime;
      }
   }

   public void setStopTime(long stopTime) {
      if (this.player.getState() == 400 && this.currentStopTime != Long.MAX_VALUE) {
         throw new IllegalStateException("StopTime already set");
      } else {
         synchronized(this.player) {
            if (this.player.isActive()) {
               try {
                  this.currentStopTime = this.stopTimeOut.setStopTime(stopTime);
               } catch (MediaException var6) {
                  this.currentStopTime = stopTime;
               }
            } else {
               this.currentStopTime = stopTime;
            }

         }
      }
   }

   public void resetStopTime() {
      this.currentStopTime = Long.MAX_VALUE;
   }
}
