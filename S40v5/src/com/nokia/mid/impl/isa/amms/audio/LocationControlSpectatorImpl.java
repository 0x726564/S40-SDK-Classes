package com.nokia.mid.impl.isa.amms.audio;

import javax.microedition.amms.control.audio3d.LocationControl;

public class LocationControlSpectatorImpl implements LocationControl {
   private int[] bv = new int[3];

   public LocationControlSpectatorImpl() {
      synchronized(SpectatorImpl.specLock) {
         nActivate();
      }
   }

   public int[] getCartesian() {
      int[] var1;
      (var1 = new int[3])[0] = this.bv[0];
      var1[1] = this.bv[1];
      var1[2] = this.bv[2];
      return var1;
   }

   public void setCartesian(int var1, int var2, int var3) {
      this.bv[0] = var1;
      this.bv[1] = var2;
      this.bv[2] = var3;
      synchronized(SpectatorImpl.specLock) {
         nApplyChanges(var1, var2, var3);
      }
   }

   public void setSpherical(int var1, int var2, int var3) {
      double var4 = Math.toRadians((double)var2);
      double var6 = Math.toRadians((double)var1);
      if (var3 < 0) {
         throw new IllegalArgumentException("Radius can't be -ve");
      } else {
         this.bv[0] = (int)((double)var3 * Math.cos(var4) * Math.sin(var6));
         this.bv[1] = (int)((double)var3 * Math.sin(var4));
         this.bv[2] = (int)((double)(-var3) * Math.cos(var4) * Math.cos(var6));
         synchronized(SpectatorImpl.specLock) {
            nApplyChanges(this.bv[0], this.bv[1], this.bv[2]);
         }
      }
   }

   private static native void nActivate();

   private static native void nApplyChanges(int var0, int var1, int var2);
}
