package com.nokia.mid.impl.isa.amms.audio;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import javax.microedition.amms.control.audio3d.DistanceAttenuationControl;
import javax.microedition.media.Player;

public class DistanceAttControlSoundSource3DImpl extends BaseSoundSource3DControlImpl implements DistanceAttenuationControl {
   private int maxDistance = Integer.MAX_VALUE;
   private int minDistance = 1000;
   private boolean muteAfterMax = true;
   private int rollOffFactor = 1000;

   public int getMaxDistance() {
      return this.maxDistance;
   }

   public int getMinDistance() {
      return this.minDistance;
   }

   public boolean getMuteAfterMax() {
      return this.muteAfterMax;
   }

   public int getRolloffFactor() {
      return this.rollOffFactor;
   }

   public synchronized void setParameters(int minDistance, int maxDistance, boolean muteAfterMax, int rolloffFactor) {
      if (maxDistance > minDistance && minDistance > 0 && maxDistance > 0 && rolloffFactor >= 0) {
         this.minDistance = minDistance;
         this.maxDistance = maxDistance;
         this.muteAfterMax = muteAfterMax;
         this.rollOffFactor = rolloffFactor;
         this.applyChanges();
      } else {
         throw new IllegalArgumentException("Bad args (see spec)");
      }
   }

   protected void doApplyChanges(BasicPlayer p) {
      nApplyChanges(p.getPlayerId(), this.minDistance, this.maxDistance, this.muteAfterMax, this.rollOffFactor);
   }

   public void activate(Player p) {
      nActivate(((BasicPlayer)p).getPlayerId());
      nApplyChanges(((BasicPlayer)p).getPlayerId(), this.minDistance, this.maxDistance, this.muteAfterMax, this.rollOffFactor);
   }

   public void deactivate(Player p) {
      nDeactivate(((BasicPlayer)p).getPlayerId());
   }

   private static native void nApplyChanges(int var0, int var1, int var2, boolean var3, int var4);

   private static native void nActivate(int var0);

   private static native void nDeactivate(int var0);
}
