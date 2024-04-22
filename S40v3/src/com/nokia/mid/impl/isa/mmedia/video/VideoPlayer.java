package com.nokia.mid.impl.isa.mmedia.video;

import com.nokia.mid.impl.isa.mmedia.MediaPlayer;
import com.nokia.mid.impl.isa.mmedia.RateCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.StopTimeCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.VolumeCtrlImpl;
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
      if (this.isVideoType()) {
         this.stopCtrl = new StopTimeCtrlImpl(this);
         this.addControl("StopTimeControl", this.stopCtrl);
      } else if (this.isAnimatedImageType()) {
         this.stopCtrl = new StopTimeCtrlImpl(this, (StopTimeOut)this.mediaOut);
         this.addControl("StopTimeControl", this.stopCtrl);
      }

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

   protected long doGetDuration() {
      return this.isVideoType() ? super.doGetDuration() : 0L;
   }

   protected boolean isVideoType() {
      return this.locator.contentType.equalsIgnoreCase("video/3gpp") || this.locator.contentType.equalsIgnoreCase("video/mp4") || this.locator.contentType.equalsIgnoreCase("video/mpeg") || this.locator.contentType.equalsIgnoreCase("video/3gpp2");
   }

   protected boolean isAnimatedImageType() {
      return this.locator.contentType.equalsIgnoreCase("image/gif");
   }
}
