package com.nokia.mid.impl.isa.mmedia;

import com.nokia.mid.impl.isa.util.SharedObjects;

public class MediaPrefs {
   public static final int SUPPORTS_RATE = 1;
   public static final int SUPPORTS_PITCH = 2;
   public static final int SUPPORTS_MIDIEVENT = 4;
   public static final byte RECORD_CTRL = 1;
   public static final byte PRE_SNAPSHOT = 2;
   public static final byte SNAPSHOT = 3;
   public static final byte RECORD_JSR75 = 4;
   public static final byte NETWORK = 5;
   public static final byte RECORD_FIRST_TIME = 6;
   public static final byte ANYONE = 0;
   public static final byte CAMERA = 1;
   public static final byte AUDIO_REC = 2;
   private static final Object cA = SharedObjects.getLock("com.nokia.mid.impl.isa.mmedia.MediaPrefs");

   public static int boundInt(int var0, int var1, int var2) {
      if (var0 < var1) {
         return var1;
      } else {
         return var0 > var2 ? var2 : var0;
      }
   }

   public static void checkPermission(int var0, int var1) throws SecurityException {
      synchronized(cA) {
         nCheckPermission(var0, var1);
      }
   }

   public static void nTrace(String var0) {
      nTrace0("MMAPI Java: " + var0);
   }

   public static native boolean nIsFeatureSupported(int var0);

   public static native String[] nGetFormats(String var0);

   public static native String[] nGetProtocols(String var0);

   public static native byte nGetLocatorTypeAndContentType(String var0, String var1, String[] var2);

   public static native boolean nIsContentSupported(String var0, String var1);

   public static native boolean nIsDataContentSupported(String var0);

   public static native String nGetAppropriatePlayer(String var0, byte var1);

   public static native void nTrace0(String var0);

   public static native int nGetDefaultVolumeLevel();

   private static native void nCheckPermission(int var0, int var1) throws SecurityException;
}
