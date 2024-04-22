package com.nokia.mid.impl.isa.pim;

import javax.microedition.pim.ContactList;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;

class PBSearchManager implements SearchManager {
   private static final int SEARCH_TYPE_BY_PIMITEM = 0;
   private static final int SEARCH_TYPE_BY_STRING = 1;
   private static final int SEARCH_TYPE_BY_CATEGORY = 2;
   private static final String DEFAULT_NUMBER_STRING = new String("0");
   protected static final int NATIVE_DEFINITION_FOR_MAX_CATEGORIES = 1;
   protected static final int NATIVE_DEFINITION_FOR_MAX_GROUPS_PER_CONTACT = 2;
   protected static final int NATIVE_DEFINITION_FOR_ARE_SUPERGROUPS_SUPPORTED = 3;
   protected static final int FIND_RECORD_FIRST = 0;
   protected static final int FIND_RECORD_NEXT = 1;
   protected static final int FIND_RECORD_ABSOLUTE = 2;
   protected static final int PHONEBOOK_SERVER_BUSY = -2147483615;
   protected static final int MAX_RETRY_COUNT = 5;
   private static PBSearchManager _instance = null;
   private int[] pbFirstEntryIndex = new int[]{0, 0};

   private PBSearchManager() {
      try {
         this.listCategories();
      } catch (PIMException var2) {
      }

   }

   public static synchronized PBSearchManager getInstance() {
      if (_instance == null) {
         _instance = new PBSearchManager();
      }

      return _instance;
   }

   public synchronized PIMItem nextElement(PIMItem var1, PIMItem var2, PIMList var3) {
      try {
         return this.findMatchingItem(var1, var2, (String)null, 0, var3);
      } catch (PIMException var5) {
         return null;
      }
   }

   public synchronized PIMItem nextElement(PIMItem var1, String var2, PIMList var3, boolean var4) {
      try {
         return this.findMatchingItem(var1, (PIMItem)null, var2, var4 ? 2 : 1, var3);
      } catch (PIMException var6) {
         return null;
      }
   }

   public synchronized void commitItem(PIMItem var1) throws PIMException {
      if (var1 == null) {
         throw new NullPointerException("Contact cannot be committed as the reference is null.");
      } else if (!(var1 instanceof ContactImp)) {
         throw new RuntimeException("Item to be committed is not a contact!");
      } else {
         ContactImp var2 = (ContactImp)var1;
         this.alterEmptyContact(var2);
         this.UpdateContactMessage(var2);
         int var3 = this.writeRecord(var2.getNamedList(), var2.getRecord());
         int var4 = 0;
         if (var3 == 0) {
            var3 = -2147483615;

            while(var3 == -2147483615 && var4 < 5) {
               var3 = this.findRecord(2, var2.getRecord().getLocationIndex(), var2.getNamedList(), var2.getRecord());
               if (var3 != 0) {
                  ++var4;

                  try {
                     Thread.sleep(20L);
                  } catch (InterruptedException var6) {
                     throw new PIMException("Error reading after writing to contact DB.", 1);
                  }
               }
            }

            if (var3 == 0) {
               this.setContactRecord(var2, var2.getRecord());
            }
         }

         if (var3 != 0) {
            throw new PIMException("Error writing to contact DB, error=0x" + Integer.toHexString(var3) + "retryCount = " + Integer.toHexString(var4), 6);
         } else {
            var2.resetModified();
         }
      }
   }

   public synchronized void removeItem(PIMItem var1) throws PIMException {
      if (var1 == null) {
         throw new NullPointerException("Could not remove the contact as the reference is null.");
      } else if (!(var1 instanceof ContactImp)) {
         throw new RuntimeException("Item passed to native code is not a contact!");
      } else {
         ContactImp var2 = (ContactImp)var1;
         if (var2.hasBeenCommitted) {
            int var3 = this.removeRecord(var2.getNamedList(), var2.getRecord());
            if (var3 != 0) {
               throw new PIMException("Error removing item from database, error=" + var3, 6);
            }

            if (var2.getLocationIndex() == this.pbFirstEntryIndex[var2.getNamedList()]) {
               this.pbFirstEntryIndex[var2.getNamedList()] = 0;
               this.findMatchingItem((PIMItem)null, (PIMItem)null, (String)null, 0, var2.getPIMList());
            }

            var2.removeLocationIndex();
         }

      }
   }

   public synchronized String[] categories() throws PIMException {
      return this.listCategories();
   }

   private PIMItem findMatchingItem(PIMItem var1, PIMItem var2, String var3, int var4, PIMList var5) throws PIMException {
      int var6 = ((ContactListImp)var5).getListType();
      if (!ContactImp.IsValidNamedList(var6)) {
         throw new RuntimeException("Contact list specified is invalid.");
      } else {
         boolean var7 = false;
         PBNativeRecord var8 = new PBNativeRecord();
         if (var8 == null) {
            throw new RuntimeException("Could not create new designated class.");
         } else {
            int var9 = 0;
            if (var1 != null) {
               var9 = ((ContactImp)var1).getLocationIndex();
            }

            int var10 = this.findRecord(var1 == null ? 0 : 1, var9, var6, var8);
            if (var10 != 0) {
               throw new PIMException("Error accessing the Contact DB.", 1);
            } else {
               ContactImp var11 = new ContactImp((ContactList)var5);
               if (var11 == null) {
                  throw new PIMException("A new Contact could not be allocated.", 1);
               } else {
                  if (var1 == null) {
                     this.pbFirstEntryIndex[var6] = var8.getLocationIndex();
                     var7 = true;
                  }

                  for(; var8 != null; var7 = false) {
                     this.setContactRecord(var11, var8);
                     if (var11.IsValidItem()) {
                        if (!var7 && this.pbFirstEntryIndex[var6] == var11.getLocationIndex()) {
                           var11 = null;
                           var8 = null;
                        } else {
                           boolean var12 = false;
                           switch(var4) {
                           case 0:
                              if (var2 == null) {
                                 var12 = true;
                              } else {
                                 var12 = var11.matches(var2);
                              }
                              break;
                           case 1:
                              var12 = var11.matches(var3);
                              break;
                           case 2:
                              var12 = var11.IsAssignedToCategory(var3) || var3 == PIMList.UNCATEGORIZED && var11.getCategories().length == 0;
                           }

                           if (var12) {
                              var8 = null;
                           } else {
                              int var13 = var11.getLocationIndex();
                              this.setContactRecord(var11, (PBNativeRecord)null);
                              var8.releaseRecord();
                              var10 = this.findRecord(1, var13, var6, var8);
                              if (var10 != 0) {
                                 throw new PIMException(" Error in search using native database.", 1);
                              }
                           }
                        }
                     } else {
                        var11 = null;
                        var8 = null;
                     }
                  }

                  return var11;
               }
            }
         }
      }
   }

   protected synchronized void setContactRecord(ContactImp var1, PBNativeRecord var2) throws PIMException {
      if (var2 != null && var2.IsValidItem()) {
         if (var1.getPIMList() != null) {
            var1.initialiseContact();
         }

         var1.setRecord(var2, true);
         this.ProcessRecordInfo(var1, ((ContactListImp)var1.list).getListType(), var1.getRecord(), true);
         String[] var3 = this.getContactCategories(var1.getRecord());
         var1.setCategories(var3);
         var1.resetModified();
         var1.hasBeenCommitted = true;
      } else {
         var1.setRecord(new PBNativeRecord(), true);
         var1.initialiseContact();
      }

   }

   protected synchronized void UpdateContactMessage(ContactImp var1) throws PIMException {
      if (var1.isModified()) {
         if (var1.getRecord() == null) {
            throw new PIMException("Internal error, native record null.");
         } else {
            int var2 = this.ProcessRecordInfo(var1, ((ContactListImp)var1.list).getListType(), var1.getRecord(), false);
            if (var2 == 0) {
               this.setContactCategories(var1.getRecord(), var1.getCategories());
            }

            if (var2 != 0) {
               throw new PIMException("Error processing the contact, error code = " + Integer.toHexString(var2), 6);
            }
         }
      }
   }

   private void alterEmptyContact(ContactImp var1) {
      boolean var2 = false;
      if (var1.getPIMList().isSupportedField(106)) {
         if (var1.countValues(106) == 0 && var1.countValues(115) == 0) {
            var2 = true;
         }
      } else if (var1.countValues(105) == 0 && var1.countValues(115) == 0) {
         var2 = true;
      }

      if (var2) {
         var1.addString(115, 0, DEFAULT_NUMBER_STRING);
      }

   }

   private native int findRecord(int var1, int var2, int var3, PBNativeRecord var4);

   private native int writeRecord(int var1, PBNativeRecord var2);

   private native int removeRecord(int var1, PBNativeRecord var2);

   private synchronized native int ProcessRecordInfo(ContactImp var1, int var2, PBNativeRecord var3, boolean var4);

   protected static synchronized native int getNativeValue(int var0);

   protected synchronized native String[] listCategories() throws PIMException;

   private static synchronized native void nativeStaticInitialisation();

   protected synchronized native boolean isValidCategory(String var1);

   protected synchronized native void addCategory(String var1) throws PIMException;

   protected synchronized native boolean deleteCategory(String var1) throws PIMException;

   public synchronized native void renameCategory(String var1, String var2) throws PIMException;

   private synchronized native String[] getContactCategories(PBNativeRecord var1) throws PIMException;

   private synchronized native void setContactCategories(PBNativeRecord var1, String[] var2) throws PIMException;

   static {
      nativeStaticInitialisation();
   }
}
