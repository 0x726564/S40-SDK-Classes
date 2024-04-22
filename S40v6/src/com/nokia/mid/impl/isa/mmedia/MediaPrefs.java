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
   public static final byte READ_JSR75 = 5;
   public static final byte NETWORK = 6;
   public static final byte RECORD_FIRST_TIME = 7;
   public static final byte ANYONE = 0;
   public static final byte CAMERA = 1;
   public static final byte AUDIO_REC = 2;
   private static final Object securityLock = SharedObjects.getLock("com.nokia.mid.impl.isa.mmedia.MediaPrefs");

   public static int boundInt(int value, int min, int max) {
      if (value < min) {
         return min;
      } else {
         return value > max ? max : value;
      }
   }

   public static void checkPermission(int policy, int requester) throws SecurityException {
      synchronized(securityLock) {
         nCheckPermission(policy, requester);
      }
   }

   public static void nTrace(String trace) {
      nTrace0("MMAPI Java: " + trace);
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
