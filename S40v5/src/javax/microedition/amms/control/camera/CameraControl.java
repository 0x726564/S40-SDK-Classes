package javax.microedition.amms.control.camera;

import javax.microedition.media.Control;
import javax.microedition.media.MediaException;

public interface CameraControl extends Control {
   int ROTATE_LEFT = 2;
   int ROTATE_RIGHT = 3;
   int ROTATE_NONE = 1;
   int UNKNOWN = -1004;

   int getCameraRotation();

   void enableShutterFeedback(boolean var1) throws MediaException;

   boolean isShutterFeedbackEnabled();

   String[] getSupportedExposureModes();

   void setExposureMode(String var1);

   String getExposureMode();

   int[] getSupportedVideoResolutions();

   int[] getSupportedStillResolutions();

   void setVideoResolution(int var1);

   void setStillResolution(int var1);

   int getVideoResolution();

   int getStillResolution();
}
