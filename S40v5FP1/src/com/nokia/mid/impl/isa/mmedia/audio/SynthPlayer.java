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
   StopTimeCtrlImpl stopCtrl;

   public SynthPlayer() {
      this.mediaOut = new AudioOutImpl(this);
      this.volCtrl = new VolumeCtrlImpl(this, this.mediaOut);
      this.addControl("MIDIControl", new MIDICtrl(this, (AudioOutImpl)this.mediaOut));
      this.addControl("VolumeControl", new VolumeCtrlImpl(this, this.mediaOut));
      this.playerId = eventConsumer.register((byte)0, this);
   }

   public void setParsedLocator(ParsedLocator loc) throws MediaException {
      super.setParsedLocator(loc);
      if (!this.locator.isMidiDeviceLocator()) {
         this.stopCtrl = new StopTimeCtrlImpl(this, (StopTimeOut)this.mediaOut);
         this.addControl("TempoControl", new TempoCtrlImpl(this, (AudioOutImpl)this.mediaOut));
         this.addControl("StopTimeControl", this.stopCtrl);
         RateCtrlImpl rateCtrl = new RateCtrlImpl(this, (RateOut)this.mediaOut);
         if (rateCtrl.isValid()) {
            this.addControl("RateControl", rateCtrl);
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
         if (!((AudioOutImpl)this.mediaOut).openMidiEventSession(this.volCtrl.readStoredVol())) {
            throw new MediaException("device error");
         }

         this.setActiveState(true);
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

   protected long doSetMediaTime(long time) throws MediaException {
      if (this.locator.isMidiDeviceLocator()) {
         throw new MediaException("No media");
      } else {
         return super.doSetMediaTime(time);
      }
   }

   public void dispatchEvent(String evt, Object evtData) {
      if (evt == "stoppedAtTime") {
         this.stopCtrl.resetStopTime();
      }

      super.dispatchEvent(evt, evtData);
   }
}
