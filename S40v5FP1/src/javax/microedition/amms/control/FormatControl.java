package javax.microedition.amms.control;

import javax.microedition.media.Control;
import javax.microedition.media.MediaException;

public interface FormatControl extends Control {
   int METADATA_NOT_SUPPORTED = 0;
   int METADATA_SUPPORTED_FIXED_KEYS = 1;
   int METADATA_SUPPORTED_FREE_KEYS = 2;
   String PARAM_BITRATE = "bitrate";
   String PARAM_BITRATE_TYPE = "bitrate type";
   String PARAM_SAMPLERATE = "sample rate";
   String PARAM_FRAMERATE = "frame rate";
   String PARAM_QUALITY = "quality";
   String PARAM_VERSION_TYPE = "version type";

   String[] getSupportedFormats();

   String[] getSupportedStrParameters();

   String[] getSupportedIntParameters();

   String[] getSupportedStrParameterValues(String var1);

   int[] getSupportedIntParameterRange(String var1);

   void setFormat(String var1);

   String getFormat();

   int setParameter(String var1, int var2);

   void setParameter(String var1, String var2);

   String getStrParameterValue(String var1);

   int getIntParameterValue(String var1);

   int getEstimatedBitRate() throws MediaException;

   void setMetadata(String var1, String var2) throws MediaException;

   String[] getSupportedMetadataKeys();

   int getMetadataSupportMode();

   void setMetadataOverride(boolean var1);

   boolean getMetadataOverride();
}
