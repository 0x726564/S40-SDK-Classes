package com.nokia.mid.impl.isa.jam;

import com.arm.cldc.mas.GlobalLock;
import com.nokia.mid.impl.isa.util.SharedObjects;
import java.io.IOException;

public class JamDebug extends GlobalLock {
   private static final String NATIVE_LOCK = "com.nokia.mid.impl.isa.jam.JamDebug";
   private static Object nativeLock = SharedObjects.getLock("com.nokia.mid.impl.isa.jam.JamDebug");

   private JamDebug() {
   }

   public static int installAppl(byte[] jadData, byte[] jarData, boolean isMIDP) throws IOException {
      synchronized(nativeLock) {
         return nInstallAppl(jadData, jarData, isMIDP);
      }
   }

   public static void uninstall(int midletID) {
      synchronized(nativeLock) {
         nUninstall(midletID);
      }
   }

   public static boolean launchAppl(int midletID, String mainClass, boolean isDebug, boolean enableCLCDProtocols) {
      synchronized(nativeLock) {
         return nLaunchAppl(midletID, mainClass, enableCLCDProtocols);
      }
   }

   public static void waitForDebuggedMIDletToExit(int midletID) {
      nWaitForDebuggedMIDletToExit(midletID);
   }

   public static void cancelWaitForDebuggedMIDletToExit(int midletID) {
      nCancelWaitForDebuggedMIDletToExit(midletID);
   }

   private static native int nInstallAppl(byte[] var0, byte[] var1, boolean var2) throws IOException;

   private static native void nUninstall(int var0);

   private static native boolean nLaunchAppl(int var0, String var1, boolean var2);

   private static native void nWaitForDebuggedMIDletToExit(int var0);

   private static native void nCancelWaitForDebuggedMIDletToExit(int var0);
}
