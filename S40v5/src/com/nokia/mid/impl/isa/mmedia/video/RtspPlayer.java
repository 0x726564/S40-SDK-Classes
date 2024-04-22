package com.nokia.mid.impl.isa.mmedia.video;

import com.nokia.mid.impl.isa.mmedia.MediaPlayer;
import com.nokia.mid.impl.isa.mmedia.MediaPrefs;
import com.nokia.mid.impl.isa.mmedia.VolumeCtrlImpl;
import javax.microedition.media.MediaException;

public class RtspPlayer extends MediaPlayer {
   public RtspPlayer() {
      this.mediaOut = new VideoOutImpl(this);
      this.volCtrl = new VolumeCtrlImpl(this, this.mediaOut);
      VideoCtrlImpl var1 = new VideoCtrlImpl(this, (VideoOutImpl)this.mediaOut);
      this.addControl("VolumeControl", this.volCtrl);
      this.addControl("GUIControl", var1);
      this.addControl("VideoControl", var1);
      this.playerId = eventConsumer.register((byte)2, this);
   }

   protected void doPrefetch() throws MediaException {
      MediaPrefs.checkPermission(5, 0);
      byte[] var1 = this.locator.getBasicLocator().getBytes();
      this.dataSource.setData(var1);
      super.doPrefetch();
   }

   public String getDeviceName() {
      return "RTSP STREAM";
   }

   protected void doSetLoopCount(int var1) {
   }
}
