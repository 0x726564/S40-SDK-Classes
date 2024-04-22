package javax.microedition.amms.control.audio3d;

import javax.microedition.media.Control;

public interface DopplerControl extends Control {
   int[] getVelocityCartesian();

   boolean isEnabled();

   void setEnabled(boolean var1);

   void setVelocityCartesian(int var1, int var2, int var3);

   void setVelocitySpherical(int var1, int var2, int var3);
}
