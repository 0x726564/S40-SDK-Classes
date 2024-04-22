package com.nokia.mid.impl.isa.mmedia.video;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import com.nokia.mid.impl.isa.mmedia.control.FramePositioningOut;
import com.nokia.mid.impl.isa.mmedia.control.Switchable;
import javax.microedition.media.control.FramePositioningControl;

public class FramePositioningCtrlImpl extends Switchable implements FramePositioningControl {
   private FramePositioningOut framePosOut;

   public FramePositioningCtrlImpl(BasicPlayer var1, FramePositioningOut var2) {
      this.player = var1;
      this.framePosOut = var2;
   }

   public int seek(int var1) {
      return this.framePosOut.seek(var1);
   }

   public int skip(int var1) {
      return this.framePosOut.skip(var1);
   }

   public long mapFrameToTime(int var1) {
      return this.framePosOut.mapFrameToTime(var1);
   }

   public int mapTimeToFrame(long var1) {
      return this.framePosOut.mapTimeToFrame(var1);
   }
}
