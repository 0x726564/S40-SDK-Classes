package com.sun.midp.io;

import java.util.Hashtable;

public class DateParser {
   protected int year;
   protected int month;
   protected int day;
   protected int hour;
   protected int minute;
   protected int second;
   protected int milli;
   protected int tzoffset;
   protected static int local_tz;
   private static Hashtable timezones;
   private int[] days_in_month = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
   private String[] month_shorts = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
   private String[] weekday_shorts = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
   private static long julianDayOffset = 2440588L;
   private static int millisPerHour = 3600000;
   private static int millisPerDay;
   private static final int JAN_1_1_JULIAN_DAY = 1721426;
   private static final String[] wtb;
   private static final int[] ttb;
   private static final int[] NUM_DAYS;
   private static final int[] LEAP_NUM_DAYS;

   DateParser(int year, int month, int day, int hour, int minute, int second) {
      if (year >= 1583 && month >= 0 && month <= 11 && day >= 0 && (day <= this.days_in_month[month] || month == 1 && day == 29 && year % 4 == 0) && hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59 && second >= 0 && second <= 59) {
         this.year = year;
         this.month = month;
         this.day = day;
         this.hour = hour;
         this.minute = minute;
         this.second = second;
         this.milli = 0;
      } else {
         throw new IllegalArgumentException();
      }
   }

   DateParser(String s) {
      this.internalParse(s);
   }

   static void setTimeZone(String tz) {
      if (timezones.get(tz) != null) {
         local_tz = (Integer)timezones.get(tz);
      }
   }

   public static long parse(String s) {
      return (new DateParser(s)).getTime();
   }

   int getYear() {
      return this.year;
   }

   int getMonth() {
      return this.month;
   }

   int getDay() {
      return this.day;
   }

   int getHour() {
      return this.hour;
   }

   int getMinute() {
      return this.minute;
   }

   int getSecond() {
      return this.second;
   }

   long getTime() {
      long julianDay = this.computeJulianDay(this.year, this.month, this.day);
      long millis = this.julianDayToMillis(julianDay);
      int millisInDay = 0;
      int millisInDay = millisInDay + this.hour;
      millisInDay *= 60;
      millisInDay += this.minute;
      millisInDay *= 60;
      millisInDay += this.second;
      millisInDay *= 1000;
      millisInDay += this.milli;
      return millis + (long)millisInDay - (long)this.tzoffset;
   }

   private final long computeJulianDay(int year, int month, int day) {
      boolean isLeap = year % 4 == 0;
      int y = year - 1;
      long julianDay = 365L * (long)y + floorDivide((long)y, 4L) + 1721423L;
      isLeap = isLeap && (year % 100 != 0 || year % 400 == 0);
      julianDay += floorDivide((long)y, 400L) - floorDivide((long)y, 100L) + 2L;
      julianDay += isLeap ? (long)LEAP_NUM_DAYS[month] : (long)NUM_DAYS[month];
      julianDay += (long)day;
      return julianDay;
   }

   private static final long floorDivide(long numerator, long denominator) {
      return numerator >= 0L ? numerator / denominator : (numerator + 1L) / denominator - 1L;
   }

   private long julianDayToMillis(long julian) {
      return (julian - julianDayOffset) * (long)millisPerDay;
   }

   private void internalParse(String s) {
      int year = -1;
      int mon = -1;
      int mday = -1;
      int hour = -1;
      int min = -1;
      int sec = -1;
      int c = true;
      int i = 0;
      int n = true;
      int tzoffset = -1;
      int prevc = 0;
      if (s != null) {
         int limit = s.length();

         while(true) {
            while(true) {
               char c;
               do {
                  do {
                     if (i >= limit) {
                        if (year >= 1583 && mon >= 0 && mday >= 0) {
                           if (sec < 0) {
                              sec = 0;
                           }

                           if (min < 0) {
                              min = 0;
                           }

                           if (hour < 0) {
                              hour = 0;
                           }

                           this.year = year;
                           this.month = mon;
                           this.day = mday;
                           this.hour = hour;
                           this.tzoffset = -tzoffset * 60 * 1000;
                           this.minute = min;
                           this.second = sec;
                           this.milli = 0;
                           return;
                        }

                        throw new IllegalArgumentException();
                     }

                     c = s.charAt(i);
                     ++i;
                  } while(c <= ' ');
               } while(c == ',');

               int st;
               if (c == '(') {
                  st = 1;

                  while(i < limit) {
                     c = s.charAt(i);
                     ++i;
                     if (c == '(') {
                        ++st;
                     } else if (c == ')') {
                        --st;
                        if (st <= 0) {
                           break;
                        }
                     }
                  }
               } else if ('0' <= c && c <= '9') {
                  int n;
                  for(n = c - 48; i < limit && '0' <= (c = s.charAt(i)) && c <= '9'; ++i) {
                     n = n * 10 + c - 48;
                  }

                  if (prevc != '+' && (prevc != '-' || year < 0)) {
                     if (n >= 70) {
                        if (year >= 0 || c > ' ' && c != ',' && c != '/' && i < limit) {
                           throw new IllegalArgumentException();
                        }

                        year = n < 100 ? n + 1900 : n;
                     } else if (c == ':') {
                        if (hour < 0) {
                           hour = (byte)n;
                        } else {
                           if (min >= 0) {
                              throw new IllegalArgumentException();
                           }

                           min = (byte)n;
                        }
                     } else if (c == '/') {
                        if (mon < 0) {
                           mon = (byte)(n - 1);
                        } else {
                           if (mday >= 0) {
                              throw new IllegalArgumentException();
                           }

                           mday = (byte)n;
                        }
                     } else {
                        if (i < limit && c != ',' && c > ' ' && c != '-') {
                           throw new IllegalArgumentException();
                        }

                        if (hour >= 0 && min < 0) {
                           min = (byte)n;
                        } else if (min >= 0 && sec < 0) {
                           sec = (byte)n;
                        } else {
                           if (mday >= 0) {
                              throw new IllegalArgumentException();
                           }

                           mday = (byte)n;
                        }
                     }
                  } else {
                     if (n < 24) {
                        n *= 60;
                     } else {
                        n = n % 100 + n / 100 * 60;
                     }

                     if (prevc == '+') {
                        n = -n;
                     }

                     if (tzoffset != 0 && tzoffset != -1) {
                        throw new IllegalArgumentException();
                     }

                     tzoffset = n;
                  }

                  prevc = 0;
               } else if (c != '/' && c != ':' && c != '+' && c != '-') {
                  for(st = i - 1; i < limit; ++i) {
                     c = s.charAt(i);
                     if (('A' > c || c > 'Z') && ('a' > c || c > 'z')) {
                        break;
                     }
                  }

                  if (i <= st + 1) {
                     throw new IllegalArgumentException();
                  }

                  int k = wtb.length;

                  while(true) {
                     --k;
                     if (k < 0) {
                        break;
                     }

                     if (wtb[k].regionMatches(true, 0, s, st, i - st)) {
                        int action = ttb[k];
                        if (action != 0) {
                           if (action == 1) {
                              if (hour > 12 || hour < 1) {
                                 throw new IllegalArgumentException();
                              }

                              if (hour < 12) {
                                 hour += 12;
                              }
                           } else if (action == 14) {
                              if (hour > 12 || hour < 1) {
                                 throw new IllegalArgumentException();
                              }

                              if (hour == 12) {
                                 hour = 0;
                              }
                           } else if (action <= 13) {
                              if (mon >= 0) {
                                 throw new IllegalArgumentException();
                              }

                              mon = (byte)(action - 2);
                           } else {
                              tzoffset = action - 10000;
                           }
                        }
                        break;
                     }
                  }

                  if (k < 0) {
                     throw new IllegalArgumentException();
                  }

                  prevc = 0;
               } else {
                  prevc = c;
               }
            }
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   static {
      millisPerDay = 24 * millisPerHour;
      wtb = new String[]{"am", "pm", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday", "january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december", "gmt", "ut", "utc", "est", "edt", "cst", "cdt", "mst", "mdt", "pst", "pdt"};
      ttb = new int[]{14, 1, 0, 0, 0, 0, 0, 0, 0, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 10000, 10000, 10000, 10300, 10240, 10360, 10300, 10420, 10360, 10480, 10420};
      NUM_DAYS = new int[]{0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
      LEAP_NUM_DAYS = new int[]{0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};
      timezones = new Hashtable();
      timezones.put("GMT", new Integer(0 * millisPerHour));
      timezones.put("UT", new Integer(0 * millisPerHour));
      timezones.put("UTC", new Integer(0 * millisPerHour));
      timezones.put("PST", new Integer(-8 * millisPerHour));
      timezones.put("PDT", new Integer(-7 * millisPerHour));
      timezones.put("JST", new Integer(9 * millisPerHour));
      local_tz = (Integer)timezones.get("PST");
   }
}
