package com.nokia.mid.impl.isa.mmedia.video;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import com.nokia.mid.impl.isa.mmedia.control.FramePositioningOut;
import com.nokia.mid.impl.isa.mmedia.control.Switchable;
import javax.microedition.media.control.FramePositioningControl;

public class FramePositioningCtrlImpl extends Switchable implements FramePositioningControl {
   private FramePositioningOut fV;

   public FramePositioningCtrlImpl(BasicPlayer var1, FramePositioningOut var2) {
      this.player = var1;
      this.fV = var2;
   }

   public int seek(int var1) {
      boolean var2 = false;
      synchronized(this.player) {
         int var5 = this.fV.seek(var1);
         return var5;
      }
   }

   public int skip(int var1) {
      boolean var2 = false;
      synchronized(this.player) {
         int var5 = this.fV.skip(var1);
         return var5;
      }
   }

   public long mapFrameToTime(int var1) {
      long var2 = 0L;
      synchronized(this.player) {
         var2 = this.fV.mapFrameToTime(var1);
         return var2;
      }
   }

   public int mapTimeToFrame(long var1) {
      boolean var3 = false;
      synchronized(this.player) {
         int var6 = this.fV.mapTimeToFrame(var1);
         return var6;
      }
   }
}
