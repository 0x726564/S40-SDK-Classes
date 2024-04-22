package com.nokia.mid.impl.isa.mmedia;

import com.nokia.mid.impl.isa.mmedia.control.Switchable;
import javax.microedition.media.MediaException;
import javax.microedition.media.control.VolumeControl;

public class VolumeCtrlImpl extends Switchable implements VolumeControl {
   protected int currentVol = MediaPrefs.nGetDefaultVolumeLevel();
   protected boolean muteState = false;
   private MediaOut mediaOut;

   public VolumeCtrlImpl(BasicPlayer var1, MediaOut var2) {
      this.player = var1;
      this.mediaOut = var2;
   }

   public void activate() {
      synchronized(this.player) {
         try {
            if (this.currentVol == 0) {
               this.mediaOut.setMute(true);
            } else {
               this.mediaOut.setMute(this.muteState);
            }

            this.mediaOut.setLevel(this.currentVol);
         } catch (MediaException var4) {
         }

      }
   }

   public void deactivate() {
   }

   public int readStoredVol() {
      return this.currentVol;
   }

   public void setMute(boolean var1) {
      boolean var2 = this.muteState;
      synchronized(this.player) {
         if (this.player.isActive() && this.currentVol != 0) {
            try {
               this.mediaOut.setMute(var1);
               this.muteState = var1;
            } catch (MediaException var6) {
            }
         } else {
            this.muteState = var1;
         }

         if (var2 != this.muteState) {
            this.player.serializeEvent(10, -1L);
         }

      }
   }

   public boolean isMuted() {
      synchronized(this.player) {
         if (this.player.isActive() && this.currentVol != 0) {
            try {
               this.muteState = this.mediaOut.isMuted();
            } catch (MediaException var4) {
            }
         }

         return this.muteState;
      }
   }

   public int setLevel(int var1) {
      var1 = MediaPrefs.boundInt(var1, 0, 100);
      synchronized(this.player) {
         if (this.player.isActive()) {
            try {
               if ((this.currentVol == 0 || var1 == 0) && !this.muteState) {
                  boolean var3 = var1 == 0;
                  this.mediaOut.setMute(var3);
               }

               this.currentVol = this.mediaOut.setLevel(var1);
            } catch (MediaException var5) {
            }
         } else {
            this.player.serializeEvent(10, -1L);
            this.currentVol = var1;
         }

         return this.currentVol;
      }
   }

   public int getLevel() {
      return this.currentVol;
   }
}
