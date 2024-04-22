package javax.microedition.amms.control;

import javax.microedition.media.Control;

public interface EffectOrderControl extends Control {
   int setEffectOrder(EffectControl var1, int var2);

   int getEffectOrder(EffectControl var1);

   EffectControl[] getEffectOrders();
}
