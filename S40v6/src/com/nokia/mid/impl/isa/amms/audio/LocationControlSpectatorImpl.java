package com.nokia.mid.impl.isa.amms.audio;

import javax.microedition.amms.control.audio3d.LocationControl;

public class LocationControlSpectatorImpl implements LocationControl {
   private int[] coords = new int[3];

   public LocationControlSpectatorImpl() {
      synchronized(SpectatorImpl.specLock) {
         nActivate();
      }
   }

   public int[] getCartesian() {
      int[] dup = new int[]{this.coords[0], this.coords[1], this.coords[2]};
      return dup;
   }

   public void setCartesian(int x, int y, int z) {
      this.coords[0] = x;
      this.coords[1] = y;
      this.coords[2] = z;
      synchronized(SpectatorImpl.specLock) {
         nApplyChanges(x, y, z);
      }
   }

   public void setSpherical(int azimuth, int elevation, int radius) {
      double elevRad = Math.toRadians((double)elevation);
      double aziRad = Math.toRadians((double)azimuth);
      if (radius < 0) {
         throw new IllegalArgumentException("Radius can't be -ve");
      } else {
         this.coords[0] = (int)((double)radius * Math.cos(elevRad) * Math.sin(aziRad));
         this.coords[1] = (int)((double)radius * Math.sin(elevRad));
         this.coords[2] = (int)((double)(-radius) * Math.cos(elevRad) * Math.cos(aziRad));
         synchronized(SpectatorImpl.specLock) {
            nApplyChanges(this.coords[0], this.coords[1], this.coords[2]);
         }
      }
   }

   private static native void nActivate();

   private static native void nApplyChanges(int var0, int var1, int var2);
}
