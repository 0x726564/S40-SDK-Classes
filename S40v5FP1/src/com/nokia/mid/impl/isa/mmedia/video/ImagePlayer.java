package com.nokia.mid.impl.isa.mmedia.video;

import com.nokia.mid.impl.isa.mmedia.MediaPlayer;
import com.nokia.mid.impl.isa.mmedia.RateCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.StopTimeCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.control.RateOut;
import com.nokia.mid.impl.isa.mmedia.control.StopTimeOut;
import javax.microedition.media.MediaException;

public class ImagePlayer extends MediaPlayer {
   private StopTimeCtrlImpl stopCtrl;

   public ImagePlayer() {
      this.mediaOut = new VideoOutImpl(this);
      this.playerId = eventConsumer.register((byte)2, this);
   }

   protected void doRealize() throws MediaException {
      super.doRealize();
      VideoCtrlImpl videoCtrl = new VideoCtrlImpl(this, (VideoOutImpl)this.mediaOut);
      if (nIsAnimatedImage(this.locator.contentType)) {
         this.stopCtrl = new StopTimeCtrlImpl(this, (StopTimeOut)this.mediaOut);
         this.addControl("StopTimeControl", this.stopCtrl);
      }

      RateCtrlImpl rateCtrl = new RateCtrlImpl(this, (RateOut)this.mediaOut);
      if (rateCtrl.isValid()) {
         this.addControl("RateControl", rateCtrl);
      }

      this.addControl("GUIControl", videoCtrl);
      this.addControl("VideoControl", videoCtrl);
   }

   public String getDeviceName() {
      return "IMAGE";
   }

   public void dispatchEvent(String evt, Object evtData) {
      if (evt == "stoppedAtTime" && this.stopCtrl != null) {
         this.stopCtrl.resetStopTime();
      }

      super.dispatchEvent(evt, evtData);
   }

   protected long doGetDuration() {
      return nIsAnimatedImage(this.locator.contentType) ? super.doGetDuration() : 0L;
   }

   static native boolean nIsAnimatedImage(String var0);
}
