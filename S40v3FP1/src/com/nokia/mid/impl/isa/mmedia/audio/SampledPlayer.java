package com.nokia.mid.impl.isa.mmedia.audio;

import com.nokia.mid.impl.isa.mmedia.MediaPlayer;
import com.nokia.mid.impl.isa.mmedia.MetaDataCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.ParsedLocator;
import com.nokia.mid.impl.isa.mmedia.RateCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.StopTimeCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.VolumeCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.control.RateOut;
import com.nokia.mid.impl.isa.mmedia.control.StopTimeOut;
import javax.microedition.media.MediaException;

public class SampledPlayer extends MediaPlayer {
   StopTimeCtrlImpl stopCtrl;
   RateCtrlImpl rateCtrl;

   public SampledPlayer() {
      this.mediaOut = new AudioOutImpl(this);
      this.volCtrl = new VolumeCtrlImpl(this, this.mediaOut);
      this.stopCtrl = new StopTimeCtrlImpl(this, (StopTimeOut)this.mediaOut);
      this.addControl("StopTimeControl", this.stopCtrl);
      this.addControl("VolumeControl", this.volCtrl);
      this.playerId = eventConsumer.register((byte)0, this);
   }

   public void dispatchEvent(String var1, Object var2) {
      if (var1 == "stoppedAtTime") {
         this.stopCtrl.resetStopTime();
      }

      super.dispatchEvent(var1, var2);
   }

   public void setParsedLocator(ParsedLocator var1) throws MediaException {
      super.setParsedLocator(var1);
      if (this.locator.contentType.equals("audio/mpeg3") || this.locator.contentType.equals("audio/mpeg") || this.locator.contentType.equals("audio/mp3") || this.locator.contentType.equals("audio/mp4") || this.locator.contentType.equals("audio/mpeg4")) {
         MetaDataCtrlImpl var2 = new MetaDataCtrlImpl(this, this.mediaOut);
         this.addControl("MetaDataControl", var2);
      }

      this.rateCtrl = new RateCtrlImpl(this, (RateOut)this.mediaOut);
      if (this.rateCtrl.isValid()) {
         this.addControl("RateControl", this.rateCtrl);
      }

   }
}
