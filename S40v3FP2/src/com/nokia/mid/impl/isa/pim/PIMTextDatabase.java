package com.nokia.mid.impl.isa.pim;

public final class PIMTextDatabase {
   public static final int TEXT_KJAVA_LAST = getLastTextID();
   static String[] usedStrings;

   private PIMTextDatabase() {
   }

   public static String getText(int var0) {
      if (var0 >= 0 && var0 < TEXT_KJAVA_LAST) {
         try {
            if (usedStrings[var0] == null) {
               usedStrings[var0] = getNativeText(var0);
            }
         } catch (Exception var2) {
            throw new IllegalArgumentException("PIMTextDatabase: " + var2);
         }

         return usedStrings[var0];
      } else {
         throw new IllegalArgumentException("PIMTextDatabase: Illegal textID");
      }
   }

   private static native String getNativeText(int var0);

   private static native int getLastTextID();

   static {
      usedStrings = new String[TEXT_KJAVA_LAST];
   }
}
