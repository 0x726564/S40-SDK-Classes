package com.nokia.mid.impl.isa.bluetooth;

public class Tracer {
   private static int TRACES_ACTIVE;
   private static int SIMULATION_TARGET_ESIM;

   public static void println(String var0) {
      if (1 == SIMULATION_TARGET_ESIM) {
         esimPrintf0(var0.getBytes(), var0.length());
      } else if (1 == TRACES_ACTIVE) {
         System.out.println("jsr82:" + var0);
      }

   }

   private static native void init0();

   private static native void esimInitConsole0();

   private static native void esimPrintf0(byte[] var0, int var1);

   static {
      init0();
      if (1 == SIMULATION_TARGET_ESIM) {
         esimInitConsole0();
      }

   }
}
