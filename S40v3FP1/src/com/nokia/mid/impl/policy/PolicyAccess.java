package com.nokia.mid.impl.policy;

public final class PolicyAccess {
   public static final int mailToID = 0;
   public static final int telID = 1;
   public static final int browserID = 2;
   private static Object inlock = new Object();

   private PolicyAccess() {
   }

   public static boolean checkPermission(String var0, String var1) {
      if (var0 != null && var1 != null) {
         if (var1.length() == 0) {
            var1 = " ";
         }

         synchronized(inlock) {
            boolean var10000;
            try {
               request0(var0, var1);
               var10000 = true;
            } catch (SecurityException var5) {
               return false;
            }

            return var10000;
         }
      } else {
         throw new IllegalArgumentException("api and message strings cannot be null.");
      }
   }

   public static boolean checkPermission(String var0, int var1) {
      String var2 = "";
      switch(var1) {
      case 0:
      case 1:
         var2 = getPromptText0(var1);
         return checkPermission(var0, var2);
      default:
         throw new IllegalArgumentException();
      }
   }

   static native void request0(String var0, String var1);

   static native String getPromptText0(int var0);
}
