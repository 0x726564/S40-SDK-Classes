package com.nokia.mid.impl.isa.amms.audio;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import javax.microedition.amms.control.audio3d.LocationControl;
import javax.microedition.media.Player;

public class LocationControlSoundSource3DImpl extends BaseSoundSource3DControlImpl implements LocationControl {
   private int[] coords = new int[3];

   public void activate(Player p) {
      int playerId = ((BasicPlayer)p).getPlayerId();
      nActivate(playerId);
      nApplyChanges(playerId, this.coords[0], this.coords[1], this.coords[2]);
   }

   public void deactivate(Player p) {
      nDeactivate(((BasicPlayer)p).getPlayerId());
   }

   public int[] getCartesian() {
      int[] dup = new int[]{this.coords[0], this.coords[1], this.coords[2]};
      return dup;
   }

   public void setCartesian(int x, int y, int z) {
      this.coords[0] = x;
      this.coords[1] = y;
      this.coords[2] = z;
      this.applyChanges();
   }

   public void setSpherical(int azimuth, int elevation, int radius) {
      double elevRad = Math.toRadians((double)elevation);
      double aziRad = Math.toRadians((double)azimuth);
      if (radius < 0) {
         throw new IllegalArgumentException("Radius cannot be -ve");
      } else {
         this.coords[0] = (int)((double)radius * Math.cos(elevRad) * Math.sin(aziRad));
         this.coords[1] = (int)((double)radius * Math.sin(elevRad));
         this.coords[2] = (int)((double)(-radius) * Math.cos(elevRad) * Math.cos(aziRad));
         this.applyChanges();
      }
   }

   protected void doApplyChanges(BasicPlayer p) {
      nApplyChanges(p.getPlayerId(), this.coords[0], this.coords[1], this.coords[2]);
   }

   private static native void nApplyChanges(int var0, int var1, int var2, int var3);

   private static native void nActivate(int var0);

   private static native void nDeactivate(int var0);
}
