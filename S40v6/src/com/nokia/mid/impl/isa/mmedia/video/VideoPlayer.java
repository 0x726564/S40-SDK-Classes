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
      VideoCtrlImpl videoCtrl = new VideoCtrlImpl(this, (VideoOutImpl)this.mediaOut);
      this.stopCtrl = new StopTimeCtrlImpl(this, (StopTimeOut)this.mediaOut);
      this.addControl("VolumeControl", this.volCtrl);
      this.addControl("GUIControl", videoCtrl);
      this.addControl("VideoControl", videoCtrl);
      this.addControl("StopTimeControl", this.stopCtrl);
      if (!this.dataSource.useActiveSource) {
         FramePositioningCtrlImpl frameCtrl = new FramePositioningCtrlImpl(this, (FramePositioningOut)this.mediaOut);
         this.addControl("FramePositioningControl", frameCtrl);
         RateCtrlImpl rateCtrl = new RateCtrlImpl(this, (RateOut)this.mediaOut);
         if (rateCtrl.isValid()) {
            this.addControl("RateControl", rateCtrl);
         }

         if (!this.locator.contentType.equalsIgnoreCase("video/mpeg")) {
            MetaDataCtrlImpl metaDataCtrl = new MetaDataCtrlImpl(this, this.mediaOut);
            this.addControl("MetaDataControl", metaDataCtrl);
         }
      }

   }

   protected void doPrefetch() throws MediaException {
      super.doPrefetch();
      nVideoInitialise(this.playerId);
   }

   public String getDeviceName() {
      return "VIDEO";
   }

   public void dispatchEvent(String evt, Object evtData) {
      if (evt == "stoppedAtTime" && this.stopCtrl != null) {
         this.stopCtrl.resetStopTime();
      }

      super.dispatchEvent(evt, evtData);
   }

   protected void doSetLoopCount(int count) {
      if (!this.dataSource.useActiveSource) {
         super.doSetLoopCount(count);
      }

   }

   private static native void nVideoInitialise(int var0);
}
