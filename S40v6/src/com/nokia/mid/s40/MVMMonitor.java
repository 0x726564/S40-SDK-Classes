package com.nokia.mid.s40;

public class MVMMonitor {
   public static native int[] get_running_midlet_ids(int[] var0);

   public static native int number_of_running_apps();

   public static final String getAppProperty(String key, int midletID) {
      if (key == null) {
         throw new NullPointerException("Jam.getAppProperty(null) called!");
      } else if (!key.equals("MIDlet-Name") && !key.equals("MIDlet-Vendor") && !key.equals("MIDlet-Version") && !key.equals("MIDlet-Description")) {
         throw new IllegalArgumentException();
      } else {
         return my_getAppProperty(key, midletID);
      }
   }

   private static native String my_getAppProperty(String var0, int var1);

   public static native int get_midlet_memory_used(int var0);

   public static native int get_midlet_memory_limit(int var0);

   public static native boolean is_in_foreground(int var0);

   public static native int[] get_heap_usages_array(int[] var0);
}
