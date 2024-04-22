package com.sun.cldc.util.j2me;

import java.util.TimeZone;

public class TimeZoneImpl extends TimeZone {
   private String ID;
   private static String[] ids = null;
   private int rawOffset;
   private static final int MILLIS_PER_HOUR = 3600000;
   private static final int MILLIS_PER_DAY = 86400000;
   private final byte[] monthLength;
   private static final byte[] staticMonthLength = new byte[]{31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
   private static final int ONE_MINUTE = 60000;
   private static final String GMT_ID = "GMT";
   private static final int GMT_ID_LENGTH = 3;
   static TimeZone[] zones = new TimeZone[]{new TimeZoneImpl(0, "GMT"), new TimeZoneImpl(0, "UTC")};

   public TimeZoneImpl() {
      this.monthLength = staticMonthLength;
   }

   private TimeZoneImpl(int var1, String var2) {
      this.monthLength = staticMonthLength;
      this.rawOffset = var1;
      this.ID = var2;
   }

   public int getOffset(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (var3 >= 0 && var3 <= 11) {
         byte var7 = staticMonthLength[var3];
         if ((var1 == 0 || var1 == 1) && var4 >= 1 && var4 <= var7 && var5 >= 1 && var5 <= 7 && var6 >= 0 && var6 < 86400000) {
            return this.rawOffset;
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         throw new IllegalArgumentException("Illegal month " + var3);
      }
   }

   public int getRawOffset() {
      return this.rawOffset;
   }

   public boolean useDaylightTime() {
      return false;
   }

   public String getID() {
      return this.ID;
   }

   public synchronized TimeZone getInstance(String var1) {
      if (var1 == null) {
         int var2 = getLocalTimeZoneInfo();
         StringBuffer var3 = new StringBuffer("GMT".length() + 6);
         var3.append("GMT");
         int var4 = Math.abs(var2);
         if (var2 != 0) {
            if (var2 > 0) {
               var3.append('+');
            } else {
               var3.append('-');
            }

            int var5 = var4 / 60;
            int var6 = var4 % 60;
            if (var5 < 10) {
               var3.append('0');
            }

            var3.append(var5);
            var3.append(':');
            if (var6 < 10) {
               var3.append('0');
            }

            var3.append(var6);
         }

         return new TimeZoneImpl(var2 * '\uea60', var3.toString());
      } else {
         return getTimeZone(var1);
      }
   }

   public static synchronized TimeZone getTimeZone(String var0) {
      for(int var1 = 0; var1 < zones.length; ++var1) {
         if (zones[var1].getID().equals(var0)) {
            return zones[var1];
         }
      }

      TimeZone var2 = parseCustomTimeZone(var0);
      if (var2 == null) {
         var2 = zones[0];
      }

      return var2;
   }

   public synchronized String[] getIDs() {
      if (ids == null) {
         ids = new String[zones.length];

         for(int var1 = 0; var1 < zones.length; ++var1) {
            ids[var1] = zones[var1].getID();
         }
      }

      return ids;
   }

   private static final TimeZone parseCustomTimeZone(String var0) {
      try {
         if (var0.length() > 3 && var0.regionMatches(true, 0, "GMT", 0, 3)) {
            boolean var1 = false;
            if (var0.charAt(3) == '-') {
               var1 = true;
            } else if (var0.charAt(3) != '+') {
               return null;
            }

            if (!Character.isDigit(var0.charAt(4))) {
               return null;
            }

            boolean var2 = false;
            boolean var3 = false;
            int var4 = var0.indexOf(58);
            int var8;
            int var9;
            int var10;
            if (var4 != -1) {
               var8 = Integer.parseInt(var0.substring(4, var4));
               String var5 = var0.substring(var4 + 1);
               if (var5.length() != 2) {
                  return null;
               }

               var9 = Integer.parseInt(var5);
               if (var9 < 0) {
                  return null;
               }
            } else {
               var10 = var0.length();
               if (var10 > 7) {
                  var8 = Integer.parseInt(var0.substring(4, 6));
                  var9 = Integer.parseInt(var0.substring(6));
               } else if (var10 > 6) {
                  var8 = Integer.parseInt(var0.substring(4, 5));
                  var9 = Integer.parseInt(var0.substring(5));
               } else {
                  var8 = Integer.parseInt(var0.substring(4));
                  var9 = 0;
               }
            }

            if (var8 < 24 && var9 < 60) {
               if (var8 == 0 & var9 == 0) {
                  var1 = false;
               }

               var10 = var8 * 60 + var9;
               if (var1) {
                  var10 = -var10;
               }

               StringBuffer var6 = new StringBuffer(10);
               var6.append("GMT");
               if (var1) {
                  var6.append("-");
               } else {
                  var6.append("+");
               }

               if (var8 < 10) {
                  var6.append("0");
               }

               var6.append(var8 + ":");
               if (var9 < 10) {
                  var6.append("0");
               }

               var6.append("" + var9);
               return new TimeZoneImpl(var10 * '\uea60', var6.toString());
            }

            return null;
         }
      } catch (NumberFormatException var7) {
      }

      return null;
   }

   private static native int getLocalTimeZoneInfo();
}
