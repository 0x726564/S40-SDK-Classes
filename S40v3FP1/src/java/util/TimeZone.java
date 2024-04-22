package java.util;

import com.sun.cldc.util.j2me.TimeZoneImpl;

public abstract class TimeZone {
   private static TimeZoneImpl defaultZone = null;
   private static String platform = null;
   private static String classRoot = null;

   public abstract int getOffset(int var1, int var2, int var3, int var4, int var5, int var6);

   public abstract int getRawOffset();

   public abstract boolean useDaylightTime();

   public String getID() {
      return null;
   }

   public static synchronized TimeZone getTimeZone(String var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         getDefault();
         TimeZone var1 = defaultZone.getInstance(var0);
         if (var1 == null) {
            var1 = defaultZone.getInstance("GMT");
         }

         return var1;
      }
   }

   public static synchronized TimeZone getDefault() {
      if (defaultZone == null) {
         try {
            Class var0 = Class.forName("com.sun.cldc.util.j2me.TimeZoneImpl");
            defaultZone = (TimeZoneImpl)var0.newInstance();
            defaultZone = (TimeZoneImpl)defaultZone.getInstance((String)null);
         } catch (Exception var1) {
         }
      }

      return defaultZone;
   }

   public static String[] getAvailableIDs() {
      getDefault();
      return defaultZone.getIDs();
   }
}
