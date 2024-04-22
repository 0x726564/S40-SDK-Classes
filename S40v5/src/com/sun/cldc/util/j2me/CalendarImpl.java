package com.sun.cldc.util.j2me;

import java.util.Calendar;

public class CalendarImpl extends Calendar {
   private static final int[] fT = new int[]{0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
   private static final int[] fU = new int[]{0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};
   private static String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
   private static String[] gS = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

   protected void computeFields() {
      int var1 = this.getTimeZone().getRawOffset();
      long var2 = this.time + (long)var1;
      if (this.time > 0L && var2 < 0L && var1 > 0) {
         var2 = Long.MAX_VALUE;
      } else if (this.time < 0L && var2 > 0L && var1 < 0) {
         var2 = Long.MIN_VALUE;
      }

      this.c(var2);
      long var4 = var2 / 86400000L;
      int var10;
      if ((var10 = (int)(var2 - var4 * 86400000L)) < 0) {
         var10 = (int)((long)var10 + 86400000L);
      }

      var1 = this.getTimeZone().getOffset(1, this.fields[1], this.fields[2], this.fields[5], this.fields[7], var10) - var1;
      if ((long)(var10 += var1) >= 86400000L) {
         long var8 = var2 + (long)var1;
         var10 = (int)((long)var10 - 86400000L);
         if (var2 > 0L && var8 < 0L && var1 > 0) {
            var8 = Long.MAX_VALUE;
         } else if (var2 < 0L && var8 > 0L && var1 < 0) {
            var8 = Long.MIN_VALUE;
         }

         this.c(var8);
      }

      this.fields[14] = var10 % 1000;
      var10 /= 1000;
      this.fields[13] = var10 % 60;
      var10 /= 60;
      this.fields[12] = var10 % 60;
      var10 /= 60;
      this.fields[11] = var10;
      this.fields[9] = var10 / 12;
      this.fields[10] = var10 % 12;
   }

   private final void c(long var1) {
      int var3;
      int var4;
      long var6;
      int var9;
      boolean var11;
      if (var1 >= -12219292800000L) {
         var6 = d(var1) - 1721426L;
         int[] var8 = new int[1];
         var9 = floorDivide(var6, 146097, var8);
         int var5 = floorDivide(var8[0], 36524, var8);
         var3 = floorDivide(var8[0], 1461, var8);
         int var10 = floorDivide(var8[0], 365, var8);
         var4 = 400 * var9 + 100 * var5 + (var3 << 2) + var10;
         var3 = var8[0];
         if (var5 != 4 && var10 != 4) {
            ++var4;
         } else {
            var3 = 365;
         }

         var11 = (var4 & 3) == 0 && (var4 % 100 != 0 || var4 % 400 == 0);
         this.fields[7] = (int)((var6 + 1L) % 7L);
      } else {
         var6 = d(var1) - 1721424L;
         var4 = (int)floorDivide(4L * var6 + 1464L, 1461L);
         long var13 = (long)(365 * (var4 - 1) + floorDivide(var4 - 1, 4));
         var3 = (int)(var6 - var13);
         var11 = (var4 & 3) == 0;
         this.fields[7] = (int)((var6 - 1L) % 7L);
      }

      int var12 = 0;
      int var7 = var11 ? 60 : 59;
      if (var3 >= var7) {
         var12 = var11 ? 1 : 2;
      }

      int var14 = (12 * (var3 + var12) + 6) / 367;
      var9 = var3 - (var11 ? fU[var14] : fT[var14]) + 1;
      int[] var10000 = this.fields;
      var10000[7] += this.fields[7] < 0 ? 8 : 1;
      this.fields[1] = var4;
      if (this.fields[1] < 1) {
         this.fields[1] = 1 - this.fields[1];
      }

      this.fields[2] = var14;
      this.fields[5] = var9;
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
         int var7;
         String var8 = Integer.toString(var7 = var0.get(1));
         String var9;
         if ((var9 = var0.getTimeZone().getID()) == null) {
            var9 = "";
         }

         StringBuffer var10;
         (var10 = new StringBuffer(25 + var9.length() + var8.length())).append(gS[var1 - 1]).append(' ');
         var10.append(months[var2]).append(' ');
         c(var10, var3).append(' ');
         c(var10, var4).append(':');
         c(var10, var5).append(':');
         c(var10, var6).append(' ');
         if (var9.length() > 0) {
            var10.append(var9).append(' ');
         }

         b(var10, var7);
         return var10.toString();
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
         int var5 = var0.get(12);
         int var6 = var0.get(13);
         String var7 = Integer.toString(var1);
         StringBuffer var9;
         b(var9 = new StringBuffer(25 + var7.length()), var1).append(' ');
         c(var9, var2).append(' ');
         c(var9, var3).append(' ');
         c(var9, var4).append(' ');
         c(var9, var5).append(' ');
         c(var9, var6).append(' ');
         int var8;
         if ((var8 = var0.getTimeZone().getRawOffset() / 1000 / 60) < 0) {
            var8 = Math.abs(var8);
            var9.append('-');
         } else {
            var9.append('+');
         }

         var1 = var8 / 60;
         var8 %= 60;
         c(var9, var1);
         c(var9, var8);
         return var9.toString();
      }
   }

   private static final StringBuffer b(StringBuffer var0, int var1) {
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

   private static final StringBuffer c(StringBuffer var0, int var1) {
      if (var1 < 10) {
         var0.append('0');
      }

      return var0.append(var1);
   }

   protected void computeTime() {
      int var13;
      if (this.isSet[11]) {
         var13 = this.fields[11] % 24;
         this.fields[11] = var13;
         this.fields[9] = var13 < 12 ? 0 : 1;
         this.isSet[11] = false;
      } else {
         if (this.isSet[9]) {
            if (this.fields[9] != 0 && this.fields[9] != 1) {
               var13 = this.fields[11];
               this.fields[9] = var13 < 12 ? 0 : 1;
            }

            this.isSet[9] = false;
         }

         if (this.isSet[10]) {
            if ((var13 = this.fields[10]) > 12) {
               this.fields[11] = var13 % 12 + 12;
               this.fields[10] = var13 % 12;
               this.fields[9] = 1;
            } else if (this.fields[9] == 1) {
               this.fields[11] = var13 + 12;
            } else {
               this.fields[11] = var13;
            }

            this.isSet[10] = false;
         }
      }

      int var1;
      boolean var2 = (var1 = this.fields[1]) >= 1582;
      long var3;
      long var5 = e(var3 = this.a(var2, var1));
      if (var2 != var5 >= -12219292800000L && var3 != -106749550580L) {
         var5 = e(var3 = this.a(!var2, var1));
      }

      boolean var14 = false;
      var1 = (((0 + this.fields[11]) * 60 + this.fields[12]) * 60 + this.fields[13]) * 1000 + this.fields[14];
      int var16 = this.getTimeZone().getRawOffset();
      var5 += (long)var1;
      int[] var15 = new int[1];
      floorDivide(var5, 86400000, var15);
      int var17 = (var17 = (int)((var3 + 1L) % 7L)) + (var17 < 0 ? 8 : 1);
      var1 = this.getTimeZone().getOffset(1, this.fields[1], this.fields[2], this.fields[5], var17, var15[0]) - var16;
      this.time = var5 - (long)var16 - (long)var1;
   }

   private final long a(boolean var1, int var2) {
      boolean var3 = false;
      int var7;
      if ((var7 = this.fields[2]) < 0 || var7 > 11) {
         int[] var4 = new int[1];
         var2 += floorDivide(var7, 12, var4);
         var7 = var4[0];
      }

      boolean var8 = var2 % 4 == 0;
      long var5 = 365L * (long)(var2 - 1) + (long)floorDivide(var2 - 1, 4) + 1721423L;
      if (var1) {
         var8 = var8 && (var2 % 100 != 0 || var2 % 400 == 0);
         var5 += (long)(floorDivide(var2 - 1, 400) - floorDivide(var2 - 1, 100) + 2);
      }

      long var10001 = var8 ? (long)fU[var7] : (long)fT[var7];
      long var10000 = var5 + var10001;
      var10001 += var5;
      return var10000 + (long)this.fields[5];
   }

   private static final long d(long var0) {
      return 2440588L + floorDivide(var0, 86400000L);
   }

   private static final long e(long var0) {
      return (var0 - 2440588L) * 86400000L;
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
