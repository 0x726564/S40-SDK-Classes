package com.nokia.mid.impl.isa.pim;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;
import javax.microedition.pim.UnsupportedFieldException;

abstract class PIMListImp implements PIMList {
   private int mode = 1;
   protected boolean open = false;

   protected PIMListImp(int openMode) {
      this.mode = openMode;
      this.open = true;
   }

   public void close() throws PIMException {
      if (this.open) {
         this.open = false;
      } else {
         throw new PIMException("The list has already been closed!", 2);
      }
   }

   public Enumeration items() throws PIMException {
      if (this.open) {
         if (this.mode != 2 && this.hasReadPermission()) {
            return new SearchResult(this.getSearchManager(), (PIMItem)null, this);
         } else {
            throw new SecurityException("Read permission denied");
         }
      } else {
         throw new PIMException("List closed!", 2);
      }
   }

   public Enumeration items(PIMItem matchingItem) throws PIMException {
      if (this.open) {
         if (matchingItem != null) {
            if (matchingItem.getPIMList() == this) {
               if (this.mode != 2 && this.hasReadPermission()) {
                  return new SearchResult(this.getSearchManager(), matchingItem, this);
               } else {
                  throw new SecurityException("Read permission denied");
               }
            } else {
               throw new IllegalArgumentException("matchingItem did not originate from this contact list.");
            }
         } else {
            throw new NullPointerException();
         }
      } else {
         throw new PIMException("List closed!", 2);
      }
   }

   public Enumeration items(String matchingValue) throws PIMException {
      if (this.open) {
         if (matchingValue != null) {
            if (this.mode != 2 && this.hasReadPermission()) {
               return new SearchResult(this.getSearchManager(), matchingValue, false, this);
            } else {
               throw new SecurityException("Read permission denied");
            }
         } else {
            throw new NullPointerException();
         }
      } else {
         throw new PIMException("List closed!", 2);
      }
   }

   public Enumeration itemsByCategory(String category) throws PIMException {
      if (category != PIMList.UNCATEGORIZED && category != null) {
         if (this.mode != 2 && this.hasReadPermission()) {
            return new Enumeration() {
               public boolean hasMoreElements() {
                  return false;
               }

               public Object nextElement() {
                  throw new NoSuchElementException();
               }
            };
         } else {
            throw new SecurityException("Read permission denied");
         }
      } else {
         return this.items();
      }
   }

   public String[] getCategories() throws PIMException {
      if (this.open) {
         return new String[0];
      } else {
         throw new PIMException("List closed!", 2);
      }
   }

   public boolean isCategory(String category) throws PIMException {
      if (this.open) {
         if (category != null) {
            return false;
         } else {
            throw new NullPointerException();
         }
      } else {
         throw new PIMException("List closed!", 2);
      }
   }

   public void addCategory(String category) throws PIMException {
      throw new PIMException("Categories are not supported", 0);
   }

   public void deleteCategory(String category, boolean deleteUnassignedItems) throws PIMException {
      throw new PIMException("Categories are not supported", 0);
   }

   public void renameCategory(String currentCategory, String newCategory) throws PIMException {
      throw new PIMException("Categories are not supported", 0);
   }

   public int maxCategories() {
      return 0;
   }

   public boolean isSupportedAttribute(int field, int attribute) {
      return false;
   }

   public int[] getSupportedAttributes(int field) {
      this.validateField(field);
      return new int[0];
   }

   public boolean isSupportedArrayElement(int stringArrayField, int arrayElement) {
      return false;
   }

   public int[] getSupportedArrayElements(int stringArrayField) {
      if (this.getFieldDataType(stringArrayField) != 5) {
         throw new IllegalArgumentException("The fields data type is not STRING_ARRAY");
      } else {
         return new int[0];
      }
   }

   public String getAttributeLabel(int attribute) {
      throw new IllegalArgumentException("Not a valid attribute.");
   }

   public String getArrayElementLabel(int stringArrayField, int arrayElement) {
      throw new IllegalArgumentException("Not a valid attribute.");
   }

   public int maxValues(int field) {
      try {
         this.validateField(field);
         return 1;
      } catch (UnsupportedFieldException var3) {
         return 0;
      }
   }

   public int stringArraySize(int stringArrayField) {
      throw new IllegalArgumentException("Not a valid field.");
   }

   int getMode() {
      return this.mode;
   }

   protected abstract boolean hasReadPermission();

   protected abstract boolean hasWritePermission(boolean var1);

   void removeItem(PIMItem item) throws PIMException {
      if (this.open) {
         if (item != null) {
            if (item.getPIMList() == this && ((PIMItemImp)item).hasBeenCommitted) {
               if (this.getMode() == 1 || !this.hasWritePermission(false)) {
                  throw new SecurityException("Read only list");
               }
            } else {
               throw new PIMException("the item does not belong to the list.");
            }
         } else {
            throw new NullPointerException();
         }
      } else {
         throw new PIMException("List closed!", 2);
      }
   }

   abstract SearchManager getSearchManager();

   abstract void validateField(int var1);

   protected static synchronized boolean hasAccessRights(int requestedAccess, int listType, boolean toBeCreated) {
      return nativeHasAccessRights(requestedAccess, listType, toBeCreated);
   }

   private static native boolean nativeHasAccessRights(int var0, int var1, boolean var2);
}
