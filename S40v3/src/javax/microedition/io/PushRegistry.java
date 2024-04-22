package javax.microedition.io;

import java.io.IOException;
import java.util.TimeZone;

public class PushRegistry {
   private static final Object listConnectionsLock = new Object();
   private static final Object checkPermissionLock = new Object();
   private static final Object unregisterConnectionLock = new Object();
   private static final Object registerConnectionLock = new Object();
   private static final Object registerAlarmLock = new Object();
   private static final String SMS_URL = "sms://";
   private static final String MMS_URL = "mms://";

   private static native long registerAlarm0(String var0, long var1);

   private static native String getFilter0(String var0);

   private static native String getMIDlet0(String var0);

   private static native String listConnections0(boolean var0);

   private static native void checkPermission0();

   private static native boolean unregisterConnection0(String var0);

   private static native void registerConnection0(String var0, String var1, String var2);

   private PushRegistry() {
   }

   public static void registerConnection(String var0, String var1, String var2) throws ClassNotFoundException, IOException {
      if (var0 != null && var2 != null) {
         safeCheckPermission();
         if (var0.startsWith("sms://") || var0.startsWith("mms://")) {
            try {
               Connection var3 = Connector.open(var0);
               var3.close();
            } catch (IOException var6) {
            }
         }

         synchronized(registerConnectionLock) {
            registerConnection0(var0, var1, var2);
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static boolean unregisterConnection(String var0) {
      synchronized(unregisterConnectionLock) {
         return unregisterConnection0(var0);
      }
   }

   public static String[] listConnections(boolean var0) {
      synchronized(listConnectionsLock) {
         String var2 = listConnections0(var0);
         if (var2 == null) {
            return new String[0];
         } else {
            int var3 = 0;
            int var4 = 0;

            do {
               var4 = var2.indexOf(44, var4 + 1);
               ++var3;
            } while(var4 > 0);

            String[] var5 = new String[var3];
            int var6 = 0;

            for(int var7 = 0; var7 < var3; ++var7) {
               var4 = var2.indexOf(44, var6);
               if (var4 > 0) {
                  var5[var7] = var2.substring(var6, var4);
               } else {
                  var5[var7] = var2.substring(var6);
               }

               var6 = var4 + 1;
            }

            return var5;
         }
      }
   }

   public static String getMIDlet(String var0) {
      return getMIDlet0(var0);
   }

   public static String getFilter(String var0) {
      return getFilter0(var0);
   }

   public static long registerAlarm(String var0, long var1) throws ClassNotFoundException, ConnectionNotFoundException {
      safeCheckPermission();
      synchronized(registerAlarmLock) {
         TimeZone var4 = TimeZone.getDefault();
         long var5 = (long)var4.getRawOffset();
         long var7 = var1 + var5;
         long var9 = registerAlarm0(var0, var7);
         if (var9 != 0L) {
            var9 -= var5;
         }

         return var9;
      }
   }

   private static void safeCheckPermission() {
      synchronized(checkPermissionLock) {
         checkPermission0();
      }
   }
}
