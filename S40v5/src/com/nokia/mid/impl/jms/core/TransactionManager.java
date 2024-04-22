package com.nokia.mid.impl.jms.core;

public class TransactionManager {
   private static int lF = 0;
   private static boolean lG = false;

   private TransactionManager() {
   }

   public static int startTransaction() {
      if (lG) {
         return 0;
      } else {
         lG = true;
         startTransaction0();
         return ++lF;
      }
   }

   public static boolean isInTransaction() {
      return lG;
   }

   public static boolean endTransaction(int var0) {
      if (lF == var0) {
         lG = false;
         endTransaction0();
         return true;
      } else {
         return false;
      }
   }

   private static native void startTransaction0();

   private static native void endTransaction0();
}
