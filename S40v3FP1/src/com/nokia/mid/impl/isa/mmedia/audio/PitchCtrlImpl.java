package com.nokia.mid.impl.isa.mmedia.audio;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import com.nokia.mid.impl.isa.mmedia.MediaPrefs;
import com.nokia.mid.impl.isa.mmedia.control.Switchable;
import javax.microedition.media.MediaException;
import javax.microedition.media.control.PitchControl;

class PitchCtrlImpl extends Switchable implements PitchControl {
   private static final int maxPitch = 24000;
   private static final int minPitch = -24000;
   private static final int defaultPitch = 0;
   private int mPitch = 0;
   AudioOutImpl audioOut;

   PitchCtrlImpl(BasicPlayer var1, AudioOutImpl var2) {
      this.player = var1;
      this.audioOut = var2;
   }

   public void activate() {
      if (this.mPitch != 0) {
         try {
            this.audioOut.setPitch(this.mPitch);
         } catch (MediaException var2) {
         }
      }

   }

   public int setPitch(int var1) {
      synchronized(this.player) {
         if (this.player.isActive()) {
            try {
               this.mPitch = this.audioOut.setPitch(var1);
            } catch (MediaException var5) {
            }
         } else {
            this.mPitch = MediaPrefs.boundInt(var1, -24000, 24000);
         }

         return this.mPitch;
      }
   }

   public int getPitch() {
      synchronized(this.player) {
         if (this.player.isActive()) {
            try {
               this.mPitch = this.audioOut.getPitch();
            } catch (MediaException var4) {
            }
         }

         return this.mPitch;
      }
   }

   public int getMaxPitch() {
      synchronized(this.player) {
         if (this.player.isActive()) {
            int var10000;
            try {
               var10000 = this.audioOut.getMaxPitch();
            } catch (MediaException var4) {
               return 24000;
            }

            return var10000;
         } else {
            return 24000;
         }
      }
   }

   public int getMinPitch() {
      synchronized(this.player) {
         if (this.player.isActive()) {
            int var10000;
            try {
               var10000 = this.audioOut.getMinPitch();
            } catch (MediaException var4) {
               return -24000;
            }

            return var10000;
         } else {
            return -24000;
         }
      }
   }
}
