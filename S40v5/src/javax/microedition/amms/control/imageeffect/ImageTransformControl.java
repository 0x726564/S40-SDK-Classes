package javax.microedition.amms.control.imageeffect;

import javax.microedition.amms.control.EffectControl;

public interface ImageTransformControl extends EffectControl {
   int getSourceWidth();

   int getSourceHeight();

   void setSourceRect(int var1, int var2, int var3, int var4);

   void setTargetSize(int var1, int var2, int var3);
}
