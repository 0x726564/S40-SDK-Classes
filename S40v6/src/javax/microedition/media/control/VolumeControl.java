package javax.microedition.media.control;

import javax.microedition.media.Control;

public interface VolumeControl extends Control {
   void setMute(boolean var1);

   boolean isMuted();

   int setLevel(int var1);

   int getLevel();
}
