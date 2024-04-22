package com.nokia.mid.impl.isa.pim;

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
   private static final int[][] SUPPORTED_FIELDS = PBSearchManager.getSupportedFieldsArray();
   private static final int[] SUPPORTED_FIELD_LABELS = getFieldLabelsArray();
   private static final int[] SUPPORTED_ATTRIB_LABELS = getAttributeLabelsArray();
   private static final int[][] SUPPORTED_FIELD_MAX_DATAVALUES = getSupportedFieldMaxValuesArray();
   private static final int[][][] SUPPORTED_ATTRIBS_FOR_FIELD = getSupportedAttributesArray();
   private static final int[][][] SUPPORTED_ARRAY_ELEMENTS = getSupportedArrayElementsArray();
   private static final int[][] SUPPORTED_ARRAY_ELEMENT_LABELS = getArrayElementLabelsArray();
   static final int[] LIST_NAMES = getListNamesArray();
   private int listType = -1;

   public ContactListImp(int mode, String namedList) throws PIMException {
      super(mode);
      if (namedList != null) {
         for(int i = 0; i < LIST_NAMES.length; ++i) {
            if (PIMTextDatabase.getText(LIST_NAMES[i]).equals(namedList)) {
               this.listType = i;
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

   protected int ignoreInvalidAttributes(int field, int attributes) {
      int[] fieldAttributes = this.getSupportedAttributes(field);
      int mask = 0;

      for(int i = 0; i < fieldAttributes.length; ++i) {
         mask |= fieldAttributes[i];
      }

      return attributes & mask;
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

   protected boolean hasWritePermission(boolean toBeCreated) {
      return PIMListImp.hasAccessRights(2, 1, toBeCreated);
   }

   public String getName() {
      return PIMTextDatabase.getText(LIST_NAMES[this.listType]);
   }

   public Enumeration itemsByCategory(String category) throws PIMException {
      this.checkListOpen();
      if (this.getMode() != 2 && this.hasReadPermission()) {
         return new SearchResult(PBSearchManager.getInstance(), category, true, this);
      } else {
         throw new SecurityException("Read permission denied");
      }
   }

   public String[] getCategories() throws PIMException {
      this.checkListOpen();
      return this.getSearchManager().categories();
   }

   public boolean isCategory(String category) throws PIMException {
      if (category == null) {
         throw new NullPointerException();
      } else {
         this.checkListOpen();
         PBSearchManager sm = (PBSearchManager)this.getSearchManager();
         return sm.isValidCategory(category);
      }
   }

   public void renameCategory(String currentCategory, String newCategory) throws PIMException {
      this.checkListOpen();
      if (this.getMode() != 2 && this.getMode() != 3) {
         throw new SecurityException("List not opened for writing.");
      } else if (!this.hasWritePermission(false)) {
         throw new SecurityException("List does not have write permissions for renaming categories.");
      } else {
         if (this.isCategory(newCategory)) {
            if (!currentCategory.equals(newCategory)) {
               Enumeration oldCatItems = this.itemsByCategory(currentCategory);

               while(oldCatItems.hasMoreElements()) {
                  Contact c = (Contact)oldCatItems.nextElement();
                  c.removeFromCategory(currentCategory);
                  c.addToCategory(newCategory);
                  c.commit();
               }

               this.deleteCategory(currentCategory, false);
            }
         } else {
            this.getSearchManager().renameCategory(currentCategory, newCategory);
         }

      }
   }

   public void addCategory(String category) throws PIMException {
      this.checkListOpen();
      if (category == null) {
         throw new NullPointerException();
      } else if (this.getMode() != 1 && this.hasWritePermission(false)) {
         PBSearchManager sm = (PBSearchManager)this.getSearchManager();
         if (!sm.isValidCategory(category)) {
            sm.addCategory(category);
         }

      } else {
         throw new SecurityException("No permission to write to the list.");
      }
   }

   public void deleteCategory(String category, boolean deleteUnassignedItems) throws PIMException {
      this.checkListOpen();
      if (category == null) {
         throw new NullPointerException();
      } else if (this.getMode() != 1 && this.hasWritePermission(false)) {
         PBSearchManager sm = (PBSearchManager)this.getSearchManager();
         if (sm.isValidCategory(category)) {
            boolean bSuperGroupsImplemented = PBSearchManager.getNativeValue(3) != 0;
            if (bSuperGroupsImplemented && deleteUnassignedItems) {
               this.deleteUnassignedItems(category);
            }

            sm.deleteCategory(category);
         }

      } else {
         throw new SecurityException("No permission to write to the list.");
      }
   }

   private void deleteUnassignedItems(String category) throws PIMException {
      SearchResult e = new SearchResult(PBSearchManager.getInstance(), category, true, this);

      while(e.hasMoreElements()) {
         Contact item = (Contact)e.nextElement();
         item.removeFromCategory(category);
         if (item.getCategories().length == 0) {
            ((ContactList)item.getPIMList()).removeContact(item);
         } else {
            item.commit();
         }
      }

   }

   public int maxCategories() {
      return MAX_SUPPORTED_CATEGORIES;
   }

   public boolean isSupportedField(int field) {
      return this.getFieldIndex(field) != -1;
   }

   public int[] getSupportedFields() {
      int[] supportedFieldsArray = SUPPORTED_FIELDS[this.listType];
      int numberOfFields = supportedFieldsArray.length;
      int[] supportedFieldsCopy = new int[numberOfFields];
      System.arraycopy(supportedFieldsArray, 0, supportedFieldsCopy, 0, numberOfFields);
      return supportedFieldsCopy;
   }

   public boolean isSupportedAttribute(int field, int attribute) {
      int index = this.getFieldIndex(field);
      if (index != -1) {
         int[] supportedAttributes = SUPPORTED_ATTRIBS_FOR_FIELD[this.listType][index];
         if (supportedAttributes != null) {
            for(int j = 0; j < supportedAttributes.length; ++j) {
               if (supportedAttributes[j] == attribute) {
                  return true;
               }
            }
         }

         if (attribute == 0) {
            return true;
         }
      }

      return false;
   }

   public int[] getSupportedAttributes(int field) {
      int index = this.getFieldIndex(field);
      if (index != -1) {
         int[] supportedFieldAttrs = SUPPORTED_ATTRIBS_FOR_FIELD[this.listType][index];
         if (supportedFieldAttrs == null) {
            return new int[1];
         } else {
            int[] supportedAttributesCopy = new int[supportedFieldAttrs.length + 1];
            System.arraycopy(supportedFieldAttrs, 0, supportedAttributesCopy, 0, supportedFieldAttrs.length);
            return supportedAttributesCopy;
         }
      } else if (!ContactImp.isValidField(field)) {
         throw new IllegalArgumentException("Invalid field.");
      } else {
         throw new UnsupportedFieldException((String)null, field);
      }
   }

   public boolean isSupportedArrayElement(int stringArrayField, int arrayElement) {
      int index = this.getFieldIndex(stringArrayField);
      if (index != -1) {
         int[] arrayElements = SUPPORTED_ARRAY_ELEMENTS[this.listType][index];
         if (arrayElements != null) {
            for(int i = 0; i < arrayElements.length; ++i) {
               if (arrayElements[i] == arrayElement) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public int[] getSupportedArrayElements(int stringArrayField) {
      int index = this.getFieldIndex(stringArrayField);
      if (index != -1) {
         int[] arrayElements = SUPPORTED_ARRAY_ELEMENTS[this.listType][index];
         if (arrayElements != null) {
            int length = arrayElements.length;
            int[] arrayElementsCopy = new int[length];
            System.arraycopy(arrayElements, 0, arrayElementsCopy, 0, length);
            return arrayElementsCopy;
         }
      }

      if (this.getFieldDataType(stringArrayField) != 5) {
         throw new IllegalArgumentException("The fields data type is not STRING_ARRAY");
      } else {
         return new int[0];
      }
   }

   public int getFieldDataType(int field) {
      if (this.isSupportedField(field)) {
         if (field != 100 && field != 106) {
            if (field == 101) {
               return 2;
            } else {
               return field == 110 ? 0 : 4;
            }
         } else {
            return 5;
         }
      } else if (!ContactImp.isValidField(field)) {
         throw new IllegalArgumentException("The field supplied is invalid");
      } else {
         throw new UnsupportedFieldException((String)null, field);
      }
   }

   public String getFieldLabel(int field) {
      int index = this.getFieldIndex(field);
      if (index != -1) {
         return PIMTextDatabase.getText(SUPPORTED_FIELD_LABELS[index]);
      } else if (!ContactImp.isValidField(field)) {
         throw new IllegalArgumentException("The field supplied is invalid");
      } else {
         throw new UnsupportedFieldException((String)null, field);
      }
   }

   public String getAttributeLabel(int attribute) {
      if (attribute == 0) {
         return "-";
      } else {
         int index = this.getFieldIndex(115);
         if (!ContactImp.isValidAttribute(attribute)) {
            throw new IllegalArgumentException("Invalid Attribute");
         } else {
            if (index != -1) {
               int[] supportedAttributes = SUPPORTED_ATTRIBS_FOR_FIELD[this.listType][index];
               if (supportedAttributes != null) {
                  for(int i = 0; i < supportedAttributes.length; ++i) {
                     if (supportedAttributes[i] == attribute) {
                        return PIMTextDatabase.getText(SUPPORTED_ATTRIB_LABELS[i]);
                     }
                  }
               }
            }

            throw new UnsupportedFieldException("Unsupported Attribute", attribute);
         }
      }
   }

   public String getArrayElementLabel(int stringArrayField, int arrayElement) {
      int index = this.getFieldIndex(stringArrayField);
      if (index != -1) {
         int[] arrayElements = SUPPORTED_ARRAY_ELEMENTS[this.listType][index];
         if (arrayElements != null) {
            for(int i = 0; i < arrayElements.length; ++i) {
               if (arrayElements[i] == arrayElement) {
                  return PIMTextDatabase.getText(SUPPORTED_ARRAY_ELEMENT_LABELS[index][i]);
               }
            }
         }
      }

      if (this.getFieldDataType(stringArrayField) == 5 && ContactImp.isValidStringArrayElement(stringArrayField, arrayElement)) {
         throw new UnsupportedFieldException("Field Array element unsupported.");
      } else {
         throw new IllegalArgumentException("Invalid field or string array element");
      }
   }

   public int maxValues(int field) {
      int index = this.getFieldIndex(field);
      if (index != -1) {
         return SUPPORTED_FIELD_MAX_DATAVALUES[this.listType][index];
      } else if (!ContactImp.isValidField(field)) {
         throw new IllegalArgumentException("Field is not valid");
      } else {
         return 0;
      }
   }

   public int stringArraySize(int stringArrayField) {
      if (!ContactImp.isValidField(stringArrayField)) {
         throw new IllegalArgumentException("Invalid field");
      } else if (this.getFieldDataType(stringArrayField) != 5) {
         throw new IllegalArgumentException("Not a valid string array field");
      } else if (stringArrayField == 100) {
         return 7;
      } else if (stringArrayField == 106) {
         return 5;
      } else {
         throw new RuntimeException("invalid field, array element combinations");
      }
   }

   public Contact createContact() {
      return new ContactImp(this);
   }

   public Contact importContact(Contact contact) {
      if (contact == null) {
         throw new NullPointerException();
      } else {
         return !super.open ? null : new ContactImp(this, (ContactImp)contact);
      }
   }

   public void removeContact(Contact contact) throws PIMException {
      this.removeItem(contact);
      if (((ContactImp)contact).getLocationIndex() == -1) {
         throw new PIMException("Cannot remove a contact when it has not been committed or retrieved from the native database.");
      } else {
         this.getSearchManager().removeItem(contact);
         ((ContactImp)contact).removeFromList();
      }
   }

   void validateField(int field) {
      if (!ContactImp.isValidField(field)) {
         throw new IllegalArgumentException("Invalid field.");
      } else if (!this.isSupportedField(field)) {
         throw new UnsupportedFieldException((String)null, field);
      }
   }

   int getFieldIndex(int field) {
      for(int i = 0; i < SUPPORTED_FIELDS[this.listType].length; ++i) {
         if (SUPPORTED_FIELDS[this.listType][i] == field) {
            return i;
         }
      }

      return -1;
   }

   int getListType() {
      return this.listType;
   }

   private static native int[][] getSupportedFieldMaxValuesArray();

   private static native int[][][] getSupportedAttributesArray();

   private static native int[][][] getSupportedArrayElementsArray();

   private static native int[] getFieldLabelsArray();

   private static native int[][] getArrayElementLabelsArray();

   private static native int[] getAttributeLabelsArray();

   private static native int[] getListNamesArray();
}
