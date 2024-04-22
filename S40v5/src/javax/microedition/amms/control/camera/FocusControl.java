package javax.microedition.amms.control.camera;

import javax.microedition.media.Control;
import javax.microedition.media.MediaException;

public interface FocusControl extends Control {
   int AUTO = -1000;
   int AUTO_LOCK = -1005;
   int NEXT = -1001;
   int PREVIOUS = -1002;
   int UNKNOWN = -1004;

   int setFocus(int var1) throws MediaException;

   int getFocus();

   int getMinFocus();

   int getFocusSteps();

   boolean isManualFocusSupported();

   boolean isAutoFocusSupported();

   boolean isMacroSupported();

   void setMacro(boolean var1) throws MediaException;

   boolean getMacro();
}
