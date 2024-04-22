package javax.microedition.amms.control.audio3d;

import javax.microedition.media.Control;

public interface LocationControl extends Control {
   int[] getCartesian();

   void setCartesian(int var1, int var2, int var3);

   void setSpherical(int var1, int var2, int var3);
}
