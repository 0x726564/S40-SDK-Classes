package com.sun.cldc.util.j2me;

import java.util.Calendar;
import java.util.TimeZone;

public class CalendarImpl extends Calendar {
   private static final int BC = 0;
   private static final int AD = 1;
   private static final int JAN_1_1_JULIAN_DAY = 1721426;
   private static final int EPOCH_JULIAN_DAY = 2440588;
   private static final int[] NUM_DAYS = new int[]{0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
   private static final int[] LEAP_NUM_DAYS = new int[]{0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};
   private static final int ONE_SECOND = 1000;
   private static final int ONE_MINUTE = 60000;
   private static final int ONE_HOUR = 3600000;
   private static final long ONE_DAY = 86400000L;
   private static final long ONE_WEEK = 604800000L;
   private static final long gregorianCutover = -12219292800000L;
   private static final int gregorianCutoverYear = 1582;
   static String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
   static String[] days = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

   protected void computeFields() {
      int var1 = this.getTimeZone().getRawOffset();
      long var2 = this.time + (long)var1;
      if (this.time > 0L && var2 < 0L && var1 > 0) {
         var2 = Long.MAX_VALUE;
      } else if (this.time < 0L && var2 > 0L && var1 < 0) {
         var2 = Long.MIN_VALUE;
      }

      this.timeToFields(var2);
      long var4 = var2 / 86400000L;
      int var6 = (int)(var2 - var4 * 86400000L);
      if (var6 < 0) {
         var6 = (int)((long)var6 + 86400000L);
      }

      int var7 = this.getTimeZone().getOffset(1, this.fields[1], this.fields[2], this.fields[5], this.fields[7], var6) - var1;
      var6 += var7;
      if ((long)var6 >= 86400000L) {
         long var8 = var2 + (long)var7;
         var6 = (int)((long)var6 - 86400000L);
         if (var2 > 0L && var8 < 0L && var7 > 0) {
            var8 = Long.MAX_VALUE;
         } else if (var2 < 0L && var8 > 0L && var7 < 0) {
            var8 = Long.MIN_VALUE;
         }

         this.timeToFields(var8);
      }

      this.fields[14] = var6 % 1000;
      var6 /= 1000;
      this.fields[13] = var6 % 60;
      var6 /= 60;
      this.fields[12] = var6 % 60;
      var6 /= 60;
      this.fields[11] = var6;
      this.fields[9] = var6 / 12;
      this.fields[10] = var6 % 12;
   }

   private final void timeToFields(long var1) {
      int var3;
      int var5;
      boolean var6;
      long var7;
      int var10;
      if (var1 >= -12219292800000L) {
         var7 = millisToJulianDay(var1) - 1721426L;
         int[] var9 = new int[1];
         var10 = floorDivide(var7, 146097, var9);
         int var11 = floorDivide(var9[0], 36524, var9);
         int var12 = floorDivide(var9[0], 1461, var9);
         int var13 = floorDivide(var9[0], 365, var9);
         var5 = 400 * var10 + 100 * var11 + 4 * var12 + var13;
         var3 = var9[0];
         if (var11 != 4 && var13 != 4) {
            ++var5;
         } else {
            var3 = 365;
         }

         var6 = (var5 & 3) == 0 && (var5 % 100 != 0 || var5 % 400 == 0);
         this.fields[7] = (int)((var7 + 1L) % 7L);
      } else {
         var7 = millisToJulianDay(var1) - 1721424L;
         var5 = (int)floorDivide(4L * var7 + 1464L, 1461L);
         long var15 = (long)(365 * (var5 - 1) + floorDivide(var5 - 1, 4));
         var3 = (int)(var7 - var15);
         var6 = (var5 & 3) == 0;
         this.fields[7] = (int)((var7 - 1L) % 7L);
      }

      int var14 = 0;
      int var8 = var6 ? 60 : 59;
      if (var3 >= var8) {
         var14 = var6 ? 1 : 2;
      }

      int var16 = (12 * (var3 + var14) + 6) / 367;
      var10 = var3 - (var6 ? LEAP_NUM_DAYS[var16] : NUM_DAYS[var16]) + 1;
      int[] var10000 = this.fields;
      var10000[7] += this.fields[7] < 0 ? 8 : 1;
      this.fields[1] = var5;
      if (this.fields[1] < 1) {
         this.fields[1] = 1 - this.fields[1];
      }

      this.fields[2] = var16 + 0;
      this.fields[5] = var10;
   }

   public static String toString(Calendar var0) {
      if (var0 == null) {
         return "Thu Jan 01 00:00:00 UTC 1970";
      } else {
         int var1 = var0.get(7);
         int var2 = var0.get(2);
         int var3 = var0.get(5);
         int var4 = var0.get(11);
         int var5 = var0.get(12);
         int var6 = var0.get(13);
         int var7 = var0.get(1);
         String var8 = Integer.toString(var7);
         TimeZone var9 = var0.getTimeZone();
         String var10 = var9.getID();
         if (var10 == null) {
            var10 = "";
         }

         StringBuffer var11 = new StringBuffer(25 + var10.length() + var8.length());
         var11.append(days[var1 - 1]).append(' ');
         var11.append(months[var2]).append(' ');
         appendTwoDigits(var11, var3).append(' ');
         appendTwoDigits(var11, var4).append(':');
         appendTwoDigits(var11, var5).append(':');
         appendTwoDigits(var11, var6).append(' ');
         if (var10.length() > 0) {
            var11.append(var10).append(' ');
         }

         appendFourDigits(var11, var7);
         return var11.toString();
      }
   }

   public static String toISO8601String(Calendar var0) {
      if (var0 == null) {
         return "0000 00 00 00 00 00 +0000";
      } else {
         int var1 = var0.get(1);
         int var2 = var0.get(2) + 1;
         int var3 = var0.get(5);
         int var4 = var0.get(11);
         int var5 = var0.get(10);
         int var6 = var0.get(12);
         int var7 = var0.get(13);
         String var8 = Integer.toString(var1);
         StringBuffer var9 = new StringBuffer(25 + var8.length());
         appendFourDigits(var9, var1).append(' ');
         appendTwoDigits(var9, var2).append(' ');
         appendTwoDigits(var9, var3).append(' ');
         appendTwoDigits(var9, var4).append(' ');
         appendTwoDigits(var9, var6).append(' ');
         appendTwoDigits(var9, var7).append(' ');
         TimeZone var10 = var0.getTimeZone();
         int var11 = var10.getRawOffset() / 1000 / 60;
         if (var11 < 0) {
            var11 = Math.abs(var11);
            var9.append('-');
         } else {
            var9.append('+');
         }

         int var12 = var11 / 60;
         int var13 = var11 % 60;
         appendTwoDigits(var9, var12);
         appendTwoDigits(var9, var13);
         return var9.toString();
      }
   }

   private static final StringBuffer appendFourDigits(StringBuffer var0, int var1) {
      if (var1 >= 0 && var1 < 1000) {
         var0.append('0');
         if (var1 < 100) {
            var0.append('0');
         }

         if (var1 < 10) {
            var0.append('0');
         }
      }

      return var0.append(var1);
   }

   private static final StringBuffer appendTwoDigits(StringBuffer var0, int var1) {
      if (var1 < 10) {
         var0.append('0');
      }

      return var0.append(var1);
   }

   protected void computeTime() {
      this.correctTime();
      int var1 = this.fields[1];
      boolean var2 = var1 >= 1582;
      long var3 = this.calculateJulianDay(var2, var1);
      long var5 = julianDayToMillis(var3);
      if (var2 != var5 >= -12219292800000L && var3 != -106749550580L) {
         var3 = this.calculateJulianDay(!var2, var1);
         var5 = julianDayToMillis(var3);
      }

      byte var7 = 0;
      int var12 = var7 + this.fields[11];
      var12 *= 60;
      var12 += this.fields[12];
      var12 *= 60;
      var12 += this.fields[13];
      var12 *= 1000;
      var12 += this.fields[14];
      int var8 = this.getTimeZone().getRawOffset();
      var5 += (long)var12;
      int[] var9 = new int[1];
      floorDivide(var5, 86400000, var9);
      int var10 = julianDayToDayOfWeek(var3);
      int var11 = this.getTimeZone().getOffset(1, this.fields[1], this.fields[2], this.fields[5], var10, var9[0]) - var8;
      this.time = var5 - (long)var8 - (long)var11;
   }

   private final long calculateJulianDay(boolean var1, int var2) {
      boolean var3 = false;
      long var4 = 0L;
      int var9 = this.fields[2] - 0;
      if (var9 < 0 || var9 > 11) {
         int[] var6 = new int[1];
         var2 += floorDivide(var9, 12, var6);
         var9 = var6[0];
      }

      boolean var10 = var2 % 4 == 0;
      long var7 = 365L * (long)(var2 - 1) + (long)floorDivide(var2 - 1, 4) + 1721423L;
      if (var1) {
         var10 = var10 && (var2 % 100 != 0 || var2 % 400 == 0);
         var7 += (long)(floorDivide(var2 - 1, 400) - floorDivide(var2 - 1, 100) + 2);
      }

      var7 += var10 ? (long)LEAP_NUM_DAYS[var9] : (long)NUM_DAYS[var9];
      var7 += (long)this.fields[5];
      return var7;
   }

   private void correctTime() {
      int var1;
      if (this.isSet[11]) {
         var1 = this.fields[11] % 24;
         this.fields[11] = var1;
         this.fields[9] = var1 < 12 ? 0 : 1;
         this.isSet[11] = false;
      } else {
         if (this.isSet[9]) {
            if (this.fields[9] != 0 && this.fields[9] != 1) {
               var1 = this.fields[11];
               this.fields[9] = var1 < 12 ? 0 : 1;
            }

            this.isSet[9] = false;
         }

         if (this.isSet[10]) {
            var1 = this.fields[10];
            if (var1 > 12) {
               this.fields[11] = var1 % 12 + 12;
               this.fields[10] = var1 % 12;
               this.fields[9] = 1;
            } else if (this.fields[9] == 1) {
               this.fields[11] = var1 + 12;
            } else {
               this.fields[11] = var1;
            }

            this.isSet[10] = false;
         }

      }
   }

   private static final long millisToJulianDay(long var0) {
      return 2440588L + floorDivide(var0, 86400000L);
   }

   private static final long julianDayToMillis(long var0) {
      return (var0 - 2440588L) * 86400000L;
   }

   private static final int julianDayToDayOfWeek(long var0) {
      int var2 = (int)((var0 + 1L) % 7L);
      return var2 + (var2 < 0 ? 8 : 1);
   }

   private static final long floorDivide(long var0, long var2) {
      return var0 >= 0L ? var0 / var2 : (var0 + 1L) / var2 - 1L;
   }

   private static final int floorDivide(int var0, int var1) {
      return var0 >= 0 ? var0 / var1 : (var0 + 1) / var1 - 1;
   }

   private static final int floorDivide(int var0, int var1, int[] var2) {
      if (var0 >= 0) {
         var2[0] = var0 % var1;
         return var0 / var1;
      } else {
         int var3 = (var0 + 1) / var1 - 1;
         var2[0] = var0 - var3 * var1;
         return var3;
      }
   }

   private static final int floorDivide(long var0, int var2, int[] var3) {
      if (var0 >= 0L) {
         var3[0] = (int)(var0 % (long)var2);
         return (int)(var0 / (long)var2);
      } else {
         int var4 = (int)((var0 + 1L) / (long)var2 - 1L);
         var3[0] = (int)(var0 - (long)(var4 * var2));
         return var4;
      }
   }
}
