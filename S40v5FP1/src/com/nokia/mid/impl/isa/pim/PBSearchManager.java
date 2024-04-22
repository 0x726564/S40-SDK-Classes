package com.nokia.mid.impl.isa.pim;

import com.nokia.mid.impl.isa.util.SharedObjects;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;

class PBSearchManager implements SearchManager {
   private static final int SEARCH_TYPE_BY_PIMITEM = 0;
   private static final int SEARCH_TYPE_BY_STRING = 1;
   private static final int SEARCH_TYPE_BY_CATEGORY = 2;
   private static final String DEFAULT_NUMBER_STRING = new String("0");
   static final int NATIVE_DEFINITION_FOR_MAX_CATEGORIES = 1;
   static final int NATIVE_DEFINITION_FOR_MAX_GROUPS_PER_CONTACT = 2;
   static final int NATIVE_DEFINITION_FOR_ARE_SUPERGROUPS_SUPPORTED = 3;
   static final int FIND_RECORD_FIRST = 0;
   static final int FIND_RECORD_NEXT = 1;
   static final int FIND_RECORD_ABSOLUTE = 2;
   static final int PHONEBOOK_SERVER_BUSY = -2147483615;
   static final int MAX_RETRY_COUNT = 5;
   private static PBSearchManager _instance = null;
   static final Object pbLock;
   private int[] pbFirstEntryIndex;

   private PBSearchManager() {
      try {
         synchronized(pbLock) {
            this.pbFirstEntryIndex = new int[]{0, 0};
            this.listCategories();
         }
      } catch (PIMException var4) {
      }

   }

   public static synchronized PBSearchManager getInstance() {
      if (_instance == null) {
         _instance = new PBSearchManager();
      }

      return _instance;
   }

   public PIMItem nextElement(PIMItem prevItem, PIMItem searchItem, PIMList pimList) {
      try {
         return this.findMatchingItem(prevItem, searchItem, (String)null, 0, pimList);
      } catch (PIMException var5) {
         return null;
      }
   }

   public PIMItem nextElement(PIMItem prevItem, String searchString, PIMList pimList, boolean byCategory) {
      try {
         return this.findMatchingItem(prevItem, (PIMItem)null, searchString, byCategory ? 2 : 1, pimList);
      } catch (PIMException var6) {
         return null;
      }
   }

   public void commitItem(PIMItem item) throws PIMException {
      if (item == null) {
         throw new NullPointerException("Contact cannot be committed as the reference is null.");
      } else if (!(item instanceof ContactImp)) {
         throw new RuntimeException("Item to be committed is not a contact!");
      } else {
         ContactImp contact = (ContactImp)item;
         this.alterEmptyContact(contact);
         this.UpdateContactMessage(contact);
         int readRetryCount = 0;
         int iErrorCode;
         synchronized(pbLock) {
            iErrorCode = this.writeRecord(contact.getNamedList(), contact.getRecord());
            if (iErrorCode == 0) {
               iErrorCode = -2147483615;

               while(iErrorCode == -2147483615 && readRetryCount < 5) {
                  iErrorCode = this.findRecord(2, contact.getRecord().getLocationIndex(), contact.getNamedList(), contact.getRecord());
                  if (iErrorCode != 0) {
                     ++readRetryCount;

                     try {
                        Thread.sleep(20L);
                     } catch (InterruptedException var8) {
                        throw new PIMException("Error reading after writing to contact DB.", 1);
                     }
                  }
               }

               if (iErrorCode == 0) {
                  this.setContactRecord(contact, contact.getRecord());
               }
            }
         }

         if (iErrorCode != 0) {
            throw new PIMException("Error writing to contact DB, error=0x" + Integer.toHexString(iErrorCode) + "retryCount = " + Integer.toHexString(readRetryCount), 6);
         } else {
            contact.resetModified();
         }
      }
   }

   public void removeItem(PIMItem item) throws PIMException {
      if (item == null) {
         throw new NullPointerException("Could not remove the contact as the reference is null.");
      } else if (!(item instanceof ContactImp)) {
         throw new RuntimeException("Item passed to native code is not a contact!");
      } else {
         ContactImp contact = (ContactImp)item;
         if (contact.hasBeenCommitted) {
            synchronized(pbLock) {
               int iErrorCode = this.removeRecord(contact.getNamedList(), contact.getRecord());
               if (iErrorCode != 0) {
                  throw new PIMException("Error removing item from database, error=" + iErrorCode, 6);
               }

               if (contact.getLocationIndex() == this.pbFirstEntryIndex[contact.getNamedList()]) {
                  this.pbFirstEntryIndex[contact.getNamedList()] = 0;
                  this.findMatchingItem((PIMItem)null, (PIMItem)null, (String)null, 0, contact.getPIMList());
               }

               contact.removeLocationIndex();
            }
         }

      }
   }

   public String[] categories() throws PIMException {
      synchronized(pbLock) {
         return this.listCategories();
      }
   }

   private PIMItem findMatchingItem(PIMItem prevItem, PIMItem searchItem, String searchString, int searchType, PIMList pimList) throws PIMException {
      int namedList = ((ContactListImp)pimList).getListType();
      if (!ContactImp.IsValidNamedList(namedList)) {
         throw new RuntimeException("Contact list specified is invalid.");
      } else {
         boolean bIgnoreWrapCheck = false;
         PBNativeRecord contactRecord = new PBNativeRecord();
         if (contactRecord == null) {
            throw new RuntimeException("Could not create new designated class.");
         } else {
            int locationIndex = 0;
            if (prevItem != null) {
               locationIndex = ((ContactImp)prevItem).getLocationIndex();
            }

            synchronized(pbLock) {
               int retCode = this.findRecord(prevItem == null ? 0 : 1, locationIndex, namedList, contactRecord);
               if (retCode != 0) {
                  throw new PIMException("Error accessing the Contact DB.", 1);
               } else {
                  ContactImp foundItem = new ContactImp((ContactList)pimList);
                  if (foundItem == null) {
                     throw new PIMException("A new Contact could not be allocated.", 1);
                  } else {
                     if (prevItem == null) {
                        this.pbFirstEntryIndex[namedList] = contactRecord.getLocationIndex();
                        bIgnoreWrapCheck = true;
                     }

                     for(; contactRecord != null; bIgnoreWrapCheck = false) {
                        this.setContactRecord(foundItem, contactRecord);
                        if (foundItem.IsValidItem()) {
                           if (!bIgnoreWrapCheck && this.pbFirstEntryIndex[namedList] == foundItem.getLocationIndex()) {
                              foundItem = null;
                              contactRecord = null;
                           } else {
                              boolean bFound = false;
                              switch(searchType) {
                              case 0:
                                 if (searchItem == null) {
                                    bFound = true;
                                 } else {
                                    bFound = foundItem.matches(searchItem);
                                 }
                                 break;
                              case 1:
                                 bFound = foundItem.matches(searchString);
                                 break;
                              case 2:
                                 bFound = foundItem.IsAssignedToCategory(searchString) || searchString == PIMList.UNCATEGORIZED && foundItem.getCategories().length == 0;
                              }

                              if (bFound) {
                                 contactRecord = null;
                              } else {
                                 int iLocIndex = foundItem.getLocationIndex();
                                 this.setContactRecord(foundItem, (PBNativeRecord)null);
                                 contactRecord.releaseRecord();
                                 retCode = this.findRecord(1, iLocIndex, namedList, contactRecord);
                                 if (retCode != 0) {
                                    throw new PIMException(" Error in search using native database.", 1);
                                 }
                              }
                           }
                        } else {
                           foundItem = null;
                           contactRecord = null;
                        }
                     }

                     return foundItem;
                  }
               }
            }
         }
      }
   }

   void setContactRecord(ContactImp contact, PBNativeRecord contactRecord) throws PIMException {
      if (contactRecord != null && contactRecord.IsValidItem()) {
         if (contact.getPIMList() != null) {
            contact.initialiseContact();
         }

         contact.setRecord(contactRecord, true);
         this.ProcessRecordInfo(contact, ((ContactListImp)contact.list).getListType(), contact.getRecord(), true);
         String[] contactAssignedCategories = this.getContactCategories(contact.getRecord());
         contact.setCategories(contactAssignedCategories);
         contact.resetModified();
         contact.hasBeenCommitted = true;
      } else {
         contact.setRecord(new PBNativeRecord(), true);
         contact.initialiseContact();
      }

   }

   void UpdateContactMessage(ContactImp contact) throws PIMException {
      if (contact.isModified()) {
         if (contact.getRecord() == null) {
            throw new PIMException("Internal error, native record null.");
         } else {
            int iErrorCode = this.ProcessRecordInfo(contact, ((ContactListImp)contact.list).getListType(), contact.getRecord(), false);
            if (iErrorCode == 0) {
               this.setContactCategories(contact.getRecord(), contact.getCategories());
            }

            if (iErrorCode != 0) {
               throw new PIMException("Error processing the contact, error code = " + Integer.toHexString(iErrorCode), 6);
            }
         }
      }
   }

   private void alterEmptyContact(ContactImp item) {
      boolean empty = false;
      if (item.getPIMList().isSupportedField(106)) {
         if (item.countValues(106) == 0 && item.countValues(115) == 0) {
            empty = true;
         }
      } else if (item.countValues(105) == 0 && item.countValues(115) == 0) {
         empty = true;
      }

      if (empty) {
         item.addString(115, 0, DEFAULT_NUMBER_STRING);
      }

   }

   private native int findRecord(int var1, int var2, int var3, PBNativeRecord var4);

   private native int writeRecord(int var1, PBNativeRecord var2);

   private native int removeRecord(int var1, PBNativeRecord var2);

   private native int ProcessRecordInfo(ContactImp var1, int var2, PBNativeRecord var3, boolean var4);

   static native int getNativeValue(int var0);

   private native String[] listCategories() throws PIMException;

   private static native void nativeStaticInitialisation();

   native boolean isValidCategory(String var1);

   void addCategory(String category) throws PIMException {
      synchronized(pbLock) {
         this.nAddCategory(category);
      }
   }

   private native void nAddCategory(String var1) throws PIMException;

   boolean deleteCategory(String category) throws PIMException {
      synchronized(pbLock) {
         return this.nDeleteCategory(category);
      }
   }

   private native boolean nDeleteCategory(String var1) throws PIMException;

   public void renameCategory(String currentCategoryName, String newCategoryName) throws PIMException {
      synchronized(pbLock) {
         this.nRenameCategory(currentCategoryName, newCategoryName);
      }
   }

   private native void nRenameCategory(String var1, String var2) throws PIMException;

   private native String[] getContactCategories(PBNativeRecord var1) throws PIMException;

   private native void setContactCategories(PBNativeRecord var1, String[] var2) throws PIMException;

   static int[][] getSupportedFieldsArray() {
      synchronized(pbLock) {
         return nGetSupportedFieldsArray();
      }
   }

   private static native int[][] nGetSupportedFieldsArray();

   static {
      nativeStaticInitialisation();
      pbLock = SharedObjects.getLock("javax.microedition.pim.pbLock");
   }
}
