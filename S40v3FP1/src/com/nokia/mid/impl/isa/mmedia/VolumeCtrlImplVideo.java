package com.nokia.mid.impl.isa.mmedia;

import javax.microedition.media.MediaException;

public class VolumeCtrlImplVideo extends VolumeCtrlImpl {
   private int mediaType;
   private static Object nativeLock = new Object();

   public VolumeCtrlImplVideo(BasicPlayer var1, int var2) {
      super(var1, (MediaOut)null);
      this.player = var1;
      this.mediaType = var2;
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

   public void setMute(boolean var1) {
      synchronized(this.player) {
         if (this.player.isActive()) {
            try {
               synchronized(nativeLock) {
                  nSetMute(this.mediaType, var1);
               }
            } catch (MediaException var7) {
            }
         } else if (this.muteState != var1) {
            this.player.serializeEvent(10, -1L);
         }

         this.muteState = var1;
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

   public int setLevel(int var1) {
      var1 = MediaPrefs.boundInt(var1, 0, 100);
      synchronized(this.player) {
         if (this.player.isActive()) {
            try {
               synchronized(nativeLock) {
                  this.currentVol = nSetLevel(this.mediaType, var1);
               }
            } catch (MediaException var7) {
            }
         } else if (this.currentVol != var1) {
            this.currentVol = var1;
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
