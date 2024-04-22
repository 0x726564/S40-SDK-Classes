package com.nokia.mid.impl.isa.pim;

import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;
import javax.microedition.pim.ToDo;

public class ToDoImp extends PIMItemImp implements ToDo {
   private int alarm;

   ToDoImp(PIMList var1) {
      super(var1);
   }

   public void setInt(int var1, int var2, int var3, int var4) {
      if (var1 == 105) {
         super.setInt(var1, var2, var3, this.presetPriority(var4));
      } else {
         super.setInt(var1, var2, var3, var4);
      }

   }

   public void addInt(int var1, int var2, int var3) {
      if (var1 == 105) {
         super.addInt(var1, var2, this.presetPriority(var3));
      } else {
         super.addInt(var1, var2, var3);
      }

   }

   static boolean isValidField(int var0) {
      return 100 <= var0 && var0 <= 108 || 200 <= var0 && var0 <= 202;
   }

   SearchManager getSearchManager() {
      return CalendarSearchManager.getInstance();
   }

   int getAlarm() {
      return this.alarm;
   }

   void copyPrivateData(PIMItem var1) {
      this.alarm = ((ToDoImp)var1).getAlarm();
   }

   native byte[] toSerial(String var1, int var2);

   private int presetPriority(int var1) {
      byte var2;
      if (0 < var1 && var1 <= 3) {
         var2 = 1;
      } else if ((4 > var1 || var1 > 6) && var1 != 0) {
         if (7 > var1 || var1 > 9) {
            throw new IllegalArgumentException("Wrong priority value");
         }

         var2 = 7;
      } else {
         var2 = 4;
      }

      return var2;
   }
}
