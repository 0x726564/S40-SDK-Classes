package com.nokia.mid.impl.isa.pim;

import java.util.Enumeration;
import javax.microedition.pim.Event;
import javax.microedition.pim.EventList;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.UnsupportedFieldException;

class EventListImp extends PIMListImp implements EventList {
   static final int MEETING = 0;
   static final int BIRTHDAY = 2;
   static final int REMINDER = 4;
   public static final int EVENT_LIST_DEFAULT = 0;
   private static final int[][] SUPPORTED_FIELDS = getSupportedFieldsArray();
   static final int[] LIST_NAMES = getListNamesArray();
   private static final int[][] FIELDS_LABELS = getSupportedFieldLabelsArray();
   private int type;

   EventListImp(int var1, String var2) throws PIMException {
      super(var1);

      for(int var3 = 0; var3 <= 4; ++var3) {
         if (PIMTextDatabase.getText(LIST_NAMES[var3]).equals(var2)) {
            this.type = var3;
            return;
         }
      }

      throw new PIMException("Invalid name", 1);
   }

   public String getName() {
      return PIMTextDatabase.getText(LIST_NAMES[this.type]);
   }

   public boolean isSupportedField(int var1) {
      for(int var2 = 0; var2 < SUPPORTED_FIELDS[this.type].length; ++var2) {
         if (SUPPORTED_FIELDS[this.type][var2] == var1) {
            return true;
         }
      }

      return false;
   }

   public int[] getSupportedFields() {
      int[] var1 = new int[SUPPORTED_FIELDS[this.type].length];
      System.arraycopy(SUPPORTED_FIELDS[this.type], 0, var1, 0, SUPPORTED_FIELDS[this.type].length);
      return var1;
   }

   public int getFieldDataType(int var1) {
      this.validateField(var1);
      switch(var1) {
      case 100:
         return 3;
      case 101:
      case 105:
      default:
         throw new IllegalArgumentException();
      case 102:
      case 106:
         return 2;
      case 103:
      case 104:
      case 107:
      case 108:
         return 4;
      }
   }

   public String getFieldLabel(int var1) {
      if (EventImp.isValidField(var1)) {
         if (var1 == 108) {
            return "";
         } else {
            for(int var2 = 1; var2 < SUPPORTED_FIELDS[this.type].length; ++var2) {
               if (SUPPORTED_FIELDS[this.type][var2] == var1) {
                  return PIMTextDatabase.getText(FIELDS_LABELS[this.type][var2 - 1]);
               }
            }

            throw new UnsupportedFieldException(String.valueOf(var1));
         }
      } else {
         throw new IllegalArgumentException("Invalid field:" + String.valueOf(var1));
      }
   }

   public Event createEvent() {
      return new EventImp(this);
   }

   public Event importEvent(Event var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         EventImp var2 = new EventImp(this);
         var2.copyData(var1);
         return var2;
      }
   }

   public void removeEvent(Event var1) throws PIMException {
      this.removeItem(var1);
      CalendarSearchManager.getInstance().removeItem(var1);
      ((EventImp)var1).removeFromList();
   }

   public Enumeration items(int var1, long var2, long var4, boolean var6) throws PIMException {
      if (!this.open) {
         throw new PIMException("List closed!", 2);
      } else if ((var1 == 0 || var1 == 1 || var1 == 2) && var4 > var2) {
         if (this.getMode() != 2 && this.hasReadPermission()) {
            return new SearchResult(this.getSearchManager(), var1, var2, var4, var6, this);
         } else {
            throw new SecurityException("Read permission denied");
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int[] getSupportedRepeatRuleFields(int var1) {
      if (this.type == 2) {
         switch(var1) {
         case 16:
         case 17:
         case 18:
            return new int[0];
         case 19:
            return new int[]{0};
         default:
            throw new IllegalArgumentException("Invalid frequency");
         }
      } else {
         switch(var1) {
         case 16:
         case 18:
         case 19:
            return new int[]{64};
         case 17:
            return new int[]{128, 64};
         default:
            throw new IllegalArgumentException("Invalid frequency");
         }
      }
   }

   SearchManager getSearchManager() {
      return CalendarSearchManager.getInstance();
   }

   void validateField(int var1) {
      if (!EventImp.isValidField(var1)) {
         throw new IllegalArgumentException("Invalid field: " + String.valueOf(var1));
      } else if (!this.isSupportedField(var1)) {
         throw new UnsupportedFieldException(String.valueOf(var1));
      }
   }

   protected boolean hasReadPermission() {
      return PIMListImp.hasAccessRights(1, 2, false);
   }

   protected boolean hasWritePermission(boolean var1) {
      return PIMListImp.hasAccessRights(2, 2, var1);
   }

   private static native int[][] getSupportedFieldsArray();

   private static native int[] getListNamesArray();

   private static native int[][] getSupportedFieldLabelsArray();
}
