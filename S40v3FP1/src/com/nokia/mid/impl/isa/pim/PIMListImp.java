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

   protected PIMListImp(int var1) {
      this.mode = var1;
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

   public Enumeration items(PIMItem var1) throws PIMException {
      if (this.open) {
         if (var1 != null) {
            if (var1.getPIMList() == this) {
               if (this.mode != 2 && this.hasReadPermission()) {
                  return new SearchResult(this.getSearchManager(), var1, this);
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

   public Enumeration items(String var1) throws PIMException {
      if (this.open) {
         if (var1 != null) {
            if (this.mode != 2 && this.hasReadPermission()) {
               return new SearchResult(this.getSearchManager(), var1, false, this);
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

   public Enumeration itemsByCategory(String var1) throws PIMException {
      if (var1 != PIMList.UNCATEGORIZED && var1 != null) {
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

   public boolean isCategory(String var1) throws PIMException {
      if (this.open) {
         if (var1 != null) {
            return false;
         } else {
            throw new NullPointerException();
         }
      } else {
         throw new PIMException("List closed!", 2);
      }
   }

   public void addCategory(String var1) throws PIMException {
      throw new PIMException("Categories are not supported", 0);
   }

   public void deleteCategory(String var1, boolean var2) throws PIMException {
      throw new PIMException("Categories are not supported", 0);
   }

   public void renameCategory(String var1, String var2) throws PIMException {
      throw new PIMException("Categories are not supported", 0);
   }

   public int maxCategories() {
      return 0;
   }

   public boolean isSupportedAttribute(int var1, int var2) {
      return false;
   }

   public int[] getSupportedAttributes(int var1) {
      this.validateField(var1);
      return new int[0];
   }

   public boolean isSupportedArrayElement(int var1, int var2) {
      return false;
   }

   public int[] getSupportedArrayElements(int var1) {
      if (this.getFieldDataType(var1) != 5) {
         throw new IllegalArgumentException("The fields data type is not STRING_ARRAY");
      } else {
         return new int[0];
      }
   }

   public String getAttributeLabel(int var1) {
      throw new IllegalArgumentException("Not a valid attribute.");
   }

   public String getArrayElementLabel(int var1, int var2) {
      throw new IllegalArgumentException("Not a valid attribute.");
   }

   public int maxValues(int var1) {
      try {
         this.validateField(var1);
         return 1;
      } catch (UnsupportedFieldException var3) {
         return 0;
      }
   }

   public int stringArraySize(int var1) {
      throw new IllegalArgumentException("Not a valid field.");
   }

   int getMode() {
      return this.mode;
   }

   protected abstract boolean hasReadPermission();

   protected abstract boolean hasWritePermission(boolean var1);

   void removeItem(PIMItem var1) throws PIMException {
      if (this.open) {
         if (var1 != null) {
            if (var1.getPIMList() == this && ((PIMItemImp)var1).hasBeenCommitted) {
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

   protected static synchronized native boolean hasAccessRights(int var0, int var1, boolean var2);

   protected static synchronized native boolean hasOpenListRights(int var0, int var1);
}
