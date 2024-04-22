package javax.microedition.lcdui;

class SearchScreen extends Screen {
   private static SearchScreen hq;
   private Displayable hr = null;
   private TextEditor hs = null;
   private int he;

   private SearchScreen() {
      synchronized(Display.hG) {
         this.setNativeDelegate(true);
         this.setSystemScreen(true);
      }

      this.he = this.nativeInitialise();
   }

   public static SearchScreen getSearchScreen() {
      if (hq == null) {
         hq = new SearchScreen();
      }

      return hq;
   }

   final void r(int var1, int var2) {
      String var5 = null;
      synchronized(Display.hG) {
         if (var1 == 6) {
            if ((var5 = this.nativeGetSearchResultStr()) != null) {
               this.hs.setSearchResultString(var5);
            }

            this.eV.c((Displayable)null, this.hr);
         }

      }
   }

   void setOwner(Displayable var1) {
      synchronized(Display.hG) {
         this.hr = var1;
      }
   }

   void setSearchResultConsumer(TextEditor var1) {
      synchronized(Display.hG) {
         this.hs = var1;
      }
   }

   final void a(Display var1) {
      super.a(var1);
      synchronized(Display.hG) {
         this.eV = var1;
         this.nativeCreateSearchDeleg(this.he);
         this.nativeShowSearchDeleg(this.hs.getConstraints());
      }
   }

   final void d(Display var1) {
      synchronized(Display.hG) {
         this.nativeDestroySearchDeleg();
      }
   }

   final boolean af() {
      return true;
   }

   private native int nativeInitialise();

   private native void nativeCreateSearchDeleg(int var1);

   private native void nativeShowSearchDeleg(int var1);

   private native void nativeDestroySearchDeleg();

   private native String nativeGetSearchResultStr();
}
