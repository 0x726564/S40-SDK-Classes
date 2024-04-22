package javax.microedition.amms.control.audio3d;

import javax.microedition.media.Control;

public interface ObstructionControl extends Control {
   int getHFLevel();

   int getLevel();

   void setHFLevel(int var1);

   void setLevel(int var1);
}
