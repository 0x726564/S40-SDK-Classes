package com.nokia.mid.impl.isa.pim;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import javax.microedition.pim.Event;
import javax.microedition.pim.FieldEmptyException;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;
import javax.microedition.pim.RepeatRule;

public class EventImp extends PIMItemImp implements Event {
   private static final int REPEAT_NONE = 0;
   private static final int REPEAT_DAILY = 1;
   private static final int REPEAT_WEEKLY = 2;
   private static final int REPEAT_BI_WEEKLY = 3;
   private static final int REPEAT_MONTHLY = 4;
   private static final int REPEAT_YEARLY = 5;
   private static final long MILLIS_IN_DAY = 86400000L;
   private int repeatFrequency = 0;
   private int dayInWeek = -1;
   private int monthInYear = -1;
   private int dayInMonth = -1;
   private int repeatEnd = -1;
   private boolean repeatModified = false;
   private boolean useRRstart = false;

   EventImp(PIMList var1) {
      super(var1);
   }

   public RepeatRule getRepeat() {
      RepeatRule var1 = null;
      if (this.repeatFrequency != 0) {
         var1 = new RepeatRule();
         switch(this.repeatFrequency) {
         case 2:
            var1.setInt(0, 17);
            break;
         case 3:
            var1.setInt(0, 17);
            var1.setInt(128, 2);
            break;
         case 4:
            var1.setInt(0, 18);
            break;
         case 5:
            var1.setInt(0, 19);
            break;
         default:
            var1.setInt(0, 16);
         }

         if (this.repeatEnd != -1) {
            var1.setDate(64, (long)this.repeatEnd * 1000L);
         }

         if (this.dayInWeek != -1) {
            var1.setInt(2, this.dayInWeek);
         }

         if (this.monthInYear != -1) {
            var1.setInt(8, this.monthInYear);
         }

         if (this.dayInMonth != -1) {
            var1.setInt(1, this.dayInMonth);
         }
      }

      return var1;
   }

   public void setRepeat(RepeatRule var1) {
      if (var1 == null) {
         this.repeatFrequency = 0;
         this.useRRstart = false;
      } else {
         int[] var2 = var1.getFields();

         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (var2[var3] == 64) {
               this.repeatEnd = (int)(var1.getDate(64) / 1000L);
            }

            if (var2[var3] == 2 && var1.getInt(2) != -1) {
               this.dayInWeek = var1.getInt(2);
            }

            if (var2[var3] == 8 && var1.getInt(8) != -1) {
               this.monthInYear = var1.getInt(8);
            }

            if (var2[var3] == 1 && var1.getInt(1) != -1) {
               this.dayInMonth = var1.getInt(1);
            }
         }

         switch(var1.getInt(0)) {
         case 16:
            this.repeatFrequency = 1;
            break;
         case 17:
            try {
               if (var1.getInt(128) == 2) {
                  this.repeatFrequency = 3;
               } else {
                  this.repeatFrequency = 2;
               }
            } catch (FieldEmptyException var4) {
               this.repeatFrequency = 2;
            }
            break;
         case 18:
            this.repeatFrequency = 4;
            break;
         case 19:
            this.repeatFrequency = 5;
            break;
         default:
            throw new RuntimeException("Wrong frequency in RepeatRule");
         }
      }

      this.repeatModified = true;
      this.useRRstart = this.dayInWeek != -1 || this.dayInMonth != -1 || this.monthInYear != -1;
   }

   public boolean isModified() {
      return super.isModified() || this.repeatModified;
   }

   public void commit() throws PIMException {
      if (this.useRRstart) {
         Date var1 = null;
         Date var2 = null;
         long var3 = 0L;
         if (this.list.isSupportedField(106) && this.countValues(106) != 0) {
            var1 = new Date(this.getDate(106, 0));
         }

         if (this.list.isSupportedField(102) && this.countValues(102) != 0) {
            var2 = new Date(this.getDate(102, 0));
         }

         if (var1 != null && var2 != null) {
            var3 = var2.getTime() - var1.getTime();
         }

         RepeatRule var5 = this.createRRForUpdate();
         Calendar var6 = Calendar.getInstance();
         Date var7 = var6.getTime();
         Date var8 = null;
         if (var1 == null) {
            var8 = var7;
         } else {
            Date var9 = new Date(var1.getTime() - 86400000L);
            var6.setTime(var9);
            var8 = var6.getTime();
         }

         long var17 = var8.getTime();
         var6.set(1, var6.get(1) + 5);
         Date var11 = var6.getTime();
         Enumeration var12 = var5.dates(var17, var17 + 1L, var11.getTime());
         Date var13 = (Date)var12.nextElement();
         if (var1 != null) {
            var6.setTime(var1);
            int var14 = var6.get(11);
            int var15 = var6.get(12);
            int var16 = var6.get(13);
            var6.setTime(var13);
            var6.set(11, var14);
            var6.set(12, var15);
            var6.set(13, var16);
            var13 = var6.getTime();
         }

         if (this.countValues(106) == 0) {
            this.addDate(106, 0, var13.getTime());
         } else {
            this.setDate(106, 0, 0, var13.getTime());
         }

         if (var3 > 0L) {
            this.setDate(102, 0, 0, var13.getTime() + var3);
         }
      }

      super.commit();
      this.repeatModified = false;
   }

   static final boolean isValidField(int var0) {
      return 100 <= var0 && var0 <= 108 || 200 <= var0 && var0 <= 202;
   }

   SearchManager getSearchManager() {
      return CalendarSearchManager.getInstance();
   }

   void copyData(PIMItem var1) {
      super.copyData(var1);
      this.setRepeat(((Event)var1).getRepeat());
   }

   void copyPrivateData(PIMItem var1) {
   }

   private RepeatRule createRRForUpdate() {
      RepeatRule var1 = new RepeatRule();
      switch(this.repeatFrequency) {
      case 1:
         var1.setInt(0, 16);
         break;
      case 3:
         var1.setInt(128, 2);
      case 2:
         var1.setInt(0, 17);
         break;
      case 4:
         var1.setInt(0, 18);
         break;
      case 5:
         var1.setInt(0, 19);
      }

      if (this.dayInWeek != -1) {
         var1.setInt(2, this.dayInWeek);
      }

      if (this.dayInMonth != -1) {
         var1.setInt(1, this.dayInMonth);
      }

      if (this.monthInYear != -1) {
         var1.setInt(8, this.monthInYear);
      }

      return var1;
   }

   native byte[] toSerial(String var1, int var2);
}
