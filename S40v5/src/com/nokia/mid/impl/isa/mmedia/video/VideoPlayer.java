package com.nokia.mid.impl.isa.mmedia.video;

import com.nokia.mid.impl.isa.mmedia.MediaPlayer;
import com.nokia.mid.impl.isa.mmedia.MetaDataCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.ParsedLocator;
import com.nokia.mid.impl.isa.mmedia.RateCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.StopTimeCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.VolumeCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.control.FramePositioningOut;
import com.nokia.mid.impl.isa.mmedia.control.RateOut;
import com.nokia.mid.impl.isa.mmedia.control.StopTimeOut;
import javax.microedition.media.MediaException;

public class VideoPlayer extends MediaPlayer {
   private StopTimeCtrlImpl am;

   public VideoPlayer() {
      this.mediaOut = new VideoOutImpl(this);
      this.playerId = eventConsumer.register((byte)2, this);
   }

   protected void doRealize() throws MediaException {
      super.doRealize();
      this.volCtrl = new VolumeCtrlImpl(this, this.mediaOut);
      VideoCtrlImpl var1 = new VideoCtrlImpl(this, (VideoOutImpl)this.mediaOut);
      this.am = new StopTimeCtrlImpl(this, (StopTimeOut)this.mediaOut);
      this.addControl("VolumeControl", this.volCtrl);
      this.addControl("GUIControl", var1);
      this.addControl("VideoControl", var1);
      this.addControl("StopTimeControl", this.am);
      if (!this.dataSource.useActiveSource) {
         FramePositioningCtrlImpl var2 = new FramePositioningCtrlImpl(this, (FramePositioningOut)this.mediaOut);
         this.addControl("FramePositioningControl", var2);
      }

      RateCtrlImpl var3;
      if ((var3 = new RateCtrlImpl(this, (RateOut)this.mediaOut)).isValid()) {
         this.addControl("RateControl", var3);
      }

   }

   protected void doPrefetch() throws MediaException {
      super.doPrefetch();
      nVideoInitialise(this.playerId);
   }

   public String getDeviceName() {
      return "VIDEO";
   }

   public void dispatchEvent(String var1, Object var2) {
      if (var1 == "stoppedAtTime" && this.am != null) {
         this.am.resetStopTime();
      }

      super.dispatchEvent(var1, var2);
   }

   public void setParsedLocator(ParsedLocator var1) throws MediaException {
      super.setParsedLocator(var1);
      if (!this.locator.contentType.equalsIgnoreCase("video/mpeg")) {
         MetaDataCtrlImpl var2 = new MetaDataCtrlImpl(this, this.mediaOut);
         this.addControl("MetaDataControl", var2);
      }

   }

   private static native void nVideoInitialise(int var0);
}
