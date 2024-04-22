package com.nokia.mid.impl.isa.pim;

import javax.microedition.pim.Event;
import javax.microedition.pim.EventList;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;
import javax.microedition.pim.ToDo;
import javax.microedition.pim.ToDoList;

public class CalendarSearchManager implements SearchManager {
   private static CalendarSearchManager _instance = null;

   private CalendarSearchManager() {
   }

   public static synchronized CalendarSearchManager getInstance() {
      if (_instance == null) {
         _instance = new CalendarSearchManager();
      }

      return _instance;
   }

   public synchronized PIMItem nextElement(PIMItem var1, PIMItem var2, PIMList var3) {
      int var4 = 0;
      if (var1 != null) {
         int var5 = var1 instanceof Event ? 108 : 108;
         if (var1.countValues(var5) != 0) {
            try {
               var4 = Integer.parseInt(var1.getString(var5, 0));
            } catch (NumberFormatException var7) {
               throw new RuntimeException("Wrong UID ");
            }
         }
      }

      PIMItem var8 = this.findIndexRecord(var4, var3, true);
      if (var2 != null) {
         while(var8 != null && !((PIMItemImp)var8).matches(var2)) {
            var8 = this.findIndexRecord(0, var3, false);
         }
      }

      return var8;
   }

   public synchronized PIMItem nextElement(PIMItem var1, String var2, PIMList var3, boolean var4) {
      int var5 = 0;
      if (var4) {
         throw new RuntimeException("Not supported");
      } else {
         if (var1 != null) {
            int var6 = var1 instanceof Event ? 108 : 108;
            if (var1.countValues(var6) != 0) {
               try {
                  var5 = Integer.parseInt(var1.getString(var6, 0));
               } catch (NumberFormatException var8) {
                  throw new RuntimeException("Wrong UID ");
               }
            }
         }

         PIMItem var9 = this.findIndexRecord(var5, var3, true);
         if (var2 != null) {
            while(var9 != null && !((PIMItemImp)var9).matches(var2)) {
               var9 = this.findIndexRecord(0, var3, false);
            }
         }

         return var9;
      }
   }

   public synchronized void commitItem(PIMItem var1) {
      if (var1 != null) {
         this.commit(var1);
      } else {
         throw new NullPointerException();
      }
   }

   public synchronized void removeItem(PIMItem var1) {
      if (var1 != null) {
         if (((PIMItemImp)var1).hasBeenCommitted) {
            this.remove(var1);
         }

      } else {
         throw new NullPointerException();
      }
   }

   public synchronized ToDo SearchByToDo(ToDo var1, int var2, long var3, long var5, ToDoList var7) {
      int var8 = 0;
      if (var1 != null) {
         byte var9 = 108;

         try {
            var8 = Integer.parseInt(var1.getString(var9, 0));
         } catch (NumberFormatException var11) {
            throw new RuntimeException("Malformed UID!");
         } catch (IndexOutOfBoundsException var12) {
         }
      }

      return this.findToDoInRange(var8, var7, var2 == 102, (int)(var3 / 1000L), (int)(var5 / 1000L));
   }

   public synchronized Event SearchByEvent(Event var1, int var2, long var3, long var5, boolean var7, EventList var8) {
      int var9 = 0;
      if (var1 != null) {
         byte var10 = 108;

         try {
            var9 = Integer.parseInt(var1.getString(var10, 0));
         } catch (NumberFormatException var12) {
            throw new RuntimeException("Malformed UID!");
         } catch (IndexOutOfBoundsException var13) {
         }
      }

      return this.findEventInRange(var9, var8, var2, (int)(var3 / 1000L), (int)(var5 / 1000L), var7);
   }

   public String[] categories() throws PIMException {
      throw new RuntimeException("Not supported.");
   }

   public synchronized void renameCategory(String var1, String var2) throws PIMException {
      throw new RuntimeException("Not supported.");
   }

   private native ToDo findToDoInRange(int var1, PIMList var2, boolean var3, int var4, int var5);

   private native Event findEventInRange(int var1, PIMList var2, int var3, int var4, int var5, boolean var6);

   private native PIMItem findIndexRecord(int var1, PIMList var2, boolean var3);

   private native void commit(PIMItem var1);

   private native void remove(PIMItem var1);
}
