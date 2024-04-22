package javax.microedition.amms.control.camera;

import javax.microedition.media.Control;

public interface ZoomControl extends Control {
   int NEXT = -1001;
   int PREVIOUS = -1002;
   int UNKNOWN = -1004;

   int setOpticalZoom(int var1);

   int getOpticalZoom();

   int getMaxOpticalZoom();

   int getOpticalZoomLevels();

   int getMinFocalLength();

   int setDigitalZoom(int var1);

   int getDigitalZoom();

   int getMaxDigitalZoom();

   int getDigitalZoomLevels();
}
