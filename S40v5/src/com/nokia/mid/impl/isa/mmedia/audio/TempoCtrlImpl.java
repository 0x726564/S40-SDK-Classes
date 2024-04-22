package com.nokia.mid.impl.isa.mmedia.audio;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import com.nokia.mid.impl.isa.mmedia.MediaPrefs;
import com.nokia.mid.impl.isa.mmedia.RateCtrlImpl;
import javax.microedition.media.MediaException;
import javax.microedition.media.control.TempoControl;

class TempoCtrlImpl extends RateCtrlImpl implements TempoControl {
   private int kz = 120000;

   TempoCtrlImpl(BasicPlayer var1, AudioOutImpl var2) {
      super(var1, var2);
   }

   public void activate() {
      if (this.kz != 120000) {
         try {
            ((AudioOutImpl)this.rateOut).setTempo(this.kz);
            return;
         } catch (MediaException var1) {
         }
      }

   }

   public int setTempo(int var1) {
      var1 = MediaPrefs.boundInt(var1, 10000, 300000);
      synchronized(this.player) {
         if (this.player.isActive()) {
            try {
               this.kz = ((AudioOutImpl)this.rateOut).setTempo(var1);
            } catch (MediaException var3) {
            }
         } else {
            this.kz = var1;
         }

         return this.kz;
      }
   }

   public int getTempo() {
      synchronized(this.player) {
         if (this.player.isActive()) {
            try {
               this.kz = ((AudioOutImpl)this.rateOut).getTempo();
            } catch (MediaException var2) {
            }
         }

         return this.kz;
      }
   }
}
