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

   ToDoListImp(int var1, String var2) throws PIMException {
      super(var1);
      if (!var2.equals(PIMTextDatabase.getText(LIST_NAMES[0]))) {
         throw new PIMException("Invalid name", 1);
      }
   }

   public String getName() {
      return PIMTextDatabase.getText(LIST_NAMES[0]);
   }

   public boolean isSupportedField(int var1) {
      for(int var2 = 0; var2 < SUPPORTED_FIELDS.length; ++var2) {
         if (SUPPORTED_FIELDS[var2] == var1) {
            return true;
         }
      }

      return false;
   }

   public int[] getSupportedFields() {
      int[] var1 = new int[SUPPORTED_FIELDS.length];
      System.arraycopy(SUPPORTED_FIELDS, 0, var1, 0, SUPPORTED_FIELDS.length);
      return var1;
   }

   public int getFieldDataType(int var1) {
      this.validateField(var1);
      switch(var1) {
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

   public String getFieldLabel(int var1) {
      if (ToDoImp.isValidField(var1)) {
         if (var1 == 108) {
            return "";
         } else {
            for(int var2 = 1; var2 < SUPPORTED_FIELDS.length; ++var2) {
               if (SUPPORTED_FIELDS[var2] == var1) {
                  return PIMTextDatabase.getText(FIELDS_LABELS[var2 - 1]);
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

   public ToDo importToDo(ToDo var1) {
      ToDoImp var2 = new ToDoImp(this);
      var2.copyData(var1);
      return var2;
   }

   public void removeToDo(ToDo var1) throws PIMException {
      this.removeItem(var1);
      CalendarSearchManager.getInstance().removeItem(var1);
      ((ToDoImp)var1).removeFromList();
   }

   public Enumeration items(int var1, long var2, long var4) throws PIMException {
      if (!this.open) {
         throw new PIMException("List closed!", 2);
      } else {
         this.validateField(var1);
         if ((var1 == 102 || var1 == 103) && var4 > var2) {
            if (this.getMode() != 2 && this.hasReadPermission()) {
               return new SearchResult(this.getSearchManager(), var1, var2, var4, this);
            } else {
               throw new SecurityException("Read permission denied");
            }
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   public int[] getSupportedRepeatRuleFields(int var1) {
      throw new RuntimeException("Not implemented");
   }

   SearchManager getSearchManager() {
      return CalendarSearchManager.getInstance();
   }

   void validateField(int var1) {
      if (!ToDoImp.isValidField(var1)) {
         throw new IllegalArgumentException("Not a valid field.");
      } else if (!this.isSupportedField(var1)) {
         throw new UnsupportedFieldException();
      }
   }

   protected boolean hasReadPermission() {
      return PIMListImp.hasAccessRights(1, 3, false);
   }

   protected boolean hasWritePermission(boolean var1) {
      return PIMListImp.hasAccessRights(2, 3, var1);
   }

   private static native int[] getSupportedFieldsArray();

   private static native int[] getListNamesArray();

   private static native int[] getSupportedFieldLabelsArray();
}
