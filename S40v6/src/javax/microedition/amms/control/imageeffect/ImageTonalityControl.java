package javax.microedition.amms.control.imageeffect;

import javax.microedition.amms.control.EffectControl;

public interface ImageTonalityControl extends EffectControl {
   int AUTO = -1000;
   int NEXT = -1001;
   int PREVIOUS = -1002;

   int setBrightness(int var1);

   int getBrightness();

   int getBrightnessLevels();

   int setContrast(int var1);

   int getContrast();

   int getContrastLevels();

   int setGamma(int var1);

   int getGamma();

   int getGammaLevels();
}
