package com.nokia.mid.impl.isa.mmedia;

public class MediaPrefs {
   public static final int SUPPORTS_RATE = 1;
   public static final int SUPPORTS_PITCH = 2;
   public static final int SUPPORTS_MIDIEVENT = 4;
   public static final int SUPPORTS_VID_RESIZE = 8;

   public static int boundInt(int var0, int var1, int var2) {
      if (var0 < var1) {
         return var1;
      } else {
         return var0 > var2 ? var2 : var0;
      }
   }

   public static native boolean nIsFeatureSupported(int var0);

   public static native String[] nGetFormats(String var0);

   public static native String[] nGetProtocols(String var0);

   public static native byte nGetLocatorTypeAndContentType(String var0, String var1, String[] var2);

   public static native boolean nIsContentSupported(String var0, String var1);

   public static native boolean nIsDataContentSupported(String var0);

   public static native String nGetPlayerForContentType(String var0);

   public static native boolean nTrace(String var0);

   public static native int nGetDefaultVolumeLevel();

   public static void trace(String var0) {
      boolean var1 = nTrace(var0);
   }
}
