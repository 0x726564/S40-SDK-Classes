package javax.microedition.media.control;

import javax.microedition.media.Control;

public interface StopTimeControl extends Control {
   long RESET = Long.MAX_VALUE;

   void setStopTime(long var1);

   long getStopTime();
}
