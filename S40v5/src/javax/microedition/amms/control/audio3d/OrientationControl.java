package javax.microedition.amms.control.audio3d;

import javax.microedition.media.Control;

public interface OrientationControl extends Control {
   int[] getOrientationVectors();

   void setOrientation(int[] var1, int[] var2) throws IllegalArgumentException;

   void setOrientation(int var1, int var2, int var3);
}
