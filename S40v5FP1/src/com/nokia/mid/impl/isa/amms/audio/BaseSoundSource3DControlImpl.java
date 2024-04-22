package com.nokia.mid.impl.isa.amms.audio;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import java.util.Enumeration;
import javax.microedition.media.Player;

public abstract class BaseSoundSource3DControlImpl {
   private SoundSource3DImpl source3D;

   public void setSoundSource(SoundSource3DImpl src) {
      this.source3D = src;
   }

   public abstract void activate(Player var1);

   public abstract void deactivate(Player var1);

   protected final void applyChanges() {
      synchronized(this.source3D) {
         Enumeration players = this.source3D.getActivePlayers();

         while(players.hasMoreElements()) {
            BasicPlayer p = (BasicPlayer)players.nextElement();
            synchronized(p) {
               this.doApplyChanges(p);
            }
         }

      }
   }

   protected abstract void doApplyChanges(BasicPlayer var1);
}
