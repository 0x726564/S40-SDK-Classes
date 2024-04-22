package com.nokia.mid.impl.isa.mmedia;

import com.nokia.mid.impl.isa.util.SharedObjects;
import javax.microedition.media.MediaException;

public class VolumeCtrlImplVideo extends VolumeCtrlImpl {
   private int dR;
   private static final Object aG = SharedObjects.getLock("com.nokia.mid.impl.isa.mmedia.VolumeCtrlImplVideo");

   public VolumeCtrlImplVideo(BasicPlayer var1, int var2) {
      super(var1, (MediaOut)null);
      this.player = var1;
      this.dR = var2;
   }

   public void activate() {
      synchronized(aG) {
         nActivate(this.dR);
         if (this.dR != 0) {
            try {
               nSetMute(this.dR, this.muteState);
               nSetLevel(this.dR, this.currentVol);
            } catch (MediaException var2) {
            }
         }

      }
   }

   public void deactivate() {
      synchronized(aG) {
         nDeactivate(this.dR);
      }
   }

   public void setMute(boolean var1) {
      synchronized(this.player) {
         if (this.player.isActive()) {
            try {
               synchronized(aG) {
                  nSetMute(this.dR, var1);
               }
            } catch (MediaException var6) {
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
               synchronized(aG) {
                  this.muteState = nIsMuted(this.dR);
               }
            } catch (MediaException var5) {
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
               synchronized(aG) {
                  this.currentVol = nSetLevel(this.dR, var1);

                  try {
                     Thread.sleep(100L);
                  } catch (Exception var5) {
                  }
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
               synchronized(aG) {
                  this.currentVol = nGetLevel(this.dR);
               }
            } catch (MediaException var5) {
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
