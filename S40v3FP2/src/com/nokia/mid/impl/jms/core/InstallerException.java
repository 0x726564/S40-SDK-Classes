package com.nokia.mid.impl.jms.core;

public class InstallerException extends Exception {
   int m_errorCode;

   public InstallerException(String var1, int var2) {
      super(var1);
      this.m_errorCode = var2;
   }

   public InstallerException(int var1) {
      this("", var1);
   }

   public int getErrorCode() {
      return this.m_errorCode;
   }

   /** @deprecated */
   public int getDetailedErrorCode() {
      return this.m_errorCode;
   }
}
