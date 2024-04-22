package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.style.UIStyle;

abstract class SoftkeyManager {
   private static SoftkeyManager instance = null;

   protected SoftkeyManager() {
   }

   static final SoftkeyManager getSoftkeyManager() {
      if (instance == null) {
         String class_name = null;
         switch(UIStyle.getNumberOfSoftKeys()) {
         case 2:
            class_name = "javax.microedition.lcdui.TwoSoftkeyManager";
            break;
         case 3:
            class_name = "javax.microedition.lcdui.ThreeSoftkeyManager";
            break;
         default:
            throw new IllegalStateException();
         }

         try {
            Class manager_class = Class.forName(class_name);
            instance = (SoftkeyManager)manager_class.newInstance();
         } catch (Exception var2) {
         }
      }

      return instance;
   }

   abstract void selectSoftkeys(Displayable var1, CommandVector var2, CommandVector var3);
}
