package com.nokia.mid.impl.isa.amms.audio;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import java.util.Enumeration;
import javax.microedition.media.Player;

public abstract class BaseSoundSource3DControlImpl {
   private SoundSource3DImpl kv;

   public void setSoundSource(SoundSource3DImpl var1) {
      this.kv = var1;
   }

   public abstract void activate(Player var1);

   public abstract void deactivate(Player var1);

   protected final void applyChanges() {
      synchronized(this.kv) {
         Enumeration var2 = this.kv.getActivePlayers();

         while(var2.hasMoreElements()) {
            BasicPlayer var3;
            synchronized(var3 = (BasicPlayer)var2.nextElement()) {
               this.doApplyChanges(var3);
            }
         }

      }
   }

   protected abstract void doApplyChanges(BasicPlayer var1);
}
