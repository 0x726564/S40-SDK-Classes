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

   public void addExceptDate(long var1) {
   }

   public Enumeration dates(long var1, long var3, long var5) {
      if (var3 > var5) {
         throw new IllegalArgumentException("susbsetBeginning greater than subsetEnding");
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
         } catch (FieldEmptyException var10) {
         }

         if (this.frequency != var3) {
            return false;
         } else {
            int var4 = -1;

            try {
               var4 = var2.getInt(128);
            } catch (FieldEmptyException var9) {
            }

            if (var4 != this.interval) {
               return false;
            } else {
               long var5 = -1L;

               try {
                  var5 = var2.getDate(64);
               } catch (FieldEmptyException var8) {
               }

               if (var5 == this.end) {
                  return true;
               } else {
                  return var5 != -1L && this.end != -1L ? this.isSameDay(var5, this.end) : false;
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
      int[] var1 = new int[(this.frequency == -1 ? 0 : 1) + (this.interval == -1 ? 0 : 1) + (this.end == -1L ? 0 : 1)];
      int var2 = 0;
      if (this.frequency != -1) {
         var1[var2] = 0;
         ++var2;
      }

      if (this.interval != -1) {
         var1[var2] = 128;
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

   class DateEnumeration implements Enumeration {
      private Date nextDate = null;
      private long subsetEnding;
      private Calendar calendar;
      private int repeatDate = 0;

      DateEnumeration(long var2, long var4, long var6) {
         if ((RepeatRule.this.end == -1L || var2 < RepeatRule.this.end) && RepeatRule.this.frequency != -1) {
            this.calendar = Calendar.getInstance();
            this.subsetEnding = RepeatRule.this.end != -1L && RepeatRule.this.end < var6 ? RepeatRule.this.end : var6;

            for(this.nextDate = new Date(var2); this.nextDate != null && this.nextDate.getTime() < var4 && var2 != var4; this.nextDate = this.getNextDate(this.nextDate)) {
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

         if (var3.getTime() <= this.subsetEnding) {
            return var3;
         } else {
            if (RepeatRule.this.frequency == 18 || RepeatRule.this.frequency == 19) {
               this.repeatDate = 0;
            }

            return null;
         }
      }

      private Date getNextDailyOccurence(Date var1, int var2) {
         this.calendar.setTime(var1);
         int[] var3 = new int[]{this.calendar.get(5), this.calendar.get(2), this.calendar.get(1)};
         var3 = this.roll(var3, var2);
         this.setCalendar(var3);
         return this.calendar.getTime();
      }

      private Date getNextWeeklyOccurence(Date var1, int var2) {
         return this.getNextDailyOccurence(var1, 7 * var2);
      }

      private Date getNextMonthlyOccurence(Date var1, int var2) {
         this.calendar.setTime(var1);
         int[] var3 = new int[]{this.repeatDate != 0 ? this.repeatDate : this.calendar.get(5), this.calendar.get(2), this.calendar.get(1)};
         boolean var4 = false;
         if (this.repeatDate == 0) {
            this.repeatDate = var3[0];
         }

         var3[2] += (var3[1] + var2) / 12;
         var3[1] = (var3[1] + var2) % 12;
         int var5 = this.getDaysInMonth(var3[1], var3[2]);
         if (var5 < var3[0]) {
            var3[0] = var5;
         }

         this.setCalendar(var3);
         return this.calendar.getTime();
      }

      private Date getNextYearlyOccurence(Date var1, int var2) {
         this.calendar.setTime(var1);
         int[] var3 = new int[]{this.repeatDate != 0 ? this.repeatDate : this.calendar.get(5), this.calendar.get(2), this.calendar.get(1)};
         boolean var4 = false;
         if (this.repeatDate == 0) {
            this.repeatDate = var3[0];
         }

         var3[2] += var2;
         int var5 = this.getDaysInMonth(var3[1], var3[2]);
         if (var5 < var3[0]) {
            var3[0] = var5;
         }

         this.setCalendar(var3);
         return this.calendar.getTime();
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
