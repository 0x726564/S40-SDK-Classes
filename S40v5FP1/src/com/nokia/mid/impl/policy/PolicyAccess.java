package com.nokia.mid.impl.policy;

public final class PolicyAccess {
   public static final int mailToID = 0;
   public static final int telID = 1;
   public static final int browserID = 2;
   private static Object inlock = new Object();

   private PolicyAccess() {
   }

   public static boolean checkPermission(String api, String msg) {
      if (api != null && msg != null) {
         if (msg.length() == 0) {
            msg = " ";
         }

         synchronized(inlock) {
            boolean var10000;
            try {
               request0(api, msg);
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

   public static boolean checkPermission(String api, int msgID) {
      String msg = "";
      switch(msgID) {
      case 0:
      case 1:
         msg = getPromptText0(msgID);
         return checkPermission(api, msg);
      default:
         throw new IllegalArgumentException();
      }
   }

   static native void request0(String var0, String var1);

   static native String getPromptText0(int var0);
}
