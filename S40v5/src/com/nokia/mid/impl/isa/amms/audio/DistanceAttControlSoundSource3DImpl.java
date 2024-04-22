package com.nokia.mid.impl.isa.amms.audio;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import javax.microedition.amms.control.audio3d.DistanceAttenuationControl;
import javax.microedition.media.Player;

public class DistanceAttControlSoundSource3DImpl extends BaseSoundSource3DControlImpl implements DistanceAttenuationControl {
   private int kw = Integer.MAX_VALUE;
   private int minDistance = 1000;
   private boolean kx = true;
   private int ky = 1000;

   public int getMaxDistance() {
      return this.kw;
   }

   public int getMinDistance() {
      return this.minDistance;
   }

   public boolean getMuteAfterMax() {
      return this.kx;
   }

   public int getRolloffFactor() {
      return this.ky;
   }

   public synchronized void setParameters(int var1, int var2, boolean var3, int var4) {
      if (var2 > var1 && var1 > 0 && var2 > 0 && var4 >= 0) {
         this.minDistance = var1;
         this.kw = var2;
         this.kx = var3;
         this.ky = var4;
         this.applyChanges();
      } else {
         throw new IllegalArgumentException("Bad args (see spec)");
      }
   }

   protected void doApplyChanges(BasicPlayer var1) {
      nApplyChanges(var1.getPlayerId(), this.minDistance, this.kw, this.kx, this.ky);
   }

   public void activate(Player var1) {
      nActivate(((BasicPlayer)var1).getPlayerId());
      nApplyChanges(((BasicPlayer)var1).getPlayerId(), this.minDistance, this.kw, this.kx, this.ky);
   }

   public void deactivate(Player var1) {
      nDeactivate(((BasicPlayer)var1).getPlayerId());
   }

   private static native void nApplyChanges(int var0, int var1, int var2, boolean var3, int var4);

   private static native void nActivate(int var0);

   private static native void nDeactivate(int var0);
}
