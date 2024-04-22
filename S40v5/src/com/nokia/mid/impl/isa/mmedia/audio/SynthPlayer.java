package com.nokia.mid.impl.isa.mmedia.audio;

import com.nokia.mid.impl.isa.mmedia.MediaPlayer;
import com.nokia.mid.impl.isa.mmedia.MediaPrefs;
import com.nokia.mid.impl.isa.mmedia.ParsedLocator;
import com.nokia.mid.impl.isa.mmedia.RateCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.StopTimeCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.VolumeCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.control.RateOut;
import com.nokia.mid.impl.isa.mmedia.control.StopTimeOut;
import javax.microedition.media.MediaException;

public class SynthPlayer extends MediaPlayer {
   private StopTimeCtrlImpl am;

   public SynthPlayer() {
      this.mediaOut = new AudioOutImpl(this);
      this.volCtrl = new VolumeCtrlImpl(this, this.mediaOut);
      this.addControl("MIDIControl", new MIDICtrl(this, (AudioOutImpl)this.mediaOut));
      this.playerId = eventConsumer.register((byte)0, this);
   }

   public void setParsedLocator(ParsedLocator var1) throws MediaException {
      super.setParsedLocator(var1);
      if (!this.locator.isMidiDeviceLocator()) {
         this.am = new StopTimeCtrlImpl(this, (StopTimeOut)this.mediaOut);
         this.addControl("TempoControl", new TempoCtrlImpl(this, (AudioOutImpl)this.mediaOut));
         this.addControl("StopTimeControl", this.am);
         this.addControl("VolumeControl", this.volCtrl);
         RateCtrlImpl var2;
         if ((var2 = new RateCtrlImpl(this, (RateOut)this.mediaOut)).isValid()) {
            this.addControl("RateControl", var2);
         }

         if (MediaPrefs.nIsFeatureSupported(2)) {
            this.addControl("PitchControl", new PitchCtrlImpl(this, (AudioOutImpl)this.mediaOut));
         }
      }

   }

   protected long doGetDuration() {
      return this.locator.isMidiDeviceLocator() ? 0L : super.doGetDuration();
   }

   protected void doRealize() throws MediaException {
      if (!this.locator.isMidiDeviceLocator()) {
         super.doRealize();
      }

   }

   protected void doPrefetch() throws MediaException {
      if (this.locator.isMidiDeviceLocator()) {
         if (!((AudioOutImpl)this.mediaOut).g(this.volCtrl.readStoredVol())) {
            throw new MediaException("device error");
         } else {
            this.setActiveState(true);
         }
      } else {
         super.doPrefetch();
      }
   }

   protected void doStart() throws MediaException {
      if (this.locator.isMidiDeviceLocator()) {
         eventConsumer.serializeEvent(this.playerId, 7, 0L);
         eventConsumer.serializeEvent(this.playerId, 4, 0L);
      } else {
         super.doStart();
      }
   }

   protected void doStop() throws MediaException {
      if (this.locator.isMidiDeviceLocator()) {
         eventConsumer.serializeEvent(this.playerId, 8, 0L);
      } else {
         super.doStop();
      }
   }

   protected long doSetMediaTime(long var1) throws MediaException {
      if (this.locator.isMidiDeviceLocator()) {
         throw new MediaException("No media");
      } else {
         return super.doSetMediaTime(var1);
      }
   }

   public void dispatchEvent(String var1, Object var2) {
      if (var1 == "stoppedAtTime") {
         this.am.resetStopTime();
      }

      super.dispatchEvent(var1, var2);
   }
}
