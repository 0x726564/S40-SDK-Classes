package com.nokia.mid.impl.isa.pim;

import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;
import javax.microedition.pim.ToDo;

public class ToDoImp extends PIMItemImp implements ToDo {
   private int alarm;

   ToDoImp(PIMList pimList) {
      super(pimList);
   }

   public void setInt(int field, int index, int attributes, int value) {
      if (field == 105) {
         super.setInt(field, index, attributes, this.presetPriority(value));
      } else {
         super.setInt(field, index, attributes, value);
      }

   }

   public void addInt(int field, int attributes, int value) {
      if (field == 105) {
         super.addInt(field, attributes, this.presetPriority(value));
      } else {
         super.addInt(field, attributes, value);
      }

   }

   static boolean isValidField(int field) {
      return 100 <= field && field <= 108 || 200 <= field && field <= 202;
   }

   SearchManager getSearchManager() {
      return CalendarSearchManager.getInstance();
   }

   int getAlarm() {
      return this.alarm;
   }

   void copyPrivateData(PIMItem item) {
      this.alarm = ((ToDoImp)item).getAlarm();
   }

   native byte[] toSerial(String var1, int var2);

   private int presetPriority(int prio) {
      byte var2;
      if (0 < prio && prio <= 3) {
         var2 = 1;
      } else if ((4 > prio || prio > 6) && prio != 0) {
         if (7 > prio || prio > 9) {
            throw new IllegalArgumentException("Wrong priority value");
         }

         var2 = 7;
      } else {
         var2 = 4;
      }

      return var2;
   }
}
