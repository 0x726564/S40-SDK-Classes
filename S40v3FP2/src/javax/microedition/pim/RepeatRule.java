package javax.microedition.pim;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public class RepeatRule {
   public static final int APRIL = 1048576;
   public static final int AUGUST = 16777216;
   public static final int COUNT = 32;
   public static final int DAILY = 16;
   public static final int DAY_IN_MONTH = 1;
   public static final int DAY_IN_WEEK = 2;
   public static final int DAY_IN_YEAR = 4;
   public static final int DECEMBER = 268435456;
   public static final int END = 64;
   public static final int FEBRUARY = 262144;
   public static final int FIFTH = 16;
   public static final int FIFTHLAST = 512;
   public static final int FIRST = 1;
   public static final int FOURTH = 8;
   public static final int FOURTHLAST = 256;
   public static final int FREQUENCY = 0;
   public static final int FRIDAY = 2048;
   public static final int INTERVAL = 128;
   public static final int JANUARY = 131072;
   public static final int JULY = 8388608;
   public static final int JUNE = 4194304;
   public static final int LAST = 32;
   public static final int MARCH = 524288;
   public static final int MAY = 2097152;
   public static final int MONDAY = 32768;
   public static final int MONTH_IN_YEAR = 8;
   public static final int MONTHLY = 18;
   public static final int NOVEMBER = 134217728;
   public static final int OCTOBER = 67108864;
   public static final int SATURDAY = 1024;
   public static final int SECOND = 2;
   public static final int SECONDLAST = 64;
   public static final int SEPTEMBER = 33554432;
   public static final int SUNDAY = 65536;
   public static final int THIRD = 4;
   public static final int THIRDLAST = 128;
   public static final int THURSDAY = 4096;
   public static final int TUESDAY = 16384;
   public static final int WEDNESDAY = 8192;
   public static final int WEEK_IN_MONTH = 16;
   public static final int WEEKLY = 17;
   public static final int YEARLY = 19;
   int frequency = -1;
   int interval = -1;
   long end = -1L;
   int dayInWeek = -1;
   int monthInYear = -1;
   int dayInMonth = -1;
   private static final boolean DEBUG = false;

   public void addExceptDate(long var1) {
   }

   public Enumeration dates(long var1, long var3, long var5) {
      if (var3 > var5) {
         throw new IllegalArgumentException("subsetBeginning greater than subsetEnding");
      } else {
         return new RepeatRule.DateEnumeration(var1, var3, var5);
      }
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof RepeatRule)) {
         return false;
      } else {
         RepeatRule var2 = (RepeatRule)var1;
         int var3 = -1;

         try {
            var3 = var2.getInt(0);
         } catch (FieldEmptyException var14) {
         }

         if (this.frequency != var3) {
            return false;
         } else {
            int var4 = -1;

            try {
               var4 = var2.getInt(2);
            } catch (FieldEmptyException var13) {
            }

            if (var4 != this.dayInWeek) {
               return false;
            } else {
               var4 = -1;

               try {
                  var4 = var2.getInt(1);
               } catch (FieldEmptyException var12) {
               }

               if (var4 != this.dayInMonth) {
                  return false;
               } else {
                  var4 = -1;

                  try {
                     var4 = var2.getInt(8);
                  } catch (FieldEmptyException var11) {
                  }

                  if (var4 != this.monthInYear) {
                     return false;
                  } else {
                     int var5 = -1;

                     try {
                        var5 = var2.getInt(128);
                     } catch (FieldEmptyException var10) {
                     }

                     if (var5 != this.interval) {
                        return false;
                     } else {
                        long var6 = -1L;

                        try {
                           var6 = var2.getDate(64);
                        } catch (FieldEmptyException var9) {
                        }

                        if (var6 == this.end) {
                           return true;
                        } else {
                           return var6 != -1L && this.end != -1L ? this.isSameDay(var6, this.end) : false;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean isSameDay(long var1, long var3) {
      if (Math.abs(var1 - var3) > 86400000L) {
         return false;
      } else {
         Calendar var5 = Calendar.getInstance();
         var5.setTime(new Date(var1));
         int[] var6 = new int[]{var5.get(5), var5.get(2), var5.get(1)};
         var5.setTime(new Date(var3));
         int[] var7 = new int[]{var5.get(5), var5.get(2), var5.get(1)};
         return var6[0] == var7[0] && var6[1] == var7[1] && var6[2] == var7[2];
      }
   }

   public long getDate(int var1) {
      if (var1 != 64) {
         throw new IllegalArgumentException("Invalid field");
      } else if (this.end == -1L) {
         throw new FieldEmptyException("No value set for this field", var1);
      } else {
         return this.end;
      }
   }

   public Enumeration getExceptDates() {
      return new Enumeration() {
         public boolean hasMoreElements() {
            return false;
         }

         public Object nextElement() {
            throw new NoSuchElementException();
         }
      };
   }

   public int[] getFields() {
      int[] var1 = new int[(this.frequency == -1 ? 0 : 1) + (this.interval == -1 ? 0 : 1) + (this.dayInWeek == -1 ? 0 : 1) + (this.monthInYear == -1 ? 0 : 1) + (this.dayInMonth == -1 ? 0 : 1) + (this.end == -1L ? 0 : 1)];
      int var2 = 0;
      if (this.frequency != -1) {
         var1[var2] = 0;
         ++var2;
      }

      if (this.interval != -1) {
         var1[var2] = 128;
         ++var2;
      }

      if (this.dayInWeek != -1) {
         var1[var2] = 2;
         ++var2;
      }

      if (this.monthInYear != -1) {
         var1[var2] = 8;
         ++var2;
      }

      if (this.dayInMonth != -1) {
         var1[var2] = 1;
         ++var2;
      }

      if (this.end != -1L) {
         var1[var2] = 64;
         ++var2;
      }

      return var1;
   }

   public int getInt(int var1) {
      switch(var1) {
      case 0:
         if (this.frequency == -1) {
            throw new FieldEmptyException("No value set for this field", var1);
         }

         return this.frequency;
      case 1:
         if (this.dayInMonth == -1) {
            throw new FieldEmptyException("No value set for this field", var1);
         }

         return this.dayInMonth;
      case 2:
         if (this.dayInWeek == -1) {
            throw new FieldEmptyException("No value set for this field", var1);
         }

         return this.dayInWeek;
      case 8:
         if (this.monthInYear == -1) {
            throw new FieldEmptyException("No value set for this field", var1);
         }

         return this.monthInYear;
      case 128:
         if (this.interval == -1) {
            throw new FieldEmptyException("No value set for this field", var1);
         }

         return this.interval;
      default:
         throw new IllegalArgumentException("Field not supported");
      }
   }

   public void removeExceptDate(long var1) {
   }

   public void setDate(int var1, long var2) {
      if (var1 != 64) {
         throw new IllegalArgumentException("Invalid field");
      } else {
         this.end = var2;
      }
   }

   public void setInt(int var1, int var2) {
      switch(var1) {
      case 0:
         switch(var2) {
         case 16:
         case 18:
         case 19:
            if (this.interval != -1) {
               throw new IllegalArgumentException("An interval is set but not supported with this frequency");
            }

            this.frequency = var2;
            return;
         case 17:
            this.frequency = var2;
            return;
         default:
            throw new IllegalArgumentException("Invalid value");
         }
      case 1:
         if (var2 < 1 || var2 > 31) {
            throw new IllegalArgumentException("Incorrect number of days for DAY_IN_MONTH");
         }

         this.dayInMonth = var2;
         break;
      case 2:
         if ((var2 & 65536) != 65536 && (var2 & '耀') != 32768 && (var2 & 16384) != 16384 && (var2 & 8192) != 8192 && (var2 & 4096) != 4096 && (var2 & 2048) != 2048 && (var2 & 1024) != 1024) {
            throw new IllegalArgumentException("DAY_IN_WEEK's value must be based on the RepeatRule constants (e.g., RepeatRule.SUNDAY)");
         }

         this.dayInWeek = var2;
         break;
      case 8:
         if (var2 != 131072 && var2 != 262144 && var2 != 524288 && var2 != 1048576 && var2 != 2097152 && var2 != 4194304 && var2 != 8388608 && var2 != 16777216 && var2 != 33554432 && var2 != 67108864 && var2 != 134217728 && var2 != 268435456) {
            throw new IllegalArgumentException("MONTH_IN_YEAR must be a constant from RepeatRule (name of month)");
         }

         this.monthInYear = var2;
         break;
      case 128:
         switch(this.frequency) {
         case -1:
         case 17:
            if (var2 != 1 && var2 != 2) {
               throw new IllegalArgumentException("Invalid value");
            }

            this.interval = var2;
            return;
         case 16:
         case 18:
         case 19:
            throw new IllegalArgumentException("INTERVAL is only supported for WEEKLY repeat rules.");
         default:
            return;
         }
      default:
         throw new IllegalArgumentException("Field not supported");
      }

   }

   private void trace(String var1) {
   }

   class DateEnumeration implements Enumeration {
      private Date nextDate = null;
      private long subsetEnding;
      private Calendar calendar;
      private boolean startDateBeforeSetBegin = true;
      private int repeatDate = 0;
      private boolean searchNextEntry = false;

      DateEnumeration(long var2, long var4, long var6) {
         if ((RepeatRule.this.end == -1L || var2 < RepeatRule.this.end) && RepeatRule.this.frequency != -1) {
            this.calendar = Calendar.getInstance();
            this.subsetEnding = RepeatRule.this.end != -1L && RepeatRule.this.end < var6 ? RepeatRule.this.end : var6;
            this.nextDate = new Date(var2);
            if (var2 >= var4) {
               this.startDateBeforeSetBegin = false;
               this.nextDate = this.getNextDate(this.nextDate);
               this.startDateBeforeSetBegin = true;
            }

            while(this.nextDate != null && this.nextDate.getTime() < var4) {
               this.nextDate = this.getNextDate(this.nextDate);
            }

         }
      }

      public boolean hasMoreElements() {
         return this.nextDate != null;
      }

      public Object nextElement() {
         if (this.nextDate == null) {
            throw new NoSuchElementException();
         } else {
            Date var1 = new Date(this.nextDate.getTime());
            this.nextDate = this.getNextDate(this.nextDate);
            return var1;
         }
      }

      private Date getNextDate(Date var1) {
         int var2 = RepeatRule.this.interval;
         var2 = var2 == -1 ? 1 : var2;
         Date var3;
         switch(RepeatRule.this.frequency) {
         case 16:
            var3 = this.getNextDailyOccurence(var1, var2);
            break;
         case 17:
            var3 = this.getNextWeeklyOccurence(var1, var2);
            break;
         case 18:
            var3 = this.getNextMonthlyOccurence(var1, var2);
            break;
         case 19:
            var3 = this.getNextYearlyOccurence(var1, var2);
            break;
         default:
            throw new RuntimeException("An error occured");
         }

         if (var3.getTime() > this.subsetEnding) {
            if (this.repeatDate != 0) {
               this.repeatDate = 0;
            }

            if (this.searchNextEntry) {
               this.searchNextEntry = false;
            }

            return null;
         } else {
            return var3;
         }
      }

      private Date getNextDailyOccurence(Date var1, int var2) {
         if (!this.startDateBeforeSetBegin) {
            return var1;
         } else {
            this.calendar.setTime(var1);
            int[] var3 = new int[]{this.calendar.get(5), this.calendar.get(2), this.calendar.get(1)};
            var3 = this.roll(var3, var2);
            this.setCalendar(var3);
            return this.calendar.getTime();
         }
      }

      private Date getNextWeeklyOccurence(Date var1, int var2) {
         boolean var3 = false;
         boolean var4 = false;
         int var5 = 0;
         RepeatRule.this.trace("In getNextWeekly");
         RepeatRule.this.trace("date = " + var1);
         if (RepeatRule.this.dayInWeek == -1) {
            return !this.startDateBeforeSetBegin ? var1 : this.getNextDailyOccurence(var1, 7 * var2);
         } else {
            RepeatRule.this.trace("dayInWeek = " + RepeatRule.this.dayInWeek);
            this.calendar.setTime(var1);
            int[] var6 = new int[]{this.calendar.get(5), this.calendar.get(2), this.calendar.get(1)};
            int var7 = 1;
            int var8 = this.CalendarDayToRR(this.calendar.get(7));
            if (this.searchNextEntry) {
               var7 = var2;
               var5 = var8 == 1024 && var2 == 2 ? 8 : 1;
            }

            while(!var3) {
               if (var5 != 0) {
                  var6 = this.roll(var6, var5);

                  for(var5 %= 7; var5-- > 0; var8 = this.getTomorrowRR(var8)) {
                  }
               }

               if ((RepeatRule.this.dayInWeek & var8) != 0) {
                  var3 = true;
                  this.setCalendar(var6);
                  this.searchNextEntry = true;
                  break;
               }

               var5 = var8 == 1024 && var7 == 2 ? 8 : 1;
            }

            RepeatRule.this.trace("returning: " + this.calendar.getTime());
            return this.calendar.getTime();
         }
      }

      private Date getNextMonthlyOccurence(Date var1, int var2) {
         RepeatRule.this.trace("In getNextMonthlyOccurrence");
         RepeatRule.this.trace("date = " + var1);
         this.calendar.setTime(var1);
         int[] var3 = new int[]{this.repeatDate != 0 ? this.repeatDate : this.calendar.get(5), this.calendar.get(2), this.calendar.get(1)};
         boolean var4 = false;
         RepeatRule.this.trace("time[0] = " + var3[0]);
         RepeatRule.this.trace("time[1] = " + var3[1]);
         RepeatRule.this.trace("time[2] = " + var3[2]);
         if (this.repeatDate == 0) {
            this.repeatDate = RepeatRule.this.dayInMonth != -1 ? RepeatRule.this.dayInMonth : var3[0];
         }

         if (RepeatRule.this.dayInMonth == -1) {
            if (this.startDateBeforeSetBegin) {
               var3[2] += (var3[1] + var2) / 12;
               var3[1] = (var3[1] + var2) % 12;
            }
         } else {
            RepeatRule.this.trace("dayInMonth = " + RepeatRule.this.dayInMonth);
            if (RepeatRule.this.dayInMonth < var3[0] || RepeatRule.this.dayInMonth == var3[0] && this.startDateBeforeSetBegin) {
               var3[0] = RepeatRule.this.dayInMonth;
               var3[2] += (var3[1] + var2) / 12;
               var3[1] = (var3[1] + var2) % 12;
            } else {
               var3[0] = RepeatRule.this.dayInMonth;
            }
         }

         int var5 = this.getDaysInMonth(var3[1], var3[2]);
         if (var5 < var3[0]) {
            var3[0] = var5;
         }

         this.setCalendar(var3);
         RepeatRule.this.trace("getNextMonthlyOccurrence returning: " + this.calendar.getTime());
         return this.calendar.getTime();
      }

      private Date getNextYearlyOccurence(Date var1, int var2) {
         this.calendar.setTime(var1);
         int[] var3 = new int[]{this.repeatDate != 0 ? this.repeatDate : this.calendar.get(5), this.calendar.get(2), this.calendar.get(1)};
         boolean var4 = false;
         if (this.repeatDate == 0) {
            this.repeatDate = var3[0];
         }

         RepeatRule.this.trace("getNextYearlyOccurence: dayInMonth" + RepeatRule.this.dayInMonth);
         RepeatRule.this.trace("getNextYearlyOccurence: monthInYear" + RepeatRule.this.monthInYear);
         if (RepeatRule.this.dayInMonth == -1 && RepeatRule.this.monthInYear != -1) {
            var3[0] = this.calendar.get(5);
            var3[1] = this.RRMonthToCalendar(RepeatRule.this.monthInYear);
         } else if (RepeatRule.this.dayInMonth != -1 && RepeatRule.this.monthInYear == -1) {
            var3[0] = RepeatRule.this.dayInMonth;
            var3[1] = this.calendar.get(2);
         } else if (RepeatRule.this.dayInMonth != -1 && RepeatRule.this.monthInYear != -1) {
            var3[0] = RepeatRule.this.dayInMonth;
            var3[1] = this.RRMonthToCalendar(RepeatRule.this.monthInYear);
         }

         var3[2] = this.startDateBeforeSetBegin ? var3[2] + var2 : var3[2];
         int var5 = this.getDaysInMonth(var3[1], var3[2]);
         if (var5 < var3[0]) {
            var3[0] = var5;
         }

         this.setCalendar(var3);
         return this.calendar.getTime();
      }

      private int CalendarDayToRR(int var1) {
         int var2 = -1;
         switch(var1) {
         case 1:
            var2 = 65536;
            break;
         case 2:
            var2 = 32768;
            break;
         case 3:
            var2 = 16384;
            break;
         case 4:
            var2 = 8192;
            break;
         case 5:
            var2 = 4096;
            break;
         case 6:
            var2 = 2048;
            break;
         case 7:
            var2 = 1024;
         }

         return var2;
      }

      private int RRDayToCalendar(int var1) {
         byte var2 = -1;
         switch(var1) {
         case 1024:
            var2 = 7;
            break;
         case 2048:
            var2 = 6;
            break;
         case 4096:
            var2 = 5;
            break;
         case 8192:
            var2 = 4;
            break;
         case 16384:
            var2 = 3;
            break;
         case 32768:
            var2 = 2;
            break;
         case 65536:
            var2 = 1;
         }

         return var2;
      }

      private int RRMonthToCalendar(int var1) {
         byte var2 = -1;
         switch(var1) {
         case 131072:
            var2 = 0;
            break;
         case 262144:
            var2 = 1;
            break;
         case 524288:
            var2 = 2;
            break;
         case 1048576:
            var2 = 3;
            break;
         case 2097152:
            var2 = 4;
            break;
         case 4194304:
            var2 = 5;
            break;
         case 8388608:
            var2 = 6;
            break;
         case 16777216:
            var2 = 7;
            break;
         case 33554432:
            var2 = 8;
            break;
         case 67108864:
            var2 = 9;
            break;
         case 134217728:
            var2 = 10;
            break;
         case 268435456:
            var2 = 11;
         }

         return var2;
      }

      private int getTomorrowRR(int var1) {
         int var2 = -1;
         switch(var1) {
         case 1024:
            var2 = 65536;
            break;
         case 2048:
            var2 = 1024;
            break;
         case 4096:
            var2 = 2048;
            break;
         case 8192:
            var2 = 4096;
            break;
         case 16384:
            var2 = 8192;
            break;
         case 32768:
            var2 = 16384;
            break;
         case 65536:
            var2 = 32768;
         }

         return var2;
      }

      private boolean isLeapYear(int var1) {
         boolean var2 = false;
         if (var1 % 4 == 0) {
            var2 = true;
            if (var1 % 100 == 0 && var1 % 400 != 0) {
               var2 = false;
            }
         }

         return var2;
      }

      private int getDaysInMonth(int var1, int var2) {
         switch(var1) {
         case 0:
         case 2:
         case 4:
         case 6:
         case 7:
         case 9:
         case 11:
            return 31;
         case 1:
            return 28 + (this.isLeapYear(var2) ? 1 : 0);
         case 3:
         case 5:
         case 8:
         case 10:
            return 30;
         default:
            return -1;
         }
      }

      private int[] roll(int[] var1, int var2) {
         var1[0] += var2;

         int var3;
         while((var3 = this.getDaysInMonth(var1[1], var1[2])) < var1[0]) {
            var1[0] -= var3;
            int var10002 = var1[1]++;
            if (var1[1] > 11) {
               var1[1] = 0;
               var10002 = var1[2]++;
            }
         }

         return var1;
      }

      private void setCalendar(int[] var1) {
         this.calendar.set(5, 1);
         this.calendar.set(2, var1[1]);
         this.calendar.set(1, var1[2]);
         this.calendar.set(5, var1[0]);
      }
   }
}
