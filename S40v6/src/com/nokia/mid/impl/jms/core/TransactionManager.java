package com.nokia.mid.impl.jms.core;

public class TransactionManager {
   private static int m_currentTransId = 0;
   private static boolean m_inTransaction = false;

   private TransactionManager() {
   }

   public static int startTransaction() {
      if (m_inTransaction) {
         return 0;
      } else {
         m_inTransaction = true;
         startTransaction0();
         return ++m_currentTransId;
      }
   }

   public static boolean isInTransaction() {
      return m_inTransaction;
   }

   public static boolean endTransaction(int transId) {
      if (m_currentTransId == transId) {
         m_inTransaction = false;
         endTransaction0();
         return true;
      } else {
         return false;
      }
   }

   private static native void startTransaction0();

   private static native void endTransaction0();
}
