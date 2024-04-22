package com.nokia.mid.impl.isa.amms.audio;

import javax.microedition.amms.control.audioeffect.EqualizerControl;
import javax.microedition.media.MediaException;

public class EqualizerControlImpl implements EqualizerControl {
   private boolean enabled = false;
   private int w = -1;
   private String[] z = null;
   private static int[] A = null;
   private int B = -1;
   private Object mutex = new Object();
   private boolean C = false;

   private synchronized void f() {
      if (this.w == -1) {
         this.nSwitchStateReq(false);
         this.nDeleteEqualizer();
         this.w = this.nCreateEqualizer();
         if (this.enabled) {
            this.nSwitchStateReq(true);
         }
      }

   }

   public void setEnabled(boolean var1) {
      synchronized(this.mutex) {
         if (this.enabled != var1) {
            this.f();
            if (this.nSwitchStateReq(var1)) {
               this.enabled = var1;
            }

         }
      }
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setPreset(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("The given preset is null.");
      } else {
         synchronized(this.mutex) {
            if (this.z == null) {
               this.getPresetNames();
            }

            boolean var3 = false;

            for(int var4 = 0; var4 < this.z.length; ++var4) {
               if (this.z[var4].equals(var1)) {
                  this.nSetPreset(var4);
                  this.B = var4;
                  var3 = true;

                  for(int var5 = 0; var5 < 8; ++var5) {
                     A[var5] = Integer.MAX_VALUE;
                  }
               }
            }

            if (!var3) {
               throw new IllegalArgumentException("The given preset is not available.");
            }
         }
      }
   }

   public String getPreset() {
      if (this.B == -1) {
         return null;
      } else {
         synchronized(this.mutex) {
            return this.getPresetNames()[this.B];
         }
      }
   }

   public String[] getPresetNames() {
      if (this.z != null) {
         return this.z;
      } else {
         synchronized(this.mutex) {
            this.z = this.nGetPresetNames();
            return this.z;
         }
      }
   }

   public int getMinBandLevel() {
      this.f();
      return this.nGetMinGain() * 100;
   }

   public int getMaxBandLevel() {
      this.f();
      return this.nGetMaxGain() * 100;
   }

   public void setBandLevel(int var1, int var2) throws IllegalArgumentException {
      this.f();
      if (var2 >= 0 && var2 < this.getNumberOfBands()) {
         if (var1 >= this.getMinBandLevel() && var1 <= this.getMaxBandLevel()) {
            synchronized(this.mutex) {
               A[var2] = var1;
               var1 /= 100;
               this.nSetBandGain(var1, var2);
               this.B = -1;
            }
         } else {
            throw new IllegalArgumentException("invalid level");
         }
      } else {
         throw new IllegalArgumentException("invalid band");
      }
   }

   public int getBandLevel(int var1) throws IllegalArgumentException {
      this.f();
      if (var1 >= 0 && var1 < this.getNumberOfBands()) {
         synchronized(this.mutex) {
            return A[var1] != Integer.MAX_VALUE ? A[var1] : this.nGetBandGain(var1) * 100;
         }
      } else {
         throw new IllegalArgumentException("invalid band");
      }
   }

   public int getNumberOfBands() {
      this.f();
      return this.nGetNumberOfBands();
   }

   public int getCenterFreq(int var1) throws IllegalArgumentException {
      this.f();
      synchronized(this.mutex) {
         int[] var3;
         if ((var3 = this.nGetCenterFreqs()) == null) {
            throw new OutOfMemoryError();
         } else if (var1 >= 0 && var1 < this.getNumberOfBands()) {
            return var3[var1];
         } else {
            throw new IllegalArgumentException("band out of range.");
         }
      }
   }

   public int getBand(int var1) {
      this.f();
      synchronized(this.mutex) {
         int[] var8;
         if ((var8 = this.nGetCenterFreqs()) == null) {
            throw new OutOfMemoryError();
         } else if (var1 < 0) {
            return -1;
         } else {
            int var3 = 0;
            int var4 = Math.abs(var8[0] - var1);

            for(int var5 = 1; var5 < var8.length; ++var5) {
               int var6;
               if ((var6 = Math.abs(var8[var5] - var1)) < var4) {
                  var3 = var5;
                  var4 = var6;
               }
            }

            return var3;
         }
      }
   }

   private int a(int var1, int var2) throws IllegalArgumentException {
      int var3 = this.getMaxBandLevel();
      int var10000 = this.getMinBandLevel();
      int var4 = var3;
      var3 = var10000;
      if (var2 <= 100 && var2 >= 0) {
         int var6 = 0;
         if (var2 > 50) {
            var6 = var4 * (var2 - 50) / 50;
         } else if (var2 < 50) {
            var6 = Math.abs(var3) * var2 / 50;
            var6 += var3;
         }

         this.setBandLevel(var6, var1);
         return var2;
      } else {
         throw new IllegalArgumentException("level out of range.");
      }
   }

   public int setBass(int var1) throws IllegalArgumentException {
      return this.a(0, var1);
   }

   public int setTreble(int var1) throws IllegalArgumentException {
      int var2 = this.getNumberOfBands() - 1;
      return this.a(var2, var1);
   }

   public int getBass() {
      int var1 = this.getBandLevel(0);
      int var2 = this.getMaxBandLevel();
      int var3 = this.getMinBandLevel();
      var2 -= var3;
      return (var1 - var3) * 100 / var2;
   }

   public int getTreble() {
      int var1 = this.getBandLevel(this.getNumberOfBands() - 1);
      int var2 = this.getMaxBandLevel();
      int var3 = this.getMinBandLevel();
      var2 -= var3;
      return (var1 - var3) * 100 / var2;
   }

   public boolean isEnforced() {
      return this.C;
   }

   public void setEnforced(boolean var1) {
      this.C = var1;
   }

   public int getScope() {
      return 1;
   }

   public void setScope(int var1) throws MediaException {
      if (var1 != 1) {
         throw new MediaException("the given scope is not supported.");
      }
   }

   static native void nInit();

   native int nCreateEqualizer();

   native void nDeleteEqualizer();

   native boolean nSwitchStateReq(boolean var1);

   native void nSetBandGain(int var1, int var2);

   native int nGetBandGain(int var1);

   native int nGetMinGain();

   native int nGetMaxGain();

   native int nGetNumberOfBands();

   native int[] nGetCenterFreqs();

   native String[] nGetPresetNames();

   native void nSetPreset(int var1);

   static {
      nInit();
      A = new int[8];

      for(int var0 = 0; var0 < 8; ++var0) {
         A[var0] = Integer.MAX_VALUE;
      }

   }
}
