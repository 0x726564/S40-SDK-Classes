package javax.microedition.lcdui;

class NativeOptionsMenu extends Screen implements OptionsMenu {
   private static NativeOptionsMenu nativeOptionsMenu;
   private String[] optionLabels;
   private int appID;

   private NativeOptionsMenu() {
      synchronized(Display.LCDUILock) {
         this.setNativeDelegate(true);
         this.setPopup(true);
         this.setSystemScreen(true);
      }

      this.appID = this.nativeInitialize();
   }

   static OptionsMenu getNativeOptionsMenu() {
      if (nativeOptionsMenu == null) {
         nativeOptionsMenu = new NativeOptionsMenu();
      }

      return nativeOptionsMenu;
   }

   void callShowNotify(Display var1) {
      super.callShowNotify(var1);
      synchronized(Display.LCDUILock) {
         this.setupOptionsMenu();
         if (this.optionLabels == null) {
            this.myDisplay.setCurrentInternal((Displayable)null, this.getParentDisplayable());
         } else {
            this.nativeLaunchOptionsList(this.appID);
         }

      }
   }

   void callHideNotifyInProgress(Display var1) {
      super.callHideNotifyInProgress(var1);
      synchronized(Display.LCDUILock) {
         this.nativeDismissOptionsList();
         this.optionLabels = null;
      }
   }

   void callDelegateEvent(int var1, int var2) {
      Displayable var3 = this.getParentDisplayable();
      int var4 = -1;
      boolean var5 = false;
      synchronized(Display.LCDUILock) {
         switch(var1) {
         case 4:
            var4 = var2;
            var5 = true;
            break;
         case 5:
            var5 = true;
         }

         if (var4 != -1) {
            Command var7 = var3.optionMenuCommands.getCommand(var4);
            if (var7 != null) {
               var5 = !var3.commandListAction(var7);
            }
         }

         if (var5) {
            this.myDisplay.setCurrentInternal((Displayable)null, var3);
         }

      }
   }

   void removedFromDisplayNotify(Display var1) {
      this.title = null;
      this.optionLabels = null;
   }

   void setTitleImpl(String var1) {
      if ((var1 == null || var1.length() == 0) && this.myDisplay != null) {
         this.title = this.myDisplay.getMIDletName();
      } else {
         this.title = var1;
      }

   }

   boolean usesSoftLabel() {
      return true;
   }

   public final int getHighlightedOptionIndex() {
      return this.nativeGetHighlightedIndex();
   }

   public final void update(int var1) {
      Displayable var2 = this.getParentDisplayable();
      if (this.isShown() && var2 != null) {
         this.setupOptionsMenu();
         if (this.optionLabels == null) {
            this.myDisplay.setCurrentInternal((Displayable)null, var2);
         } else {
            int var3;
            if (var1 < 0) {
               var3 = 0;
            } else if (var1 < this.optionLabels.length) {
               var3 = var1;
            } else {
               var3 = this.optionLabels.length - 1;
            }

            this.nativeUpdateOptionsList(var3, this.appID);
         }

      }
   }

   private void setupOptionsMenu() {
      Displayable var1 = this.getParentDisplayable();
      int var2 = var1.optionMenuCommands.length();
      if (var2 == 0) {
         this.optionLabels = null;
      } else {
         this.setTitleImpl(var1.title);
         this.optionLabels = new String[var2];

         for(int var3 = 0; var3 < var2; ++var3) {
            this.optionLabels[var3] = var1.optionMenuCommands.getCommand(var3).menuLabel;
         }
      }

   }

   private native int nativeInitialize();

   private native void nativeLaunchOptionsList(int var1);

   private native void nativeUpdateOptionsList(int var1, int var2);

   private native void nativeDismissOptionsList();

   private native int nativeGetHighlightedIndex();
}
