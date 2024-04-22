package javax.microedition.amms.control.camera;

import javax.microedition.media.Control;

public interface FlashControl extends Control {
   int OFF = 1;
   int AUTO = 2;
   int AUTO_WITH_REDEYEREDUCE = 3;
   int FORCE = 4;
   int FORCE_WITH_REDEYEREDUCE = 5;
   int FILLIN = 6;

   int[] getSupportedModes();

   void setMode(int var1);

   int getMode();

   boolean isFlashReady();
}
