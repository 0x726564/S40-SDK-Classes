package com.nokia.mid.impl.isa.mmedia.video;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import com.nokia.mid.impl.isa.mmedia.control.FramePositioningOut;
import com.nokia.mid.impl.isa.mmedia.control.Switchable;
import javax.microedition.media.control.FramePositioningControl;

public class FramePositioningCtrlImpl extends Switchable implements FramePositioningControl {
   private FramePositioningOut framePosOut;

   public FramePositioningCtrlImpl(BasicPlayer player, FramePositioningOut framePosOut) {
      this.player = player;
      this.framePosOut = framePosOut;
   }

   public int seek(int frameNumber) {
      int result = false;
      synchronized(this.player) {
         int result = this.framePosOut.seek(frameNumber);
         return result;
      }
   }

   public int skip(int framesToSkip) {
      int result = false;
      synchronized(this.player) {
         int result = this.framePosOut.skip(framesToSkip);
         return result;
      }
   }

   public long mapFrameToTime(int frameNumber) {
      long result = 0L;
      if (frameNumber < 0) {
         result = -1L;
      } else {
         synchronized(this.player) {
            result = this.framePosOut.mapFrameToTime(frameNumber);
         }
      }

      return result;
   }

   public int mapTimeToFrame(long time) {
      int result = false;
      int result;
      if (time < 0L) {
         result = -1;
      } else {
         synchronized(this.player) {
            result = this.framePosOut.mapTimeToFrame(time);
         }
      }

      return result;
   }
}
