package com.sun.midp.io;

public abstract class NetworkConnectionBase extends BufferedConnectionAdapter {
   protected int handle;
   private int iocb;

   public static void initializeNativeNetwork() {
   }

   private static native void initializeInternal();

   protected NetworkConnectionBase(int sizeOfBuffer) {
      super(sizeOfBuffer);
   }

   static {
      initializeInternal();
   }
}
