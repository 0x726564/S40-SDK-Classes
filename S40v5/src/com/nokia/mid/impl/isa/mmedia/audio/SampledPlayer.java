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
   private StopTimeCtrlImpl am;
   private RateCtrlImpl bQ;

   public SampledPlayer() {
      this.mediaOut = new AudioOutImpl(this);
      this.volCtrl = new VolumeCtrlImpl(this, this.mediaOut);
      this.am = new StopTimeCtrlImpl(this, (StopTimeOut)this.mediaOut);
      this.addControl("StopTimeControl", this.am);
      this.addControl("VolumeControl", this.volCtrl);
      this.playerId = eventConsumer.register((byte)0, this);
   }

   public void dispatchEvent(String var1, Object var2) {
      if (var1 == "stoppedAtTime") {
         this.am.resetStopTime();
      }

      super.dispatchEvent(var1, var2);
   }

   public void doRealize() throws MediaException {
      super.doRealize();
      if (!this.dataSource.useActiveSource && ((AudioOutImpl)this.mediaOut).nIsMetaDataSupported(this.locator.contentType)) {
         MetaDataCtrlImpl var1 = new MetaDataCtrlImpl(this, this.mediaOut);
         this.addControl("MetaDataControl", var1);
      }

      this.bQ = new RateCtrlImpl(this, (RateOut)this.mediaOut);
      if (this.bQ.isValid()) {
         this.addControl("RateControl", this.bQ);
      }

   }
}
