package com.nokia.mid.impl.isa.mmedia.control;

public interface FramePositioningOut {
   int seek(int var1);

   int skip(int var1);

   long mapFrameToTime(int var1);

   int mapTimeToFrame(long var1);
}
