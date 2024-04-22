package com.nokia.mid.impl.isa.jsr75;

public class SecurityPermission {
   public static synchronized boolean isPermitted(int requestedAccess, String path, boolean toBeCreated) {
      return nativeIsPermitted(requestedAccess, path, toBeCreated);
   }

   private static native boolean nativeIsPermitted(int var0, String var1, boolean var2);
}
