package com.nokia.mid.impl.isa.pim;

import javax.microedition.pim.Event;
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
   private int repeatFrequency = 0;
   private int repeatEnd = -1;
   private boolean repeatModified = false;

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
      }

      return var1;
   }

   public void setRepeat(RepeatRule var1) {
      if (var1 == null) {
         this.repeatFrequency = 0;
      } else {
         int[] var2 = var1.getFields();

         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (var2[var3] == 64) {
               this.repeatEnd = (int)(var1.getDate(64) / 1000L);
            }
         }

         switch(var1.getInt(0)) {
         case 16:
            this.repeatFrequency = 1;
            break;
         case 17:
            if (var1.getInt(128) == 2) {
               this.repeatFrequency = 3;
            } else {
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
   }

   public boolean isModified() {
      return super.isModified() || this.repeatModified;
   }

   public void commit() throws PIMException {
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

   native byte[] toSerial(String var1);
}
