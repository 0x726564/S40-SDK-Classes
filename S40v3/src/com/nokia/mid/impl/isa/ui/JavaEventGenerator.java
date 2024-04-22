package com.nokia.mid.impl.isa.ui;

public class JavaEventGenerator {
   public static void s_generateEvent(int var0, int var1, int var2, int var3) {
      if (var1 != 2 || var2 != 1 && var2 != 2) {
         s_generateEventInternal(var0, var1, var2, var3, false);
      } else {
         s_generateEventInternal(var0, var1, var2, var3, true);
      }
   }

   private static native void s_generateEventInternal(int var0, int var1, int var2, int var3, boolean var4);
}
