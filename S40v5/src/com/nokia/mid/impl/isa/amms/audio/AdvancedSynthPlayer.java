package com.nokia.mid.impl.isa.amms.audio;

import com.nokia.mid.impl.isa.mmedia.audio.SynthPlayer;
import javax.microedition.media.MediaException;

public class AdvancedSynthPlayer extends SynthPlayer implements AttachableToSoundSource3D {
   protected void doDeallocate() {
      SoundSource3DImpl.b(this);
      super.doDeallocate();
   }

   protected void doPrefetch() throws MediaException {
      super.doPrefetch();
      SoundSource3DImpl.a(this);
   }

   public synchronized void dispatchEvent(String var1, Object var2) {
      if (var1 == "deviceUnavailable") {
         SoundSource3DImpl.b(this);
      }

      super.dispatchEvent(var1, var2);
   }
}
