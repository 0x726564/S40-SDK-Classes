package com.nokia.mid.impl.isa.pim;

public final class PIMTextDatabase {
   public static final int TEXT_KJAVA_LAST = getLastTextID();
   static String[] usedStrings;

   private PIMTextDatabase() {
   }

   public static String getText(int textID) {
      if (textID >= 0 && textID < TEXT_KJAVA_LAST) {
         try {
            if (usedStrings[textID] == null) {
               usedStrings[textID] = getNativeText(textID);
            }
         } catch (Exception var2) {
            throw new IllegalArgumentException("PIMTextDatabase: " + var2);
         }

         return usedStrings[textID];
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
