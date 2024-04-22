package javax.microedition.lcdui;

class SearchScreen extends Screen {
   private static SearchScreen searchScreen;
   private Displayable owner = null;
   private TextEditor sresult = null;
   private int appID;

   private SearchScreen() {
      synchronized(Display.LCDUILock) {
         this.setNativeDelegate(true);
         this.setSystemScreen(true);
      }

      this.appID = this.nativeInitialise();
   }

   public static SearchScreen getSearchScreen() {
      if (searchScreen == null) {
         searchScreen = new SearchScreen();
      }

      return searchScreen;
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

   void setSearchResultConsumer(TextEditor var1) {
      synchronized(Display.LCDUILock) {
         this.sresult = var1;
      }
   }

   void callShowNotify(Display var1) {
      super.callShowNotify(var1);
      synchronized(Display.LCDUILock) {
         this.myDisplay = var1;
         this.nativeCreateSearchDeleg(this.appID);
         this.nativeShowSearchDeleg(this.sresult.getConstraints());
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

   private native int nativeInitialise();

   private native void nativeCreateSearchDeleg(int var1);

   private native void nativeShowSearchDeleg(int var1);

   private native void nativeDestroySearchDeleg();

   private native String nativeGetSearchResultStr();
}
