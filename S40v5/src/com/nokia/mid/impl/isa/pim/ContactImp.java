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
   private static int gU = PBSearchManager.getNativeValue(2);
   private static final int[] gV = new int[]{100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118};
   private static final int[] gW = new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 16777216};
   private int[] gX;
   private PBNativeRecord gY;
   private int gZ;
   private int[] ha;
   private Vector hb;

   private ContactImp() {
      super((PIMList)null);
      this.gY = null;
      this.gZ = -1;
      this.ha = null;
      this.hb = new Vector();
      throw new RuntimeException();
   }

   private static String L(String var0) {
      StringBuffer var1 = new StringBuffer();
      int var2 = var0.length();

      for(int var4 = 0; var4 < var2; ++var4) {
         char var3;
         if ((var3 = var0.charAt(var4)) != '-' && var3 != '(' && var3 != ')') {
            var1.append(var3);
         }
      }

      return new String(var1);
   }

   public ContactImp(ContactList var1) {
      super(var1);
      this.gY = null;
      this.gZ = -1;
      this.ha = null;
      this.hb = new Vector();
      if (var1 != null) {
         this.gZ = ((ContactListImp)var1).getListType();
      }

      this.S();
   }

   protected ContactImp(ContactList var1, ContactImp var2) {
      this(var1);
      this.c(var2);
      this.modified = var2.modified;
      if (var2.m()) {
         this.a(new PBNativeRecord(var2.getRecord()), true);
      }

      try {
         PBSearchManager.getInstance().a(this);
      } catch (PIMException var3) {
      }
   }

   protected ContactImp(PBNativeRecord var1) throws PIMException {
      this((ContactList)(new ContactListImp(1, PIMTextDatabase.getText(ContactListImp.cw[0]))));
      this.removeFromList();

      try {
         PBSearchManager.getInstance().a(this, var1);
      } catch (PIMException var2) {
      }

      this.setUID(this.getLocationIndex());
   }

   final void a(PIMItem var1) {
      String[] var4 = var1.getCategories();

      try {
         for(int var2 = 0; var2 < var4.length; ++var2) {
            this.addToCategory(var4[var2]);
         }

      } catch (PIMException var3) {
      }
   }

   static SearchManager getSearchManager() {
      return PBSearchManager.getInstance();
   }

   static final boolean e(int var0) {
      for(int var1 = 0; var1 < gV.length; ++var1) {
         if (gV[var1] == var0) {
            return true;
         }
      }

      return false;
   }

   protected static final boolean n(int var0) {
      for(int var1 = 0; var1 < gW.length; ++var1) {
         if (gW[var1] == var0) {
            return true;
         }
      }

      return false;
   }

   protected final boolean m() {
      return this.gY != null && this.gY.m();
   }

   protected final void R() {
      this.modified = 0;
      this.setUID(this.getLocationIndex());
   }

   protected int getNamedList() {
      return this.gZ;
   }

   protected int getLocationIndex() {
      int var1 = -1;
      if (this.gY != null && (var1 = this.gY.getLocationIndex()) == 65535) {
         var1 = -1;
      }

      return var1;
   }

   protected PBNativeRecord getRecord() {
      return this.gY;
   }

   protected final void a(PBNativeRecord var1, boolean var2) {
      this.gY = var1;
      if (var2) {
         this.setUID(this.getLocationIndex());
      }

   }

   protected final void l() {
      this.gY.l();
      this.setUID(this.getLocationIndex());
   }

   protected final void S() {
      this.gX = new int[this.getPIMList().getSupportedFields().length];
      this.gY = null;
      this.hb.removeAllElements();
      this.ha = null;
      this.modified = 0;
      this.data = new Object[this.list.getSupportedFields().length];
      this.hasBeenCommitted = false;
      this.a(new PBNativeRecord(), false);
   }

   protected final boolean M(String var1) {
      if (var1 == PIMList.UNCATEGORIZED) {
         return false;
      } else {
         Object var2 = null;
         Enumeration var3 = this.hb.elements();

         do {
            if (!var3.hasMoreElements()) {
               return false;
            }
         } while(!((String)var3.nextElement()).equals(var1));

         return true;
      }
   }

   public void removeFromCategory(String var1) {
      if (var1 == null) {
         throw new NullPointerException("Null category specified in removeFromCategory");
      } else {
         for(int var2 = 0; var2 < this.hb.size(); ++var2) {
            String var3 = (String)this.hb.elementAt(var2);
            if (var1.compareTo(var3) == 0) {
               this.hb.removeElementAt(var2);
               this.modified |= 4096;
               return;
            }
         }

      }
   }

   public void commit() throws PIMException {
      if (!this.fN && this.getPIMList() != null) {
         if (!this.hasBeenCommitted) {
            this.setUID(-1);
         }

         this.T();
         super.commit();
      } else {
         throw new PIMException("No list is assigned to the PIMItem.", 3);
      }
   }

   public void addToCategory(String var1) throws PIMException {
      if (var1 == null) {
         throw new NullPointerException("Null category string given to addToCategory method.");
      } else if (!this.M(var1)) {
         if (this.hb.size() == gU) {
            throw new PIMException("A Contact can only belong to " + gU + "category.", 4);
         } else if (!PBSearchManager.getInstance().isValidCategory(var1)) {
            throw new PIMException("Category specified is not valid", 1);
         } else {
            this.hb.addElement(var1);
            this.modified |= 4096;
         }
      }
   }

   public String[] getCategories() {
      if (this.hb.size() == 0) {
         return new String[0];
      } else {
         String[] var1 = new String[this.hb.size()];
         this.hb.copyInto(var1);
         return var1;
      }
   }

   protected void setCategories(String[] var1) throws PIMException {
      this.hb.removeAllElements();
      if (var1.length > this.maxCategories()) {
         throw new PIMException("Too many native categories found.");
      } else {
         for(int var2 = 0; var2 < var1.length; ++var2) {
            this.addToCategory(var1[var2]);
         }

      }
   }

   public int maxCategories() {
      return gU;
   }

   public int getPreferredIndex(int var1) {
      this.c(var1, 0, super.list.getFieldDataType(var1));
      byte var2 = -1;
      if (var1 != 115) {
         if (this.countValues(var1) != 0 && (this.gX[((ContactListImp)this.list).getFieldIndex(var1)] & 128) != 0) {
            var2 = 0;
         }

         return var2;
      } else {
         int var3 = 0;

         for(var1 = 0; this.ha != null && var1 < this.ha.length; ++var1) {
            if ((this.ha[var1] & 128) != 0) {
               var3 = var1;
               break;
            }
         }

         return var3;
      }
   }

   public String getString(int var1, int var2) {
      if (var1 != 115 && var1 != 103) {
         return super.getString(var1, var2);
      } else {
         this.c(var1, 0, 4);
         if (var2 < super.list.maxValues(var1) && var2 >= 0 && var2 < this.countValues(var1)) {
            return (String)((Object[])this.data[((ContactListImp)this.list).getFieldIndex(var1)])[var2];
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public void addString(int var1, int var2, String var3) {
      var2 = ((ContactListImp)super.list).i(var1, var2);
      if (var1 != 115 && var1 != 103) {
         super.addString(var1, var2, var3);
         this.gX[((ContactListImp)this.list).getFieldIndex(var1)] = var2;
      } else {
         this.c(var1, 0, 4);
         if (this.countValues(var1) >= this.list.maxValues(var1)) {
            throw new FieldFullException();
         } else if (var3 == null) {
            throw new NullPointerException("The data value string was null.");
         } else {
            String var4;
            if (var1 != 103) {
               var4 = L(var3);
            } else {
               var4 = var3;
            }

            int var7 = ((ContactListImp)this.list).getFieldIndex(var1);
            int var5;
            String[] var6 = new String[(var5 = this.countValues(var1)) + 1];
            if (var5 > 0) {
               System.arraycopy(this.data[var7], 0, var6, 0, var5);
            }

            var6[var5] = var4;
            this.data[var7] = var6;
            int[] var8 = new int[var5 + 1];
            if (var5 > 0 && var1 == 115) {
               System.arraycopy(this.ha, 0, var8, 0, var5);
            }

            if (var1 == 115) {
               var8[var5] = var2;
               this.ha = var8;
            }

            this.modified |= 1 << var7;
         }
      }
   }

   public void setString(int var1, int var2, int var3, String var4) {
      var3 = ((ContactListImp)super.list).i(var1, var3);
      if (var1 != 115 && var1 != 103) {
         super.setString(var1, var2, var3, var4);
         this.gX[((ContactListImp)this.list).getFieldIndex(var1)] = var3;
      } else {
         this.c(var1, 0, 4);
         if (var2 < this.countValues(var1) && var2 >= 0 && this.countValues(var1) != 0) {
            if (var4 == null) {
               throw new NullPointerException("The data value string was null.");
            } else {
               int var5 = ((ContactListImp)this.list).getFieldIndex(var1);
               ((String[])this.data[var5])[var2] = var4;
               if (var1 == 115) {
                  this.ha[var2] = var3;
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
         this.h(var1, var2);
      } else {
         throw new IllegalArgumentException("STRING_ARRAY size is incorrect");
      }
   }

   public void setStringArray(int var1, int var2, int var3, String[] var4) {
      if ((var1 != 100 || var4.length == 7) && (var1 != 106 || var4.length == 5)) {
         super.setStringArray(var1, var2, var3, var4);
         this.h(var1, var3);
      } else {
         throw new IllegalArgumentException("STRING_ARRAY size is incorrect");
      }
   }

   private void h(int var1, int var2) {
      var2 = ((ContactListImp)super.list).i(var1, var2);
      this.gX[((ContactListImp)this.list).getFieldIndex(var1)] = var2;
   }

   public int countValues(int var1) {
      if (var1 != 115 && var1 != 103) {
         return super.countValues(var1);
      } else {
         this.c(var1, 0, 4);
         Object[] var2;
         return (var2 = (Object[])this.data[((ContactListImp)this.list).getFieldIndex(var1)]) == null ? 0 : var2.length;
      }
   }

   public void removeValue(int var1, int var2) {
      if (var1 != 115 && var1 != 103) {
         super.removeValue(var1, var2);
      } else {
         this.c(var1, 0, 4);
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
               int[] var9 = new int[var3 - 1];
               var8 = 0;

               for(var1 = 0; var1 < this.ha.length; ++var1) {
                  if (var1 != var2) {
                     var9[var8] = this.ha[var1];
                     ++var8;
                  }
               }

               this.ha = var9;
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
         if (var2 < this.ha.length && var2 >= 0) {
            return this.ha[var2];
         } else {
            throw new IndexOutOfBoundsException();
         }
      } else {
         this.c(var1, var3, var1 != 100 && var1 != 106 ? (var1 == 101 ? 2 : 4) : 5);
         return this.gX[((ContactListImp)this.list).getFieldIndex(var1)];
      }
   }

   private void T() {
      int var1;
      if ((var1 = this.getPreferredIndex(115)) > 0) {
         int var2 = ((ContactListImp)this.list).getFieldIndex(115);
         int var3;
         String[] var4 = new String[var3 = this.countValues(115)];
         int[] var5 = new int[var3];
         System.arraycopy(this.data[var2], 0, var4, 0, var3);
         System.arraycopy(this.ha, 0, var5, 0, var3);
         String[] var7 = (String[])this.data[var2];
         var4[0] = var7[var1];
         var5[0] = this.ha[var1];

         for(int var6 = 0; var6 < var1; ++var6) {
            var4[var6 + 1] = var7[var6];
            var5[var6 + 1] = this.ha[var6];
         }

         this.data[var2] = var4;
         this.ha = var5;
      }

   }

   byte[] toSerial(String var1, int var2) {
      try {
         PBSearchManager.getInstance().a(this);
      } catch (PIMException var3) {
         return null;
      }

      return this.toVCard(this.getRecord().getMessage(), var1, var2);
   }

   native byte[] toVCard(byte[] var1, String var2, int var3);
}
