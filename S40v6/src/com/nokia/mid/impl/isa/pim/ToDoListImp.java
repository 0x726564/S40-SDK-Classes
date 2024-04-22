package com.nokia.mid.impl.isa.pim;

import java.util.Enumeration;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.ToDo;
import javax.microedition.pim.ToDoList;
import javax.microedition.pim.UnsupportedFieldException;

class ToDoListImp extends PIMListImp implements ToDoList {
   private static final int[] SUPPORTED_FIELDS = getSupportedFieldsArray();
   static final int[] LIST_NAMES = getListNamesArray();
   private static final int[] FIELDS_LABELS = getSupportedFieldLabelsArray();

   ToDoListImp(int openMode, String name) throws PIMException {
      super(openMode);
      if (!name.equals(PIMTextDatabase.getText(LIST_NAMES[0]))) {
         throw new PIMException("Invalid name", 1);
      }
   }

   public String getName() {
      return PIMTextDatabase.getText(LIST_NAMES[0]);
   }

   public boolean isSupportedField(int field) {
      for(int i = 0; i < SUPPORTED_FIELDS.length; ++i) {
         if (SUPPORTED_FIELDS[i] == field) {
            return true;
         }
      }

      return false;
   }

   public int[] getSupportedFields() {
      int[] retFields = new int[SUPPORTED_FIELDS.length];
      System.arraycopy(SUPPORTED_FIELDS, 0, retFields, 0, SUPPORTED_FIELDS.length);
      return retFields;
   }

   public int getFieldDataType(int field) {
      this.validateField(field);
      switch(field) {
      case 101:
         return 1;
      case 102:
      case 103:
         return 2;
      case 104:
      case 106:
      default:
         throw new IllegalArgumentException();
      case 105:
         return 3;
      case 107:
      case 108:
         return 4;
      }
   }

   public String getFieldLabel(int field) {
      if (ToDoImp.isValidField(field)) {
         if (field == 108) {
            return "";
         } else {
            for(int i = 1; i < SUPPORTED_FIELDS.length; ++i) {
               if (SUPPORTED_FIELDS[i] == field) {
                  return PIMTextDatabase.getText(FIELDS_LABELS[i - 1]);
               }
            }

            throw new UnsupportedFieldException();
         }
      } else {
         throw new IllegalArgumentException("Not a valid field.");
      }
   }

   public ToDo createToDo() {
      return new ToDoImp(this);
   }

   public ToDo importToDo(ToDo item) {
      ToDoImp newToDo = new ToDoImp(this);
      newToDo.copyData(item);
      return newToDo;
   }

   public void removeToDo(ToDo item) throws PIMException {
      this.removeItem(item);
      CalendarSearchManager.getInstance().removeItem(item);
      ((ToDoImp)item).removeFromList();
   }

   public Enumeration items(int field, long startDate, long endDate) throws PIMException {
      if (!this.open) {
         throw new PIMException("List closed!", 2);
      } else {
         this.validateField(field);
         if ((field == 102 || field == 103) && endDate > startDate) {
            if (this.getMode() != 2 && this.hasReadPermission()) {
               return new SearchResult(this.getSearchManager(), field, startDate, endDate, this);
            } else {
               throw new SecurityException("Read permission denied");
            }
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   public int[] getSupportedRepeatRuleFields(int frequency) {
      throw new RuntimeException("Not implemented");
   }

   SearchManager getSearchManager() {
      return CalendarSearchManager.getInstance();
   }

   void validateField(int field) {
      if (!ToDoImp.isValidField(field)) {
         throw new IllegalArgumentException("Not a valid field.");
      } else if (!this.isSupportedField(field)) {
         throw new UnsupportedFieldException();
      }
   }

   protected boolean hasReadPermission() {
      return PIMListImp.hasAccessRights(1, 3, false);
   }

   protected boolean hasWritePermission(boolean toBeCreated) {
      return PIMListImp.hasAccessRights(2, 3, toBeCreated);
   }

   private static native int[] getSupportedFieldsArray();

   private static native int[] getListNamesArray();

   private static native int[] getSupportedFieldLabelsArray();
}
