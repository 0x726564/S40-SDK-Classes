package javax.microedition.amms.control.camera;

import javax.microedition.media.Control;
import javax.microedition.media.MediaException;

public interface ExposureControl extends Control {
   int[] getSupportedFStops();

   int getFStop();

   void setFStop(int var1) throws MediaException;

   int getMinExposureTime();

   int getMaxExposureTime();

   int getExposureTime();

   int setExposureTime(int var1) throws MediaException;

   int[] getSupportedISOs();

   int getISO();

   void setISO(int var1) throws MediaException;

   int[] getSupportedExposureCompensations();

   int getExposureCompensation();

   void setExposureCompensation(int var1) throws MediaException;

   int getExposureValue();

   String[] getSupportedLightMeterings();

   void setLightMetering(String var1);

   String getLightMetering();
}
