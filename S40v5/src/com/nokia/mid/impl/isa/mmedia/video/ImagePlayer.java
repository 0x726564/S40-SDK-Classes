package com.nokia.mid.impl.isa.mmedia.video;

import com.nokia.mid.impl.isa.mmedia.MediaPlayer;
import com.nokia.mid.impl.isa.mmedia.RateCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.StopTimeCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.control.RateOut;
import com.nokia.mid.impl.isa.mmedia.control.StopTimeOut;
import javax.microedition.media.MediaException;

public class ImagePlayer extends MediaPlayer {
   private StopTimeCtrlImpl am;

   public ImagePlayer() {
      this.mediaOut = new VideoOutImpl(this);
      this.playerId = eventConsumer.register((byte)2, this);
   }

   protected void doRealize() throws MediaException {
      super.doRealize();
      VideoCtrlImpl var1 = new VideoCtrlImpl(this, (VideoOutImpl)this.mediaOut);
      if (nIsAnimatedImage(this.locator.contentType)) {
         this.am = new StopTimeCtrlImpl(this, (StopTimeOut)this.mediaOut);
         this.addControl("StopTimeControl", this.am);
      }

      RateCtrlImpl var2;
      if ((var2 = new RateCtrlImpl(this, (RateOut)this.mediaOut)).isValid()) {
         this.addControl("RateControl", var2);
      }

      this.addControl("GUIControl", var1);
      this.addControl("VideoControl", var1);
   }

   public String getDeviceName() {
      return "IMAGE";
   }

   public void dispatchEvent(String var1, Object var2) {
      if (var1 == "stoppedAtTime" && this.am != null) {
         this.am.resetStopTime();
      }

      super.dispatchEvent(var1, var2);
   }

   protected long doGetDuration() {
      return nIsAnimatedImage(this.locator.contentType) ? super.doGetDuration() : 0L;
   }

   static native boolean nIsAnimatedImage(String var0);
}
