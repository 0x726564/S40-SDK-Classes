package com.nokia.mid.impl.isa.mmedia;

import com.nokia.mid.impl.isa.util.SharedObjects;
import javax.microedition.media.MediaException;

public class VolumeCtrlImplVideo extends VolumeCtrlImpl {
   private int mediaType;
   private static final Object nativeLock = SharedObjects.getLock("com.nokia.mid.impl.isa.mmedia.VolumeCtrlImplVideo");

   public VolumeCtrlImplVideo(BasicPlayer player, int mediaType) {
      super(player, (MediaOut)null);
      this.player = player;
      this.mediaType = mediaType;
   }

   public void activate() {
      synchronized(nativeLock) {
         nActivate(this.mediaType);
         if (this.mediaType != 0) {
            try {
               nSetMute(this.mediaType, this.muteState);
               nSetLevel(this.mediaType, this.currentVol);
            } catch (MediaException var4) {
            }
         }

      }
   }

   public void deactivate() {
      synchronized(nativeLock) {
         nDeactivate(this.mediaType);
      }
   }

   public void setMute(boolean mute) {
      synchronized(this.player) {
         if (this.player.isActive()) {
            try {
               synchronized(nativeLock) {
                  nSetMute(this.mediaType, mute);
               }
            } catch (MediaException var7) {
            }
         } else if (this.muteState != mute) {
            this.player.serializeEvent(10, -1L);
         }

         this.muteState = mute;
      }
   }

   public boolean isMuted() {
      synchronized(this.player) {
         if (this.player.isActive()) {
            try {
               synchronized(nativeLock) {
                  this.muteState = nIsMuted(this.mediaType);
               }
            } catch (MediaException var6) {
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
               synchronized(nativeLock) {
                  this.currentVol = nSetLevel(this.mediaType, newVol);

                  try {
                     Thread.sleep(100L);
                  } catch (Exception var7) {
                  }
               }
            } catch (MediaException var9) {
            }
         } else if (this.currentVol != newVol) {
            this.currentVol = newVol;
            this.player.serializeEvent(10, -1L);
         }

         return this.currentVol;
      }
   }

   public int getLevel() {
      synchronized(this.player) {
         if (this.player.isActive()) {
            try {
               synchronized(nativeLock) {
                  this.currentVol = nGetLevel(this.mediaType);
               }
            } catch (MediaException var6) {
            }
         }

         return this.currentVol;
      }
   }

   private static native void nInitVolume();

   private static native void nActivate(int var0);

   private static native void nDeactivate(int var0);

   private static native int nSetLevel(int var0, int var1) throws MediaException;

   private static native int nGetLevel(int var0) throws MediaException;

   private static native void nSetMute(int var0, boolean var1) throws MediaException;

   private static native boolean nIsMuted(int var0) throws MediaException;

   static {
      nInitVolume();
   }
}
