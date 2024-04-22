package com.sun.midp.io;

public abstract class NetworkConnectionBase extends BufferedConnectionAdapter {
   protected int handle;

   public static void initializeNativeNetwork() {
   }

   private static native void initializeInternal();

   protected NetworkConnectionBase(int var1) {
      super(var1);
   }

   static {
      initializeInternal();
   }
}
