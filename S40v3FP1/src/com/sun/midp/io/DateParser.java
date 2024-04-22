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

   DateParser(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (var1 >= 1583 && var2 >= 0 && var2 <= 11 && var3 >= 0 && (var3 <= this.days_in_month[var2] || var2 == 1 && var3 == 29 && var1 % 4 == 0) && var4 >= 0 && var4 <= 23 && var5 >= 0 && var5 <= 59 && var6 >= 0 && var6 <= 59) {
         this.year = var1;
         this.month = var2;
         this.day = var3;
         this.hour = var4;
         this.minute = var5;
         this.second = var6;
         this.milli = 0;
      } else {
         throw new IllegalArgumentException();
      }
   }

   DateParser(String var1) {
      this.internalParse(var1);
   }

   static void setTimeZone(String var0) {
      if (timezones.get(var0) != null) {
         local_tz = (Integer)timezones.get(var0);
      }
   }

   public static long parse(String var0) {
      return (new DateParser(var0)).getTime();
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
      long var1 = this.computeJulianDay(this.year, this.month, this.day);
      long var3 = this.julianDayToMillis(var1);
      byte var5 = 0;
      int var6 = var5 + this.hour;
      var6 *= 60;
      var6 += this.minute;
      var6 *= 60;
      var6 += this.second;
      var6 *= 1000;
      var6 += this.milli;
      return var3 + (long)var6 - (long)this.tzoffset;
   }

   private final long computeJulianDay(int var1, int var2, int var3) {
      long var5 = 0L;
      boolean var7 = var1 % 4 == 0;
      int var4 = var1 - 1;
      long var8 = 365L * (long)var4 + floorDivide((long)var4, 4L) + 1721423L;
      var7 = var7 && (var1 % 100 != 0 || var1 % 400 == 0);
      var8 += floorDivide((long)var4, 400L) - floorDivide((long)var4, 100L) + 2L;
      var8 += var7 ? (long)LEAP_NUM_DAYS[var2] : (long)NUM_DAYS[var2];
      var8 += (long)var3;
      return var8;
   }

   private static final long floorDivide(long var0, long var2) {
      return var0 >= 0L ? var0 / var2 : (var0 + 1L) / var2 - 1L;
   }

   private long julianDayToMillis(long var1) {
      return (var1 - julianDayOffset) * (long)millisPerDay;
   }

   private void internalParse(String var1) {
      int var2 = -1;
      byte var3 = -1;
      byte var4 = -1;
      int var5 = -1;
      byte var6 = -1;
      byte var7 = -1;
      boolean var8 = true;
      boolean var9 = true;
      int var10 = 0;
      boolean var11 = true;
      boolean var12 = true;
      int var13 = -1;
      char var14 = 0;
      if (var1 != null) {
         int var15 = var1.length();

         while(true) {
            while(true) {
               char var19;
               do {
                  do {
                     if (var10 >= var15) {
                        if (var2 >= 1583 && var3 >= 0 && var4 >= 0) {
                           if (var7 < 0) {
                              var7 = 0;
                           }

                           if (var6 < 0) {
                              var6 = 0;
                           }

                           if (var5 < 0) {
                              var5 = 0;
                           }

                           this.year = var2;
                           this.month = var3;
                           this.day = var4;
                           this.hour = var5;
                           this.tzoffset = -var13 * 60 * 1000;
                           this.minute = var6;
                           this.second = var7;
                           this.milli = 0;
                           return;
                        }

                        throw new IllegalArgumentException();
                     }

                     var19 = var1.charAt(var10);
                     ++var10;
                  } while(var19 <= ' ');
               } while(var19 == ',');

               int var16;
               if (var19 == '(') {
                  var16 = 1;

                  while(var10 < var15) {
                     var19 = var1.charAt(var10);
                     ++var10;
                     if (var19 == '(') {
                        ++var16;
                     } else if (var19 == ')') {
                        --var16;
                        if (var16 <= 0) {
                           break;
                        }
                     }
                  }
               } else if ('0' <= var19 && var19 <= '9') {
                  int var20;
                  for(var20 = var19 - 48; var10 < var15 && '0' <= (var19 = var1.charAt(var10)) && var19 <= '9'; ++var10) {
                     var20 = var20 * 10 + var19 - 48;
                  }

                  if (var14 != '+' && (var14 != '-' || var2 < 0)) {
                     if (var20 >= 70) {
                        if (var2 >= 0 || var19 > ' ' && var19 != ',' && var19 != '/' && var10 < var15) {
                           throw new IllegalArgumentException();
                        }

                        var2 = var20 < 100 ? var20 + 1900 : var20;
                     } else if (var19 == ':') {
                        if (var5 < 0) {
                           var5 = (byte)var20;
                        } else {
                           if (var6 >= 0) {
                              throw new IllegalArgumentException();
                           }

                           var6 = (byte)var20;
                        }
                     } else if (var19 == '/') {
                        if (var3 < 0) {
                           var3 = (byte)(var20 - 1);
                        } else {
                           if (var4 >= 0) {
                              throw new IllegalArgumentException();
                           }

                           var4 = (byte)var20;
                        }
                     } else {
                        if (var10 < var15 && var19 != ',' && var19 > ' ' && var19 != '-') {
                           throw new IllegalArgumentException();
                        }

                        if (var5 >= 0 && var6 < 0) {
                           var6 = (byte)var20;
                        } else if (var6 >= 0 && var7 < 0) {
                           var7 = (byte)var20;
                        } else {
                           if (var4 >= 0) {
                              throw new IllegalArgumentException();
                           }

                           var4 = (byte)var20;
                        }
                     }
                  } else {
                     if (var20 < 24) {
                        var20 *= 60;
                     } else {
                        var20 = var20 % 100 + var20 / 100 * 60;
                     }

                     if (var14 == '+') {
                        var20 = -var20;
                     }

                     if (var13 != 0 && var13 != -1) {
                        throw new IllegalArgumentException();
                     }

                     var13 = var20;
                  }

                  var14 = 0;
               } else if (var19 != '/' && var19 != ':' && var19 != '+' && var19 != '-') {
                  for(var16 = var10 - 1; var10 < var15; ++var10) {
                     var19 = var1.charAt(var10);
                     if (('A' > var19 || var19 > 'Z') && ('a' > var19 || var19 > 'z')) {
                        break;
                     }
                  }

                  if (var10 <= var16 + 1) {
                     throw new IllegalArgumentException();
                  }

                  int var17 = wtb.length;

                  while(true) {
                     --var17;
                     if (var17 < 0) {
                        break;
                     }

                     if (wtb[var17].regionMatches(true, 0, var1, var16, var10 - var16)) {
                        int var18 = ttb[var17];
                        if (var18 != 0) {
                           if (var18 == 1) {
                              if (var5 > 12 || var5 < 1) {
                                 throw new IllegalArgumentException();
                              }

                              if (var5 < 12) {
                                 var5 += 12;
                              }
                           } else if (var18 == 14) {
                              if (var5 > 12 || var5 < 1) {
                                 throw new IllegalArgumentException();
                              }

                              if (var5 == 12) {
                                 var5 = 0;
                              }
                           } else if (var18 <= 13) {
                              if (var3 >= 0) {
                                 throw new IllegalArgumentException();
                              }

                              var3 = (byte)(var18 - 2);
                           } else {
                              var13 = var18 - 10000;
                           }
                        }
                        break;
                     }
                  }

                  if (var17 < 0) {
                     throw new IllegalArgumentException();
                  }

                  var14 = 0;
               } else {
                  var14 = var19;
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
