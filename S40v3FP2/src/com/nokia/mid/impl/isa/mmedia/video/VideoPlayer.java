package com.nokia.mid.impl.isa.mmedia.video;

import com.nokia.mid.impl.isa.mmedia.MediaPlayer;
import com.nokia.mid.impl.isa.mmedia.MetaDataCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.RateCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.StopTimeCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.VolumeCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.control.FramePositioningOut;
import com.nokia.mid.impl.isa.mmedia.control.RateOut;
import com.nokia.mid.impl.isa.mmedia.control.StopTimeOut;
import javax.microedition.media.MediaException;

public class VideoPlayer extends MediaPlayer {
   private StopTimeCtrlImpl stopCtrl;

   public VideoPlayer() {
      this.mediaOut = new VideoOutImpl(this);
      this.playerId = eventConsumer.register((byte)2, this);
   }

   protected void doRealize() throws MediaException {
      super.doRealize();
      this.volCtrl = new VolumeCtrlImpl(this, this.mediaOut);
      VideoCtrlImpl var1 = new VideoCtrlImpl(this, (VideoOutImpl)this.mediaOut);
      this.stopCtrl = new StopTimeCtrlImpl(this, (StopTimeOut)this.mediaOut);
      this.addControl("StopTimeControl", this.stopCtrl);
      FramePositioningCtrlImpl var4 = new FramePositioningCtrlImpl(this, (FramePositioningOut)this.mediaOut);
      if (var4 != null) {
         this.addControl("FramePositioningControl", var4);
      }

      MetaDataCtrlImpl var3 = new MetaDataCtrlImpl(this, this.mediaOut);
      this.addControl("MetaDataControl", var3);
      RateCtrlImpl var2 = new RateCtrlImpl(this, (RateOut)this.mediaOut);
      if (var2.isValid()) {
         this.addControl("RateControl", var2);
      }

      this.addControl("VolumeControl", this.volCtrl);
      this.addControl("GUIControl", var1);
      this.addControl("VideoControl", var1);
   }

   public String getDeviceName() {
      return "VIDEO";
   }

   public void dispatchEvent(String var1, Object var2) {
      if (var1 == "stoppedAtTime" && this.stopCtrl != null) {
         this.stopCtrl.resetStopTime();
      }

      super.dispatchEvent(var1, var2);
   }
}
