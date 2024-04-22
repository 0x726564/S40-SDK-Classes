package javax.microedition.amms.control.audioeffect;

import javax.microedition.amms.control.EffectControl;

public interface EqualizerControl extends EffectControl {
   int UNDEFINED = -1004;

   int getMinBandLevel();

   int getMaxBandLevel();

   void setBandLevel(int var1, int var2) throws IllegalArgumentException;

   int getBandLevel(int var1) throws IllegalArgumentException;

   int getNumberOfBands();

   int getCenterFreq(int var1) throws IllegalArgumentException;

   int getBand(int var1);

   int setBass(int var1) throws IllegalArgumentException;

   int setTreble(int var1) throws IllegalArgumentException;

   int getBass();

   int getTreble();
}
