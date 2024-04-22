package com.nokia.mid.impl.isa.pim;

import com.nokia.mid.impl.isa.util.SharedObjects;
import javax.microedition.pim.Event;
import javax.microedition.pim.EventList;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;
import javax.microedition.pim.ToDo;
import javax.microedition.pim.ToDoList;

public class CalendarSearchManager implements SearchManager {
   private static CalendarSearchManager _instance = null;
   private static final Object cldrLock = SharedObjects.getLock("javax.microedition.pim.cldrLock");

   private CalendarSearchManager() {
   }

   public static synchronized CalendarSearchManager getInstance() {
      if (_instance == null) {
         _instance = new CalendarSearchManager();
      }

      return _instance;
   }

   public PIMItem nextElement(PIMItem prevItem, PIMItem searchItem, PIMList list) {
      int prevUIDval = 0;
      if (prevItem != null) {
         int prevUID = prevItem instanceof Event ? 108 : 108;
         if (prevItem.countValues(prevUID) != 0) {
            try {
               prevUIDval = Integer.parseInt(prevItem.getString(prevUID, 0));
            } catch (NumberFormatException var8) {
               throw new RuntimeException("Wrong UID ");
            }
         }
      }

      synchronized(cldrLock) {
         PIMItem itemFound = this.findIndexRecord(prevUIDval, list, true);
         if (searchItem != null) {
            while(itemFound != null && !((PIMItemImp)itemFound).matches(searchItem)) {
               itemFound = this.findIndexRecord(0, list, false);
            }
         }

         return itemFound;
      }
   }

   public PIMItem nextElement(PIMItem prevItem, String searchString, PIMList list, boolean byCategory) {
      int prevUIDval = 0;
      if (byCategory) {
         throw new RuntimeException("Not supported");
      } else {
         if (prevItem != null) {
            int prevUID = prevItem instanceof Event ? 108 : 108;
            if (prevItem.countValues(prevUID) != 0) {
               try {
                  prevUIDval = Integer.parseInt(prevItem.getString(prevUID, 0));
               } catch (NumberFormatException var9) {
                  throw new RuntimeException("Wrong UID ");
               }
            }
         }

         synchronized(cldrLock) {
            PIMItem itemFound = this.findIndexRecord(prevUIDval, list, true);
            if (searchString != null) {
               while(itemFound != null && !((PIMItemImp)itemFound).matches(searchString)) {
                  itemFound = this.findIndexRecord(0, list, false);
               }
            }

            return itemFound;
         }
      }
   }

   public void commitItem(PIMItem item) {
      if (item != null) {
         synchronized(cldrLock) {
            this.commit(item);
         }
      } else {
         throw new NullPointerException();
      }
   }

   public void removeItem(PIMItem item) {
      if (item != null) {
         if (((PIMItemImp)item).hasBeenCommitted) {
            synchronized(cldrLock) {
               this.remove(item);
            }
         }

      } else {
         throw new NullPointerException();
      }
   }

   public ToDo SearchByToDo(ToDo prevItem, int field, long startDate, long endDate, ToDoList list) {
      int prevUIDval = 0;
      if (prevItem != null) {
         byte prevUID = 108;

         try {
            prevUIDval = Integer.parseInt(prevItem.getString(prevUID, 0));
         } catch (NumberFormatException var13) {
            throw new RuntimeException("Malformed UID!");
         } catch (IndexOutOfBoundsException var14) {
         }
      }

      synchronized(cldrLock) {
         return this.findToDoInRange(prevUIDval, list, field == 102, (int)(startDate / 1000L), (int)(endDate / 1000L));
      }
   }

   public Event SearchByEvent(Event prevItem, int searchType, long startDate, long endDate, boolean initialEventOnly, EventList list) {
      int prevUIDval = 0;
      if (prevItem != null) {
         byte prevUID = 108;

         try {
            prevUIDval = Integer.parseInt(prevItem.getString(prevUID, 0));
         } catch (NumberFormatException var14) {
            throw new RuntimeException("Malformed UID!");
         } catch (IndexOutOfBoundsException var15) {
         }
      }

      synchronized(cldrLock) {
         return this.findEventInRange(prevUIDval, list, searchType, (int)(startDate / 1000L), (int)(endDate / 1000L), initialEventOnly);
      }
   }

   public String[] categories() throws PIMException {
      throw new RuntimeException("Not supported.");
   }

   public void renameCategory(String currentCategoryName, String newCategoryName) throws PIMException {
      throw new RuntimeException("Not supported.");
   }

   private native ToDo findToDoInRange(int var1, PIMList var2, boolean var3, int var4, int var5);

   private native Event findEventInRange(int var1, PIMList var2, int var3, int var4, int var5, boolean var6);

   private native PIMItem findIndexRecord(int var1, PIMList var2, boolean var3);

   private native void commit(PIMItem var1);

   private native void remove(PIMItem var1);
}
