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

   void callDelegateEvent(int eventType, int eventValue) {
      String searchResultStr = null;
      synchronized(Display.LCDUILock) {
         if (eventType == 6) {
            searchResultStr = this.nativeGetSearchResultStr();
            if (searchResultStr != null) {
               this.sresult.setSearchResultString(searchResultStr);
            }

            searchResultStr = null;
            this.myDisplay.setCurrentInternal((Displayable)null, this.owner);
         }

      }
   }

   void setOwner(Displayable owner) {
      synchronized(Display.LCDUILock) {
         this.owner = owner;
      }
   }

   void setSearchResultConsumer(TextEditor src) {
      synchronized(Display.LCDUILock) {
         this.sresult = src;
      }
   }

   void callShowNotify(Display d) {
      super.callShowNotify(d);
      synchronized(Display.LCDUILock) {
         this.myDisplay = d;
         this.nativeCreateSearchDeleg(this.appID);
         this.nativeShowSearchDeleg(this.sresult.getConstraints());
      }
   }

   void removedFromDisplayNotify(Display d) {
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
