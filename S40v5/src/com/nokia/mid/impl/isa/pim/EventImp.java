package com.nokia.mid.impl.isa.pim;

import java.util.Calendar;
import java.util.Date;
import javax.microedition.pim.Event;
import javax.microedition.pim.FieldEmptyException;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;
import javax.microedition.pim.RepeatRule;

public class EventImp extends PIMItemImp implements Event {
   private int ld = 0;
   private int le = -1;
   private int lf = -1;
   private int lg = -1;
   private int lh = -1;
   private boolean li = false;
   private boolean lj = false;

   EventImp(PIMList var1) {
      super(var1);
   }

   public RepeatRule getRepeat() {
      RepeatRule var1 = null;
      if (this.ld != 0) {
         var1 = new RepeatRule();
         switch(this.ld) {
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

         if (this.lh != -1) {
            var1.setDate(64, (long)this.lh * 1000L);
         }

         if (this.le != -1) {
            var1.setInt(2, this.le);
         }

         if (this.lf != -1) {
            var1.setInt(8, this.lf);
         }

         if (this.lg != -1) {
            var1.setInt(1, this.lg);
         }
      }

      return var1;
   }

   public void setRepeat(RepeatRule var1) {
      if (var1 == null) {
         this.ld = 0;
         this.lj = false;
      } else {
         int[] var2 = var1.getFields();

         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (var2[var3] == 64) {
               this.lh = (int)(var1.getDate(64) / 1000L);
            }

            if (var2[var3] == 2 && var1.getInt(2) != -1) {
               this.le = var1.getInt(2);
            }

            if (var2[var3] == 8 && var1.getInt(8) != -1) {
               this.lf = var1.getInt(8);
            }

            if (var2[var3] == 1 && var1.getInt(1) != -1) {
               this.lg = var1.getInt(1);
            }
         }

         switch(var1.getInt(0)) {
         case 16:
            this.ld = 1;
            break;
         case 17:
            try {
               if (var1.getInt(128) == 2) {
                  this.ld = 3;
               } else {
                  this.ld = 2;
               }
            } catch (FieldEmptyException var4) {
               this.ld = 2;
            }
            break;
         case 18:
            this.ld = 4;
            break;
         case 19:
            this.ld = 5;
            break;
         default:
            throw new RuntimeException("Wrong frequency in RepeatRule");
         }
      }

      this.li = true;
      this.lj = this.le != -1 || this.lg != -1 || this.lf != -1;
   }

   public boolean isModified() {
      return super.isModified() || this.li;
   }

   public void commit() throws PIMException {
      if (this.lj) {
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

         RepeatRule var5 = new RepeatRule();
         switch(this.ld) {
         case 1:
            var5.setInt(0, 16);
            break;
         case 3:
            var5.setInt(128, 2);
         case 2:
            var5.setInt(0, 17);
            break;
         case 4:
            var5.setInt(0, 18);
            break;
         case 5:
            var5.setInt(0, 19);
         }

         if (this.le != -1) {
            var5.setInt(2, this.le);
         }

         if (this.lg != -1) {
            var5.setInt(1, this.lg);
         }

         if (this.lf != -1) {
            var5.setInt(8, this.lf);
         }

         RepeatRule var12 = var5;
         Calendar var13;
         Date var6 = (var13 = Calendar.getInstance()).getTime();
         Date var7 = null;
         if (var1 == null) {
            var7 = var6;
         } else {
            Date var9 = new Date(var1.getTime() - 86400000L);
            var13.setTime(var9);
            var7 = var13.getTime();
         }

         long var16 = var7.getTime();
         var13.set(1, var13.get(1) + 5);
         var6 = var13.getTime();
         var2 = (Date)var12.dates(var16, var16 + 1L, var6.getTime()).nextElement();
         if (var1 != null) {
            var13.setTime(var1);
            int var11 = var13.get(11);
            int var14 = var13.get(12);
            int var15 = var13.get(13);
            var13.setTime(var2);
            var13.set(11, var11);
            var13.set(12, var14);
            var13.set(13, var15);
            var2 = var13.getTime();
         }

         if (this.countValues(106) == 0) {
            this.addDate(106, 0, var2.getTime());
         } else {
            this.setDate(106, 0, 0, var2.getTime());
         }

         if (var3 > 0L) {
            this.setDate(102, 0, 0, var2.getTime() + var3);
         }
      }

      super.commit();
      this.li = false;
   }

   static final boolean e(int var0) {
      return 100 <= var0 && var0 <= 108 || 200 <= var0 && var0 <= 202;
   }

   SearchManager getSearchManager() {
      return CalendarSearchManager.getInstance();
   }

   final void c(PIMItem var1) {
      super.c(var1);
      this.setRepeat(((Event)var1).getRepeat());
   }

   final void a(PIMItem var1) {
   }

   native byte[] toSerial(String var1, int var2);
}
