package com.nokia.mid.impl.isa.mmedia.audio;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import com.nokia.mid.impl.isa.mmedia.MediaPrefs;
import com.nokia.mid.impl.isa.mmedia.RateCtrlImpl;
import javax.microedition.media.MediaException;
import javax.microedition.media.control.TempoControl;

class TempoCtrlImpl extends RateCtrlImpl implements TempoControl {
   private static final int defaultTempo = 120000;
   private int mTempo = 120000;

   TempoCtrlImpl(BasicPlayer player, AudioOutImpl tempoOut) {
      super(player, tempoOut);
   }

   public void activate() {
      if (this.mTempo != 120000) {
         try {
            ((AudioOutImpl)this.rateOut).setTempo(this.mTempo);
         } catch (MediaException var2) {
         }
      }

   }

   public int setTempo(int milliTempo) {
      milliTempo = MediaPrefs.boundInt(milliTempo, 10000, 300000);
      synchronized(this.player) {
         if (this.player.isActive()) {
            try {
               this.mTempo = ((AudioOutImpl)this.rateOut).setTempo(milliTempo);
            } catch (MediaException var5) {
            }
         } else {
            this.mTempo = milliTempo;
         }

         return this.mTempo;
      }
   }

   public int getTempo() {
      synchronized(this.player) {
         if (this.player.isActive()) {
            try {
               this.mTempo = ((AudioOutImpl)this.rateOut).getTempo();
            } catch (MediaException var4) {
            }
         }

         return this.mTempo;
      }
   }
}
