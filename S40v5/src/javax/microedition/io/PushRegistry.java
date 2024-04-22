package javax.microedition.io;

import java.io.IOException;
import java.util.TimeZone;

public class PushRegistry {
   private static final Object hg = new Object();
   private static final Object hh = new Object();
   private static final Object hi = new Object();
   private static final Object hj = new Object();
   private static final Object hk = new Object();

   private static native long registerAlarm0(String var0, long var1);

   private static native String getFilter0(String var0);

   private static native String getMIDlet0(String var0);

   private static native String listConnections0(boolean var0);

   private static native void checkPermission0();

   private static native boolean unregisterConnection0(String var0);

   private static native void registerConnection0(String var0, String var1, String var2);

   private static native int getDaylightSavingInfo0();

   private PushRegistry() {
   }

   public static void registerConnection(String var0, String var1, String var2) throws ClassNotFoundException, IOException {
      if (var0 != null && var2 != null) {
         aA();
         if (var0.startsWith("sms://") || var0.startsWith("mms://")) {
            try {
               Connector.open(var0).close();
            } catch (IOException var5) {
            }
         }

         synchronized(hj) {
            registerConnection0(var0, var1, var2);
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static boolean unregisterConnection(String var0) {
      synchronized(hi) {
         return unregisterConnection0(var0);
      }
   }

   public static String[] listConnections(boolean var0) {
      synchronized(hg) {
         String var8;
         if ((var8 = listConnections0(var0)) == null) {
            return new String[0];
         } else {
            int var2 = 0;
            int var3 = 0;

            do {
               var3 = var8.indexOf(44, var3 + 1);
               ++var2;
            } while(var3 > 0);

            String[] var4 = new String[var2];
            int var5 = 0;

            for(int var6 = 0; var6 < var2; ++var6) {
               if ((var3 = var8.indexOf(44, var5)) > 0) {
                  var4[var6] = var8.substring(var5, var3);
               } else {
                  var4[var6] = var8.substring(var5);
               }

               var5 = var3 + 1;
            }

            return var4;
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
      aA();
      synchronized(hk) {
         long var5 = (long)TimeZone.getDefault().getRawOffset();
         long var7 = getDaylightSavingOffsetInMillis();
         long var9 = var1 + var5 + var7;
         long var11;
         if ((var11 = registerAlarm0(var0, var9)) != 0L) {
            var11 = var11 - var5 - var7;
         }

         return var11;
      }
   }

   private static long getDaylightSavingOffsetInMillis() {
      return (long)(getDaylightSavingInfo0() * '\uea60');
   }

   private static void aA() {
      synchronized(hh) {
         checkPermission0();
      }
   }
}
