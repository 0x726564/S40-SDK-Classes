package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.style.UIStyle;

abstract class SoftkeyManager {
   private static SoftkeyManager instance = null;

   protected SoftkeyManager() {
   }

   static final SoftkeyManager getSoftkeyManager() {
      if (instance == null) {
         String var0 = null;
         switch(UIStyle.getNumberOfSoftKeys()) {
         case 2:
            var0 = "javax.microedition.lcdui.TwoSoftkeyManager";
            break;
         case 3:
            if (UIStyle.isRotator()) {
               var0 = "javax.microedition.lcdui.RotatorSoftkeyManager";
            } else {
               var0 = "javax.microedition.lcdui.ThreeSoftkeyManager";
            }
            break;
         default:
            throw new IllegalStateException();
         }

         try {
            Class var1 = Class.forName(var0);
            instance = (SoftkeyManager)var1.newInstance();
         } catch (Exception var2) {
         }
      }

      return instance;
   }

   abstract void selectSoftkeys(Displayable var1, CommandVector var2, CommandVector var3);
}
