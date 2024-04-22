package com.nokia.mid.impl.isa.amms.audio;

import com.nokia.mid.impl.isa.mmedia.audio.SynthPlayer;
import javax.microedition.media.MediaException;

public class AdvancedSynthPlayer extends SynthPlayer implements AttachableToSoundSource3D {
   protected void doDeallocate() {
      SoundSource3DImpl.notifyInactive(this);
      super.doDeallocate();
   }

   protected void doPrefetch() throws MediaException {
      super.doPrefetch();
      SoundSource3DImpl.notifyActive(this);
   }

   public synchronized void dispatchEvent(String evt, Object evtData) {
      if (evt == "deviceUnavailable") {
         SoundSource3DImpl.notifyInactive(this);
      }

      super.dispatchEvent(evt, evtData);
   }
}
