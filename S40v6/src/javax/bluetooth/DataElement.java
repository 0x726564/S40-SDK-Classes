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

   public DataElement(boolean bool) {
      this.dataType = 40;
      this.dataObject = new Boolean(bool);
   }

   public DataElement(int valueType) {
      switch(valueType) {
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

      this.dataType = valueType;
   }

   public DataElement(int valueType, long value) {
      switch(valueType) {
      case 8:
         if (value < 0L || value > 255L) {
            throw new IllegalArgumentException("value not within range");
         }
         break;
      case 9:
         if (value >= 0L && value <= 65535L) {
            break;
         }

         throw new IllegalArgumentException("value not within range");
      case 10:
         if (value >= 0L && value <= 4294967295L) {
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
         if (value >= -128L && value <= 127L) {
            break;
         }

         throw new IllegalArgumentException("value not within range");
      case 17:
         if (value < -32768L || value > 32767L) {
            throw new IllegalArgumentException("value not within range");
         }
         break;
      case 18:
         if (value < -2147483648L || value > 2147483647L) {
            throw new IllegalArgumentException("value not within range");
         }
      case 19:
      }

      this.dataType = valueType;
      this.dataObject = new Long(value);
   }

   public DataElement(int valueType, Object value) {
      if (valueType == 24 && value instanceof UUID) {
         String stringUUID = ((UUID)value).toString();
         this.dataObject = new UUID(stringUUID, false);
      } else if ((valueType == 64 || valueType == 32) && value instanceof String) {
         this.dataObject = new String((String)value);
      } else {
         int length;
         if (valueType == 11 && value instanceof byte[]) {
            length = ((byte[])value).length;
            if (length != 8) {
               throw new IllegalArgumentException("wrong array length: " + length);
            }

            this.dataObject = new byte[length];
            System.arraycopy(value, 0, this.dataObject, 0, length);
         } else {
            if (valueType != 20 && valueType != 12 || !(value instanceof byte[])) {
               throw new IllegalArgumentException("valueType invalid");
            }

            length = ((byte[])value).length;
            if (length != 16) {
               throw new IllegalArgumentException("wrong array length: " + length);
            }

            this.dataObject = new byte[length];
            System.arraycopy(value, 0, this.dataObject, 0, length);
         }
      }

      this.dataType = valueType;
   }

   public void addElement(DataElement elem) {
      if (elem == null) {
         throw new NullPointerException("invalid DataElement");
      } else if (this.dataType != 56 && this.dataType != 48) {
         throw new ClassCastException("this object is not of type DATALT or DATSEQ");
      } else {
         ((Vector)this.dataObject).addElement(elem);
      }
   }

   public void insertElementAt(DataElement elem, int index) {
      if (elem == null) {
         throw new NullPointerException("invalid DataElement");
      } else if (this.dataType != 56 && this.dataType != 48) {
         throw new ClassCastException("this object is not of type DATALT or DATSEQ");
      } else {
         ((Vector)this.dataObject).insertElementAt(elem, index);
      }
   }

   public boolean removeElement(DataElement elem) {
      if (elem == null) {
         throw new NullPointerException("invalid DataElement");
      } else if (this.dataType != 56 && this.dataType != 48) {
         throw new ClassCastException("this object is not of type DATALT or DATSEQ");
      } else {
         return ((Vector)this.dataObject).removeElement(elem);
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
      byte[] value;
      switch(this.dataType) {
      case 11:
         value = new byte[8];
         System.arraycopy(this.dataObject, 0, value, 0, 8);
         return value;
      case 12:
      case 20:
         value = new byte[16];
         System.arraycopy(this.dataObject, 0, value, 0, 16);
         return value;
      case 24:
         String stringUUID = ((UUID)this.dataObject).toString();
         return new UUID(stringUUID, false);
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
}
