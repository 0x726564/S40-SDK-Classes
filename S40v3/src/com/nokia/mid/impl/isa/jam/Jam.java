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

   public static native void install_transitory_appl(byte[] var0, int var1, byte[] var2, int var3, boolean var4);

   public static native void chain_and_return(String var0, int var1, int var2);

   public static native void http_media_set_wire();

   public static native void http_media_set_air();

   public static native void set_test_automated_true();

   public static native void set_test_automated_false();

   public static native void init_security();
}
