package java.util;

import com.sun.cldc.util.j2me.TimeZoneImpl;

public abstract class TimeZone {
   private static String platform = null;
   private static String classRoot = null;

   public abstract int getOffset(int var1, int var2, int var3, int var4, int var5, int var6);

   public abstract int getRawOffset();

   public abstract boolean useDaylightTime();

   public String getID() {
      return null;
   }

   public static synchronized TimeZone getTimeZone(String ID) {
      if (ID == null) {
         throw new NullPointerException();
      } else {
         TimeZoneImpl defaultZone = (TimeZoneImpl)getDefault();
         TimeZone tz = defaultZone.getInstance(ID);
         if (tz == null) {
            tz = defaultZone.getInstance("GMT");
         }

         return tz;
      }
   }

   public static synchronized TimeZone getDefault() {
      TimeZoneImpl defaultZone = null;

      try {
         Class clazz = Class.forName("com.sun.cldc.util.j2me.TimeZoneImpl");
         defaultZone = (TimeZoneImpl)clazz.newInstance();
         defaultZone = (TimeZoneImpl)defaultZone.getInstance((String)null);
      } catch (Exception var2) {
      }

      return defaultZone;
   }

   public static String[] getAvailableIDs() {
      return ((TimeZoneImpl)getDefault()).getIDs();
   }
}
