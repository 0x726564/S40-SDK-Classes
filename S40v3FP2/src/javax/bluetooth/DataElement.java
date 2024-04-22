package javax.bluetooth;

import java.util.Vector;

public class DataElement {
   public static final int NULL = 0;
   public static final int U_INT_1 = 8;
   public static final int U_INT_2 = 9;
   public static final int U_INT_4 = 10;
   public static final int U_INT_8 = 11;
   public static final int U_INT_16 = 12;
   public static final int INT_1 = 16;
   public static final int INT_2 = 17;
   public static final int INT_4 = 18;
   public static final int INT_8 = 19;
   public static final int INT_16 = 20;
   public static final int URL = 64;
   public static final int UUID = 24;
   public static final int BOOL = 40;
   public static final int STRING = 32;
   public static final int DATSEQ = 48;
   public static final int DATALT = 56;
   private final int dataType;
   private final Object dataObject;

   public DataElement(boolean var1) {
      this.dataType = 40;
      this.dataObject = new Boolean(var1);
   }

   public DataElement(int var1) {
      switch(var1) {
      case 0:
         this.dataObject = null;
         break;
      case 48:
      case 56:
         this.dataObject = new Vector();
         break;
      default:
         throw new IllegalArgumentException("valueType invalid");
      }

      this.dataType = var1;
   }

   public DataElement(int var1, long var2) {
      switch(var1) {
      case 8:
         if (var2 < 0L || var2 > 255L) {
            throw new IllegalArgumentException("value not within range");
         }
         break;
      case 9:
         if (var2 >= 0L && var2 <= 65535L) {
            break;
         }

         throw new IllegalArgumentException("value not within range");
      case 10:
         if (var2 >= 0L && var2 <= 4294967295L) {
            break;
         }

         throw new IllegalArgumentException("value not within range");
      case 11:
      case 12:
      case 13:
      case 14:
      case 15:
      default:
         throw new IllegalArgumentException("valueType invalid");
      case 16:
         if (var2 >= -128L && var2 <= 127L) {
            break;
         }

         throw new IllegalArgumentException("value not within range");
      case 17:
         if (var2 < -32768L || var2 > 32767L) {
            throw new IllegalArgumentException("value not within range");
         }
         break;
      case 18:
         if (var2 < -2147483648L || var2 > 2147483647L) {
            throw new IllegalArgumentException("value not within range");
         }
      case 19:
      }

      this.dataType = var1;
      this.dataObject = new Long(var2);
   }

   public DataElement(int var1, Object var2) {
      if (var1 == 24 && var2 instanceof UUID) {
         String var4 = ((UUID)var2).toString();
         this.dataObject = new UUID(var4, false);
      } else if ((var1 == 64 || var1 == 32) && var2 instanceof String) {
         this.dataObject = new String((String)var2);
      } else {
         int var3;
         if (var1 == 11 && var2 instanceof byte[]) {
            var3 = ((byte[])var2).length;
            if (var3 != 8) {
               throw new IllegalArgumentException("wrong array length: " + var3);
            }

            this.dataObject = new byte[var3];
            System.arraycopy(var2, 0, this.dataObject, 0, var3);
         } else {
            if (var1 != 20 && var1 != 12 || !(var2 instanceof byte[])) {
               throw new IllegalArgumentException("valueType invalid");
            }

            var3 = ((byte[])var2).length;
            if (var3 != 16) {
               throw new IllegalArgumentException("wrong array length: " + var3);
            }

            this.dataObject = new byte[var3];
            System.arraycopy(var2, 0, this.dataObject, 0, var3);
         }
      }

      this.dataType = var1;
   }

   public void addElement(DataElement var1) {
      if (var1 == null) {
         throw new NullPointerException("invalid DataElement");
      } else if (this.dataType != 56 && this.dataType != 48) {
         throw new ClassCastException("this object is not of type DATALT or DATSEQ");
      } else {
         ((Vector)this.dataObject).addElement(var1);
      }
   }

   public void insertElementAt(DataElement var1, int var2) throws IndexOutOfBoundsException {
      if (var1 == null) {
         throw new NullPointerException("invalid DataElement");
      } else if (this.dataType != 56 && this.dataType != 48) {
         throw new ClassCastException("this object is not of type DATALT or DATSEQ");
      } else {
         ((Vector)this.dataObject).insertElementAt(var1, var2);
      }
   }

   public boolean removeElement(DataElement var1) {
      if (var1 == null) {
         throw new NullPointerException("invalid DataElement");
      } else if (this.dataType != 56 && this.dataType != 48) {
         throw new ClassCastException("this object is not of type DATALT or DATSEQ");
      } else {
         return ((Vector)this.dataObject).removeElement(var1);
      }
   }

   public int getDataType() {
      return this.dataType;
   }

   public boolean getBoolean() {
      if (this.dataType == 40) {
         return (Boolean)this.dataObject;
      } else {
         throw new ClassCastException("this object is not of type BOOL");
      }
   }

   public long getLong() {
      switch(this.dataType) {
      case 8:
      case 9:
      case 10:
      case 16:
      case 17:
      case 18:
      case 19:
         return (Long)this.dataObject;
      case 11:
      case 12:
      case 13:
      case 14:
      case 15:
      default:
         throw new ClassCastException("this object is not of type LONG");
      }
   }

   public int getSize() {
      if (this.dataType != 56 && this.dataType != 48) {
         throw new ClassCastException("this object is not of type DATALT or DATSEQ");
      } else {
         return ((Vector)this.dataObject).size();
      }
   }

   public Object getValue() {
      byte[] var2;
      switch(this.dataType) {
      case 11:
         var2 = new byte[8];
         System.arraycopy(this.dataObject, 0, var2, 0, 8);
         return var2;
      case 12:
      case 20:
         var2 = new byte[16];
         System.arraycopy(this.dataObject, 0, var2, 0, 16);
         return var2;
      case 24:
         String var1 = ((UUID)this.dataObject).toString();
         return new UUID(var1, false);
      case 32:
      case 64:
         return new String((String)this.dataObject);
      case 48:
      case 56:
         return ((Vector)this.dataObject).elements();
      default:
         throw new ClassCastException("wrong data type: " + this.dataType);
      }
   }

   public DataElement clone() {
      DataElement var1 = null;
      switch(this.dataType) {
      case 0:
         var1 = new DataElement(this.dataType);
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 13:
      case 14:
      case 15:
      case 21:
      case 22:
      case 23:
      case 25:
      case 26:
      case 27:
      case 28:
      case 29:
      case 30:
      case 31:
      case 33:
      case 34:
      case 35:
      case 36:
      case 37:
      case 38:
      case 39:
      case 41:
      case 42:
      case 43:
      case 44:
      case 45:
      case 46:
      case 47:
      case 49:
      case 50:
      case 51:
      case 52:
      case 53:
      case 54:
      case 55:
      case 57:
      case 58:
      case 59:
      case 60:
      case 61:
      case 62:
      case 63:
      default:
         break;
      case 8:
      case 9:
      case 10:
      case 16:
      case 17:
      case 18:
      case 19:
         var1 = new DataElement(this.dataType, this.getLong());
         break;
      case 11:
      case 12:
      case 20:
      case 24:
      case 32:
      case 64:
         var1 = new DataElement(this.dataType, this.dataObject);
         break;
      case 40:
         var1 = new DataElement(this.getBoolean());
         break;
      case 48:
      case 56:
         var1 = new DataElement(this.dataType);
         int var3 = this.getSize();

         for(int var4 = 0; var4 < var3; ++var4) {
            DataElement var2 = (DataElement)((Vector)this.dataObject).elementAt(var4);
            var1.addElement(var2.clone());
         }
      }

      return var1;
   }
}
