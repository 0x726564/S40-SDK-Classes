package com.nokia.mid.impl.isa.amms.audio;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import javax.microedition.amms.control.audio3d.LocationControl;
import javax.microedition.media.Player;

public class LocationControlSoundSource3DImpl extends BaseSoundSource3DControlImpl implements LocationControl {
   private int[] bv = new int[3];

   public void activate(Player var1) {
      int var2;
      nActivate(var2 = ((BasicPlayer)var1).getPlayerId());
      nApplyChanges(var2, this.bv[0], this.bv[1], this.bv[2]);
   }

   public void deactivate(Player var1) {
      nDeactivate(((BasicPlayer)var1).getPlayerId());
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
      this.applyChanges();
   }

   public void setSpherical(int var1, int var2, int var3) {
      double var4 = Math.toRadians((double)var2);
      double var6 = Math.toRadians((double)var1);
      if (var3 < 0) {
         throw new IllegalArgumentException("Radius cannot be -ve");
      } else {
         this.bv[0] = (int)((double)var3 * Math.cos(var4) * Math.sin(var6));
         this.bv[1] = (int)((double)var3 * Math.sin(var4));
         this.bv[2] = (int)((double)(-var3) * Math.cos(var4) * Math.cos(var6));
         this.applyChanges();
      }
   }

   protected void doApplyChanges(BasicPlayer var1) {
      nApplyChanges(var1.getPlayerId(), this.bv[0], this.bv[1], this.bv[2]);
   }

   private static native void nApplyChanges(int var0, int var1, int var2, int var3);

   private static native void nActivate(int var0);

   private static native void nDeactivate(int var0);
}
