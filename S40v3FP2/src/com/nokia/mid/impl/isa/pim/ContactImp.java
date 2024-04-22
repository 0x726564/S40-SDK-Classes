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
   private static final int[] VALID_FIELDS = new int[]{100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118};
   private static final int[] VALID_ATTRIBUTES = new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 16777216};
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

   private String formatPhoneNumber(String var1) {
      StringBuffer var2 = new StringBuffer();
      int var3 = var1.length();

      for(int var5 = 0; var5 < var3; ++var5) {
         char var4 = var1.charAt(var5);
         if (var4 != '-' && var4 != '(' && var4 != ')') {
            var2.append(var4);
         }
      }

      return new String(var2);
   }

   public ContactImp(ContactList var1) {
      super(var1);
      this.contactRecord = null;
      this.namedList = -1;
      this.telephoneAttributes = null;
      this.assignedCategories = new Vector();
      if (var1 != null) {
         this.namedList = ((ContactListImp)var1).getListType();
      }

      this.initialiseContact();
   }

   protected ContactImp(ContactList var1, ContactImp var2) {
      this(var1);
      this.copyData(var2);
      this.modified = var2.modified;
      if (var2.IsValidItem()) {
         this.setRecord(new PBNativeRecord(var2.getRecord()), true);
      }

      try {
         PBSearchManager.getInstance().UpdateContactMessage(this);
      } catch (PIMException var4) {
      }

   }

   protected ContactImp(PBNativeRecord var1) throws PIMException {
      this((ContactList)(new ContactListImp(1, PIMTextDatabase.getText(ContactListImp.LIST_NAMES[0]))));
      this.removeFromList();

      try {
         PBSearchManager.getInstance().setContactRecord(this, var1);
      } catch (PIMException var3) {
      }

      this.setUID(this.getLocationIndex());
   }

   void copyPrivateData(PIMItem var1) {
      String[] var2 = var1.getCategories();

      try {
         for(int var3 = 0; var3 < var2.length; ++var3) {
            this.addToCategory(var2[var3]);
         }
      } catch (PIMException var4) {
      }

   }

   static SearchManager getSearchManager() {
      return PBSearchManager.getInstance();
   }

   static final boolean isValidField(int var0) {
      for(int var1 = 0; var1 < VALID_FIELDS.length; ++var1) {
         if (VALID_FIELDS[var1] == var0) {
            return true;
         }
      }

      return false;
   }

   static final boolean isValidStringArrayElement(int var0, int var1) {
      if (var1 < 0) {
         return false;
      } else {
         return var0 == 100 && var1 < 7 || var0 == 106 && var1 < 5;
      }
   }

   protected static final boolean isValidAttribute(int var0) {
      for(int var1 = 0; var1 < VALID_ATTRIBUTES.length; ++var1) {
         if (VALID_ATTRIBUTES[var1] == var0) {
            return true;
         }
      }

      return false;
   }

   protected boolean IsValidItem() {
      return this.contactRecord != null && this.contactRecord.IsValidItem();
   }

   protected static final boolean IsValidNamedList(int var0) {
      return var0 == 1 || var0 == 0;
   }

   protected void resetModified() {
      this.modified = 0;
      this.setUID(this.getLocationIndex());
   }

   protected int getNamedList() {
      return this.namedList;
   }

   protected int getLocationIndex() {
      int var1 = -1;
      if (this.contactRecord != null) {
         var1 = this.contactRecord.getLocationIndex();
         if (var1 == 65535) {
            var1 = -1;
         }
      }

      return var1;
   }

   protected PBNativeRecord getRecord() {
      return this.contactRecord;
   }

   protected void setRecord(PBNativeRecord var1, boolean var2) {
      this.contactRecord = var1;
      if (var2) {
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

   protected boolean IsAssignedToCategory(String var1) {
      if (var1 == PIMList.UNCATEGORIZED) {
         return false;
      } else {
         String var2 = null;
         Enumeration var3 = this.assignedCategories.elements();

         do {
            if (!var3.hasMoreElements()) {
               return false;
            }

            var2 = (String)var3.nextElement();
         } while(!var2.equals(var1));

         return true;
      }
   }

   public void removeFromCategory(String var1) {
      if (var1 == null) {
         throw new NullPointerException("Null category specified in removeFromCategory");
      } else {
         for(int var2 = 0; var2 < this.assignedCategories.size(); ++var2) {
            String var3 = (String)this.assignedCategories.elementAt(var2);
            if (var1.compareTo(var3) == 0) {
               this.assignedCategories.removeElementAt(var2);
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

         super.commit();
      } else {
         throw new PIMException("No list is assigned to the PIMItem.", 3);
      }
   }

   public void addToCategory(String var1) throws PIMException {
      if (var1 == null) {
         throw new NullPointerException("Null category string given to addToCategory method.");
      } else if (!this.IsAssignedToCategory(var1)) {
         if (this.assignedCategories.size() == NUMBER_OF_CONTACT_CATEGORIES_ALLOWED) {
            throw new PIMException("A Contact can only belong to " + NUMBER_OF_CONTACT_CATEGORIES_ALLOWED + "category.", 4);
         } else if (!PBSearchManager.getInstance().isValidCategory(var1)) {
            throw new PIMException("Category specified is not valid", 1);
         } else {
            this.assignedCategories.addElement(var1);
            this.modified |= 4096;
         }
      }
   }

   public String[] getCategories() {
      if (this.assignedCategories.size() == 0) {
         return new String[0];
      } else {
         String[] var1 = new String[this.assignedCategories.size()];
         this.assignedCategories.copyInto(var1);
         return var1;
      }
   }

   protected void setCategories(String[] var1) throws PIMException {
      this.assignedCategories.removeAllElements();
      if (var1.length > this.maxCategories()) {
         throw new PIMException("Too many native categories found.");
      } else {
         for(int var2 = 0; var2 < var1.length; ++var2) {
            this.addToCategory(var1[var2]);
         }

      }
   }

   public int maxCategories() {
      return NUMBER_OF_CONTACT_CATEGORIES_ALLOWED;
   }

   public int getPreferredIndex(int var1) {
      this.checkField(var1, 0, var1 != 100 && var1 != 106 ? (var1 == 101 ? 2 : 4) : 5);
      int var2 = -1;
      if (var1 != 115) {
         if (this.countValues(var1) != 0 && (this.stringAttributes[((ContactListImp)this.list).getFieldIndex(var1)] & 128) != 0) {
            var2 = 0;
         }

         return var2;
      } else {
         for(int var3 = 0; this.telephoneAttributes != null && var3 < this.telephoneAttributes.length; ++var3) {
            if ((this.telephoneAttributes[var3] & 128) != 0) {
               var2 = var3;
               break;
            }
         }

         return var2;
      }
   }

   public String getString(int var1, int var2) {
      if (var1 != 115 && var1 != 103) {
         return super.getString(var1, var2);
      } else {
         this.checkField(var1, 0, 4);
         if (var2 < super.list.maxValues(var1) && var2 >= 0 && var2 < this.countValues(var1)) {
            return (String)((Object[])this.data[((ContactListImp)this.list).getFieldIndex(var1)])[var2];
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public void addString(int var1, int var2, String var3) {
      var2 = ((ContactListImp)super.list).ignoreInvalidAttributes(var1, var2);
      if (var1 != 115 && var1 != 103) {
         super.addString(var1, var2, var3);
         this.stringAttributes[((ContactListImp)this.list).getFieldIndex(var1)] = var2;
      } else {
         this.checkField(var1, 0, 4);
         if (this.countValues(var1) >= this.list.maxValues(var1)) {
            throw new FieldFullException();
         } else if (var3 == null) {
            throw new NullPointerException("The data value string was null.");
         } else {
            String var4 = this.formatPhoneNumber(var3);
            int var5 = ((ContactListImp)this.list).getFieldIndex(var1);
            int var6 = this.countValues(var1);
            String[] var7 = new String[var6 + 1];
            if (var6 > 0) {
               System.arraycopy(this.data[var5], 0, var7, 0, var6);
            }

            var7[var6] = var4;
            this.data[var5] = var7;
            int[] var8 = new int[var6 + 1];
            if (var6 > 0 && var1 == 115) {
               System.arraycopy(this.telephoneAttributes, 0, var8, 0, var6);
            }

            if (var1 == 115) {
               var8[var6] = var2;
               this.telephoneAttributes = var8;
            }

            this.modified |= 1 << var5;
         }
      }
   }

   public void setString(int var1, int var2, int var3, String var4) {
      var3 = ((ContactListImp)super.list).ignoreInvalidAttributes(var1, var3);
      if (var1 != 115 && var1 != 103) {
         super.setString(var1, var2, var3, var4);
         this.stringAttributes[((ContactListImp)this.list).getFieldIndex(var1)] = var3;
      } else {
         this.checkField(var1, 0, 4);
         if (var2 < this.countValues(var1) && var2 >= 0 && this.countValues(var1) != 0) {
            if (var4 == null) {
               throw new NullPointerException("The data value string was null.");
            } else {
               int var5 = ((ContactListImp)this.list).getFieldIndex(var1);
               String[] var6 = (String[])this.data[var5];
               var6[var2] = var4;
               if (var1 == 115) {
                  this.telephoneAttributes[var2] = var3;
               }

               this.modified |= 1 << var5;
            }
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public void addStringArray(int var1, int var2, String[] var3) {
      if ((var1 != 100 || var3.length == 7) && (var1 != 106 || var3.length == 5)) {
         super.addStringArray(var1, var2, var3);
         this.stringArrayTidyUpChecks(var1, var2, var3);
      } else {
         throw new IllegalArgumentException("STRING_ARRAY size is incorrect");
      }
   }

   public void setStringArray(int var1, int var2, int var3, String[] var4) {
      if ((var1 != 100 || var4.length == 7) && (var1 != 106 || var4.length == 5)) {
         super.setStringArray(var1, var2, var3, var4);
         this.stringArrayTidyUpChecks(var1, var3, var4);
      } else {
         throw new IllegalArgumentException("STRING_ARRAY size is incorrect");
      }
   }

   private void stringArrayTidyUpChecks(int var1, int var2, String[] var3) {
      var2 = ((ContactListImp)super.list).ignoreInvalidAttributes(var1, var2);
      this.stringAttributes[((ContactListImp)this.list).getFieldIndex(var1)] = var2;
   }

   public int countValues(int var1) {
      if (var1 != 115 && var1 != 103) {
         return super.countValues(var1);
      } else {
         this.checkField(var1, 0, 4);
         Object[] var2 = (Object[])this.data[((ContactListImp)this.list).getFieldIndex(var1)];
         return var2 == null ? 0 : var2.length;
      }
   }

   public void removeValue(int var1, int var2) {
      if (var1 != 115 && var1 != 103) {
         super.removeValue(var1, var2);
      } else {
         this.checkField(var1, 0, 4);
         int var3 = this.countValues(var1);
         if (var2 < var3 && var2 >= 0) {
            int var4 = ((ContactListImp)this.list).getFieldIndex(var1);
            String[] var5 = new String[var3 - 1];
            String[] var6 = (String[])this.data[var4];
            int var7 = 0;

            int var8;
            for(var8 = 0; var8 < var6.length; ++var8) {
               if (var8 != var2) {
                  var5[var7] = var6[var8];
                  ++var7;
               }
            }

            this.data[var4] = var5;
            if (var1 == 115) {
               int[] var10 = new int[var3 - 1];
               var8 = 0;

               for(int var9 = 0; var9 < this.telephoneAttributes.length; ++var9) {
                  if (var9 != var2) {
                     var10[var8] = this.telephoneAttributes[var9];
                     ++var8;
                  }
               }

               this.telephoneAttributes = var10;
            }

            this.modified |= 1 << var4;
            if (((Object[])this.data[var4]).length == 0) {
               this.data[var4] = null;
            }

         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public int getAttributes(int var1, int var2) {
      int var3 = var1 != 115 && var1 != 103 ? var2 : 0;
      if (var1 == 115) {
         if (var2 < this.telephoneAttributes.length && var2 >= 0) {
            return this.telephoneAttributes[var2];
         } else {
            throw new IndexOutOfBoundsException();
         }
      } else {
         this.checkField(var1, var3, var1 != 100 && var1 != 106 ? (var1 == 101 ? 2 : 4) : 5);
         return this.stringAttributes[((ContactListImp)this.list).getFieldIndex(var1)];
      }
   }

   byte[] toSerial(String var1, int var2) {
      try {
         PBSearchManager.getInstance().UpdateContactMessage(this);
      } catch (PIMException var4) {
         return null;
      }

      return this.toVCard(this.getRecord().getMessage(), var1, var2);
   }

   native byte[] toVCard(byte[] var1, String var2, int var3);
}
