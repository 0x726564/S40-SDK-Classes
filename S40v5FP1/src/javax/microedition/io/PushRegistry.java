package javax.microedition.io;

import java.io.IOException;
import java.util.TimeZone;

public class PushRegistry {
   private static final Object listConnectionsLock = new Object();
   private static final Object checkPermissionLock = new Object();
   private static final Object unregisterConnectionLock = new Object();
   private static final Object registerConnectionLock = new Object();
   private static final Object registerAlarmLock = new Object();
   private static final Object getDaylightSavingInfoLock = new Object();
   private static final String SMS_URL = "sms://";
   private static final String MMS_URL = "mms://";

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

   public static void registerConnection(String connection, String midlet, String filter) throws ClassNotFoundException, IOException {
      if (connection != null && filter != null) {
         safeCheckPermission();
         if (connection.startsWith("sms://") || connection.startsWith("mms://")) {
            try {
               Connection tmpCon = Connector.open(connection);
               tmpCon.close();
            } catch (IOException var6) {
            }
         }

         synchronized(registerConnectionLock) {
            registerConnection0(connection, midlet, filter);
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static boolean unregisterConnection(String connection) {
      synchronized(unregisterConnectionLock) {
         return unregisterConnection0(connection);
      }
   }

   public static String[] listConnections(boolean available) {
      synchronized(listConnectionsLock) {
         String connections = listConnections0(available);
         if (connections == null) {
            return new String[0];
         } else {
            int count = 0;
            int offset = 0;

            do {
               offset = connections.indexOf(44, offset + 1);
               ++count;
            } while(offset > 0);

            String[] ret = new String[count];
            int start = 0;

            for(int i = 0; i < count; ++i) {
               offset = connections.indexOf(44, start);
               if (offset > 0) {
                  ret[i] = connections.substring(start, offset);
               } else {
                  ret[i] = connections.substring(start);
               }

               start = offset + 1;
            }

            return ret;
         }
      }
   }

   public static String getMIDlet(String connection) {
      return getMIDlet0(connection);
   }

   public static String getFilter(String connection) {
      return getFilter0(connection);
   }

   public static long registerAlarm(String midlet, long time) throws ClassNotFoundException, ConnectionNotFoundException {
      safeCheckPermission();
      synchronized(registerAlarmLock) {
         TimeZone Tz_Default = TimeZone.getDefault();
         long timezone_offset = (long)Tz_Default.getRawOffset();
         long dst_offset = getDaylightSavingOffsetInMillis();
         long newtime = time + timezone_offset + dst_offset;
         long rettime = registerAlarm0(midlet, newtime);
         if (rettime != 0L) {
            rettime = rettime - timezone_offset - dst_offset;
         }

         return rettime;
      }
   }

   public static long getDaylightSavingOffsetInMillis() {
      synchronized(getDaylightSavingInfoLock) {
         return (long)(getDaylightSavingInfo0() * '\uea60');
      }
   }

   private static void safeCheckPermission() {
      synchronized(checkPermissionLock) {
         checkPermission0();
      }
   }
}
