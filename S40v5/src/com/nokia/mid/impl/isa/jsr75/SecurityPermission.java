package com.nokia.mid.impl.isa.jsr75;

public class SecurityPermission {
   public static synchronized boolean isPermitted(int var0, String var1, boolean var2) {
      return nativeIsPermitted(var0, var1, var2);
   }

   private static native boolean nativeIsPermitted(int var0, String var1, boolean var2);
}
