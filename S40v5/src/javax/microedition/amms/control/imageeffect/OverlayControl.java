package javax.microedition.amms.control.imageeffect;

import javax.microedition.amms.control.EffectControl;

public interface OverlayControl extends EffectControl {
   int insertImage(Object var1, int var2, int var3, int var4) throws IllegalArgumentException;

   int insertImage(Object var1, int var2, int var3, int var4, int var5) throws IllegalArgumentException;

   void removeImage(Object var1);

   Object getImage(int var1);

   int numberOfImages();

   void clear();
}
