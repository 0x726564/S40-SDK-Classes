package javax.microedition.amms.control.audioeffect;

import javax.microedition.amms.control.EffectControl;

public interface ChorusControl extends EffectControl {
   int setWetLevel(int var1);

   int getWetLevel();

   void setModulationRate(int var1);

   int getModulationRate();

   int getMinModulationRate();

   int getMaxModulationRate();

   void setModulationDepth(int var1);

   int getModulationDepth();

   int getMaxModulationDepth();

   void setAverageDelay(int var1);

   int getAverageDelay();

   int getMaxAverageDelay();
}
