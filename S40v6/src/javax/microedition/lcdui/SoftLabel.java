package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.style.UIStyle;

class SoftLabel {
   public static final int SOFTLABEL_LEFT = 0;
   public static final int SOFTLABEL_MIDDLE = 1;
   public static final int SOFTLABEL_RIGHT = 2;
   public static final int MAX_JAM_SOFTKEY = 3;
   private static final int SOFTKEY_TEXT_LEN_MAX = 32;
   private static final String INVISIBLE_LABEL = "";
   static final int MAXIMUM_SOFTKEYS = 3;
   static final int NUM_SOFTKEYS = UIStyle.getNumberOfSoftKeys();
   public static final int SOFTLABEL_SELECT;
   private static String[] displayedLabels;
   private Displayable parent;
   private String[] labels = new String[3];

   SoftLabel(Displayable d) {
      for(int i = 0; i < 3; ++i) {
         this.labels[i] = null;
      }

      this.parent = d;
   }

   static void makeAllInvisible(boolean updateNative) {
      for(int i = 0; i < 3; ++i) {
         displayedLabels[i] = null;
         if (updateNative) {
            updateNative("", i);
         }
      }

   }

   void set(String str, int index) {
      if (index < 3) {
         this.labels[index] = str.trim();
         if (this.labels[index].length() > 32) {
            this.labels[index] = this.labels[index].substring(0, 32);
         }

         if (this.parent.isShownImpl()) {
            this.update(index);
         }
      }

   }

   void assignAll(CommandVector softkeys) {
      for(int i = 0; i < 3; ++i) {
         if (NUM_SOFTKEYS != 2 || i != 1) {
            Command cmd = softkeys.getCommand(i);
            this.labels[i] = cmd != null ? cmd.getLabelImpl().trim() : null;
         }
      }

   }

   void updateAll() {
      if (this.parent.isShownImpl()) {
         for(int i = 0; i < 3; ++i) {
            this.update(i);
         }
      }

   }

   private void update(int index) {
      String labelText = "";
      boolean doUpdate = false;
      if (this.labels[index] != null) {
         if (!this.labels[index].equals(displayedLabels[index])) {
            displayedLabels[index] = this.labels[index];
            labelText = this.labels[index];
            doUpdate = true;
         }
      } else if (displayedLabels[index] != null) {
         displayedLabels[index] = this.labels[index];
         doUpdate = true;
      }

      if (doUpdate) {
         if (this.parent.nativeWrapper) {
            this.parent.updateSoftKeyLabel(labelText, index);
         } else {
            updateNative(labelText, index);
         }
      }

   }

   private static native void updateNative(String var0, int var1);

   static {
      SOFTLABEL_SELECT = NUM_SOFTKEYS > 2 ? 1 : 0;
      displayedLabels = new String[3];
   }
}
