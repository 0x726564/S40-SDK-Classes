package com.nokia.mid.impl.jms.core;

public class InstallerException extends Exception {
   private int dO;

   public InstallerException(String var1, int var2) {
      super(var1);
      this.dO = var2;
   }

   public InstallerException(int var1) {
      this("", var1);
   }

   public int getErrorCode() {
      return this.dO;
   }

   /** @deprecated */
   public int getDetailedErrorCode() {
      return this.dO;
   }
}
