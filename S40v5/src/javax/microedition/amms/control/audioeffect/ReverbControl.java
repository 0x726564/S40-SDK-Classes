package javax.microedition.amms.control.audioeffect;

import javax.microedition.amms.control.EffectControl;
import javax.microedition.media.MediaException;

public interface ReverbControl extends EffectControl {
   int setReverbLevel(int var1) throws IllegalArgumentException;

   int getReverbLevel();

   void setReverbTime(int var1) throws IllegalArgumentException, MediaException;

   int getReverbTime() throws MediaException;
}
