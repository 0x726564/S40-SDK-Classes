package javax.microedition.amms.control.audio3d;

import javax.microedition.media.Control;

public interface DistanceAttenuationControl extends Control {
   int getMaxDistance();

   int getMinDistance();

   boolean getMuteAfterMax();

   int getRolloffFactor();

   void setParameters(int var1, int var2, boolean var3, int var4);
}
