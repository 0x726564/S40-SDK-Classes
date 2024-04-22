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

   SoftLabel(Displayable var1) {
      for(int var2 = 0; var2 < 3; ++var2) {
         this.labels[var2] = null;
      }

      this.parent = var1;
   }

   static void makeAllInvisible() {
      for(int var0 = 0; var0 < 3; ++var0) {
         displayedLabels[var0] = null;
         updateNative("", var0);
      }

   }

   void set(String var1, int var2) {
      if (var2 < 3) {
         this.labels[var2] = var1.trim();
         if (this.labels[var2].length() > 32) {
            this.labels[var2] = this.labels[var2].substring(0, 32);
         }

         if (this.parent.isShown()) {
            this.update(var2);
         }
      }

   }

   void assignAll(CommandVector var1) {
      for(int var3 = 0; var3 < 3; ++var3) {
         if (NUM_SOFTKEYS != 2 || var3 != 1) {
            Command var2 = var1.getCommand(var3);
            this.labels[var3] = var2 != null ? var2.label.trim() : null;
         }
      }

   }

   void updateAll() {
      if (this.parent.isShown()) {
         for(int var1 = 0; var1 < 3; ++var1) {
            this.update(var1);
         }
      }

   }

   private void update(int var1) {
      if (this.labels[var1] != null) {
         if (!this.labels[var1].equals(displayedLabels[var1])) {
            displayedLabels[var1] = this.labels[var1];
            updateNative(this.labels[var1], var1);
         }
      } else if (displayedLabels[var1] != null) {
         displayedLabels[var1] = this.labels[var1];
         updateNative("", var1);
      }

   }

   private static native void updateNative(String var0, int var1);

   static {
      SOFTLABEL_SELECT = NUM_SOFTKEYS > 2 ? 1 : 0;
      displayedLabels = new String[3];
      makeAllInvisible();
   }
}
