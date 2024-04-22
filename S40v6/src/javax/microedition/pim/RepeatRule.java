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
   private static final int DAYS_OF_WEEK = 130048;
   int frequency = -1;
   int interval = -1;
   long end = -1L;
   int dayInWeek = -1;
   int monthInYear = -1;
   int dayInMonth = -1;
   private static final boolean DEBUG = false;

   public void addExceptDate(long date) {
   }

   public Enumeration dates(long startDate, long subsetBeginning, long subsetEnding) {
      if (subsetBeginning > subsetEnding) {
         throw new IllegalArgumentException("subsetBeginning greater than subsetEnding");
      } else {
         return new RepeatRule.DateEnumeration(startDate, subsetBeginning, subsetEnding);
      }
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof RepeatRule)) {
         return false;
      } else {
         RepeatRule rr = (RepeatRule)obj;
         int rrFrequency = -1;

         try {
            rrFrequency = rr.getInt(0);
         } catch (FieldEmptyException var14) {
         }

         if (this.frequency != rrFrequency) {
            return false;
         } else {
            int checkValue = -1;

            try {
               checkValue = rr.getInt(2);
            } catch (FieldEmptyException var13) {
            }

            if (checkValue != this.dayInWeek) {
               return false;
            } else {
               checkValue = -1;

               try {
                  checkValue = rr.getInt(1);
               } catch (FieldEmptyException var12) {
               }

               if (checkValue != this.dayInMonth) {
                  return false;
               } else {
                  checkValue = -1;

                  try {
                     checkValue = rr.getInt(8);
                  } catch (FieldEmptyException var11) {
                  }

                  if (checkValue != this.monthInYear) {
                     return false;
                  } else {
                     int rrInterval = -1;

                     try {
                        rrInterval = rr.getInt(128);
                     } catch (FieldEmptyException var10) {
                     }

                     if (rrInterval != this.interval) {
                        return false;
                     } else {
                        long rrEnd = -1L;

                        try {
                           rrEnd = rr.getDate(64);
                        } catch (FieldEmptyException var9) {
                        }

                        if (rrEnd == this.end) {
                           return true;
                        } else {
                           return rrEnd != -1L && this.end != -1L ? this.isSameDay(rrEnd, this.end) : false;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean isSameDay(long longDate1, long longDate2) {
      if (Math.abs(longDate1 - longDate2) > 86400000L) {
         return false;
      } else {
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(new Date(longDate1));
         int[] date1 = new int[]{calendar.get(5), calendar.get(2), calendar.get(1)};
         calendar.setTime(new Date(longDate2));
         int[] date2 = new int[]{calendar.get(5), calendar.get(2), calendar.get(1)};
         return date1[0] == date2[0] && date1[1] == date2[1] && date1[2] == date2[2];
      }
   }

   public long getDate(int field) {
      if (field != 64) {
         throw new IllegalArgumentException("Invalid field");
      } else if (this.end == -1L) {
         throw new FieldEmptyException("No value set for this field", field);
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
      int[] fields = new int[(this.frequency == -1 ? 0 : 1) + (this.interval == -1 ? 0 : 1) + (this.dayInWeek == -1 ? 0 : 1) + (this.monthInYear == -1 ? 0 : 1) + (this.dayInMonth == -1 ? 0 : 1) + (this.end == -1L ? 0 : 1)];
      int index = 0;
      if (this.frequency != -1) {
         fields[index] = 0;
         ++index;
      }

      if (this.interval != -1) {
         fields[index] = 128;
         ++index;
      }

      if (this.dayInWeek != -1) {
         fields[index] = 2;
         ++index;
      }

      if (this.monthInYear != -1) {
         fields[index] = 8;
         ++index;
      }

      if (this.dayInMonth != -1) {
         fields[index] = 1;
         ++index;
      }

      if (this.end != -1L) {
         fields[index] = 64;
         ++index;
      }

      return fields;
   }

   public int getInt(int field) {
      switch(field) {
      case 0:
         if (this.frequency == -1) {
            throw new FieldEmptyException("No value set for this field", field);
         }

         return this.frequency;
      case 1:
         if (this.dayInMonth == -1) {
            throw new FieldEmptyException("No value set for this field", field);
         }

         return this.dayInMonth;
      case 2:
         if (this.dayInWeek == -1) {
            throw new FieldEmptyException("No value set for this field", field);
         }

         return this.dayInWeek;
      case 8:
         if (this.monthInYear == -1) {
            throw new FieldEmptyException("No value set for this field", field);
         }

         return this.monthInYear;
      case 128:
         if (this.interval == -1) {
            throw new FieldEmptyException("No value set for this field", field);
         }

         return this.interval;
      default:
         throw new IllegalArgumentException("Field not supported");
      }
   }

   public void removeExceptDate(long date) {
   }

   public void setDate(int field, long value) {
      if (field != 64) {
         throw new IllegalArgumentException("Invalid field");
      } else {
         this.end = value;
      }
   }

   public void setInt(int field, int value) {
      switch(field) {
      case 0:
         switch(value) {
         case 16:
         case 18:
         case 19:
            if (this.interval != -1) {
               throw new IllegalArgumentException("An interval is set but not supported with this frequency");
            }

            this.frequency = value;
            return;
         case 17:
            this.frequency = value;
            return;
         default:
            throw new IllegalArgumentException("Invalid value");
         }
      case 1:
         if (value < 1 || value > 31) {
            throw new IllegalArgumentException("Incorrect number of days for DAY_IN_MONTH");
         }

         this.dayInMonth = value;
         break;
      case 2:
         if (value == 0 || (value | 130048) != 130048) {
            throw new IllegalArgumentException("DAY_IN_WEEK's value must be based on the RepeatRule constants (e.g., RepeatRule.SUNDAY)");
         }

         this.dayInWeek = value;
         break;
      case 8:
         if (value != 131072 && value != 262144 && value != 524288 && value != 1048576 && value != 2097152 && value != 4194304 && value != 8388608 && value != 16777216 && value != 33554432 && value != 67108864 && value != 134217728 && value != 268435456) {
            throw new IllegalArgumentException("MONTH_IN_YEAR must be a constant from RepeatRule (name of month)");
         }

         this.monthInYear = value;
         break;
      case 128:
         switch(this.frequency) {
         case -1:
         case 17:
            if (value != 1 && value != 2) {
               throw new IllegalArgumentException("Invalid value");
            }

            this.interval = value;
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

   private void trace(String out) {
   }

   class DateEnumeration implements Enumeration {
      private Date nextDate = null;
      private long subsetEnding;
      private Calendar calendar;
      private boolean startDateBeforeSetBegin = true;
      private int repeatDate = 0;
      private boolean searchNextEntry = false;

      DateEnumeration(long startDate, long subsetBeginning, long subsetEnding) {
         if ((RepeatRule.this.end == -1L || startDate < RepeatRule.this.end) && RepeatRule.this.frequency != -1) {
            this.calendar = Calendar.getInstance();
            this.subsetEnding = RepeatRule.this.end != -1L && RepeatRule.this.end < subsetEnding ? RepeatRule.this.end : subsetEnding;
            this.nextDate = new Date(startDate);
            if (startDate >= subsetBeginning) {
               this.startDateBeforeSetBegin = false;
               this.nextDate = this.getNextDate(this.nextDate);
               this.startDateBeforeSetBegin = true;
            }

            while(this.nextDate != null && this.nextDate.getTime() < subsetBeginning) {
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
            Date element = new Date(this.nextDate.getTime());
            this.nextDate = this.getNextDate(this.nextDate);
            return element;
         }
      }

      private Date getNextDate(Date currentDate) {
         int interval = RepeatRule.this.interval;
         interval = interval == -1 ? 1 : interval;
         Date var3;
         switch(RepeatRule.this.frequency) {
         case 16:
            var3 = this.getNextDailyOccurence(currentDate, interval);
            break;
         case 17:
            var3 = this.getNextWeeklyOccurence(currentDate, interval);
            break;
         case 18:
            var3 = this.getNextMonthlyOccurence(currentDate, interval);
            break;
         case 19:
            var3 = this.getNextYearlyOccurence(currentDate, interval);
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

      private Date getNextDailyOccurence(Date date, int interval) {
         if (!this.startDateBeforeSetBegin) {
            return date;
         } else {
            this.calendar.setTime(date);
            int[] time = new int[]{this.calendar.get(5), this.calendar.get(2), this.calendar.get(1)};
            time = this.roll(time, interval);
            this.setCalendar(time);
            return this.calendar.getTime();
         }
      }

      private Date getNextWeeklyOccurence(Date date, int interval) {
         boolean found = false;
         int referenceDayx = false;
         int numDays = 0;
         RepeatRule.this.trace("In getNextWeekly");
         RepeatRule.this.trace("date = " + date);
         if (RepeatRule.this.dayInWeek == -1) {
            return !this.startDateBeforeSetBegin ? date : this.getNextDailyOccurence(date, 7 * interval);
         } else {
            RepeatRule.this.trace("dayInWeek = " + RepeatRule.this.dayInWeek);
            this.calendar.setTime(date);
            int[] time = new int[]{this.calendar.get(5), this.calendar.get(2), this.calendar.get(1)};
            int newInterval = 1;
            int referenceDay = this.CalendarDayToRR(this.calendar.get(7));
            if (this.searchNextEntry) {
               newInterval = interval;
               numDays = referenceDay == 1024 && interval == 2 ? 8 : 1;
            }

            while(!found) {
               if (numDays != 0) {
                  time = this.roll(time, numDays);

                  for(numDays %= 7; numDays-- > 0; referenceDay = this.getTomorrowRR(referenceDay)) {
                  }
               }

               if ((RepeatRule.this.dayInWeek & referenceDay) != 0) {
                  found = true;
                  this.setCalendar(time);
                  this.searchNextEntry = true;
                  break;
               }

               numDays = referenceDay == 1024 && newInterval == 2 ? 8 : 1;
            }

            RepeatRule.this.trace("returning: " + this.calendar.getTime());
            return this.calendar.getTime();
         }
      }

      private Date getNextMonthlyOccurence(Date date, int interval) {
         RepeatRule.this.trace("In getNextMonthlyOccurrence");
         RepeatRule.this.trace("date = " + date);
         this.calendar.setTime(date);
         int[] time = new int[]{this.repeatDate != 0 ? this.repeatDate : this.calendar.get(5), this.calendar.get(2), this.calendar.get(1)};
         int numDaysInMonth = false;
         RepeatRule.this.trace("time[0] = " + time[0]);
         RepeatRule.this.trace("time[1] = " + time[1]);
         RepeatRule.this.trace("time[2] = " + time[2]);
         if (this.repeatDate == 0) {
            this.repeatDate = RepeatRule.this.dayInMonth != -1 ? RepeatRule.this.dayInMonth : time[0];
         }

         if (RepeatRule.this.dayInMonth == -1) {
            if (this.startDateBeforeSetBegin) {
               time[2] += (time[1] + interval) / 12;
               time[1] = (time[1] + interval) % 12;
            }
         } else {
            RepeatRule.this.trace("dayInMonth = " + RepeatRule.this.dayInMonth);
            if (RepeatRule.this.dayInMonth < time[0] || RepeatRule.this.dayInMonth == time[0] && this.startDateBeforeSetBegin) {
               time[0] = RepeatRule.this.dayInMonth;
               time[2] += (time[1] + interval) / 12;
               time[1] = (time[1] + interval) % 12;
            } else {
               time[0] = RepeatRule.this.dayInMonth;
            }
         }

         int numDaysInMonthx = this.getDaysInMonth(time[1], time[2]);
         if (numDaysInMonthx < time[0]) {
            time[0] = numDaysInMonthx;
         }

         this.setCalendar(time);
         RepeatRule.this.trace("getNextMonthlyOccurrence returning: " + this.calendar.getTime());
         return this.calendar.getTime();
      }

      private Date getNextYearlyOccurence(Date date, int interval) {
         this.calendar.setTime(date);
         int[] time = new int[]{this.repeatDate != 0 ? this.repeatDate : this.calendar.get(5), this.calendar.get(2), this.calendar.get(1)};
         int numDaysInMonth = false;
         if (this.repeatDate == 0) {
            this.repeatDate = time[0];
         }

         RepeatRule.this.trace("getNextYearlyOccurence: dayInMonth" + RepeatRule.this.dayInMonth);
         RepeatRule.this.trace("getNextYearlyOccurence: monthInYear" + RepeatRule.this.monthInYear);
         if (RepeatRule.this.dayInMonth == -1 && RepeatRule.this.monthInYear != -1) {
            time[0] = this.calendar.get(5);
            time[1] = this.RRMonthToCalendar(RepeatRule.this.monthInYear);
         } else if (RepeatRule.this.dayInMonth != -1 && RepeatRule.this.monthInYear == -1) {
            time[0] = RepeatRule.this.dayInMonth;
            time[1] = this.calendar.get(2);
         } else if (RepeatRule.this.dayInMonth != -1 && RepeatRule.this.monthInYear != -1) {
            time[0] = RepeatRule.this.dayInMonth;
            time[1] = this.RRMonthToCalendar(RepeatRule.this.monthInYear);
         }

         time[2] = this.startDateBeforeSetBegin ? time[2] + interval : time[2];
         int numDaysInMonthx = this.getDaysInMonth(time[1], time[2]);
         if (numDaysInMonthx < time[0]) {
            time[0] = numDaysInMonthx;
         }

         this.setCalendar(time);
         return this.calendar.getTime();
      }

      private int CalendarDayToRR(int day) {
         int today = -1;
         switch(day) {
         case 1:
            today = 65536;
            break;
         case 2:
            today = 32768;
            break;
         case 3:
            today = 16384;
            break;
         case 4:
            today = 8192;
            break;
         case 5:
            today = 4096;
            break;
         case 6:
            today = 2048;
            break;
         case 7:
            today = 1024;
         }

         return today;
      }

      private int RRDayToCalendar(int day) {
         int today = -1;
         switch(day) {
         case 1024:
            today = 7;
            break;
         case 2048:
            today = 6;
            break;
         case 4096:
            today = 5;
            break;
         case 8192:
            today = 4;
            break;
         case 16384:
            today = 3;
            break;
         case 32768:
            today = 2;
            break;
         case 65536:
            today = 1;
         }

         return today;
      }

      private int RRMonthToCalendar(int RRMonth) {
         int calMonth = -1;
         switch(RRMonth) {
         case 131072:
            calMonth = 0;
            break;
         case 262144:
            calMonth = 1;
            break;
         case 524288:
            calMonth = 2;
            break;
         case 1048576:
            calMonth = 3;
            break;
         case 2097152:
            calMonth = 4;
            break;
         case 4194304:
            calMonth = 5;
            break;
         case 8388608:
            calMonth = 6;
            break;
         case 16777216:
            calMonth = 7;
            break;
         case 33554432:
            calMonth = 8;
            break;
         case 67108864:
            calMonth = 9;
            break;
         case 134217728:
            calMonth = 10;
            break;
         case 268435456:
            calMonth = 11;
         }

         return calMonth;
      }

      private int getTomorrowRR(int today) {
         int tomorrow = -1;
         switch(today) {
         case 1024:
            tomorrow = 65536;
            break;
         case 2048:
            tomorrow = 1024;
            break;
         case 4096:
            tomorrow = 2048;
            break;
         case 8192:
            tomorrow = 4096;
            break;
         case 16384:
            tomorrow = 8192;
            break;
         case 32768:
            tomorrow = 16384;
            break;
         case 65536:
            tomorrow = 32768;
         }

         return tomorrow;
      }

      private boolean isLeapYear(int year) {
         boolean isLeapYear = false;
         if (year % 4 == 0) {
            isLeapYear = true;
            if (year % 100 == 0 && year % 400 != 0) {
               isLeapYear = false;
            }
         }

         return isLeapYear;
      }

      private int getDaysInMonth(int month, int year) {
         switch(month) {
         case 0:
         case 2:
         case 4:
         case 6:
         case 7:
         case 9:
         case 11:
            return 31;
         case 1:
            return 28 + (this.isLeapYear(year) ? 1 : 0);
         case 3:
         case 5:
         case 8:
         case 10:
            return 30;
         default:
            return -1;
         }
      }

      private int[] roll(int[] time, int offset) {
         time[0] += offset;

         int numOfDays;
         while((numOfDays = this.getDaysInMonth(time[1], time[2])) < time[0]) {
            time[0] -= numOfDays;
            int var10002 = time[1]++;
            if (time[1] > 11) {
               time[1] = 0;
               var10002 = time[2]++;
            }
         }

         return time;
      }

      private void setCalendar(int[] time) {
         this.calendar.set(5, 1);
         this.calendar.set(2, time[1]);
         this.calendar.set(1, time[2]);
         this.calendar.set(5, time[0]);
      }
   }
}
