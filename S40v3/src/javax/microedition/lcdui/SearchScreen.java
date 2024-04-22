package javax.microedition.lcdui;

class SearchScreen extends Screen {
   private Displayable owner = null;
   private SearchResultConsumer sresult = null;

   SearchScreen() {
      synchronized(Display.LCDUILock) {
         this.setNativeDelegate(true);
         this.setSystemScreen(true);
      }

      this.nativeInitialise();
   }

   void callDelegateEvent(int var1, int var2) {
      String var3 = null;
      synchronized(Display.LCDUILock) {
         if (var1 == 6) {
            var3 = this.nativeGetSearchResultStr();
            if (var3 != null) {
               this.sresult.setSearchResultString(var3);
            }

            var3 = null;
            this.myDisplay.setCurrentInternal((Displayable)null, this.owner);
         }

      }
   }

   void setOwner(Displayable var1) {
      synchronized(Display.LCDUILock) {
         this.owner = var1;
      }
   }

   void setSearchResultConsumer(SearchResultConsumer var1) {
      synchronized(Display.LCDUILock) {
         this.sresult = var1;
      }
   }

   void callShowNotify(Display var1) {
      super.callShowNotify(var1);
      synchronized(Display.LCDUILock) {
         this.myDisplay = var1;
         this.nativeCreateSearchDeleg();
         this.nativeShowSearchDeleg(this.sresult.getSearchConstraints());
      }
   }

   void removedFromDisplayNotify(Display var1) {
      synchronized(Display.LCDUILock) {
         this.nativeDestroySearchDeleg();
      }
   }

   boolean usesSoftLabel() {
      return true;
   }

   private native void nativeInitialise();

   private native void nativeCreateSearchDeleg();

   private native void nativeShowSearchDeleg(int var1);

   private native void nativeDestroySearchDeleg();

   private native String nativeGetSearchResultStr();
}
