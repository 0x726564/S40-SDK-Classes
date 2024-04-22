package com.nokia.mid.impl.isa.mmedia.audio;

import com.nokia.mid.impl.isa.mmedia.MediaPlayer;
import com.nokia.mid.impl.isa.mmedia.MetaDataCtrlImpl;
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

   public void dispatchEvent(String evt, Object evtData) {
      if (evt == "stoppedAtTime") {
         this.stopCtrl.resetStopTime();
      }

      super.dispatchEvent(evt, evtData);
   }

   public void doRealize() throws MediaException {
      super.doRealize();
      if (!this.dataSource.useActiveSource && ((AudioOutImpl)this.mediaOut).nIsMetaDataSupported(this.locator.contentType)) {
         MetaDataCtrlImpl metaDataCtrl = new MetaDataCtrlImpl(this, this.mediaOut);
         this.addControl("MetaDataControl", metaDataCtrl);
      }

      this.rateCtrl = new RateCtrlImpl(this, (RateOut)this.mediaOut);
      if (this.rateCtrl.isValid()) {
         this.addControl("RateControl", this.rateCtrl);
      }

   }
}
