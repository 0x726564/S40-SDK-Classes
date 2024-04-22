package com.nokia.mid.impl.isa.jam;

public class Jam {
   private static Jam jam;

   protected Jam() {
      if (jam != null) {
         throw new Error("Only on JAM instance allowed");
      }
   }

   public static synchronized Jam getJam() {
      if (jam == null) {
         jam = new Jam();
      }

      return jam;
   }

   public static native void install_appl(byte[] var0, int var1, byte[] var2, int var3);

   public static synchronized native void install_transitory_appl(byte[] var0, int var1, byte[] var2, int var3, boolean var4);

   public static native void chain_and_return(String var0, int var1, int var2);

   public static native void http_media_set_wire();

   public static native void http_media_set_air();

   public static native void set_test_automated_true();

   public static native void set_test_automated_false();

   public static native void set_foreground_tck_operation();

   public static native void set_background_tck_operation();

   public static native void init_security();

   public static native int get_vm_type();

   public static native int launch_transitory_appl(String var0, int var1);

   public static native int number_of_running_apps();

   public static synchronized native void wait_for_transitory_appl_completion(int var0);
}
