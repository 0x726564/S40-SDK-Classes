package com.nokia.mid.impl.jms.core;

public class InstallerException extends Exception {
   int m_errorCode;

   public InstallerException(String message, int errorCode) {
      super(message);
      this.m_errorCode = errorCode;
   }

   public InstallerException(int errorCode) {
      this("", errorCode);
   }

   public int getErrorCode() {
      return this.m_errorCode;
   }

   /** @deprecated */
   public int getDetailedErrorCode() {
      return this.m_errorCode;
   }
}
