package javax.microedition.media.control;

import javax.microedition.media.Control;

public interface PitchControl extends Control {
   int setPitch(int var1);

   int getPitch();

   int getMaxPitch();

   int getMinPitch();
}
