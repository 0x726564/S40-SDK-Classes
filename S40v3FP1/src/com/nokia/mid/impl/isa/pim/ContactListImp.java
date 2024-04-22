package com.nokia.mid.impl.isa.pim;

import com.nokia.mid.pri.PriAccess;
import java.util.Enumeration;
import javax.microedition.pim.Contact;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.UnsupportedFieldException;

class ContactListImp extends PIMListImp implements ContactList {
   public static final int CONTACT_LIST_PHONE = 0;
   public static final int CONTACT_LIST_SIM = 1;
   public static final int CONTACT_LIST_DEFAULT = 0;
   protected static final int MAX_SUPPORTED_CATEGORIES = PBSearchManager.getNativeValue(1);
   private static final int[][] SUPPORTED_FIELDS = getSupportedFieldsArray();
   private static final int[] SUPPORTED_FIELD_LABELS = getFieldLabelsArray();
   private static final int[] SUPPORTED_ATTRIB_LABELS = getAttributeLabelsArray();
   private static final int[][] SUPPORTED_FIELD_MAX_DATAVALUES = getSupportedFieldMaxValuesArray();
   private static final int[][][] SUPPORTED_ATTRIBS_FOR_FIELD = getSupportedAttributesArray();
   private static final int[][][] SUPPORTED_ARRAY_ELEMENTS = getSupportedArrayElementsArray();
   private static final int[][] SUPPORTED_ARRAY_ELEMENT_LABELS = getArrayElementLabelsArray();
   static final int[] LIST_NAMES = getListNamesArray();
   private static final int[] MAP_ARRAY = new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 16777216};
   private int listType = -1;

   public ContactListImp(int var1, String var2) throws PIMException {
      super(var1);
      if (var2 != null) {
         for(int var3 = 0; var3 < LIST_NAMES.length; ++var3) {
            if (PIMTextDatabase.getText(LIST_NAMES[var3]).equals(var2)) {
               this.listType = var3;
               break;
            }
         }
      }

      if (this.listType == -1) {
         throw new PIMException("Invalid name", 1);
      }
   }

   protected void checkListOpen() throws PIMException {
      if (!super.open) {
         throw new PIMException("List closed.", 2);
      }
   }

   protected int ignoreInvalidAttributes(int var1, int var2) {
      int[] var3 = this.getSupportedAttributes(var1);
      int var4 = 0;

      for(int var5 = 0; var5 < var3.length; ++var5) {
         var4 |= var3[var5];
      }

      return var2 & var4;
   }

   SearchManager getSearchManager() {
      return PBSearchManager.getInstance();
   }

   protected int getListNameAsIndex() {
      return this.listType;
   }

   protected boolean hasReadPermission() {
      return PIMListImp.hasAccessRights(1, 1, false);
   }

   protected boolean hasWritePermission(boolean var1) {
      return PIMListImp.hasAccessRights(2, 1, var1);
   }

   public String getName() {
      return PIMTextDatabase.getText(LIST_NAMES[this.listType]);
   }

   public Enumeration itemsByCategory(String var1) throws PIMException {
      this.checkListOpen();
      if (this.getMode() != 2 && this.hasReadPermission()) {
         return new SearchResult(PBSearchManager.getInstance(), var1, true, this);
      } else {
         throw new SecurityException("Read permission denied");
      }
   }

   public String[] getCategories() throws PIMException {
      this.checkListOpen();
      return this.getSearchManager().categories();
   }

   public boolean isCategory(String var1) throws PIMException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.checkListOpen();
         PBSearchManager var2 = (PBSearchManager)this.getSearchManager();
         return var2.isValidCategory(var1);
      }
   }

   public void renameCategory(String var1, String var2) throws PIMException {
      this.checkListOpen();
      if (this.getMode() != 2 && this.getMode() != 3) {
         throw new SecurityException("List not opened for writing.");
      } else if (!this.hasWritePermission(false)) {
         throw new SecurityException("List does not have write permissions for renaming categories.");
      } else {
         if (this.isCategory(var2)) {
            if (!var1.equals(var2)) {
               Enumeration var3 = this.itemsByCategory(var1);

               while(var3.hasMoreElements()) {
                  Contact var4 = (Contact)var3.nextElement();
                  var4.removeFromCategory(var1);
                  var4.addToCategory(var2);
                  var4.commit();
               }

               this.deleteCategory(var1, false);
            }
         } else {
            this.getSearchManager().renameCategory(var1, var2);
         }

      }
   }

   public void addCategory(String var1) throws PIMException {
      this.checkListOpen();
      if (var1 == null) {
         throw new NullPointerException();
      } else if (this.getMode() != 1 && this.hasWritePermission(false)) {
         PBSearchManager var2 = (PBSearchManager)this.getSearchManager();
         if (!var2.isValidCategory(var1)) {
            var2.addCategory(var1);
         }

      } else {
         throw new SecurityException("No permission to write to the list.");
      }
   }

   public void deleteCategory(String var1, boolean var2) throws PIMException {
      this.checkListOpen();
      if (var1 == null) {
         throw new NullPointerException();
      } else if (this.getMode() != 1 && this.hasWritePermission(false)) {
         PBSearchManager var3 = (PBSearchManager)this.getSearchManager();
         if (var3.isValidCategory(var1)) {
            boolean var4 = PBSearchManager.getNativeValue(3) != 0;
            if (var4 && var2) {
               this.deleteUnassignedItems(var1);
            }

            var3.deleteCategory(var1);
         }

      } else {
         throw new SecurityException("No permission to write to the list.");
      }
   }

   private void deleteUnassignedItems(String var1) throws PIMException {
      SearchResult var2 = new SearchResult(PBSearchManager.getInstance(), var1, true, this);

      while(var2.hasMoreElements()) {
         Contact var3 = (Contact)var2.nextElement();
         var3.removeFromCategory(var1);
         if (var3.getCategories().length == 0) {
            ((ContactList)var3.getPIMList()).removeContact(var3);
         } else {
            var3.commit();
         }
      }

   }

   public int maxCategories() {
      return MAX_SUPPORTED_CATEGORIES;
   }

   public boolean isSupportedField(int var1) {
      return this.getFieldIndex(var1) != -1;
   }

   public int[] getSupportedFields() {
      int[] var1 = SUPPORTED_FIELDS[this.listType];
      int var2 = var1.length;
      int[] var3 = new int[var2];
      System.arraycopy(var1, 0, var3, 0, var2);
      return var3;
   }

   public boolean isSupportedAttribute(int var1, int var2) {
      int var3 = this.getFieldIndex(var1);
      if (var3 != -1) {
         int[] var4 = SUPPORTED_ATTRIBS_FOR_FIELD[this.listType][var3];
         if (var4 != null) {
            for(int var5 = 0; var5 < var4.length; ++var5) {
               if (var4[var5] == var2) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public int[] getSupportedAttributes(int var1) {
      int var2 = this.getFieldIndex(var1);
      if (var2 != -1) {
         int[] var3 = SUPPORTED_ATTRIBS_FOR_FIELD[this.listType][var2];
         if (var3 == null) {
            return new int[0];
         } else {
            int[] var4 = new int[var3.length];
            System.arraycopy(var3, 0, var4, 0, var4.length);
            return var4;
         }
      } else if (!ContactImp.isValidField(var1)) {
         throw new IllegalArgumentException("Invalid field.");
      } else {
         throw new UnsupportedFieldException((String)null, var1);
      }
   }

   public boolean isSupportedArrayElement(int var1, int var2) {
      int var3 = this.getFieldIndex(var1);
      if (var3 != -1) {
         int[] var4 = SUPPORTED_ARRAY_ELEMENTS[this.listType][var3];
         if (var4 != null) {
            for(int var5 = 0; var5 < var4.length; ++var5) {
               if (var4[var5] == var2) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public int[] getSupportedArrayElements(int var1) {
      int var2 = this.getFieldIndex(var1);
      if (var2 != -1) {
         int[] var3 = SUPPORTED_ARRAY_ELEMENTS[this.listType][var2];
         if (var3 != null) {
            int var4 = var3.length;
            int[] var5 = new int[var4];
            System.arraycopy(var3, 0, var5, 0, var4);
            return var5;
         }
      }

      if (this.getFieldDataType(var1) != 5) {
         throw new IllegalArgumentException("The fields data type is not STRING_ARRAY");
      } else {
         return new int[0];
      }
   }

   public int getFieldDataType(int var1) {
      if (this.isSupportedField(var1)) {
         if (var1 != 100 && var1 != 106) {
            if (var1 == 101) {
               return 2;
            } else {
               return PriAccess.getInt(5) == 1 && var1 == 110 ? 0 : 4;
            }
         } else {
            return 5;
         }
      } else if (!ContactImp.isValidField(var1)) {
         throw new IllegalArgumentException("The field supplied is invalid");
      } else {
         throw new UnsupportedFieldException((String)null, var1);
      }
   }

   public String getFieldLabel(int var1) {
      int var2 = this.getFieldIndex(var1);
      if (var2 != -1) {
         return PIMTextDatabase.getText(SUPPORTED_FIELD_LABELS[var2]);
      } else if (!ContactImp.isValidField(var1)) {
         throw new IllegalArgumentException("The field supplied is invalid");
      } else {
         throw new UnsupportedFieldException((String)null, var1);
      }
   }

   public String getAttributeLabel(int var1) {
      if (!ContactImp.isValidAttribute(var1)) {
         throw new IllegalArgumentException("Invalid Attribute");
      } else {
         int var2 = 0;

         for(int var3 = 0; var3 < MAP_ARRAY.length && var1 != MAP_ARRAY[var3]; ++var3) {
            ++var2;
         }

         if (var2 < SUPPORTED_ATTRIB_LABELS.length && SUPPORTED_ATTRIB_LABELS[var2] != -1) {
            return PIMTextDatabase.getText(SUPPORTED_ATTRIB_LABELS[var2]);
         } else {
            throw new UnsupportedFieldException("Unsupported Attribute", var1);
         }
      }
   }

   public String getArrayElementLabel(int var1, int var2) {
      int var3 = this.getFieldIndex(var1);
      if (var3 != -1) {
         int[] var4 = SUPPORTED_ARRAY_ELEMENTS[this.listType][var3];
         if (var4 != null) {
            for(int var5 = 0; var5 < var4.length; ++var5) {
               if (var4[var5] == var2) {
                  return PIMTextDatabase.getText(SUPPORTED_ARRAY_ELEMENT_LABELS[var3][var5]);
               }
            }
         }
      }

      if (this.getFieldDataType(var1) == 5 && ContactImp.isValidStringArrayElement(var1, var2)) {
         throw new UnsupportedFieldException("Field Array element unsupported.");
      } else {
         throw new IllegalArgumentException("Invalid field or string array element");
      }
   }

   public int maxValues(int var1) {
      int var2 = this.getFieldIndex(var1);
      if (var2 != -1) {
         return SUPPORTED_FIELD_MAX_DATAVALUES[this.listType][var2];
      } else if (!ContactImp.isValidField(var1)) {
         throw new IllegalArgumentException("Field is not valid");
      } else {
         return 0;
      }
   }

   public int stringArraySize(int var1) {
      if (!ContactImp.isValidField(var1)) {
         throw new IllegalArgumentException("Invalid field");
      } else if (this.getFieldDataType(var1) != 5) {
         throw new IllegalArgumentException("Not a valid string array field");
      } else if (var1 == 100) {
         return 7;
      } else if (var1 == 106) {
         return 5;
      } else {
         throw new RuntimeException("invalid field, array element combinations");
      }
   }

   public Contact createContact() {
      return new ContactImp(this);
   }

   public Contact importContact(Contact var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return !super.open ? null : new ContactImp(this, (ContactImp)var1);
      }
   }

   public void removeContact(Contact var1) throws PIMException {
      this.removeItem(var1);
      if (((ContactImp)var1).getLocationIndex() == -1) {
         throw new PIMException("Cannot remove a contact when it has not been committed or retrieved from the native database.");
      } else {
         this.getSearchManager().removeItem(var1);
         ((ContactImp)var1).removeFromList();
      }
   }

   void validateField(int var1) {
      if (!ContactImp.isValidField(var1)) {
         throw new IllegalArgumentException("Invalid field.");
      } else if (!this.isSupportedField(var1)) {
         throw new UnsupportedFieldException((String)null, var1);
      }
   }

   int getFieldIndex(int var1) {
      for(int var2 = 0; var2 < SUPPORTED_FIELDS[this.listType].length; ++var2) {
         if (SUPPORTED_FIELDS[this.listType][var2] == var1) {
            return var2;
         }
      }

      return -1;
   }

   int getListType() {
      return this.listType;
   }

   private static native int[][] getSupportedFieldsArray();

   private static native int[][] getSupportedFieldMaxValuesArray();

   private static native int[][][] getSupportedAttributesArray();

   private static native int[][][] getSupportedArrayElementsArray();

   private static native int[] getFieldLabelsArray();

   private static native int[][] getArrayElementLabelsArray();

   private static native int[] getAttributeLabelsArray();

   private static native int[] getListNamesArray();
}
