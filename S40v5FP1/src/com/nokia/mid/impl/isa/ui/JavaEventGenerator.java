package com.nokia.mid.impl.isa.ui;

public class JavaEventGenerator {
   public static void s_generateEvent(int delay, int category, int type, int param) {
      if (category != 2 || type != 1 && type != 2) {
         s_generateEventInternal(delay, category, type, param, false);
      } else {
         s_generateEventInternal(delay, category, type, param, true);
      }
   }

   private static native void s_generateEventInternal(int var0, int var1, int var2, int var3, boolean var4);
}
