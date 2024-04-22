package javax.microedition.amms.control.tuner;

import javax.microedition.media.Control;
import javax.microedition.media.MediaException;

public interface TunerControl extends Control {
   int MONO = 1;
   int STEREO = 2;
   int AUTO = 3;
   String MODULATION_FM = "fm";
   String MODULATION_AM = "am";

   int getMinFreq(String var1);

   int getMaxFreq(String var1);

   int setFrequency(int var1, String var2);

   int getFrequency();

   int seek(int var1, String var2, boolean var3) throws MediaException;

   boolean getSquelch();

   void setSquelch(boolean var1) throws MediaException;

   String getModulation();

   int getSignalStrength() throws MediaException;

   int getStereoMode();

   void setStereoMode(int var1);

   int getNumberOfPresets();

   void usePreset(int var1);

   void setPreset(int var1);

   void setPreset(int var1, int var2, String var3, int var4);

   int getPresetFrequency(int var1);

   String getPresetModulation(int var1);

   int getPresetStereoMode(int var1) throws MediaException;

   String getPresetName(int var1);

   void setPresetName(int var1, String var2);
}
