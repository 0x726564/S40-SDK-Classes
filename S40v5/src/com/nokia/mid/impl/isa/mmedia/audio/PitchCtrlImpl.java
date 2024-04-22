package com.nokia.mid.impl.isa.mmedia.audio;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import com.nokia.mid.impl.isa.mmedia.MediaPrefs;
import com.nokia.mid.impl.isa.mmedia.control.Switchable;
import javax.microedition.media.MediaException;
import javax.microedition.media.control.PitchControl;

class PitchCtrlImpl extends Switchable implements PitchControl {
   private int jC = 0;
   private AudioOutImpl iH;

   PitchCtrlImpl(BasicPlayer var1, AudioOutImpl var2) {
      this.player = var1;
      this.iH = var2;
   }

   public void activate() {
      if (this.jC != 0) {
         try {
            this.iH.setPitch(this.jC);
            return;
         } catch (MediaException var1) {
         }
      }

   }

   public int setPitch(int var1) {
      synchronized(this.player) {
         if (this.player.isActive()) {
            try {
               this.jC = this.iH.setPitch(var1);
            } catch (MediaException var3) {
            }
         } else {
            this.jC = MediaPrefs.boundInt(var1, -24000, 24000);
         }

         return this.jC;
      }
   }

   public int getPitch() {
      synchronized(this.player) {
         if (this.player.isActive()) {
            try {
               this.jC = this.iH.getPitch();
            } catch (MediaException var2) {
            }
         }

         return this.jC;
      }
   }

   public int getMaxPitch() {
      synchronized(this.player) {
         if (this.player.isActive()) {
            int var10000;
            try {
               var10000 = this.iH.getMaxPitch();
            } catch (MediaException var2) {
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
               var10000 = this.iH.getMinPitch();
            } catch (MediaException var2) {
               return -24000;
            }

            return var10000;
         } else {
            return -24000;
         }
      }
   }
}
