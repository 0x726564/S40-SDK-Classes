package com.nokia.mid.impl.isa.pim;

import javax.microedition.pim.FieldFullException;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;
import javax.microedition.pim.UnsupportedFieldException;

abstract class PIMItemImp implements PIMItem {
   protected static final int ITEM_NOT_COMMITTED = -1;
   protected Object[] data;
   protected int modified = 0;
   protected PIMListImp list;
   protected boolean hasBeenCommitted = false;
   boolean fN = false;

   PIMItemImp(PIMList var1) {
      this.list = (PIMListImp)var1;
      this.data = new Object[this.list.getSupportedFields().length];
   }

   public PIMList getPIMList() {
      return !this.fN ? this.list : null;
   }

   public boolean isModified() {
      return this.modified != 0 || !this.hasBeenCommitted || this.hasBeenCommitted && Integer.valueOf((String)this.data[0]) == -1;
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
      this.c(var1, var2, 0);
      if (this.data[this.indexOfField(var1)] != null) {
         byte[] var3;
         byte[] var4 = new byte[(var3 = (byte[])this.data[this.indexOfField(var1)]).length];
         System.arraycopy(var3, 0, var4, 0, var3.length);
         return var4;
      } else {
         throw new IndexOutOfBoundsException("Empty field.");
      }
   }

   public void addBinary(int var1, int var2, byte[] var3, int var4, int var5) {
      if (var3 == null) {
         throw new NullPointerException();
      } else if (var4 >= 0 && var4 < var3.length && var5 > 0 && var3.length != 0) {
         this.c(var1, 0, 0);
         var1 = this.indexOfField(var1);
         if (this.data[var1] == null) {
            var2 = var5;
            if (var4 + var5 > var3.length) {
               var2 = var3.length - var4;
            }

            byte[] var6 = new byte[var2];
            System.arraycopy(var3, var4, var6, 0, var2);
            this.a(var6, var1);
         } else {
            throw new FieldFullException();
         }
      } else {
         throw new IllegalArgumentException("Incorrect offset or value.");
      }
   }

   public void setBinary(int var1, int var2, int var3, byte[] var4, int var5, int var6) {
      if (var4 == null) {
         throw new NullPointerException();
      } else if (var5 >= 0 && var5 < var4.length && var6 > 0 && var4.length != 0) {
         this.c(var1, var2, 0);
         var1 = this.indexOfField(var1);
         if (this.data[var1] != null) {
            var2 = var6;
            if (var5 + var6 > var4.length) {
               var2 = var4.length - var5;
            }

            byte[] var7 = new byte[var2];
            System.arraycopy(var4, var5, var7, 0, var2);
            this.a(var7, var1);
         } else {
            throw new IndexOutOfBoundsException("Empty field.");
         }
      } else {
         throw new IllegalArgumentException("Incorrect offset or value.");
      }
   }

   public long getDate(int var1, int var2) {
      this.c(var1, var2, 2);
      int[] var3;
      if ((var3 = (int[])this.data[this.indexOfField(var1)]) != null) {
         return (long)var3[0] * 1000L;
      } else {
         throw new IndexOutOfBoundsException("Empty field.");
      }
   }

   public void addDate(int var1, int var2, long var3) {
      this.c(var1, 0, 2);
      var1 = this.indexOfField(var1);
      if (this.data[var1] == null) {
         int[] var5 = new int[]{(int)(var3 / 1000L)};
         this.a(var5, var1);
      } else {
         throw new FieldFullException();
      }
   }

   public void setDate(int var1, int var2, int var3, long var4) {
      this.c(var1, var2, 2);
      var1 = this.indexOfField(var1);
      if (this.data[var1] != null) {
         int[] var6 = new int[]{(int)(var4 / 1000L)};
         this.a(var6, var1);
      } else {
         throw new IndexOutOfBoundsException("Empty field.");
      }
   }

   public int getInt(int var1, int var2) {
      this.c(var1, var2, 3);
      int[] var3;
      if ((var3 = (int[])this.data[this.indexOfField(var1)]) != null) {
         return var3[0];
      } else {
         throw new IndexOutOfBoundsException("Empty field.");
      }
   }

   public void addInt(int var1, int var2, int var3) {
      this.c(var1, 0, 3);
      var1 = this.indexOfField(var1);
      if (this.data[var1] == null) {
         int[] var4 = new int[]{var3};
         this.a(var4, var1);
      } else {
         throw new FieldFullException();
      }
   }

   public void setInt(int var1, int var2, int var3, int var4) {
      this.c(var1, var2, 3);
      var1 = this.indexOfField(var1);
      if (this.data[var1] != null) {
         int[] var5 = new int[]{var4};
         this.a(var5, var1);
      } else {
         throw new IndexOutOfBoundsException("Empty field.");
      }
   }

   public String getString(int var1, int var2) {
      this.c(var1, var2, 4);
      String var3;
      if ((var3 = (String)this.data[this.indexOfField(var1)]) != null) {
         return new String(var3);
      } else {
         throw new IndexOutOfBoundsException("Empty field.");
      }
   }

   public void addString(int var1, int var2, String var3) {
      this.c(var1, 0, 4);
      var1 = this.indexOfField(var1);
      if (this.data[var1] == null) {
         if (this.isUIDFieldIndex(var1) && this.hasBeenCommitted) {
            throw new IllegalArgumentException("UID field is read only after first commit");
         } else {
            this.a(new String(var3), var1);
         }
      } else {
         throw new FieldFullException();
      }
   }

   public void setString(int var1, int var2, int var3, String var4) {
      this.c(var1, var2, 4);
      var1 = this.indexOfField(var1);
      if (this.data[var1] != null) {
         if (this.isUIDFieldIndex(var1) && this.hasBeenCommitted) {
            throw new IllegalArgumentException("UID field is read only after first commit");
         } else {
            this.a(new String(var4), var1);
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public boolean getBoolean(int var1, int var2) {
      this.c(var1, var2, 1);
      boolean[] var3;
      if ((var3 = (boolean[])this.data[this.indexOfField(var1)]) != null) {
         return var3[0];
      } else {
         throw new IndexOutOfBoundsException("Empty field.");
      }
   }

   public void addBoolean(int var1, int var2, boolean var3) {
      this.c(var1, 0, 1);
      var1 = this.indexOfField(var1);
      if (this.data[var1] == null) {
         boolean[] var4 = new boolean[]{var3};
         this.a(var4, var1);
      } else {
         throw new FieldFullException();
      }
   }

   public void setBoolean(int var1, int var2, int var3, boolean var4) {
      this.c(var1, var2, 1);
      var1 = this.indexOfField(var1);
      if (this.data[var1] != null) {
         boolean[] var5 = new boolean[]{var4};
         this.a(var5, var1);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public String[] getStringArray(int var1, int var2) {
      this.c(var1, var2, 5);
      String[] var3;
      if ((var3 = (String[])this.data[this.indexOfField(var1)]) != null) {
         String[] var4 = new String[var3.length];

         for(var2 = 0; var2 < var4.length; ++var2) {
            if (var3[var2] != null) {
               var4[var2] = new String(var3[var2]);
            }
         }

         return var4;
      } else {
         throw new IndexOutOfBoundsException("Empty field.");
      }
   }

   public void addStringArray(int var1, int var2, String[] var3) {
      b(var3);
      this.c(var1, 0, 5);
      var2 = this.indexOfField(var1);
      if (this.data[var2] == null) {
         String[] var4 = new String[var3.length];
         this.a(var1, var4, var3);
         this.a(var4, var2);
      } else {
         throw new FieldFullException();
      }
   }

   public void setStringArray(int var1, int var2, int var3, String[] var4) {
      this.c(var1, var2, 5);
      b(var4);
      var2 = this.indexOfField(var1);
      if (this.data[var2] != null) {
         String[] var5 = new String[var4.length];
         this.a(var1, var5, var4);
         this.a(var5, var2);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int countValues(int var1) {
      this.list.a(var1);
      var1 = this.indexOfField(var1);
      if (!this.hasBeenCommitted && this.isUIDFieldIndex(var1)) {
         return 0;
      } else {
         return this.data[var1] == null ? 0 : 1;
      }
   }

   public void removeValue(int var1, int var2) {
      this.list.a(var1);
      if (var2 == 0 && var2 < this.countValues(var1)) {
         var1 = this.indexOfField(var1);
         if (this.isUIDFieldIndex(var1) && this.hasBeenCommitted) {
            throw new IllegalArgumentException("UID is read only");
         } else {
            this.data[var1] = null;
            this.modified |= 1 << var1;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int getAttributes(int var1, int var2) {
      this.list.a(var1);
      if (var2 == 0) {
         return 0;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void commit() throws PIMException {
      if (!this.fN) {
         if (this.list.open) {
            if (this.list.getMode() != 1) {
               if (this.list.a(!this.hasBeenCommitted)) {
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
      int[] var3 = this.list.getSupportedFields();

      for(int var2 = 0; var2 < var3.length; ++var2) {
         if (var3[var2] == var1) {
            return var2;
         }
      }

      throw new UnsupportedFieldException(String.valueOf(var1));
   }

   protected void removeFromList() {
      this.fN = true;
      this.setUID(-1);
   }

   protected void setUID(int var1) {
      this.data[0] = new String((new Integer(var1)).toString());
   }

   protected boolean isUIDFieldIndex(int var1) {
      return var1 == 0;
   }

   final boolean b(PIMItem var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         boolean var2 = true;
         int[] var3 = var1.getFields();

         for(int var4 = 0; var4 < var3.length && var2; ++var4) {
            int var7;
            switch(this.list.getFieldDataType(var3[var4])) {
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
               String var10;
               if ((var10 = var1.getString(var3[var4], 0)) != null) {
                  int var11 = this.countValues(var3[var4]);
                  var2 = false;
                  if (var11 != 0) {
                     for(var7 = 0; var7 < var11 && !var2; ++var7) {
                        String var12 = this.getString(var3[var4], var7);
                        var2 = this.matchStringRegion(var12, var10);
                     }
                  }
               }
               break;
            case 5:
               String[] var6 = var1.getStringArray(var3[var4], 0);
               var7 = this.countValues(var3[var4]);
               var2 = false;

               for(int var8 = 0; var8 < var7 && !var2; ++var8) {
                  String[] var5;
                  if ((var5 = this.getStringArray(var3[var4], var8)).length == var6.length) {
                     var2 = true;

                     for(int var9 = 0; var9 < var5.length && var2; ++var9) {
                        if (var6[var9] != null) {
                           if (var5[var9] == null) {
                              var2 = false;
                           } else if (!var6[var9].equals("")) {
                              var2 = this.matchStringRegion(var5[var9], var6[var9]);
                           }
                        }
                     }

                     var2 = var2;
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
         int var6 = var1.length();
         int var3 = var2.length();
         var6 = var6 - var3 + 1;
         boolean var4 = false;

         for(int var5 = 0; var5 < var6; ++var5) {
            if (var1.regionMatches(true, var5, var2, 0, var3)) {
               var4 = true;
               break;
            }
         }

         return var4;
      }
   }

   final boolean matches(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         int[] var2 = this.getFields();
         if (var1 == "") {
            return var2.length != 0;
         } else {
            for(int var3 = 0; var3 < var2.length; ++var3) {
               int var4;
               if ((var4 = this.list.getFieldDataType(var2[var3])) == 4 || var4 == 5) {
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
                           String var9;
                           if ((var9 = var7[var8]) != null && this.matchStringRegion(var9, var1)) {
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

   final void c(int var1, int var2, int var3) {
      if (this.list.getFieldDataType(var1) == var3) {
         if (var2 != 0) {
            throw new IndexOutOfBoundsException("One value only");
         }
      } else {
         throw new IllegalArgumentException("Wrong data type.");
      }
   }

   abstract byte[] toSerial(String var1, int var2);

   private void a(Object var1, int var2) {
      this.data[var2] = var1;
      this.modified |= 1 << var2;
   }

   private void a(int var1, String[] var2, String[] var3) {
      int[] var6;
      if ((var6 = this.list.getSupportedArrayElements(var1)).length != 0) {
         var1 = 0;
         boolean var4 = true;

         for(int var5 = 0; var5 < var2.length; ++var5) {
            if (var5 == var6[var1]) {
               if (var1 < var6.length - 1) {
                  ++var1;
               }

               if (var3[var5] != null) {
                  var2[var5] = new String(var3[var5]);
                  var4 = false;
               } else {
                  var2[var5] = null;
               }
            }
         }

         if (var4) {
            throw new IllegalArgumentException("All the supported elements are null");
         }
      }
   }

   void c(PIMItem var1) {
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

      this.a(var1);
   }

   abstract void a(PIMItem var1);

   private static void b(String[] var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         boolean var1 = true;

         for(int var2 = 0; var2 < var0.length; ++var2) {
            if (var0[var2] != null) {
               var1 = false;
               break;
            }
         }

         if (var1) {
            throw new IllegalArgumentException();
         }
      }
   }
}
