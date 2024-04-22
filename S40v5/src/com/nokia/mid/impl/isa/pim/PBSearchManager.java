package com.nokia.mid.impl.isa.pim;

import com.nokia.mid.impl.isa.util.SharedObjects;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;

class PBSearchManager implements SearchManager {
   private static final String by = new String("0");
   private static PBSearchManager bz = null;
   private static Object bA;
   private int[] bB;

   private PBSearchManager() {
      try {
         synchronized(bA) {
            this.bB = new int[]{0, 0};
            this.listCategories();
         }
      } catch (PIMException var3) {
      }

   }

   public static synchronized PBSearchManager getInstance() {
      if (bz == null) {
         bz = new PBSearchManager();
      }

      return bz;
   }

   public PIMItem nextElement(PIMItem var1, PIMItem var2, PIMList var3) {
      try {
         return this.a(var1, var2, (String)null, 0, var3);
      } catch (PIMException var4) {
         return null;
      }
   }

   public PIMItem nextElement(PIMItem var1, String var2, PIMList var3, boolean var4) {
      try {
         return this.a(var1, (PIMItem)null, var2, var4 ? 2 : 1, var3);
      } catch (PIMException var5) {
         return null;
      }
   }

   public void commitItem(PIMItem var1) throws PIMException {
      if (var1 == null) {
         throw new NullPointerException("Contact cannot be committed as the reference is null.");
      } else if (!(var1 instanceof ContactImp)) {
         throw new RuntimeException("Item to be committed is not a contact!");
      } else {
         ContactImp var7;
         ContactImp var2 = var7 = (ContactImp)var1;
         boolean var3 = false;
         if (var2.getPIMList().isSupportedField(106)) {
            if (var2.countValues(106) == 0 && var2.countValues(115) == 0) {
               var3 = true;
            }
         } else if (var2.countValues(105) == 0 && var2.countValues(115) == 0) {
            var3 = true;
         }

         if (var3) {
            var2.addString(115, 0, by);
         }

         this.a(var7);
         int var9 = 0;
         int var8;
         synchronized(bA) {
            if ((var8 = this.writeRecord(var7.getNamedList(), var7.getRecord())) == 0) {
               var8 = -2147483615;

               while(var8 == -2147483615 && var9 < 5) {
                  if ((var8 = this.findRecord(2, var7.getRecord().getLocationIndex(), var7.getNamedList(), var7.getRecord())) != 0) {
                     ++var9;

                     try {
                        Thread.sleep(20L);
                     } catch (InterruptedException var5) {
                        throw new PIMException("Error reading after writing to contact DB.", 1);
                     }
                  }
               }

               if (var8 == 0) {
                  this.a(var7, var7.getRecord());
               }
            }
         }

         if (var8 != 0) {
            throw new PIMException("Error writing to contact DB, error=0x" + Integer.toHexString(var8) + "retryCount = " + Integer.toHexString(var9), 6);
         } else {
            var7.R();
         }
      }
   }

   public void removeItem(PIMItem var1) throws PIMException {
      if (var1 == null) {
         throw new NullPointerException("Could not remove the contact as the reference is null.");
      } else if (!(var1 instanceof ContactImp)) {
         throw new RuntimeException("Item passed to native code is not a contact!");
      } else {
         ContactImp var5;
         if ((var5 = (ContactImp)var1).hasBeenCommitted) {
            synchronized(bA) {
               int var2;
               if ((var2 = this.removeRecord(var5.getNamedList(), var5.getRecord())) != 0) {
                  throw new PIMException("Error removing item from database, error=" + var2, 6);
               } else {
                  if (var5.getLocationIndex() == this.bB[var5.getNamedList()]) {
                     this.bB[var5.getNamedList()] = 0;
                     this.a((PIMItem)null, (PIMItem)null, (String)null, 0, var5.getPIMList());
                  }

                  var5.l();
               }
            }
         }
      }
   }

   public String[] categories() throws PIMException {
      synchronized(bA) {
         return this.listCategories();
      }
   }

   private PIMItem a(PIMItem var1, PIMItem var2, String var3, int var4, PIMList var5) throws PIMException {
      int var6;
      int var7;
      if ((var7 = var6 = ((ContactListImp)var5).getListType()) != 1 && var7 != 0) {
         throw new RuntimeException("Contact list specified is invalid.");
      } else {
         boolean var15 = false;
         PBNativeRecord var8 = new PBNativeRecord();
         int var9 = 0;
         if (var1 != null) {
            var9 = ((ContactImp)var1).getLocationIndex();
         }

         synchronized(bA) {
            if (this.findRecord(var1 == null ? 0 : 1, var9, var6, var8) != 0) {
               throw new PIMException("Error accessing the Contact DB.", 1);
            } else {
               ContactImp var14 = new ContactImp((ContactList)var5);
               if (var1 == null) {
                  this.bB[var6] = var8.getLocationIndex();
                  var15 = true;
               }

               for(; var8 != null; var15 = false) {
                  this.a(var14, var8);
                  if (var14.m() && (var15 || this.bB[var6] != var14.getLocationIndex())) {
                     boolean var12;
                     label71: {
                        var12 = false;
                        boolean var10000;
                        switch(var4) {
                        case 0:
                           var10000 = var2 == null ? true : var14.b(var2);
                           break;
                        case 1:
                           var10000 = var14.matches(var3);
                           break;
                        case 2:
                           var10000 = var14.M(var3) || var3 == PIMList.UNCATEGORIZED && var14.getCategories().length == 0;
                           break;
                        default:
                           break label71;
                        }

                        var12 = var10000;
                     }

                     if (var12) {
                        var8 = null;
                     } else {
                        int var13 = var14.getLocationIndex();
                        this.a(var14, (PBNativeRecord)null);
                        var8.n();
                        if (this.findRecord(1, var13, var6, var8) != 0) {
                           throw new PIMException(" Error in search using native database.", 1);
                        }
                     }
                  } else {
                     var14 = null;
                     var8 = null;
                  }
               }

               return var14;
            }
         }
      }
   }

   final void a(ContactImp var1, PBNativeRecord var2) throws PIMException {
      if (var2 != null && var2.m()) {
         if (var1.getPIMList() != null) {
            var1.S();
         }

         var1.a(var2, true);
         this.ProcessRecordInfo(var1, ((ContactListImp)var1.list).getListType(), var1.getRecord(), true);
         String[] var3 = this.getContactCategories(var1.getRecord());
         var1.setCategories(var3);
         var1.R();
         var1.hasBeenCommitted = true;
      } else {
         var1.a(new PBNativeRecord(), true);
         var1.S();
      }
   }

   final void a(ContactImp var1) throws PIMException {
      if (var1.isModified()) {
         if (var1.getRecord() == null) {
            throw new PIMException("Internal error, native record null.");
         } else {
            int var2;
            if ((var2 = this.ProcessRecordInfo(var1, ((ContactListImp)var1.list).getListType(), var1.getRecord(), false)) == 0) {
               this.setContactCategories(var1.getRecord(), var1.getCategories());
            }

            if (var2 != 0) {
               throw new PIMException("Error processing the contact, error code = " + Integer.toHexString(var2), 6);
            }
         }
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

   final void addCategory(String var1) throws PIMException {
      synchronized(bA) {
         this.nAddCategory(var1);
      }
   }

   private native void nAddCategory(String var1) throws PIMException;

   final boolean r(String var1) throws PIMException {
      synchronized(bA) {
         return this.nDeleteCategory(var1);
      }
   }

   private native boolean nDeleteCategory(String var1) throws PIMException;

   public void renameCategory(String var1, String var2) throws PIMException {
      synchronized(bA) {
         this.nRenameCategory(var1, var2);
      }
   }

   private native void nRenameCategory(String var1, String var2) throws PIMException;

   private native String[] getContactCategories(PBNativeRecord var1) throws PIMException;

   private native void setContactCategories(PBNativeRecord var1, String[] var2) throws PIMException;

   static int[][] getSupportedFieldsArray() {
      synchronized(bA) {
         return nGetSupportedFieldsArray();
      }
   }

   private static native int[][] nGetSupportedFieldsArray();

   static {
      nativeStaticInitialisation();
      bA = SharedObjects.getLock("javax.microedition.pim.pbLock");
   }
}
