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
   private static Hashtable fO;
   private static long fP = 2440588L;
   private static int millisPerHour = 3600000;
   private static int fQ;
   private static final String[] fR;
   private static final int[] fS;
   private static final int[] fT;
   private static final int[] fU;

   private DateParser(String var1) {
      int[] var10000 = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
      String[] var17 = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
      var17 = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
      var1 = var1;
      int var2 = -1;
      byte var3 = -1;
      byte var4 = -1;
      int var5 = -1;
      byte var6 = -1;
      byte var7 = -1;
      boolean var8 = false;
      int var9 = 0;
      boolean var10 = false;
      int var11 = -1;
      char var12 = 0;
      if (var1 != null) {
         int var13 = var1.length();

         while(true) {
            while(true) {
               char var14;
               do {
                  do {
                     if (var9 >= var13) {
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
                           this.tzoffset = -var11 * 60 * 1000;
                           this.minute = var6;
                           this.second = var7;
                           this.milli = 0;
                           return;
                        }

                        throw new IllegalArgumentException();
                     }

                     var14 = var1.charAt(var9);
                     ++var9;
                  } while(var14 <= ' ');
               } while(var14 == ',');

               int var16;
               if (var14 == '(') {
                  var16 = 1;

                  while(var9 < var13) {
                     var14 = var1.charAt(var9);
                     ++var9;
                     if (var14 == '(') {
                        ++var16;
                     } else if (var14 == ')') {
                        --var16;
                        if (var16 <= 0) {
                           break;
                        }
                     }
                  }
               } else if ('0' <= var14 && var14 <= '9') {
                  for(var16 = var14 - 48; var9 < var13 && '0' <= (var14 = var1.charAt(var9)) && var14 <= '9'; ++var9) {
                     var16 = var16 * 10 + var14 - 48;
                  }

                  if (var12 == '+' || var12 == '-' && var2 >= 0) {
                     if (var16 < 24) {
                        var16 *= 60;
                     } else {
                        var16 = var16 % 100 + var16 / 100 * 60;
                     }

                     if (var12 == '+') {
                        var16 = -var16;
                     }

                     if (var11 != 0 && var11 != -1) {
                        throw new IllegalArgumentException();
                     }

                     var11 = var16;
                  } else if (var16 >= 70) {
                     if (var2 >= 0 || var14 > ' ' && var14 != ',' && var14 != '/' && var9 < var13) {
                        throw new IllegalArgumentException();
                     }

                     var2 = var16 < 100 ? var16 + 1900 : var16;
                  } else if (var14 == ':') {
                     if (var5 < 0) {
                        var5 = (byte)var16;
                     } else {
                        if (var6 >= 0) {
                           throw new IllegalArgumentException();
                        }

                        var6 = (byte)var16;
                     }
                  } else if (var14 == '/') {
                     if (var3 < 0) {
                        var3 = (byte)(var16 - 1);
                     } else {
                        if (var4 >= 0) {
                           throw new IllegalArgumentException();
                        }

                        var4 = (byte)var16;
                     }
                  } else {
                     if (var9 < var13 && var14 != ',' && var14 > ' ' && var14 != '-') {
                        throw new IllegalArgumentException();
                     }

                     if (var5 >= 0 && var6 < 0) {
                        var6 = (byte)var16;
                     } else if (var6 >= 0 && var7 < 0) {
                        var7 = (byte)var16;
                     } else {
                        if (var4 >= 0) {
                           throw new IllegalArgumentException();
                        }

                        var4 = (byte)var16;
                     }
                  }

                  var12 = 0;
               } else if (var14 != '/' && var14 != ':' && var14 != '+' && var14 != '-') {
                  for(var16 = var9 - 1; var9 < var13; ++var9) {
                     var14 = var1.charAt(var9);
                     if (('A' > var14 || var14 > 'Z') && ('a' > var14 || var14 > 'z')) {
                        break;
                     }
                  }

                  if (var9 <= var16 + 1) {
                     throw new IllegalArgumentException();
                  }

                  int var15 = fR.length;

                  while(true) {
                     --var15;
                     if (var15 < 0) {
                        break;
                     }

                     if (fR[var15].regionMatches(true, 0, var1, var16, var9 - var16)) {
                        if ((var16 = fS[var15]) != 0) {
                           if (var16 == 1) {
                              if (var5 > 12 || var5 < 1) {
                                 throw new IllegalArgumentException();
                              }

                              if (var5 < 12) {
                                 var5 += 12;
                              }
                           } else if (var16 == 14) {
                              if (var5 > 12 || var5 < 1) {
                                 throw new IllegalArgumentException();
                              }

                              if (var5 == 12) {
                                 var5 = 0;
                              }
                           } else if (var16 <= 13) {
                              if (var3 >= 0) {
                                 throw new IllegalArgumentException();
                              }

                              var3 = (byte)(var16 - 2);
                           } else {
                              var11 = var16 - 10000;
                           }
                        }
                        break;
                     }
                  }

                  if (var15 < 0) {
                     throw new IllegalArgumentException();
                  }

                  var12 = 0;
               } else {
                  var12 = var14;
               }
            }
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   static void setTimeZone(String var0) {
      if (fO.get(var0) != null) {
         local_tz = (Integer)fO.get(var0);
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
      int var10000 = this.year;
      int var1 = this.day;
      int var7 = this.month;
      int var6 = var10000;
      boolean var3 = var10000 % 4 == 0;
      int var2 = var6 - 1;
      long var11 = 365L * (long)var2 + floorDivide((long)var2, 4L) + 1721423L;
      var3 = var3 && (var6 % 100 != 0 || var6 % 400 == 0);
      long var14 = (var11 + floorDivide((long)var2, 400L) - floorDivide((long)var2, 100L) + 2L + (var3 ? (long)fU[var7] : (long)fT[var7]) + (long)var1 - fP) * (long)fQ;
      boolean var13 = false;
      var1 = (((0 + this.hour) * 60 + this.minute) * 60 + this.second) * 1000 + this.milli;
      return var14 + (long)var1 - (long)this.tzoffset;
   }

   private static final long floorDivide(long var0, long var2) {
      return var0 >= 0L ? var0 / var2 : (var0 + 1L) / var2 - 1L;
   }

   static {
      fQ = 24 * millisPerHour;
      fR = new String[]{"am", "pm", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday", "january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december", "gmt", "ut", "utc", "est", "edt", "cst", "cdt", "mst", "mdt", "pst", "pdt"};
      fS = new int[]{14, 1, 0, 0, 0, 0, 0, 0, 0, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 10000, 10000, 10000, 10300, 10240, 10360, 10300, 10420, 10360, 10480, 10420};
      fT = new int[]{0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
      fU = new int[]{0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};
      (fO = new Hashtable()).put("GMT", new Integer(0 * millisPerHour));
      fO.put("UT", new Integer(0 * millisPerHour));
      fO.put("UTC", new Integer(0 * millisPerHour));
      fO.put("PST", new Integer(-8 * millisPerHour));
      fO.put("PDT", new Integer(-7 * millisPerHour));
      fO.put("JST", new Integer(9 * millisPerHour));
      local_tz = (Integer)fO.get("PST");
   }
}
