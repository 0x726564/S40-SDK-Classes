package com.nokia.mid.impl.isa.mmedia;

import com.nokia.mid.impl.isa.mmedia.control.Switchable;
import javax.microedition.media.MediaException;
import javax.microedition.media.control.VolumeControl;

public class VolumeCtrlImpl extends Switchable implements VolumeControl {
   protected int currentVol = MediaPrefs.nGetDefaultVolumeLevel();
   protected boolean muteState = false;
   private MediaOut mediaOut;

   public VolumeCtrlImpl(BasicPlayer player, MediaOut mediaOut) {
      this.player = player;
      this.mediaOut = mediaOut;
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

   public void setMute(boolean mute) {
      boolean oldMuteState = this.muteState;
      synchronized(this.player) {
         if (this.player.isActive() && this.currentVol != 0) {
            try {
               this.mediaOut.setMute(mute);
               this.muteState = mute;
            } catch (MediaException var6) {
            }
         } else {
            this.muteState = mute;
         }

         if (oldMuteState != this.muteState) {
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

   public int setLevel(int newVol) {
      newVol = MediaPrefs.boundInt(newVol, 0, 100);
      synchronized(this.player) {
         if (this.player.isActive()) {
            try {
               if ((this.currentVol == 0 || newVol == 0) && !this.muteState) {
                  boolean newState = newVol == 0;
                  this.mediaOut.setMute(newState);
               }

               this.currentVol = this.mediaOut.setLevel(newVol);
            } catch (MediaException var5) {
            }
         } else {
            this.player.serializeEvent(10, -1L);
            this.currentVol = newVol;
         }

         return this.currentVol;
      }
   }

   public int getLevel() {
      return this.currentVol;
   }
}
