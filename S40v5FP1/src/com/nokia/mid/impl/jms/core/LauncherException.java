package com.nokia.mid.impl.jms.core;

public class LauncherException extends Exception {
   public LauncherException(String message) {
      super(message);
   }

   public int getErrorCode() {
      return 0;
   }
}
