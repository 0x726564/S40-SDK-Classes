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
      int rawOffset = this.getTimeZone().getRawOffset();
      long localMillis = this.time + (long)rawOffset;
      if (this.time > 0L && localMillis < 0L && rawOffset > 0) {
         localMillis = Long.MAX_VALUE;
      } else if (this.time < 0L && localMillis > 0L && rawOffset < 0) {
         localMillis = Long.MIN_VALUE;
      }

      this.timeToFields(localMillis);
      long days = localMillis / 86400000L;
      int millisInDay = (int)(localMillis - days * 86400000L);
      if (millisInDay < 0) {
         millisInDay = (int)((long)millisInDay + 86400000L);
      }

      int dstOffset = this.getTimeZone().getOffset(1, this.fields[1], this.fields[2], this.fields[5], this.fields[7], millisInDay) - rawOffset;
      millisInDay += dstOffset;
      if ((long)millisInDay >= 86400000L) {
         long dstMillis = localMillis + (long)dstOffset;
         millisInDay = (int)((long)millisInDay - 86400000L);
         if (localMillis > 0L && dstMillis < 0L && dstOffset > 0) {
            dstMillis = Long.MAX_VALUE;
         } else if (localMillis < 0L && dstMillis > 0L && dstOffset < 0) {
            dstMillis = Long.MIN_VALUE;
         }

         this.timeToFields(dstMillis);
      }

      this.fields[14] = millisInDay % 1000;
      millisInDay /= 1000;
      this.fields[13] = millisInDay % 60;
      millisInDay /= 60;
      this.fields[12] = millisInDay % 60;
      millisInDay /= 60;
      this.fields[11] = millisInDay;
      this.fields[9] = millisInDay / 12;
      this.fields[10] = millisInDay % 12;
   }

   private final void timeToFields(long theTime) {
      int dayOfYear;
      int rawYear;
      boolean isLeap;
      long gregorianEpochDay;
      int date_field;
      if (theTime >= -12219292800000L) {
         gregorianEpochDay = millisToJulianDay(theTime) - 1721426L;
         int[] rem = new int[1];
         date_field = floorDivide(gregorianEpochDay, 146097, rem);
         int n100 = floorDivide(rem[0], 36524, rem);
         int n4 = floorDivide(rem[0], 1461, rem);
         int n1 = floorDivide(rem[0], 365, rem);
         rawYear = 400 * date_field + 100 * n100 + 4 * n4 + n1;
         dayOfYear = rem[0];
         if (n100 != 4 && n1 != 4) {
            ++rawYear;
         } else {
            dayOfYear = 365;
         }

         isLeap = (rawYear & 3) == 0 && (rawYear % 100 != 0 || rawYear % 400 == 0);
         this.fields[7] = (int)((gregorianEpochDay + 1L) % 7L);
      } else {
         gregorianEpochDay = millisToJulianDay(theTime) - 1721424L;
         rawYear = (int)floorDivide(4L * gregorianEpochDay + 1464L, 1461L);
         long january1 = (long)(365 * (rawYear - 1) + floorDivide(rawYear - 1, 4));
         dayOfYear = (int)(gregorianEpochDay - january1);
         isLeap = (rawYear & 3) == 0;
         this.fields[7] = (int)((gregorianEpochDay - 1L) % 7L);
      }

      int correction = 0;
      int march1 = isLeap ? 60 : 59;
      if (dayOfYear >= march1) {
         correction = isLeap ? 1 : 2;
      }

      int month_field = (12 * (dayOfYear + correction) + 6) / 367;
      date_field = dayOfYear - (isLeap ? LEAP_NUM_DAYS[month_field] : NUM_DAYS[month_field]) + 1;
      int[] var10000 = this.fields;
      var10000[7] += this.fields[7] < 0 ? 8 : 1;
      this.fields[1] = rawYear;
      if (this.fields[1] < 1) {
         this.fields[1] = 1 - this.fields[1];
      }

      this.fields[2] = month_field + 0;
      this.fields[5] = date_field;
   }

   public static String toString(Calendar calendar) {
      if (calendar == null) {
         return "Thu Jan 01 00:00:00 UTC 1970";
      } else {
         int dow = calendar.get(7);
         int month = calendar.get(2);
         int day = calendar.get(5);
         int hour_of_day = calendar.get(11);
         int minute = calendar.get(12);
         int seconds = calendar.get(13);
         int year = calendar.get(1);
         String yr = Integer.toString(year);
         TimeZone zone = calendar.getTimeZone();
         String zoneID = zone.getID();
         if (zoneID == null) {
            zoneID = "";
         }

         StringBuffer sb = new StringBuffer(25 + zoneID.length() + yr.length());
         sb.append(days[dow - 1]).append(' ');
         sb.append(months[month]).append(' ');
         appendTwoDigits(sb, day).append(' ');
         appendTwoDigits(sb, hour_of_day).append(':');
         appendTwoDigits(sb, minute).append(':');
         appendTwoDigits(sb, seconds).append(' ');
         if (zoneID.length() > 0) {
            sb.append(zoneID).append(' ');
         }

         appendFourDigits(sb, year);
         return sb.toString();
      }
   }

   public static String toISO8601String(Calendar calendar) {
      if (calendar == null) {
         return "0000 00 00 00 00 00 +0000";
      } else {
         int year = calendar.get(1);
         int month = calendar.get(2) + 1;
         int day = calendar.get(5);
         int hour_of_day = calendar.get(11);
         int minute = calendar.get(12);
         int seconds = calendar.get(13);
         String yr = Integer.toString(year);
         StringBuffer sb = new StringBuffer(25 + yr.length());
         appendFourDigits(sb, year).append(' ');
         appendTwoDigits(sb, month).append(' ');
         appendTwoDigits(sb, day).append(' ');
         appendTwoDigits(sb, hour_of_day).append(' ');
         appendTwoDigits(sb, minute).append(' ');
         appendTwoDigits(sb, seconds).append(' ');
         TimeZone t = calendar.getTimeZone();
         int zoneOffsetInMinutes = t.getRawOffset() / 1000 / 60;
         if (zoneOffsetInMinutes < 0) {
            zoneOffsetInMinutes = Math.abs(zoneOffsetInMinutes);
            sb.append('-');
         } else {
            sb.append('+');
         }

         int zoneHours = zoneOffsetInMinutes / 60;
         int zoneMinutes = zoneOffsetInMinutes % 60;
         appendTwoDigits(sb, zoneHours);
         appendTwoDigits(sb, zoneMinutes);
         return sb.toString();
      }
   }

   private static final StringBuffer appendFourDigits(StringBuffer sb, int number) {
      if (number >= 0 && number < 1000) {
         sb.append('0');
         if (number < 100) {
            sb.append('0');
         }

         if (number < 10) {
            sb.append('0');
         }
      }

      return sb.append(number);
   }

   private static final StringBuffer appendTwoDigits(StringBuffer sb, int number) {
      if (number < 10) {
         sb.append('0');
      }

      return sb.append(number);
   }

   protected void computeTime() {
      this.correctTime();
      int year = this.fields[1];
      boolean isGregorian = year >= 1582;
      long julianDay = this.calculateJulianDay(isGregorian, year);
      long millis = julianDayToMillis(julianDay);
      if (isGregorian != millis >= -12219292800000L && julianDay != -106749550580L) {
         julianDay = this.calculateJulianDay(!isGregorian, year);
         millis = julianDayToMillis(julianDay);
      }

      int millisInDay = 0;
      int millisInDay = millisInDay + this.fields[11];
      millisInDay *= 60;
      millisInDay += this.fields[12];
      millisInDay *= 60;
      millisInDay += this.fields[13];
      millisInDay *= 1000;
      millisInDay += this.fields[14];
      int zoneOffset = this.getTimeZone().getRawOffset();
      millis += (long)millisInDay;
      int[] normalizedMillisInDay = new int[1];
      floorDivide(millis, 86400000, normalizedMillisInDay);
      int dow = julianDayToDayOfWeek(julianDay);
      int dstOffset = this.getTimeZone().getOffset(1, this.fields[1], this.fields[2], this.fields[5], dow, normalizedMillisInDay[0]) - zoneOffset;
      this.time = millis - (long)zoneOffset - (long)dstOffset;
   }

   private final long calculateJulianDay(boolean isGregorian, int year) {
      int month = false;
      int month = this.fields[2] - 0;
      if (month < 0 || month > 11) {
         int[] rem = new int[1];
         year += floorDivide(month, 12, rem);
         month = rem[0];
      }

      boolean isLeap = year % 4 == 0;
      long julianDay = 365L * (long)(year - 1) + (long)floorDivide(year - 1, 4) + 1721423L;
      if (isGregorian) {
         isLeap = isLeap && (year % 100 != 0 || year % 400 == 0);
         julianDay += (long)(floorDivide(year - 1, 400) - floorDivide(year - 1, 100) + 2);
      }

      julianDay += isLeap ? (long)LEAP_NUM_DAYS[month] : (long)NUM_DAYS[month];
      julianDay += (long)this.fields[5];
      return julianDay;
   }

   private void correctTime() {
      int value;
      if (this.isSet[11]) {
         value = this.fields[11] % 24;
         this.fields[11] = value;
         this.fields[9] = value < 12 ? 0 : 1;
         this.isSet[11] = false;
      } else {
         if (this.isSet[9]) {
            if (this.fields[9] != 0 && this.fields[9] != 1) {
               value = this.fields[11];
               this.fields[9] = value < 12 ? 0 : 1;
            }

            this.isSet[9] = false;
         }

         if (this.isSet[10]) {
            value = this.fields[10];
            if (value > 12) {
               this.fields[11] = value % 12 + 12;
               this.fields[10] = value % 12;
               this.fields[9] = 1;
            } else if (this.fields[9] == 1) {
               this.fields[11] = value + 12;
            } else {
               this.fields[11] = value;
            }

            this.isSet[10] = false;
         }

      }
   }

   private static final long millisToJulianDay(long millis) {
      return 2440588L + floorDivide(millis, 86400000L);
   }

   private static final long julianDayToMillis(long julian) {
      return (julian - 2440588L) * 86400000L;
   }

   private static final int julianDayToDayOfWeek(long julian) {
      int dayOfWeek = (int)((julian + 1L) % 7L);
      return dayOfWeek + (dayOfWeek < 0 ? 8 : 1);
   }

   private static final long floorDivide(long numerator, long denominator) {
      return numerator >= 0L ? numerator / denominator : (numerator + 1L) / denominator - 1L;
   }

   private static final int floorDivide(int numerator, int denominator) {
      return numerator >= 0 ? numerator / denominator : (numerator + 1) / denominator - 1;
   }

   private static final int floorDivide(int numerator, int denominator, int[] remainder) {
      if (numerator >= 0) {
         remainder[0] = numerator % denominator;
         return numerator / denominator;
      } else {
         int quotient = (numerator + 1) / denominator - 1;
         remainder[0] = numerator - quotient * denominator;
         return quotient;
      }
   }

   private static final int floorDivide(long numerator, int denominator, int[] remainder) {
      if (numerator >= 0L) {
         remainder[0] = (int)(numerator % (long)denominator);
         return (int)(numerator / (long)denominator);
      } else {
         int quotient = (int)((numerator + 1L) / (long)denominator - 1L);
         remainder[0] = (int)(numerator - (long)(quotient * denominator));
         return quotient;
      }
   }
}
