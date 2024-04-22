package com.nokia.mid.impl.isa.mmedia.video;

import com.nokia.mid.impl.isa.mmedia.MediaPlayer;
import com.nokia.mid.impl.isa.mmedia.MediaPrefs;
import com.nokia.mid.impl.isa.mmedia.VolumeCtrlImpl;
import javax.microedition.media.MediaException;

public class RtspPlayer extends MediaPlayer {
   public RtspPlayer() {
      this.mediaOut = new VideoOutImpl(this);
      this.volCtrl = new VolumeCtrlImpl(this, this.mediaOut);
      VideoCtrlImpl videoCtrl = new VideoCtrlImpl(this, (VideoOutImpl)this.mediaOut);
      this.addControl("VolumeControl", this.volCtrl);
      this.addControl("GUIControl", videoCtrl);
      this.addControl("VideoControl", videoCtrl);
      this.playerId = eventConsumer.register((byte)2, this);
   }

   protected void doPrefetch() throws MediaException {
      MediaPrefs.checkPermission(6, 0);
      byte[] locatorAsData = this.locator.getBasicLocator().getBytes();
      this.dataSource.setData(locatorAsData);
      super.doPrefetch();
   }

   public String getDeviceName() {
      return "RTSP STREAM";
   }

   protected void doSetLoopCount(int count) {
   }
}
