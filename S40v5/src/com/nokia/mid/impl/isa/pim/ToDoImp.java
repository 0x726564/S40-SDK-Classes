package com.nokia.mid.impl.isa.pim;

import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;
import javax.microedition.pim.ToDo;

public class ToDoImp extends PIMItemImp implements ToDo {
   private int bo;

   ToDoImp(PIMList var1) {
      super(var1);
   }

   public void setInt(int var1, int var2, int var3, int var4) {
      if (var1 == 105) {
         super.setInt(var1, var2, var3, f(var4));
      } else {
         super.setInt(var1, var2, var3, var4);
      }
   }

   public void addInt(int var1, int var2, int var3) {
      if (var1 == 105) {
         super.addInt(var1, var2, f(var3));
      } else {
         super.addInt(var1, var2, var3);
      }
   }

   static boolean e(int var0) {
      return 100 <= var0 && var0 <= 108 || 200 <= var0 && var0 <= 202;
   }

   SearchManager getSearchManager() {
      return CalendarSearchManager.getInstance();
   }

   int getAlarm() {
      return this.bo;
   }

   final void a(PIMItem var1) {
      this.bo = ((ToDoImp)var1).getAlarm();
   }

   native byte[] toSerial(String var1, int var2);

   private static int f(int var0) {
      byte var1;
      if (0 < var0 && var0 <= 3) {
         var1 = 1;
      } else if ((4 > var0 || var0 > 6) && var0 != 0) {
         if (7 > var0 || var0 > 9) {
            throw new IllegalArgumentException("Wrong priority value");
         }

         var1 = 7;
      } else {
         var1 = 4;
      }

      return var1;
   }
}
