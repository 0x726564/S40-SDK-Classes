package javax.microedition.lcdui;

class NativeOptionsMenu extends Screen implements OptionsMenu {
   private static NativeOptionsMenu nativeOptionsMenu;
   private String[] optionLabels;
   private int appID;
   private boolean keepRootOptionsMenu;

   private NativeOptionsMenu() {
      synchronized(Display.LCDUILock) {
         this.setNativeDelegate(true);
         this.setPopup(true);
         this.setSystemScreen(true);
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
            this.nativeLaunchOptionsList(this.appID);
         }

      }
   }

   void callHideNotifyInProgress(Display d) {
      super.callHideNotifyInProgress(d);
      synchronized(Display.LCDUILock) {
         if (!this.keepRootOptionsMenu) {
            this.nativeDismissOptionsList();
            this.optionLabels = null;
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
            this.nativeDismissOptionsList();
            this.myDisplay.setCurrentInternal((Displayable)null, localParent);
         }

      }
   }

   void removedFromDisplayNotify(Display d) {
      this.title = null;
      this.optionLabels = null;
   }

   void setTitleImpl(String newTitle) {
      if ((newTitle == null || newTitle.length() == 0) && this.myDisplay != null) {
         this.title = this.myDisplay.getMIDletName();
      } else {
         this.title = newTitle;
      }

   }

   boolean usesSoftLabel() {
      return true;
   }

   public final void update(int newHighlightedIndex) {
      Displayable localParent = this.getParentDisplayable();
      if (this.isShown() && localParent != null && !this.keepRootOptionsMenu) {
         this.setupOptionsMenu();
         if (this.optionLabels == null) {
            this.myDisplay.setCurrentInternal((Displayable)null, localParent);
         } else {
            int highlightedOptionIndex;
            if (newHighlightedIndex < 0) {
               highlightedOptionIndex = 0;
            } else if (newHighlightedIndex < this.optionLabels.length) {
               highlightedOptionIndex = newHighlightedIndex;
            } else {
               highlightedOptionIndex = this.optionLabels.length - 1;
            }

            this.nativeUpdateOptionsList(highlightedOptionIndex, this.appID);
         }

      }
   }

   public void setKeepRootOptionsMenu(boolean keepRootOptionsMenu) {
      if (keepRootOptionsMenu != this.keepRootOptionsMenu) {
         this.keepRootOptionsMenu = keepRootOptionsMenu;
      }

   }

   public final int getHighlightedOptionIndex() {
      return this.nativeGetHighlightedIndex();
   }

   private void setupOptionsMenu() {
      Displayable localParent = this.getParentDisplayable();
      int numberOfOptions = localParent.optionMenuCommands.length();
      if (numberOfOptions == 0) {
         this.optionLabels = null;
      } else {
         this.setTitleImpl(localParent.title);
         this.optionLabels = new String[numberOfOptions];

         for(int i = 0; i < numberOfOptions; ++i) {
            this.optionLabels[i] = localParent.optionMenuCommands.getCommand(i).menuLabel;
         }
      }

   }

   private native int nativeInitialize();

   private native void nativeLaunchOptionsList(int var1);

   private native void nativeUpdateOptionsList(int var1, int var2);

   public native void nativeDismissOptionsList();

   private native int nativeGetHighlightedIndex();
}
