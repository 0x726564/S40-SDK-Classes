package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.ui.style.UIStyle;

class SoftLabel {
   private static int bC = UIStyle.getNumberOfSoftKeys();
   private static String[] bD = new String[3];
   private Displayable bt;
   private String[] bE = new String[3];

   SoftLabel(Displayable var1) {
      for(int var2 = 0; var2 < 3; ++var2) {
         this.bE[var2] = null;
      }

      this.bt = var1;
   }

   static void K() {
      for(int var0 = 0; var0 < 3; ++var0) {
         bD[var0] = null;
         updateNative("", var0);
      }

   }

   final void a(CommandVector var1) {
      for(int var3 = 0; var3 < 3; ++var3) {
         if (bC != 2 || var3 != 1) {
            Command var2 = var1.getCommand(var3);
            this.bE[var3] = var2 != null ? var2.label.trim() : null;
         }
      }

   }

   final void L() {
      if (this.bt.isShown()) {
         for(int var1 = 0; var1 < 3; ++var1) {
            if (this.bE[var1] != null) {
               if (!this.bE[var1].equals(bD[var1])) {
                  bD[var1] = this.bE[var1];
                  updateNative(this.bE[var1], var1);
               }
            } else if (bD[var1] != null) {
               bD[var1] = this.bE[var1];
               updateNative("", var1);
            }
         }
      }

   }

   private static native void updateNative(String var0, int var1);

   static {
      K();
   }
}
