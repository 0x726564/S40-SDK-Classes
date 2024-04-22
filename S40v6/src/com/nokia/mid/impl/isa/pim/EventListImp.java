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

   EventListImp(int openMode, String name) throws PIMException {
      super(openMode);

      for(int noteType = 0; noteType <= 4; ++noteType) {
         if (PIMTextDatabase.getText(LIST_NAMES[noteType]).equals(name)) {
            this.type = noteType;
            return;
         }
      }

      throw new PIMException("Invalid name", 1);
   }

   public String getName() {
      return PIMTextDatabase.getText(LIST_NAMES[this.type]);
   }

   public boolean isSupportedField(int field) {
      for(int i = 0; i < SUPPORTED_FIELDS[this.type].length; ++i) {
         if (SUPPORTED_FIELDS[this.type][i] == field) {
            return true;
         }
      }

      return false;
   }

   public int[] getSupportedFields() {
      int[] retFields = new int[SUPPORTED_FIELDS[this.type].length];
      System.arraycopy(SUPPORTED_FIELDS[this.type], 0, retFields, 0, SUPPORTED_FIELDS[this.type].length);
      return retFields;
   }

   public int getFieldDataType(int field) {
      this.validateField(field);
      switch(field) {
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

   public String getFieldLabel(int field) {
      if (EventImp.isValidField(field)) {
         if (field == 108) {
            return "";
         } else {
            for(int i = 1; i < SUPPORTED_FIELDS[this.type].length; ++i) {
               if (SUPPORTED_FIELDS[this.type][i] == field) {
                  return PIMTextDatabase.getText(FIELDS_LABELS[this.type][i - 1]);
               }
            }

            throw new UnsupportedFieldException(String.valueOf(field));
         }
      } else {
         throw new IllegalArgumentException("Invalid field:" + String.valueOf(field));
      }
   }

   public Event createEvent() {
      return new EventImp(this);
   }

   public Event importEvent(Event item) {
      if (item == null) {
         throw new NullPointerException();
      } else {
         EventImp newEvent = new EventImp(this);
         newEvent.copyData(item);
         return newEvent;
      }
   }

   public void removeEvent(Event item) throws PIMException {
      this.removeItem(item);
      CalendarSearchManager.getInstance().removeItem(item);
      ((EventImp)item).removeFromList();
   }

   public Enumeration items(int searchType, long startDate, long endDate, boolean initialEventOnly) throws PIMException {
      if (!this.open) {
         throw new PIMException("List closed!", 2);
      } else if ((searchType == 0 || searchType == 1 || searchType == 2) && endDate > startDate) {
         if (this.getMode() != 2 && this.hasReadPermission()) {
            return new SearchResult(this.getSearchManager(), searchType, startDate, endDate, initialEventOnly, this);
         } else {
            throw new SecurityException("Read permission denied");
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public int[] getSupportedRepeatRuleFields(int frequency) {
      if (this.type == 2) {
         switch(frequency) {
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
         switch(frequency) {
         case 16:
            return new int[]{64};
         case 17:
            return new int[]{128, 2, 64};
         case 18:
            return new int[]{1, 64};
         case 19:
            return new int[]{1, 8, 64};
         default:
            throw new IllegalArgumentException("Invalid frequency");
         }
      }
   }

   SearchManager getSearchManager() {
      return CalendarSearchManager.getInstance();
   }

   void validateField(int field) {
      if (!EventImp.isValidField(field)) {
         throw new IllegalArgumentException("Invalid field: " + String.valueOf(field));
      } else if (!this.isSupportedField(field)) {
         throw new UnsupportedFieldException(String.valueOf(field));
      }
   }

   protected boolean hasReadPermission() {
      return PIMListImp.hasAccessRights(1, 2, false);
   }

   protected boolean hasWritePermission(boolean toBeCreated) {
      return PIMListImp.hasAccessRights(2, 2, toBeCreated);
   }

   private static native int[][] getSupportedFieldsArray();

   private static native int[] getListNamesArray();

   private static native int[][] getSupportedFieldLabelsArray();
}
