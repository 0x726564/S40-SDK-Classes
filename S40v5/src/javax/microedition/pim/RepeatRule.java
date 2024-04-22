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
   int k = -1;
   int l = -1;
   long m = -1L;
   int n = -1;
   int o = -1;
   int p = -1;

   public void addExceptDate(long var1) {
   }

   public Enumeration dates(long var1, long var3, long var5) {
      if (var3 > var5) {
         throw new IllegalArgumentException("subsetBeginning greater than subsetEnding");
      } else {
         return new RepeatRule.DateEnumeration(this, var1, var3, var5);
      }
   }

   public final boolean equals(Object var1) {
      if (!(var1 instanceof RepeatRule)) {
         return false;
      } else {
         RepeatRule var20 = (RepeatRule)var1;
         int var2 = -1;

         try {
            var2 = var20.getInt(0);
         } catch (FieldEmptyException var17) {
         }

         if (this.k != var2) {
            return false;
         } else {
            var2 = -1;

            try {
               var2 = var20.getInt(2);
            } catch (FieldEmptyException var16) {
            }

            if (var2 != this.n) {
               return false;
            } else {
               var2 = -1;

               try {
                  var2 = var20.getInt(1);
               } catch (FieldEmptyException var15) {
               }

               if (var2 != this.p) {
                  return false;
               } else {
                  var2 = -1;

                  try {
                     var2 = var20.getInt(8);
                  } catch (FieldEmptyException var14) {
                  }

                  if (var2 != this.o) {
                     return false;
                  } else {
                     var2 = -1;

                     try {
                        var2 = var20.getInt(128);
                     } catch (FieldEmptyException var13) {
                     }

                     if (var2 != this.l) {
                        return false;
                     } else {
                        long var6 = -1L;

                        try {
                           var6 = var20.getDate(64);
                        } catch (FieldEmptyException var12) {
                        }

                        if (var6 == this.m) {
                           return true;
                        } else if (var6 != -1L && this.m != -1L) {
                           long var10 = this.m;
                           if (Math.abs(var6 - var10) <= 86400000L) {
                              Calendar var18;
                              (var18 = Calendar.getInstance()).setTime(new Date(var6));
                              int[] var21 = new int[]{var18.get(5), var18.get(2), var18.get(1)};
                              var18.setTime(new Date(var10));
                              int[] var19 = new int[]{var18.get(5), var18.get(2), var18.get(1)};
                              if (var21[0] == var19[0] && var21[1] == var19[1] && var21[2] == var19[2]) {
                                 return true;
                              }
                           }

                           return false;
                        } else {
                           return false;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public long getDate(int var1) {
      if (var1 != 64) {
         throw new IllegalArgumentException("Invalid field");
      } else if (this.m == -1L) {
         throw new FieldEmptyException("No value set for this field", var1);
      } else {
         return this.m;
      }
   }

   public Enumeration getExceptDates() {
      return new Enumeration(this) {
         public boolean hasMoreElements() {
            return false;
         }

         public Object nextElement() {
            throw new NoSuchElementException();
         }
      };
   }

   public int[] getFields() {
      int[] var1 = new int[(this.k == -1 ? 0 : 1) + (this.l == -1 ? 0 : 1) + (this.n == -1 ? 0 : 1) + (this.o == -1 ? 0 : 1) + (this.p == -1 ? 0 : 1) + (this.m == -1L ? 0 : 1)];
      int var2 = 0;
      if (this.k != -1) {
         var1[0] = 0;
         ++var2;
      }

      if (this.l != -1) {
         var1[var2] = 128;
         ++var2;
      }

      if (this.n != -1) {
         var1[var2] = 2;
         ++var2;
      }

      if (this.o != -1) {
         var1[var2] = 8;
         ++var2;
      }

      if (this.p != -1) {
         var1[var2] = 1;
         ++var2;
      }

      if (this.m != -1L) {
         var1[var2] = 64;
      }

      return var1;
   }

   public int getInt(int var1) {
      switch(var1) {
      case 0:
         if (this.k == -1) {
            throw new FieldEmptyException("No value set for this field", var1);
         }

         return this.k;
      case 1:
         if (this.p == -1) {
            throw new FieldEmptyException("No value set for this field", var1);
         }

         return this.p;
      case 2:
         if (this.n == -1) {
            throw new FieldEmptyException("No value set for this field", var1);
         }

         return this.n;
      case 8:
         if (this.o == -1) {
            throw new FieldEmptyException("No value set for this field", var1);
         }

         return this.o;
      case 128:
         if (this.l == -1) {
            throw new FieldEmptyException("No value set for this field", var1);
         }

         return this.l;
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
         this.m = var2;
      }
   }

   public void setInt(int var1, int var2) {
      switch(var1) {
      case 0:
         switch(var2) {
         case 16:
         case 18:
         case 19:
            if (this.l != -1) {
               throw new IllegalArgumentException("An interval is set but not supported with this frequency");
            }

            this.k = var2;
            return;
         case 17:
            this.k = var2;
            return;
         default:
            throw new IllegalArgumentException("Invalid value");
         }
      case 1:
         if (var2 >= 1 && var2 <= 31) {
            this.p = var2;
            return;
         }

         throw new IllegalArgumentException("Incorrect number of days for DAY_IN_MONTH");
      case 2:
         if ((var2 & 65536) != 65536 && (var2 & '耀') != 32768 && (var2 & 16384) != 16384 && (var2 & 8192) != 8192 && (var2 & 4096) != 4096 && (var2 & 2048) != 2048 && (var2 & 1024) != 1024) {
            throw new IllegalArgumentException("DAY_IN_WEEK's value must be based on the RepeatRule constants (e.g., RepeatRule.SUNDAY)");
         }

         this.n = var2;
         return;
      case 8:
         if (var2 != 131072 && var2 != 262144 && var2 != 524288 && var2 != 1048576 && var2 != 2097152 && var2 != 4194304 && var2 != 8388608 && var2 != 16777216 && var2 != 33554432 && var2 != 67108864 && var2 != 134217728 && var2 != 268435456) {
            throw new IllegalArgumentException("MONTH_IN_YEAR must be a constant from RepeatRule (name of month)");
         }

         this.o = var2;
         return;
      case 128:
         switch(this.k) {
         case -1:
         case 17:
            if (var2 != 1 && var2 != 2) {
               throw new IllegalArgumentException("Invalid value");
            }

            this.l = var2;
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
      private Date a;
      private long b;
      private Calendar calendar;
      private boolean c;
      private int d;
      private boolean e;
      private final RepeatRule f;

      DateEnumeration(RepeatRule var1, long var2, long var4, long var6) {
         this.f = var1;
         this.a = null;
         this.c = true;
         this.d = 0;
         this.e = false;
         if ((var1.m == -1L || var2 < var1.m) && var1.k != -1) {
            this.calendar = Calendar.getInstance();
            this.b = var1.m != -1L && var1.m < var6 ? var1.m : var6;
            this.a = new Date(var2);
            if (var2 >= var4) {
               this.c = false;
               this.a = this.a(this.a);
               this.c = true;
            }

            while(this.a != null && this.a.getTime() < var4) {
               this.a = this.a(this.a);
            }

         }
      }

      public boolean hasMoreElements() {
         return this.a != null;
      }

      public Object nextElement() {
         if (this.a == null) {
            throw new NoSuchElementException();
         } else {
            Date var1 = new Date(this.a.getTime());
            this.a = this.a(this.a);
            return var1;
         }
      }

      private Date a(Date var1) {
         int var2 = (var2 = this.f.l) == -1 ? 1 : var2;
         Date var3;
         int[] var4;
         boolean var6;
         int var11;
         switch(this.f.k) {
         case 16:
            var3 = this.a(var1, var2);
            break;
         case 17:
            RepeatRule.DateEnumeration var10 = this;
            var6 = false;
            int var7 = 0;
            (new StringBuffer()).append("date = ").append((Object)var1);
            Date var12;
            if (this.f.n == -1) {
               var12 = !this.c ? var1 : this.a(var1, 7 * var2);
            } else {
               (new StringBuffer()).append("dayInWeek = ").append(this.f.n);
               this.calendar.setTime(var1);
               var4 = new int[]{this.calendar.get(5), this.calendar.get(2), this.calendar.get(1)};
               int var8 = 1;
               var11 = this.calendar.get(7);
               int var9 = -1;
               switch(var11) {
               case 1:
                  var9 = 65536;
                  break;
               case 2:
                  var9 = 32768;
                  break;
               case 3:
                  var9 = 16384;
                  break;
               case 4:
                  var9 = 8192;
                  break;
               case 5:
                  var9 = 4096;
                  break;
               case 6:
                  var9 = 2048;
                  break;
               case 7:
                  var9 = 1024;
               }

               var11 = var9;
               if (this.e) {
                  var8 = var2;
                  var7 = var9 == 1024 && var2 == 2 ? 8 : 1;
               }

               while(true) {
                  if (var7 != 0) {
                     var4 = var10.a(var4, var7);

                     for(var7 %= 7; var7-- > 0; var11 = var10.getTomorrowRR(var11)) {
                     }
                  }

                  if ((var10.f.n & var11) != 0) {
                     var10.setCalendar(var4);
                     var10.e = true;
                     (new StringBuffer()).append("returning: ").append((Object)var10.calendar.getTime());
                     var12 = var10.calendar.getTime();
                     break;
                  }

                  var7 = var11 == 1024 && var8 == 2 ? 8 : 1;
               }
            }

            var3 = var12;
            break;
         case 18:
            (new StringBuffer()).append("date = ").append((Object)var1);
            this.calendar.setTime(var1);
            var4 = new int[]{this.d != 0 ? this.d : this.calendar.get(5), this.calendar.get(2), this.calendar.get(1)};
            var6 = false;
            (new StringBuffer()).append("time[0] = ").append(var4[0]);
            (new StringBuffer()).append("time[1] = ").append(var4[1]);
            (new StringBuffer()).append("time[2] = ").append(var4[2]);
            if (this.d == 0) {
               this.d = this.f.p != -1 ? this.f.p : var4[0];
            }

            if (this.f.p == -1) {
               if (this.c) {
                  var4[2] += (var4[1] + var2) / 12;
                  var4[1] = (var4[1] + var2) % 12;
               }
            } else {
               (new StringBuffer()).append("dayInMonth = ").append(this.f.p);
               if (this.f.p >= var4[0] && (this.f.p != var4[0] || !this.c)) {
                  var4[0] = this.f.p;
               } else {
                  var4[0] = this.f.p;
                  var4[2] += (var4[1] + var2) / 12;
                  var4[1] = (var4[1] + var2) % 12;
               }
            }

            if ((var11 = this.a(var4[1], var4[2])) < var4[0]) {
               var4[0] = var11;
            }

            this.setCalendar(var4);
            (new StringBuffer()).append("getNextMonthlyOccurrence returning: ").append((Object)this.calendar.getTime());
            var3 = this.calendar.getTime();
            break;
         case 19:
            this.calendar.setTime(var1);
            var4 = new int[]{this.d != 0 ? this.d : this.calendar.get(5), this.calendar.get(2), this.calendar.get(1)};
            var6 = false;
            if (this.d == 0) {
               this.d = var4[0];
            }

            (new StringBuffer()).append("getNextYearlyOccurence: dayInMonth").append(this.f.p);
            (new StringBuffer()).append("getNextYearlyOccurence: monthInYear").append(this.f.o);
            if (this.f.p == -1 && this.f.o != -1) {
               var4[0] = this.calendar.get(5);
               var4[1] = a(this.f.o);
            } else if (this.f.p != -1 && this.f.o == -1) {
               var4[0] = this.f.p;
               var4[1] = this.calendar.get(2);
            } else if (this.f.p != -1 && this.f.o != -1) {
               var4[0] = this.f.p;
               var4[1] = a(this.f.o);
            }

            var4[2] = this.c ? var4[2] + var2 : var4[2];
            if ((var11 = this.a(var4[1], var4[2])) < var4[0]) {
               var4[0] = var11;
            }

            this.setCalendar(var4);
            var3 = this.calendar.getTime();
            break;
         default:
            throw new RuntimeException("An error occured");
         }

         if (var3.getTime() > this.b) {
            if (this.d != 0) {
               this.d = 0;
            }

            if (this.e) {
               this.e = false;
            }

            return null;
         } else {
            return var3;
         }
      }

      private Date a(Date var1, int var2) {
         if (!this.c) {
            return var1;
         } else {
            this.calendar.setTime(var1);
            int[] var3 = new int[]{this.calendar.get(5), this.calendar.get(2), this.calendar.get(1)};
            var3 = this.a(var3, var2);
            this.setCalendar(var3);
            return this.calendar.getTime();
         }
      }

      private static int a(int var0) {
         byte var1 = -1;
         switch(var0) {
         case 131072:
            var1 = 0;
            break;
         case 262144:
            var1 = 1;
            break;
         case 524288:
            var1 = 2;
            break;
         case 1048576:
            var1 = 3;
            break;
         case 2097152:
            var1 = 4;
            break;
         case 4194304:
            var1 = 5;
            break;
         case 8388608:
            var1 = 6;
            break;
         case 16777216:
            var1 = 7;
            break;
         case 33554432:
            var1 = 8;
            break;
         case 67108864:
            var1 = 9;
            break;
         case 134217728:
            var1 = 10;
            break;
         case 268435456:
            var1 = 11;
         }

         return var1;
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

      private int a(int var1, int var2) {
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
            boolean var3 = false;
            if (var2 % 4 == 0) {
               var3 = true;
               if (var2 % 100 == 0 && var2 % 400 != 0) {
                  var3 = false;
               }
            }

            return 28 + (var3 ? 1 : 0);
         case 3:
         case 5:
         case 8:
         case 10:
            return 30;
         default:
            return -1;
         }
      }

      private int[] a(int[] var1, int var2) {
         var1[0] += var2;

         while((var2 = this.a(var1[1], var1[2])) < var1[0]) {
            var1[0] -= var2;
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
