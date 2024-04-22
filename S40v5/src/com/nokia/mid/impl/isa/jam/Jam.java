package com.nokia.mid.impl.isa.jam;

public class Jam {
   private static Jam dv;

   protected Jam() {
      if (dv != null) {
         throw new Error("Only on JAM instance allowed");
      }
   }

   public static synchronized Jam getJam() {
      if (dv == null) {
         dv = new Jam();
      }

      return dv;
   }

   public static native void install_appl(byte[] var0, int var1, byte[] var2, int var3);

   public static native void install_transitory_appl(byte[] var0, int var1, byte[] var2, int var3, boolean var4);

   public static native void chain_and_return(String var0, int var1, int var2);

   public static native void http_media_set_wire();

   public static native void http_media_set_air();

   public static native void set_test_automated_true();

   public static native void set_test_automated_false();

   public static native void init_security();

   public static native int launch_transitory_appl(String var0, int var1);

   public static native int number_of_running_apps();
}
