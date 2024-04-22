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

   PIMItemImp(PIMList pimList) {
      this.list = (PIMListImp)pimList;
      this.data = new Object[this.list.getSupportedFields().length];
   }

   public PIMList getPIMList() {
      return !this.removedFromList ? this.list : null;
   }

   public boolean isModified() {
      return this.modified != 0 || !this.hasBeenCommitted || this.hasBeenCommitted && this.uidNotSet();
   }

   public int[] getFields() {
      int[] fields = this.list.getSupportedFields();
      int nbFields = 0;

      for(int i = 0; i < this.data.length; ++i) {
         if (this.data[i] != null) {
            fields[nbFields++] = fields[i];
         }
      }

      int[] ret = new int[nbFields];
      System.arraycopy(fields, 0, ret, 0, nbFields);
      return ret;
   }

   public byte[] getBinary(int field, int index) {
      this.checkField(field, index, 0);
      if (this.data[this.indexOfField(field)] != null) {
         byte[] temp = (byte[])this.data[this.indexOfField(field)];
         byte[] dest = new byte[temp.length];
         System.arraycopy(temp, 0, dest, 0, temp.length);
         return dest;
      } else {
         throw new IndexOutOfBoundsException("Empty field.");
      }
   }

   public void addBinary(int field, int attributes, byte[] value, int offset, int length) {
      if (value == null) {
         throw new NullPointerException();
      } else if (offset >= 0 && offset < value.length && length > 0 && value.length != 0) {
         this.checkField(field, 0, 0);
         int ind = this.indexOfField(field);
         if (this.data[ind] == null) {
            int useLength = length;
            if (offset + length > value.length) {
               useLength = value.length - offset;
            }

            byte[] dest = new byte[useLength];
            System.arraycopy(value, offset, dest, 0, useLength);
            this.setData(dest, ind);
         } else {
            throw new FieldFullException();
         }
      } else {
         throw new IllegalArgumentException("Incorrect offset or value.");
      }
   }

   public void setBinary(int field, int index, int attributes, byte[] value, int offset, int length) {
      if (value == null) {
         throw new NullPointerException();
      } else if (offset >= 0 && offset < value.length && length > 0 && value.length != 0) {
         this.checkField(field, index, 0);
         int ind = this.indexOfField(field);
         if (this.data[ind] != null) {
            int useLength = length;
            if (offset + length > value.length) {
               useLength = value.length - offset;
            }

            byte[] dest = new byte[useLength];
            System.arraycopy(value, offset, dest, 0, useLength);
            this.setData(dest, ind);
         } else {
            throw new IndexOutOfBoundsException("Empty field.");
         }
      } else {
         throw new IllegalArgumentException("Incorrect offset or value.");
      }
   }

   public long getDate(int field, int index) {
      this.checkField(field, index, 2);
      int[] ia = (int[])this.data[this.indexOfField(field)];
      if (ia != null) {
         return (long)ia[0] * 1000L;
      } else {
         throw new IndexOutOfBoundsException("Empty field.");
      }
   }

   public void addDate(int field, int attributes, long value) {
      this.checkField(field, 0, 2);
      int ind = this.indexOfField(field);
      if (this.data[ind] == null) {
         int[] la = new int[]{(int)(value / 1000L)};
         this.setData(la, ind);
      } else {
         throw new FieldFullException();
      }
   }

   public void setDate(int field, int index, int attributes, long value) {
      this.checkField(field, index, 2);
      int ind = this.indexOfField(field);
      if (this.data[ind] != null) {
         int[] la = new int[]{(int)(value / 1000L)};
         this.setData(la, ind);
      } else {
         throw new IndexOutOfBoundsException("Empty field.");
      }
   }

   public int getInt(int field, int index) {
      this.checkField(field, index, 3);
      int[] ia = (int[])this.data[this.indexOfField(field)];
      if (ia != null) {
         return ia[0];
      } else {
         throw new IndexOutOfBoundsException("Empty field.");
      }
   }

   public void addInt(int field, int attributes, int value) {
      this.checkField(field, 0, 3);
      int ind = this.indexOfField(field);
      if (this.data[ind] == null) {
         int[] ia = new int[]{value};
         this.setData(ia, ind);
      } else {
         throw new FieldFullException();
      }
   }

   public void setInt(int field, int index, int attributes, int value) {
      this.checkField(field, index, 3);
      int ind = this.indexOfField(field);
      if (this.data[ind] != null) {
         int[] ia = new int[]{value};
         this.setData(ia, ind);
      } else {
         throw new IndexOutOfBoundsException("Empty field.");
      }
   }

   public String getString(int field, int index) {
      this.checkField(field, index, 4);
      String s = (String)this.data[this.indexOfField(field)];
      if (s != null) {
         return new String(s);
      } else {
         throw new IndexOutOfBoundsException("Empty field.");
      }
   }

   public void addString(int field, int attributes, String value) {
      if (value == null) {
         throw new NullPointerException("The data value string was null.");
      } else {
         this.checkField(field, 0, 4);
         int ind = this.indexOfField(field);
         if (this.data[ind] == null) {
            if (this.isUIDFieldIndex(ind) && this.hasBeenCommitted) {
               throw new IllegalArgumentException("UID field is read only after first commit");
            } else {
               this.setData(new String(value), ind);
            }
         } else {
            throw new FieldFullException();
         }
      }
   }

   public void setString(int field, int index, int attributes, String value) {
      this.checkField(field, index, 4);
      int ind = this.indexOfField(field);
      if (this.data[ind] != null) {
         if (this.isUIDFieldIndex(ind) && this.hasBeenCommitted) {
            throw new IllegalArgumentException("UID field is read only after first commit");
         } else {
            this.setData(new String(value), ind);
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public boolean getBoolean(int field, int index) {
      this.checkField(field, index, 1);
      boolean[] ba = (boolean[])this.data[this.indexOfField(field)];
      if (ba != null) {
         return ba[0];
      } else {
         throw new IndexOutOfBoundsException("Empty field.");
      }
   }

   public void addBoolean(int field, int attributes, boolean value) {
      this.checkField(field, 0, 1);
      int ind = this.indexOfField(field);
      if (this.data[ind] == null) {
         boolean[] ba = new boolean[]{value};
         this.setData(ba, ind);
      } else {
         throw new FieldFullException();
      }
   }

   public void setBoolean(int field, int index, int attributes, boolean value) {
      this.checkField(field, index, 1);
      int ind = this.indexOfField(field);
      if (this.data[ind] != null) {
         boolean[] ba = new boolean[]{value};
         this.setData(ba, ind);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public String[] getStringArray(int field, int index) {
      this.checkField(field, index, 5);
      String[] sa = (String[])this.data[this.indexOfField(field)];
      if (sa != null) {
         String[] sa2 = new String[sa.length];

         for(int i = 0; i < sa2.length; ++i) {
            if (sa[i] != null) {
               sa2[i] = new String(sa[i]);
            }
         }

         return sa2;
      } else {
         throw new IndexOutOfBoundsException("Empty field.");
      }
   }

   public void addStringArray(int field, int attributes, String[] value) {
      this.verifyStringArrayData(value);
      this.checkField(field, 0, 5);
      int ind = this.indexOfField(field);
      if (this.data[ind] == null) {
         String[] sa = new String[value.length];
         this.copySupportedStringArrayElements(field, sa, value);
         this.setData(sa, ind);
      } else {
         throw new FieldFullException();
      }
   }

   public void setStringArray(int field, int index, int attributes, String[] value) {
      this.checkField(field, index, 5);
      this.verifyStringArrayData(value);
      int ind = this.indexOfField(field);
      if (this.data[ind] != null) {
         String[] sa = new String[value.length];
         this.copySupportedStringArrayElements(field, sa, value);
         this.setData(sa, ind);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int countValues(int field) {
      this.list.validateField(field);
      int ind = this.indexOfField(field);
      if (!this.hasBeenCommitted && this.isUIDFieldIndex(ind)) {
         return 0;
      } else {
         return this.data[ind] == null ? 0 : 1;
      }
   }

   public void removeValue(int field, int index) {
      this.list.validateField(field);
      if (index == 0 && index < this.countValues(field)) {
         int ind = this.indexOfField(field);
         if (this.isUIDFieldIndex(ind) && this.hasBeenCommitted) {
            throw new IllegalArgumentException("UID is read only");
         } else {
            this.data[ind] = null;
            this.modified |= 1 << ind;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int getAttributes(int field, int index) {
      this.list.validateField(field);
      if (index == 0) {
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

   public void addToCategory(String category) throws PIMException {
      if (category != null) {
         throw new PIMException("Unsupported.", 0);
      } else {
         throw new NullPointerException();
      }
   }

   public void removeFromCategory(String category) {
      if (category == null) {
         throw new NullPointerException();
      }
   }

   public String[] getCategories() {
      return new String[0];
   }

   public int maxCategories() {
      return 0;
   }

   protected int indexOfField(int field) {
      int[] supported = this.list.getSupportedFields();

      for(int i = 0; i < supported.length; ++i) {
         if (supported[i] == field) {
            return i;
         }
      }

      throw new UnsupportedFieldException(String.valueOf(field));
   }

   protected void removeFromList() {
      this.removedFromList = true;
      this.setUID(-1);
   }

   protected void setUID(int uidVal) {
      this.data[0] = new String((new Integer(uidVal)).toString());
   }

   protected boolean isUIDFieldIndex(int indexOfFieldValue) {
      return indexOfFieldValue == 0;
   }

   boolean matches(PIMItem matchingItem) {
      if (matchingItem == null) {
         throw new NullPointerException();
      } else {
         boolean match = true;
         int[] fields = matchingItem.getFields();

         for(int i = 0; i < fields.length && match; ++i) {
            int fieldDataType = this.list.getFieldDataType(fields[i]);
            int valIndex;
            switch(fieldDataType) {
            case 1:
               match = this.getBoolean(fields[i], 0) == matchingItem.getBoolean(fields[i], 0);
               break;
            case 2:
               match = this.getDate(fields[i], 0) == matchingItem.getDate(fields[i], 0);
               break;
            case 3:
               match = this.getInt(fields[i], 0) == matchingItem.getInt(fields[i], 0);
               break;
            case 4:
               String matchingString = matchingItem.getString(fields[i], 0);
               if (matchingString != null) {
                  int numVals = this.countValues(fields[i]);
                  match = false;
                  if (numVals != 0) {
                     for(valIndex = 0; valIndex < numVals && !match; ++valIndex) {
                        String thisString = this.getString(fields[i], valIndex);
                        match = this.matchStringRegion(thisString, matchingString);
                     }
                  }
               }
               break;
            case 5:
               String[] matchingSa = matchingItem.getStringArray(fields[i], 0);
               valIndex = this.countValues(fields[i]);
               match = false;

               for(int valIndex = 0; valIndex < valIndex && !match; ++valIndex) {
                  String[] thisSa = this.getStringArray(fields[i], valIndex);
                  if (thisSa.length == matchingSa.length) {
                     boolean saMatch = true;

                     for(int j = 0; j < thisSa.length && saMatch; ++j) {
                        if (matchingSa[j] != null) {
                           if (thisSa[j] == null) {
                              saMatch = false;
                           } else if (!matchingSa[j].equals("")) {
                              saMatch = this.matchStringRegion(thisSa[j], matchingSa[j]);
                           }
                        }
                     }

                     match = saMatch;
                  }
               }
            }
         }

         return match;
      }
   }

   protected boolean matchStringRegion(String thisString, String matchingString) {
      if (matchingString.equals("")) {
         return true;
      } else {
         int thisLength = thisString.length();
         int matchingLength = matchingString.length();
         int maxOffset = thisLength - matchingLength + 1;
         boolean match = false;

         for(int off = 0; off < maxOffset; ++off) {
            if (thisString.regionMatches(true, off, matchingString, 0, matchingLength)) {
               match = true;
               break;
            }
         }

         return match;
      }
   }

   boolean matches(String matchingString) {
      if (matchingString == null) {
         throw new NullPointerException();
      } else {
         int[] fields = this.getFields();
         if (matchingString == "") {
            return fields.length != 0;
         } else {
            for(int i = 0; i < fields.length; ++i) {
               int type = this.list.getFieldDataType(fields[i]);
               if (type == 4 || type == 5) {
                  int numVals = this.countValues(fields[i]);

                  for(int valIndex = 0; valIndex < numVals; ++valIndex) {
                     if (type == 4) {
                        String thisString = this.getString(fields[i], valIndex);
                        if (this.matchStringRegion(thisString, matchingString)) {
                           return true;
                        }
                     } else {
                        String[] values = this.getStringArray(fields[i], valIndex);

                        for(int j = 0; j < values.length; ++j) {
                           String value = values[j];
                           if (value != null && this.matchStringRegion(value, matchingString)) {
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

   void checkField(int field, int index, int type) {
      if (this.list.getFieldDataType(field) == type) {
         if (index != 0) {
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

   private void setData(Object value, int index) {
      this.data[index] = value;
      this.modified |= 1 << index;
   }

   private void copySupportedStringArrayElements(int field, String[] destsa, String[] srcsa) {
      int[] se = this.list.getSupportedArrayElements(field);
      if (se.length != 0) {
         int seIndex = 0;
         boolean allStringsNull = true;

         for(int i = 0; i < destsa.length; ++i) {
            if (i == se[seIndex]) {
               if (seIndex < se.length - 1) {
                  ++seIndex;
               }

               if (srcsa[i] != null) {
                  destsa[i] = new String(srcsa[i]);
                  allStringsNull = false;
               } else {
                  destsa[i] = null;
               }
            }
         }

         if (allStringsNull) {
            throw new IllegalArgumentException("All the supported elements are null");
         }
      }
   }

   void copyData(PIMItem item) {
      int[] fields = item.getFields();

      for(int i = 0; i < fields.length; ++i) {
         int field = fields[i];
         if (this.list.isSupportedField(field)) {
            try {
               int copiedValues = Math.min(item.countValues(field), this.list.maxValues(field));

               for(int j = 0; j < copiedValues; ++j) {
                  int attributes = item.getAttributes(field, j);
                  switch(this.list.getFieldDataType(field)) {
                  case 0:
                     byte[] value = item.getBinary(field, j);
                     this.addBinary(field, attributes, value, 0, value.length);
                     break;
                  case 1:
                     this.addBoolean(field, attributes, item.getBoolean(field, j));
                     break;
                  case 2:
                     this.addDate(field, attributes, item.getDate(field, j));
                     break;
                  case 3:
                     this.addInt(field, attributes, item.getInt(field, j));
                     break;
                  case 4:
                     this.addString(field, attributes, item.getString(field, j));
                     break;
                  case 5:
                     this.addStringArray(field, attributes, item.getStringArray(field, j));
                     break;
                  default:
                     throw new RuntimeException("An error occurs");
                  }
               }
            } catch (FieldFullException var9) {
            }
         }
      }

      this.copyPrivateData(item);
   }

   abstract void copyPrivateData(PIMItem var1);

   private void verifyStringArrayData(String[] sa) {
      if (sa == null) {
         throw new NullPointerException();
      } else {
         boolean allStringsNull = true;

         for(int i = 0; i < sa.length; ++i) {
            if (sa[i] != null) {
               allStringsNull = false;
               break;
            }
         }

         if (allStringsNull) {
            throw new IllegalArgumentException();
         }
      }
   }
}
