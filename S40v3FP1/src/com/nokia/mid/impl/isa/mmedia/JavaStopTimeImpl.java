package com.nokia.mid.impl.isa.mmedia;

import com.nokia.mid.impl.isa.mmedia.control.StopTimeOut;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;

class JavaStopTimeImpl extends TimerTask implements StopTimeOut, PlayerListener {
   private Timer timer = new Timer();
   private long currentStopTime = Long.MAX_VALUE;
   BasicPlayer player;
   private long currentTime;
   private long currentTimeFactor;

   JavaStopTimeImpl(BasicPlayer var1) {
      this.player = var1;
      var1.addPlayerListener(this);
      this.timer.scheduleAtFixedRate(this, 100L, 100L);
   }

   public long getStopTime() {
      return this.currentStopTime;
   }

   public long setStopTime(long var1) {
      this.currentStopTime = var1;
      return this.currentStopTime;
   }

   public void run() {
      try {
         if (this.player.getState() == 0) {
            this.timer.cancel();
         } else {
            synchronized(this) {
               if (this.player.isActive() && this.currentStopTime != Long.MAX_VALUE && this.currentTime > this.currentStopTime) {
                  this.player.serializeEvent(9, this.currentStopTime);
                  this.currentStopTime = Long.MAX_VALUE;
                  this.player.stop();
               }

               this.currentTime += this.currentTimeFactor * 100000L;
            }
         }
      } catch (Exception var4) {
      }

   }

   public void playerUpdate(Player var1, String var2, Object var3) {
      synchronized(this) {
         if (var2 == "started") {
            this.currentTime = (Long)var3;
            this.currentTimeFactor = 1L;
         } else if (var2 == "stopped" || var2 == "stoppedAtTime" || var2 == "endOfMedia" || var2 == "deviceUnavailable") {
            this.currentTimeFactor = 0L;
         }

      }
   }
}
