package javax.microedition.lcdui;

class NativeOptionsMenu extends Screen implements OptionsMenu {
   private static NativeOptionsMenu nativeOptionsMenu;
   private String[] optionLabels;
   private boolean[] optionParents;
   private int appID;
   private boolean keepRootOptionsMenu;
   private boolean showing;

   private NativeOptionsMenu() {
      synchronized(Display.LCDUILock) {
         this.setNativeDelegate(true);
         this.setPopup(true);
         this.setSystemScreen(true);
         this.nativeWrapper = true;
      }

      this.appID = this.nativeInitialize();
   }

   static OptionsMenu getNativeOptionsMenu(Display display) {
      if (nativeOptionsMenu == null) {
         nativeOptionsMenu = new NativeOptionsMenu();
      }

      if (nativeOptionsMenu.myDisplay != null && nativeOptionsMenu.myDisplay != display) {
         nativeOptionsMenu.myDisplay = null;
      }

      return nativeOptionsMenu;
   }

   void callShowNotify(Display d) {
      super.callShowNotify(d);
      synchronized(Display.LCDUILock) {
         this.setupOptionsMenu();
         if (this.optionLabels == null) {
            this.myDisplay.setCurrentInternal((Displayable)null, this.getParentDisplayable());
         } else {
            this.nativeLaunch(this.appID);
            this.showing = true;
         }

      }
   }

   void callHideNotifyInProgress(Display d) {
      super.callHideNotifyInProgress(d);
      synchronized(Display.LCDUILock) {
         if (!this.keepRootOptionsMenu) {
            this.dismiss();
            this.optionLabels = null;
            this.optionParents = null;
         }

      }
   }

   void callDelegateEvent(int eventType, int eventValue) {
      Displayable localParent = this.getParentDisplayable();
      int selectedOptionIndex = -1;
      boolean removeOptionsMenuFromDisplay = false;
      synchronized(Display.LCDUILock) {
         switch(eventType) {
         case 4:
            selectedOptionIndex = eventValue;
            removeOptionsMenuFromDisplay = true;
            break;
         case 5:
            removeOptionsMenuFromDisplay = true;
         }

         if (selectedOptionIndex != -1) {
            Command selectedCmd = localParent.optionMenuCommands.getCommand(selectedOptionIndex);
            if (selectedCmd != null) {
               removeOptionsMenuFromDisplay = !localParent.commandListAction(selectedCmd);
            }
         }

         if (removeOptionsMenuFromDisplay) {
            this.setKeepRootOptionsMenu(false);
            this.dismiss();
            this.myDisplay.setCurrentInternal((Displayable)null, localParent);
         }

      }
   }

   void removedFromDisplayNotify(Display d) {
      this.title = null;
      this.optionLabels = null;
      this.optionParents = null;
   }

   void setTitleImpl(String newTitle) {
      if ((newTitle == null || newTitle.length() == 0) && this.myDisplay != null) {
         newTitle = this.myDisplay.getMIDletName();
      }

      this.title = newTitle;
      if (newTitle == null) {
         newTitle = "";
      }

      this.nativeSetTitle(this.title);
   }

   boolean usesSoftLabel() {
      return true;
   }

   boolean isPowerSavingActive() {
      return false;
   }

   public boolean isShowing() {
      return this.showing;
   }

   public final void update(int newHighlightedIndex) {
      Displayable localParent = this.getParentDisplayable();
      if (this.isShown() && localParent != null && !this.keepRootOptionsMenu) {
         this.setupOptionsMenu();
         if (this.optionLabels == null) {
            this.myDisplay.setCurrentInternal((Displayable)null, localParent);
         } else {
            this.nativeUpdate(newHighlightedIndex);
         }

      }
   }

   public void setKeepRootOptionsMenu(boolean keepRootOptionsMenu) {
      if (keepRootOptionsMenu != this.keepRootOptionsMenu) {
         this.keepRootOptionsMenu = keepRootOptionsMenu;
      }

   }

   public final int getHighlightedIndex() {
      return this.nativeGetHighlightedIndex();
   }

   public void dismiss() {
      this.showing = false;
      this.nativeDismiss();
   }

   public int getItemCount() {
      return this.nativeGetItemCount();
   }

   private void setupOptionsMenu() {
      Displayable localParent = this.getParentDisplayable();
      int numberOfOptions = localParent.optionMenuCommands.length();
      if (numberOfOptions == 0) {
         this.optionLabels = null;
         this.optionParents = null;
      } else {
         this.setTitleImpl(localParent.title);
         this.optionLabels = new String[numberOfOptions];
         this.optionParents = new boolean[numberOfOptions];

         for(int i = 0; i < numberOfOptions; ++i) {
            this.optionLabels[i] = localParent.optionMenuCommands.getCommand(i).getMenuLabel();
            this.optionParents[i] = localParent.optionMenuCommands.getCommand(i).isParent;
         }
      }

   }

   private native int nativeInitialize();

   private native void nativeLaunch(int var1);

   private native void nativeHighlight(int var1);

   private native void nativeDismiss();

   private native int nativeGetHighlightedIndex();

   private native void nativeUpdate(int var1);

   private native int nativeGetItemCount();

   private native void nativeSetTitle(String var1);
}
