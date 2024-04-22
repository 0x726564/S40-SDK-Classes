package javax.microedition.lcdui;

class NativeOptionsMenu extends Screen implements OptionsMenu {
   private static NativeOptionsMenu hc;
   private String[] hd;
   private int he;
   private boolean hf;

   private NativeOptionsMenu() {
      synchronized(Display.hG) {
         this.setNativeDelegate(true);
         this.setPopup(true);
         this.setSystemScreen(true);
      }

      this.he = this.nativeInitialize();
   }

   static OptionsMenu g(Display var0) {
      if (hc == null) {
         hc = new NativeOptionsMenu();
      }

      if (hc.eV != null && hc.eV != var0) {
         hc.eV = null;
      }

      return hc;
   }

   final void a(Display var1) {
      super.a(var1);
      synchronized(Display.hG) {
         this.az();
         if (this.hd == null) {
            this.eV.c((Displayable)null, this.getParentDisplayable());
         } else {
            this.nativeLaunchOptionsList(this.he);
         }

      }
   }

   final void e(Display var1) {
      super.e(var1);
      synchronized(Display.hG) {
         if (!this.hf) {
            this.nativeDismissOptionsList();
            this.hd = null;
         }

      }
   }

   final void r(int var1, int var2) {
      Displayable var3 = this.getParentDisplayable();
      int var4 = -1;
      boolean var5 = false;
      synchronized(Display.hG) {
         switch(var1) {
         case 4:
            var4 = var2;
            var5 = true;
            break;
         case 5:
            var5 = true;
         }

         Command var10;
         if (var4 != -1 && (var10 = var3.eY.getCommand(var4)) != null) {
            Object var11 = null;
            Object var12 = null;
            Item var7 = null;
            boolean var8 = false;
            if (var3.eW.j(var10)) {
               var11 = var3;
               var12 = var3.fa;
            } else if ((var7 = var3.getCurrentItem()) != null && var7.aG != null && var7.aG.j(var10)) {
               var11 = var7;
               var12 = var7.at;
            } else {
               var8 = var3.a(var10);
            }

            if (var12 != null) {
               var3.eV.a(var10, var11, var12);
            }

            var5 = !var8;
         }

         if (var5) {
            this.setKeepRootOptionsMenu(false);
            this.nativeDismissOptionsList();
            this.eV.c((Displayable)null, var3);
         }

      }
   }

   final void d(Display var1) {
      this.title = null;
      this.hd = null;
   }

   void setTitleImpl(String var1) {
      if ((var1 == null || var1.length() == 0) && this.eV != null) {
         this.title = this.eV.getMIDletName();
      } else {
         this.title = var1;
      }
   }

   final boolean af() {
      return true;
   }

   public final void update(int var1) {
      Displayable var2 = this.getParentDisplayable();
      if (this.isShown() && var2 != null && !this.hf) {
         this.az();
         if (this.hd == null) {
            this.eV.c((Displayable)null, var2);
         } else {
            int var3;
            if (var1 < 0) {
               var3 = 0;
            } else if (var1 < this.hd.length) {
               var3 = var1;
            } else {
               var3 = this.hd.length - 1;
            }

            this.nativeUpdateOptionsList(var3, this.he);
         }
      }
   }

   public void setKeepRootOptionsMenu(boolean var1) {
      if (var1 != this.hf) {
         this.hf = var1;
      }

   }

   public final int getHighlightedOptionIndex() {
      return this.nativeGetHighlightedIndex();
   }

   private void az() {
      Displayable var1;
      int var2;
      if ((var2 = (var1 = this.getParentDisplayable()).eY.length()) == 0) {
         this.hd = null;
      } else {
         this.setTitleImpl(var1.title);
         this.hd = new String[var2];

         for(int var3 = 0; var3 < var2; ++var3) {
            this.hd[var3] = var1.eY.getCommand(var3).c;
         }

      }
   }

   private native int nativeInitialize();

   private native void nativeLaunchOptionsList(int var1);

   private native void nativeUpdateOptionsList(int var1, int var2);

   public native void nativeDismissOptionsList();

   private native int nativeGetHighlightedIndex();
}
