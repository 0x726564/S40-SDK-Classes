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

   public static synchronized TimeZone getTimeZone(String var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         TimeZoneImpl var1 = (TimeZoneImpl)getDefault();
         TimeZone var2 = var1.getInstance(var0);
         if (var2 == null) {
            var2 = var1.getInstance("GMT");
         }

         return var2;
      }
   }

   public static synchronized TimeZone getDefault() {
      TimeZoneImpl var0 = null;

      try {
         Class var1 = Class.forName("com.sun.cldc.util.j2me.TimeZoneImpl");
         var0 = (TimeZoneImpl)var1.newInstance();
         var0 = (TimeZoneImpl)var0.getInstance((String)null);
      } catch (Exception var2) {
      }

      return var0;
   }

   public static String[] getAvailableIDs() {
      return ((TimeZoneImpl)getDefault()).getIDs();
   }
}
