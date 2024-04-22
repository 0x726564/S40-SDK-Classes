package com.nokia.mid.impl.isa.ui;

public interface ExitManager {
   void abortOnError(Throwable var1);

   void exitOnError(Throwable var1);

   void exit();
}
