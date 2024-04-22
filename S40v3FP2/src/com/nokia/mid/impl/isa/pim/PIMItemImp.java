package com.nokia.mid.impl.isa.pim;

import javax.microedition.pim.FieldFullException;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;
import javax.microedition.pim.UnsupportedFieldException;

abstract class PIMItemImp implements PIMItem {
   protected static final int ITEM_NOT_COMMITTED = -1;
   private static final int UID_FIELD_DATA_ARRAY_OFFSET_INDEX = 0;
   protected Object[] data;
   protected int modified = 0;
   protected PIMListImp list;
   protected boolean hasBeenCommitted = false;
   boolean removedFromList = false;

   PIMItemImp(PIMList var1) {
      this.list = (PIMListImp)var1;
      this.data = new Object[this.list.getSupportedFields().length];
   }

   public PIMList getPIMList() {
      return !this.removedFromList ? this.list : null;
   }

   public boolean isModified() {
      return this.modified != 0 || !this.hasBeenCommitted || this.hasBeenCommitted && this.uidNotSet();
   }

   public int[] getFields() {
      int[] var1 = this.list.getSupportedFields();
      int var2 = 0;

      for(int var3 = 0; var3 < this.data.length; ++var3) {
         if (this.data[var3] != null) {
            var1[var2++] = var1[var3];
         }
      }

      int[] var4 = new int[var2];
      System.arraycopy(var1, 0, var4, 0, var2);
      return var4;
   }

   public byte[] getBinary(int var1, int var2) {
      this.checkField(var1, var2, 0);
      return null;
   }

   public void addBinary(int var1, int var2, byte[] var3, int var4, int var5) {
      this.setBinary(var1, 0, var2, var3, var4, var5);
   }

   public void setBinary(int var1, int var2, int var3, byte[] var4, int var5, int var6) {
      if (var4 == null) {
         throw new NullPointerException();
      } else if (var5 >= 0 && var5 < var4.length && var6 > 0 && var4.length != 0) {
         this.checkField(var1, var2, 0);
      } else {
         throw new IllegalArgumentException("Incorrect offset or value.");
      }
   }

   public long getDate(int var1, int var2) {
      this.checkField(var1, var2, 2);
      int[] var3 = (int[])this.data[this.indexOfField(var1)];
      if (var3 != null) {
         return (long)var3[0] * 1000L;
      } else {
         throw new IndexOutOfBoundsException("Empty field.");
      }
   }

   public void addDate(int var1, int var2, long var3) {
      this.checkField(var1, 0, 2);
      int var5 = this.indexOfField(var1);
      if (this.data[var5] == null) {
         int[] var6 = new int[]{(int)(var3 / 1000L)};
         this.setData(var6, var5);
      } else {
         throw new FieldFullException();
      }
   }

   public void setDate(int var1, int var2, int var3, long var4) {
      this.checkField(var1, var2, 2);
      int var6 = this.indexOfField(var1);
      if (this.data[var6] != null) {
         int[] var7 = new int[]{(int)(var4 / 1000L)};
         this.setData(var7, var6);
      } else {
         throw new IndexOutOfBoundsException("Empty field.");
      }
   }

   public int getInt(int var1, int var2) {
      this.checkField(var1, var2, 3);
      int[] var3 = (int[])this.data[this.indexOfField(var1)];
      if (var3 != null) {
         return var3[0];
      } else {
         throw new IndexOutOfBoundsException("Empty field.");
      }
   }

   public void addInt(int var1, int var2, int var3) {
      this.checkField(var1, 0, 3);
      int var4 = this.indexOfField(var1);
      if (this.data[var4] == null) {
         int[] var5 = new int[]{var3};
         this.setData(var5, var4);
      } else {
         throw new FieldFullException();
      }
   }

   public void setInt(int var1, int var2, int var3, int var4) {
      this.checkField(var1, var2, 3);
      int var5 = this.indexOfField(var1);
      if (this.data[var5] != null) {
         int[] var6 = new int[]{var4};
         this.setData(var6, var5);
      } else {
         throw new IndexOutOfBoundsException("Empty field.");
      }
   }

   public String getString(int var1, int var2) {
      this.checkField(var1, var2, 4);
      String var3 = (String)this.data[this.indexOfField(var1)];
      if (var3 != null) {
         return new String(var3);
      } else {
         throw new IndexOutOfBoundsException("Empty field.");
      }
   }

   public void addString(int var1, int var2, String var3) {
      this.checkField(var1, 0, 4);
      int var4 = this.indexOfField(var1);
      if (this.data[var4] == null) {
         if (this.isUIDFieldIndex(var4) && this.hasBeenCommitted) {
            throw new IllegalArgumentException("UID field is read only after first commit");
         } else {
            this.setData(new String(var3), var4);
         }
      } else {
         throw new FieldFullException();
      }
   }

   public void setString(int var1, int var2, int var3, String var4) {
      this.checkField(var1, var2, 4);
      int var5 = this.indexOfField(var1);
      if (this.data[var5] != null) {
         if (this.isUIDFieldIndex(var5) && this.hasBeenCommitted) {
            throw new IllegalArgumentException("UID field is read only after first commit");
         } else {
            this.setData(new String(var4), var5);
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public boolean getBoolean(int var1, int var2) {
      this.checkField(var1, var2, 1);
      boolean[] var3 = (boolean[])this.data[this.indexOfField(var1)];
      if (var3 != null) {
         return var3[0];
      } else {
         throw new IndexOutOfBoundsException("Empty field.");
      }
   }

   public void addBoolean(int var1, int var2, boolean var3) {
      this.checkField(var1, 0, 1);
      int var4 = this.indexOfField(var1);
      if (this.data[var4] == null) {
         boolean[] var5 = new boolean[]{var3};
         this.setData(var5, var4);
      } else {
         throw new FieldFullException();
      }
   }

   public void setBoolean(int var1, int var2, int var3, boolean var4) {
      this.checkField(var1, var2, 1);
      int var5 = this.indexOfField(var1);
      if (this.data[var5] != null) {
         boolean[] var6 = new boolean[]{var4};
         this.setData(var6, var5);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public String[] getStringArray(int var1, int var2) {
      this.checkField(var1, var2, 5);
      String[] var3 = (String[])this.data[this.indexOfField(var1)];
      if (var3 != null) {
         String[] var4 = new String[var3.length];

         for(int var5 = 0; var5 < var4.length; ++var5) {
            if (var3[var5] != null) {
               var4[var5] = new String(var3[var5]);
            }
         }

         return var4;
      } else {
         throw new IndexOutOfBoundsException("Empty field.");
      }
   }

   public void addStringArray(int var1, int var2, String[] var3) {
      this.verifyStringArrayData(var3);
      this.checkField(var1, 0, 5);
      int var4 = this.indexOfField(var1);
      if (this.data[var4] == null) {
         String[] var5 = new String[var3.length];
         this.copySupportedStringArrayElements(var1, var5, var3);
         this.setData(var5, var4);
      } else {
         throw new FieldFullException();
      }
   }

   public void setStringArray(int var1, int var2, int var3, String[] var4) {
      this.checkField(var1, var2, 5);
      this.verifyStringArrayData(var4);
      int var5 = this.indexOfField(var1);
      if (this.data[var5] != null) {
         String[] var6 = new String[var4.length];
         this.copySupportedStringArrayElements(var1, var6, var4);
         this.setData(var6, var5);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int countValues(int var1) {
      this.list.validateField(var1);
      int var2 = this.indexOfField(var1);
      if (!this.hasBeenCommitted && this.isUIDFieldIndex(var2)) {
         return 0;
      } else {
         return this.data[var2] == null ? 0 : 1;
      }
   }

   public void removeValue(int var1, int var2) {
      this.list.validateField(var1);
      if (var2 == 0 && var2 < this.countValues(var1)) {
         int var3 = this.indexOfField(var1);
         if (this.isUIDFieldIndex(var3) && this.hasBeenCommitted) {
            throw new IllegalArgumentException("UID is read only");
         } else {
            this.data[var3] = null;
            this.modified |= 1 << var3;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int getAttributes(int var1, int var2) {
      this.list.validateField(var1);
      if (var2 == 0) {
         return 0;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void commit() throws PIMException {
      if (!this.removedFromList) {
         if (this.list.open) {
            if (this.list.getMode() != 1) {
               if (this.list.hasWritePermission(!this.hasBeenCommitted)) {
                  if (this.isModified()) {
                     this.list.getSearchManager().commitItem(this);
                     this.modified = 0;
                     this.hasBeenCommitted = true;
                  }

               } else {
                  throw new SecurityException();
               }
            } else {
               throw new SecurityException("Read-only list");
            }
         } else {
            throw new PIMException("List is not open.", 2);
         }
      } else {
         throw new PIMException("No list is assigned to the PIMItem.", 3);
      }
   }

   public void addToCategory(String var1) throws PIMException {
      if (var1 != null) {
         throw new PIMException("Unsupported.", 0);
      } else {
         throw new NullPointerException();
      }
   }

   public void removeFromCategory(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      }
   }

   public String[] getCategories() {
      return new String[0];
   }

   public int maxCategories() {
      return 0;
   }

   protected int indexOfField(int var1) {
      int[] var2 = this.list.getSupportedFields();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var2[var3] == var1) {
            return var3;
         }
      }

      throw new UnsupportedFieldException(String.valueOf(var1));
   }

   protected void removeFromList() {
      this.removedFromList = true;
      this.setUID(-1);
   }

   protected void setUID(int var1) {
      this.data[0] = new String((new Integer(var1)).toString());
   }

   protected boolean isUIDFieldIndex(int var1) {
      return var1 == 0;
   }

   boolean matches(PIMItem var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         boolean var2 = true;
         int[] var3 = var1.getFields();

         for(int var4 = 0; var4 < var3.length && var2; ++var4) {
            int var5 = this.list.getFieldDataType(var3[var4]);
            int var8;
            switch(var5) {
            case 1:
               var2 = this.getBoolean(var3[var4], 0) == var1.getBoolean(var3[var4], 0);
               break;
            case 2:
               var2 = this.getDate(var3[var4], 0) == var1.getDate(var3[var4], 0);
               break;
            case 3:
               var2 = this.getInt(var3[var4], 0) == var1.getInt(var3[var4], 0);
               break;
            case 4:
               String var6 = var1.getString(var3[var4], 0);
               if (var6 != null) {
                  int var13 = this.countValues(var3[var4]);
                  var2 = false;
                  if (var13 != 0) {
                     for(var8 = 0; var8 < var13 && !var2; ++var8) {
                        String var14 = this.getString(var3[var4], var8);
                        var2 = this.matchStringRegion(var14, var6);
                     }
                  }
               }
               break;
            case 5:
               String[] var7 = var1.getStringArray(var3[var4], 0);
               var8 = this.countValues(var3[var4]);
               var2 = false;

               for(int var9 = 0; var9 < var8 && !var2; ++var9) {
                  String[] var10 = this.getStringArray(var3[var4], var9);
                  if (var10.length == var7.length) {
                     boolean var11 = true;

                     for(int var12 = 0; var12 < var10.length && var11; ++var12) {
                        if (var7[var12] != null) {
                           if (var10[var12] == null) {
                              var11 = false;
                           } else if (!var7[var12].equals("")) {
                              var11 = this.matchStringRegion(var10[var12], var7[var12]);
                           }
                        }
                     }

                     var2 = var11;
                  }
               }
            }
         }

         return var2;
      }
   }

   protected boolean matchStringRegion(String var1, String var2) {
      if (var2.equals("")) {
         return true;
      } else {
         int var3 = var1.length();
         int var4 = var2.length();
         int var5 = var3 - var4 + 1;
         boolean var6 = false;

         for(int var7 = 0; var7 < var5; ++var7) {
            if (var1.regionMatches(true, var7, var2, 0, var4)) {
               var6 = true;
               break;
            }
         }

         return var6;
      }
   }

   boolean matches(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         int[] var2 = this.getFields();
         if (var1 == "") {
            return var2.length != 0;
         } else {
            for(int var3 = 0; var3 < var2.length; ++var3) {
               int var4 = this.list.getFieldDataType(var2[var3]);
               if (var4 == 4 || var4 == 5) {
                  int var5 = this.countValues(var2[var3]);

                  for(int var6 = 0; var6 < var5; ++var6) {
                     if (var4 == 4) {
                        String var10 = this.getString(var2[var3], var6);
                        if (this.matchStringRegion(var10, var1)) {
                           return true;
                        }
                     } else {
                        String[] var7 = this.getStringArray(var2[var3], var6);

                        for(int var8 = 0; var8 < var7.length; ++var8) {
                           String var9 = var7[var8];
                           if (var9 != null && this.matchStringRegion(var9, var1)) {
                              return true;
                           }
                        }
                     }
                  }
               }
            }

            return false;
         }
      }
   }

   void checkField(int var1, int var2, int var3) {
      if (this.list.getFieldDataType(var1) == var3) {
         if (var2 != 0) {
            throw new IndexOutOfBoundsException("One value only");
         }
      } else {
         throw new IllegalArgumentException("Wrong data type.");
      }
   }

   abstract byte[] toSerial(String var1, int var2);

   private boolean uidNotSet() {
      return Integer.valueOf((String)this.data[0]) == -1;
   }

   private void setData(Object var1, int var2) {
      this.data[var2] = var1;
      this.modified |= 1 << var2;
   }

   private void copySupportedStringArrayElements(int var1, String[] var2, String[] var3) {
      int[] var4 = this.list.getSupportedArrayElements(var1);
      if (var4.length != 0) {
         int var5 = 0;
         boolean var6 = true;

         for(int var7 = 0; var7 < var2.length; ++var7) {
            if (var7 == var4[var5]) {
               if (var5 < var4.length - 1) {
                  ++var5;
               }

               if (var3[var7] != null) {
                  var2[var7] = new String(var3[var7]);
                  var6 = false;
               } else {
                  var2[var7] = null;
               }
            }
         }

         if (var6) {
            throw new IllegalArgumentException("All the supported elements are null");
         }
      }
   }

   void copyData(PIMItem var1) {
      int[] var2 = var1.getFields();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         int var4 = var2[var3];
         if (this.list.isSupportedField(var4)) {
            try {
               int var5 = Math.min(var1.countValues(var4), this.list.maxValues(var4));

               for(int var6 = 0; var6 < var5; ++var6) {
                  int var7 = var1.getAttributes(var4, var6);
                  switch(this.list.getFieldDataType(var4)) {
                  case 0:
                     byte[] var8 = var1.getBinary(var4, var6);
                     this.addBinary(var4, var7, var8, 0, var8.length);
                     break;
                  case 1:
                     this.addBoolean(var4, var7, var1.getBoolean(var4, var6));
                     break;
                  case 2:
                     this.addDate(var4, var7, var1.getDate(var4, var6));
                     break;
                  case 3:
                     this.addInt(var4, var7, var1.getInt(var4, var6));
                     break;
                  case 4:
                     this.addString(var4, var7, var1.getString(var4, var6));
                     break;
                  case 5:
                     this.addStringArray(var4, var7, var1.getStringArray(var4, var6));
                     break;
                  default:
                     throw new RuntimeException("An error occurs");
                  }
               }
            } catch (FieldFullException var9) {
            }
         }
      }

      this.copyPrivateData(var1);
   }

   abstract void copyPrivateData(PIMItem var1);

   private void verifyStringArrayData(String[] var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         boolean var2 = true;

         for(int var3 = 0; var3 < var1.length; ++var3) {
            if (var1[var3] != null) {
               var2 = false;
               break;
            }
         }

         if (var2) {
            throw new IllegalArgumentException();
         }
      }
   }
}
