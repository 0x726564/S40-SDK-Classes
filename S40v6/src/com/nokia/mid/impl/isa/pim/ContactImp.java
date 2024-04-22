package com.nokia.mid.impl.isa.pim;

import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.pim.Contact;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.FieldFullException;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;

class ContactImp extends PIMItemImp implements Contact {
   private static final int CATEGORYID = 12;
   protected static final int NOT_MODIFIED = 0;
   protected static final int NUMBER_OF_CONTACT_CATEGORIES_ALLOWED = PBSearchManager.getNativeValue(2);
   protected static final int NAMED_LIST_PHONE = 0;
   protected static final int NAMED_LIST_SIM = 1;
   protected static final int NAMED_LIST_NONE = -1;
   protected static final int NAMED_LIST_CATEGORIES = 2;
   protected static final int CONTACT_ADDR_SA_SIZE = 7;
   protected static final int CONTACT_NAME_SA_SIZE = 5;
   private static final int[] VALID_FIELDS = new int[]{100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 16777216};
   private static final int[] VALID_ATTRIBUTES = new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 16777216, 0};
   public static final int ATTR_VIDEO_CALL = 16777216;
   public static final int VIDEO_URL = 16777216;
   private int[] stringAttributes;
   private PBNativeRecord contactRecord;
   private int namedList;
   private int[] telephoneAttributes;
   private Vector assignedCategories;

   private ContactImp() {
      super((PIMList)null);
      this.contactRecord = null;
      this.namedList = -1;
      this.telephoneAttributes = null;
      this.assignedCategories = new Vector();
      throw new RuntimeException();
   }

   private String formatPhoneNumber(String original) {
      StringBuffer retValue = new StringBuffer();
      int len = original.length();

      for(int i = 0; i < len; ++i) {
         char ch = original.charAt(i);
         if (ch != '-' && ch != '(' && ch != ')') {
            retValue.append(ch);
         }
      }

      return new String(retValue);
   }

   public ContactImp(ContactList contactList) {
      super(contactList);
      this.contactRecord = null;
      this.namedList = -1;
      this.telephoneAttributes = null;
      this.assignedCategories = new Vector();
      if (contactList != null) {
         this.namedList = ((ContactListImp)contactList).getListType();
      }

      this.initialiseContact();
   }

   protected ContactImp(ContactList contactList, ContactImp other) {
      this(contactList);
      this.copyData(other);
      this.modified = other.modified;
      if (other.IsValidItem()) {
         this.setRecord(new PBNativeRecord(other.getRecord()), true);
      }

      try {
         PBSearchManager.getInstance().UpdateContactMessage(this);
      } catch (PIMException var4) {
      }

   }

   protected ContactImp(PBNativeRecord nr) throws PIMException {
      this((ContactList)(new ContactListImp(1, PIMTextDatabase.getText(ContactListImp.LIST_NAMES[0]))));
      this.removeFromList();

      try {
         PBSearchManager.getInstance().setContactRecord(this, nr);
      } catch (PIMException var3) {
      }

      this.setUID(this.getLocationIndex());
   }

   void copyPrivateData(PIMItem item) {
      String[] categories = item.getCategories();

      try {
         for(int i = 0; i < categories.length; ++i) {
            this.addToCategory(categories[i]);
         }
      } catch (PIMException var4) {
      }

   }

   static SearchManager getSearchManager() {
      return PBSearchManager.getInstance();
   }

   static final boolean isValidField(int field) {
      for(int i = 0; i < VALID_FIELDS.length; ++i) {
         if (VALID_FIELDS[i] == field) {
            return true;
         }
      }

      return false;
   }

   static final boolean isValidStringArrayElement(int field, int arrayElement) {
      if (arrayElement < 0) {
         return false;
      } else {
         return field == 100 && arrayElement < 7 || field == 106 && arrayElement < 5;
      }
   }

   protected static final boolean isValidAttribute(int attribute) {
      for(int i = 0; i < VALID_ATTRIBUTES.length; ++i) {
         if (VALID_ATTRIBUTES[i] == attribute) {
            return true;
         }
      }

      return false;
   }

   protected boolean IsValidItem() {
      return this.contactRecord != null && this.contactRecord.IsValidItem();
   }

   protected static final boolean IsValidNamedList(int iNamedList) {
      return iNamedList == 1 || iNamedList == 0;
   }

   protected void resetModified() {
      this.modified = 0;
      this.setUID(this.getLocationIndex());
   }

   protected int getNamedList() {
      return this.namedList;
   }

   protected int getLocationIndex() {
      int id = -1;
      if (this.contactRecord != null) {
         id = this.contactRecord.getLocationIndex();
         if (id == PBNativeRecord.getNativePND_LOCATION_ANY()) {
            id = -1;
         }
      }

      return id;
   }

   protected PBNativeRecord getRecord() {
      return this.contactRecord;
   }

   protected void setRecord(PBNativeRecord contactRecord, boolean modifyUID) {
      this.contactRecord = contactRecord;
      if (modifyUID) {
         this.setUID(this.getLocationIndex());
      }

   }

   protected void removeLocationIndex() {
      this.contactRecord.removeLocationIndex();
      this.setUID(this.getLocationIndex());
   }

   protected void initialiseContact() {
      this.stringAttributes = new int[this.getPIMList().getSupportedFields().length];
      this.contactRecord = null;
      this.assignedCategories.removeAllElements();
      this.telephoneAttributes = null;
      this.modified = 0;
      this.data = new Object[this.list.getSupportedFields().length];
      this.hasBeenCommitted = false;
      this.setRecord(new PBNativeRecord(), false);
   }

   protected boolean IsAssignedToCategory(String category) {
      if (category == PIMList.UNCATEGORIZED) {
         return false;
      } else {
         String assignedCategory = null;
         Enumeration e = this.assignedCategories.elements();

         do {
            if (!e.hasMoreElements()) {
               return false;
            }

            assignedCategory = (String)e.nextElement();
         } while(!assignedCategory.equals(category));

         return true;
      }
   }

   public void removeFromCategory(String category) {
      if (category == null) {
         throw new NullPointerException("Null category specified in removeFromCategory");
      } else {
         for(int i = 0; i < this.assignedCategories.size(); ++i) {
            String currentCategory = (String)this.assignedCategories.elementAt(i);
            if (category.compareTo(currentCategory) == 0) {
               this.assignedCategories.removeElementAt(i);
               this.modified |= 4096;
               break;
            }
         }

      }
   }

   public void commit() throws PIMException {
      if (!this.removedFromList && this.getPIMList() != null) {
         if (!this.hasBeenCommitted) {
            this.setUID(-1);
         }

         this.setDefaultPhoneNumber();
         super.commit();
      } else {
         throw new PIMException("No list is assigned to the PIMItem.", 3);
      }
   }

   public void addToCategory(String category) throws PIMException {
      if (category == null) {
         throw new NullPointerException("Null category string given to addToCategory method.");
      } else if (!this.IsAssignedToCategory(category)) {
         if (this.assignedCategories.size() == NUMBER_OF_CONTACT_CATEGORIES_ALLOWED) {
            throw new PIMException("A Contact can only belong to " + NUMBER_OF_CONTACT_CATEGORIES_ALLOWED + "category.", 4);
         } else if (!PBSearchManager.getInstance().isValidCategory(category)) {
            throw new PIMException("Category specified is not valid", 1);
         } else {
            this.assignedCategories.addElement(category);
            this.modified |= 4096;
         }
      }
   }

   public String[] getCategories() {
      if (this.assignedCategories.size() == 0) {
         return new String[0];
      } else {
         String[] sCategories = new String[this.assignedCategories.size()];
         this.assignedCategories.copyInto(sCategories);
         return sCategories;
      }
   }

   protected void setCategories(String[] newAssignedCategories) throws PIMException {
      this.assignedCategories.removeAllElements();
      if (newAssignedCategories.length > this.maxCategories()) {
         throw new PIMException("Too many native categories found.");
      } else {
         for(int i = 0; i < newAssignedCategories.length; ++i) {
            this.addToCategory(newAssignedCategories[i]);
         }

      }
   }

   public int maxCategories() {
      return NUMBER_OF_CONTACT_CATEGORIES_ALLOWED;
   }

   public int getPreferredIndex(int field) {
      this.checkField(field, 0, super.list.getFieldDataType(field));
      int prefAttrIndex = -1;
      if (field != 115) {
         if (this.countValues(field) != 0 && (this.stringAttributes[((ContactListImp)this.list).getFieldIndex(field)] & 128) != 0) {
            prefAttrIndex = 0;
         }

         return prefAttrIndex;
      } else {
         int prefAttrIndex = 0;

         for(int i = 0; this.telephoneAttributes != null && i < this.telephoneAttributes.length; ++i) {
            if ((this.telephoneAttributes[i] & 128) != 0) {
               prefAttrIndex = i;
               break;
            }
         }

         return prefAttrIndex;
      }
   }

   public String getString(int field, int index) {
      if (field != 115 && field != 103) {
         return super.getString(field, index);
      } else {
         this.checkField(field, 0, 4);
         if (index < super.list.maxValues(field) && index >= 0 && index < this.countValues(field)) {
            return (String)((Object[])this.data[((ContactListImp)this.list).getFieldIndex(field)])[index];
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public void addString(int field, int attributes, String value) {
      attributes = ((ContactListImp)super.list).ignoreInvalidAttributes(field, attributes);
      if (field != 115 && field != 103) {
         super.addString(field, attributes, value);
         this.stringAttributes[((ContactListImp)this.list).getFieldIndex(field)] = attributes;
      } else if (value == null) {
         throw new NullPointerException("The data value string was null");
      } else {
         this.checkField(field, 0, 4);
         if (this.countValues(field) >= this.list.maxValues(field)) {
            throw new FieldFullException();
         } else {
            String formattedValue;
            if (field != 103) {
               formattedValue = this.formatPhoneNumber(value);
            } else {
               formattedValue = value;
            }

            int fieldIndex = ((ContactListImp)this.list).getFieldIndex(field);
            int currentLen = this.countValues(field);
            String[] tel = new String[currentLen + 1];
            if (currentLen > 0) {
               System.arraycopy(this.data[fieldIndex], 0, tel, 0, currentLen);
            }

            tel[currentLen] = formattedValue;
            this.data[fieldIndex] = tel;
            int[] attr = new int[currentLen + 1];
            if (currentLen > 0 && field == 115) {
               System.arraycopy(this.telephoneAttributes, 0, attr, 0, currentLen);
            }

            if (field == 115) {
               attr[currentLen] = attributes;
               this.telephoneAttributes = attr;
            }

            this.modified |= 1 << fieldIndex;
         }
      }
   }

   public void setString(int field, int index, int attributes, String value) {
      attributes = ((ContactListImp)super.list).ignoreInvalidAttributes(field, attributes);
      if (field != 115 && field != 103) {
         super.setString(field, index, attributes, value);
         this.stringAttributes[((ContactListImp)this.list).getFieldIndex(field)] = attributes;
      } else {
         this.checkField(field, 0, 4);
         if (index < this.countValues(field) && index >= 0 && this.countValues(field) != 0) {
            if (value == null) {
               throw new NullPointerException("The data value string was null.");
            } else {
               int fieldIndex = ((ContactListImp)this.list).getFieldIndex(field);
               String[] sa = (String[])this.data[fieldIndex];
               sa[index] = value;
               if (field == 115) {
                  this.telephoneAttributes[index] = attributes;
               }

               this.modified |= 1 << fieldIndex;
            }
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public void addStringArray(int field, int attributes, String[] value) {
      if ((field != 100 || value.length == 7) && (field != 106 || value.length == 5)) {
         super.addStringArray(field, attributes, value);
         this.stringArrayTidyUpChecks(field, attributes, value);
      } else {
         throw new IllegalArgumentException("STRING_ARRAY size is incorrect");
      }
   }

   public void setStringArray(int field, int index, int attributes, String[] value) {
      if ((field != 100 || value.length == 7) && (field != 106 || value.length == 5)) {
         super.setStringArray(field, index, attributes, value);
         this.stringArrayTidyUpChecks(field, attributes, value);
      } else {
         throw new IllegalArgumentException("STRING_ARRAY size is incorrect");
      }
   }

   private void stringArrayTidyUpChecks(int field, int attributes, String[] value) {
      attributes = ((ContactListImp)super.list).ignoreInvalidAttributes(field, attributes);
      this.stringAttributes[((ContactListImp)this.list).getFieldIndex(field)] = attributes;
   }

   public int countValues(int field) {
      if (field != 115 && field != 103) {
         return super.countValues(field);
      } else {
         this.checkField(field, 0, 4);
         Object[] oa = (Object[])this.data[((ContactListImp)this.list).getFieldIndex(field)];
         return oa == null ? 0 : oa.length;
      }
   }

   public void removeValue(int field, int index) {
      if (field != 115 && field != 103) {
         super.removeValue(field, index);
      } else {
         this.checkField(field, 0, 4);
         int values = this.countValues(field);
         if (index < values && index >= 0) {
            int fieldIndex = ((ContactListImp)this.list).getFieldIndex(field);
            String[] destSa = new String[values - 1];
            String[] srcSa = (String[])this.data[fieldIndex];
            int destIndex = 0;

            int destIndex;
            for(destIndex = 0; destIndex < srcSa.length; ++destIndex) {
               if (destIndex != index) {
                  destSa[destIndex] = srcSa[destIndex];
                  ++destIndex;
               }
            }

            this.data[fieldIndex] = destSa;
            if (field == 115) {
               int[] destAttrs = new int[values - 1];
               destIndex = 0;

               for(int srcIndex = 0; srcIndex < this.telephoneAttributes.length; ++srcIndex) {
                  if (srcIndex != index) {
                     destAttrs[destIndex] = this.telephoneAttributes[srcIndex];
                     ++destIndex;
                  }
               }

               this.telephoneAttributes = destAttrs;
            }

            this.modified |= 1 << fieldIndex;
            if (((Object[])this.data[fieldIndex]).length == 0) {
               this.data[fieldIndex] = null;
            }

         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public int getAttributes(int field, int index) {
      int indexToCheck = field != 115 && field != 103 ? index : 0;
      if (field == 115) {
         if (this.telephoneAttributes != null && index < this.telephoneAttributes.length && index >= 0) {
            return this.telephoneAttributes[index];
         } else {
            throw new IndexOutOfBoundsException();
         }
      } else {
         this.checkField(field, indexToCheck, field != 100 && field != 106 ? (field == 101 ? 2 : (field == 110 ? 0 : 4)) : 5);
         return this.stringAttributes[((ContactListImp)this.list).getFieldIndex(field)];
      }
   }

   protected void setDefaultPhoneNumber() {
      int thisField = 115;
      int preferredIndex = this.getPreferredIndex(thisField);
      if (preferredIndex > 0) {
         int fieldIndex = ((ContactListImp)this.list).getFieldIndex(thisField);
         int currentLen = this.countValues(thisField);
         String[] tel = new String[currentLen];
         int[] attr = new int[currentLen];
         System.arraycopy(this.data[fieldIndex], 0, tel, 0, currentLen);
         System.arraycopy(this.telephoneAttributes, 0, attr, 0, currentLen);
         String[] sa = (String[])this.data[fieldIndex];
         tel[0] = sa[preferredIndex];
         attr[0] = this.telephoneAttributes[preferredIndex];

         for(int i = 0; i < preferredIndex; ++i) {
            tel[i + 1] = sa[i];
            attr[i + 1] = this.telephoneAttributes[i];
         }

         this.data[fieldIndex] = tel;
         this.telephoneAttributes = attr;
      }

   }

   byte[] toSerial(String charset, int dataType) {
      try {
         PBSearchManager.getInstance().UpdateContactMessage(this);
      } catch (PIMException var4) {
         return null;
      }

      return this.toVCard(this.getRecord().getMessage(), charset, dataType);
   }

   native byte[] toVCard(byte[] var1, String var2, int var3);
}
