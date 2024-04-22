package javax.microedition.media.control;

import javax.microedition.media.Control;

public interface FramePositioningControl extends Control {
   int seek(int var1);

   int skip(int var1);

   long mapFrameToTime(int var1);

   int mapTimeToFrame(long var1);
}
