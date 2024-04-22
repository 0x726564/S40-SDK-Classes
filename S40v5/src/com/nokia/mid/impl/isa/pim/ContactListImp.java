package com.nokia.mid.impl.isa.pim;

import java.util.Enumeration;
import javax.microedition.pim.Contact;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.UnsupportedFieldException;

class ContactListImp extends PIMListImp implements ContactList {
   private static int hG = PBSearchManager.getNativeValue(1);
   private static final int[][] dD = PBSearchManager.getSupportedFieldsArray();
   private static final int[] hH = getFieldLabelsArray();
   private static final int[] hI = getAttributeLabelsArray();
   private static final int[][] hJ = getSupportedFieldMaxValuesArray();
   private static final int[][][] hK = getSupportedAttributesArray();
   private static final int[][][] hL = getSupportedArrayElementsArray();
   private static final int[][] hM = getArrayElementLabelsArray();
   static final int[] cw = getListNamesArray();
   private int hN = -1;

   public ContactListImp(int var1, String var2) throws PIMException {
      super(var1);
      if (var2 != null) {
         for(var1 = 0; var1 < cw.length; ++var1) {
            if (PIMTextDatabase.getText(cw[var1]).equals(var2)) {
               this.hN = var1;
               break;
            }
         }
      }

      if (this.hN == -1) {
         throw new PIMException("Invalid name", 1);
      }
   }

   private void V() throws PIMException {
      if (!super.open) {
         throw new PIMException("List closed.", 2);
      }
   }

   protected final int i(int var1, int var2) {
      int[] var4 = this.getSupportedAttributes(var1);
      var1 = 0;

      for(int var3 = 0; var3 < var4.length; ++var3) {
         var1 |= var4[var3];
      }

      return var2 & var1;
   }

   SearchManager getSearchManager() {
      return PBSearchManager.getInstance();
   }

   protected int getListNameAsIndex() {
      return this.hN;
   }

   protected final boolean e() {
      return PIMListImp.a(1, 1, false);
   }

   protected final boolean a(boolean var1) {
      return PIMListImp.a(2, 1, var1);
   }

   public String getName() {
      return PIMTextDatabase.getText(cw[this.hN]);
   }

   public Enumeration itemsByCategory(String var1) throws PIMException {
      this.V();
      if (this.getMode() != 2 && PIMListImp.a(1, 1, false)) {
         return new SearchResult(PBSearchManager.getInstance(), var1, true, this);
      } else {
         throw new SecurityException("Read permission denied");
      }
   }

   public String[] getCategories() throws PIMException {
      this.V();
      return this.getSearchManager().categories();
   }

   public boolean isCategory(String var1) throws PIMException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.V();
         return ((PBSearchManager)this.getSearchManager()).isValidCategory(var1);
      }
   }

   public void renameCategory(String var1, String var2) throws PIMException {
      this.V();
      if (this.getMode() != 2 && this.getMode() != 3) {
         throw new SecurityException("List not opened for writing.");
      } else {
         boolean var4 = false;
         if (!PIMListImp.a(2, 1, var4)) {
            throw new SecurityException("List does not have write permissions for renaming categories.");
         } else {
            if (this.isCategory(var2)) {
               if (!var1.equals(var2)) {
                  Enumeration var3 = this.itemsByCategory(var1);

                  while(var3.hasMoreElements()) {
                     Contact var5;
                     (var5 = (Contact)var3.nextElement()).removeFromCategory(var1);
                     var5.addToCategory(var2);
                     var5.commit();
                  }

                  this.deleteCategory(var1, false);
                  return;
               }
            } else {
               this.getSearchManager().renameCategory(var1, var2);
            }

         }
      }
   }

   public void addCategory(String var1) throws PIMException {
      this.V();
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         if (this.getMode() != 1) {
            boolean var3 = false;
            if (PIMListImp.a(2, 1, var3)) {
               PBSearchManager var4;
               if (!(var4 = (PBSearchManager)this.getSearchManager()).isValidCategory(var1)) {
                  var4.addCategory(var1);
               }

               return;
            }
         }

         throw new SecurityException("No permission to write to the list.");
      }
   }

   public void deleteCategory(String var1, boolean var2) throws PIMException {
      this.V();
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         if (this.getMode() != 1) {
            boolean var5 = false;
            if (PIMListImp.a(2, 1, var5)) {
               PBSearchManager var3;
               if ((var3 = (PBSearchManager)this.getSearchManager()).isValidCategory(var1)) {
                  if (PBSearchManager.getNativeValue(3) != 0 && var2) {
                     String var8 = var1;
                     SearchResult var6 = new SearchResult(PBSearchManager.getInstance(), var1, true, this);

                     while(var6.hasMoreElements()) {
                        Contact var7;
                        (var7 = (Contact)var6.nextElement()).removeFromCategory(var8);
                        if (var7.getCategories().length == 0) {
                           ((ContactList)var7.getPIMList()).removeContact(var7);
                        } else {
                           var7.commit();
                        }
                     }
                  }

                  var3.r(var1);
               }

               return;
            }
         }

         throw new SecurityException("No permission to write to the list.");
      }
   }

   public int maxCategories() {
      return hG;
   }

   public boolean isSupportedField(int var1) {
      return this.getFieldIndex(var1) != -1;
   }

   public int[] getSupportedFields() {
      int var1;
      int[] var3;
      int[] var2 = new int[var1 = (var3 = dD[this.hN]).length];
      System.arraycopy(var3, 0, var2, 0, var1);
      return var2;
   }

   public boolean isSupportedAttribute(int var1, int var2) {
      int[] var3;
      if ((var1 = this.getFieldIndex(var1)) != -1 && (var3 = hK[this.hN][var1]) != null) {
         for(var1 = 0; var1 < var3.length; ++var1) {
            if (var3[var1] == var2) {
               return true;
            }
         }
      }

      return false;
   }

   public int[] getSupportedAttributes(int var1) {
      int var2;
      if ((var2 = this.getFieldIndex(var1)) != -1) {
         int[] var3;
         if ((var3 = hK[this.hN][var2]) == null) {
            return new int[0];
         } else {
            int[] var4 = new int[var3.length];
            System.arraycopy(var3, 0, var4, 0, var4.length);
            return var4;
         }
      } else if (!ContactImp.e(var1)) {
         throw new IllegalArgumentException("Invalid field.");
      } else {
         throw new UnsupportedFieldException((String)null, var1);
      }
   }

   public boolean isSupportedArrayElement(int var1, int var2) {
      int[] var3;
      if ((var1 = this.getFieldIndex(var1)) != -1 && (var3 = hL[this.hN][var1]) != null) {
         for(var1 = 0; var1 < var3.length; ++var1) {
            if (var3[var1] == var2) {
               return true;
            }
         }
      }

      return false;
   }

   public int[] getSupportedArrayElements(int var1) {
      int var2;
      int[] var5;
      if ((var2 = this.getFieldIndex(var1)) != -1 && (var5 = hL[this.hN][var2]) != null) {
         int var3;
         int[] var4 = new int[var3 = var5.length];
         System.arraycopy(var5, 0, var4, 0, var3);
         return var4;
      } else if (this.getFieldDataType(var1) != 5) {
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
               return var1 == 110 ? 0 : 4;
            }
         } else {
            return 5;
         }
      } else if (!ContactImp.e(var1)) {
         throw new IllegalArgumentException("The field supplied is invalid");
      } else {
         throw new UnsupportedFieldException((String)null, var1);
      }
   }

   public String getFieldLabel(int var1) {
      int var2;
      if ((var2 = this.getFieldIndex(var1)) != -1) {
         return PIMTextDatabase.getText(hH[var2]);
      } else if (!ContactImp.e(var1)) {
         throw new IllegalArgumentException("The field supplied is invalid");
      } else {
         throw new UnsupportedFieldException((String)null, var1);
      }
   }

   public String getAttributeLabel(int var1) {
      int var2 = this.getFieldIndex(115);
      if (!ContactImp.n(var1)) {
         throw new IllegalArgumentException("Invalid Attribute");
      } else {
         int[] var3;
         if (var2 != -1 && (var3 = hK[this.hN][var2]) != null) {
            for(var2 = 0; var2 < var3.length; ++var2) {
               if (var3[var2] == var1) {
                  return PIMTextDatabase.getText(hI[var2]);
               }
            }
         }

         throw new UnsupportedFieldException("Unsupported Attribute", var1);
      }
   }

   public String getArrayElementLabel(int var1, int var2) {
      int var3;
      int[] var4;
      if ((var3 = this.getFieldIndex(var1)) != -1 && (var4 = hL[this.hN][var3]) != null) {
         for(int var5 = 0; var5 < var4.length; ++var5) {
            if (var4[var5] == var2) {
               return PIMTextDatabase.getText(hM[var3][var5]);
            }
         }
      }

      if (this.getFieldDataType(var1) == 5 && var2 >= 0 && (var1 == 100 && var2 < 7 || var1 == 106 && var2 < 5)) {
         throw new UnsupportedFieldException("Field Array element unsupported.");
      } else {
         throw new IllegalArgumentException("Invalid field or string array element");
      }
   }

   public int maxValues(int var1) {
      int var2;
      if ((var2 = this.getFieldIndex(var1)) != -1) {
         return hJ[this.hN][var2];
      } else if (!ContactImp.e(var1)) {
         throw new IllegalArgumentException("Field is not valid");
      } else {
         return 0;
      }
   }

   public int stringArraySize(int var1) {
      if (!ContactImp.e(var1)) {
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

   final void a(int var1) {
      if (!ContactImp.e(var1)) {
         throw new IllegalArgumentException("Invalid field.");
      } else if (!this.isSupportedField(var1)) {
         throw new UnsupportedFieldException((String)null, var1);
      }
   }

   int getFieldIndex(int var1) {
      for(int var2 = 0; var2 < dD[this.hN].length; ++var2) {
         if (dD[this.hN][var2] == var1) {
            return var2;
         }
      }

      return -1;
   }

   int getListType() {
      return this.hN;
   }

   private static native int[][] getSupportedFieldMaxValuesArray();

   private static native int[][][] getSupportedAttributesArray();

   private static native int[][][] getSupportedArrayElementsArray();

   private static native int[] getFieldLabelsArray();

   private static native int[][] getArrayElementLabelsArray();

   private static native int[] getAttributeLabelsArray();

   private static native int[] getListNamesArray();
}
