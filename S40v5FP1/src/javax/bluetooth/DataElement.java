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
   private final Object s;

   public DataElement(boolean var1) {
      this.dataType = 40;
      this.s = new Boolean(var1);
   }

   public DataElement(int var1) {
      switch(var1) {
      case 0:
         this.s = null;
         break;
      case 48:
      case 56:
         this.s = new Vector();
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
      this.s = new Long(var2);
   }

   public DataElement(int var1, Object var2) {
      if (var1 == 24 && var2 instanceof UUID) {
         String var4 = ((UUID)var2).toString();
         this.s = new UUID(var4, false);
      } else if ((var1 == 64 || var1 == 32) && var2 instanceof String) {
         this.s = new String((String)var2);
      } else {
         int var3;
         if (var1 == 11 && var2 instanceof byte[]) {
            if ((var3 = ((byte[])var2).length) != 8) {
               throw new IllegalArgumentException("wrong array length: " + var3);
            }

            this.s = new byte[var3];
            System.arraycopy(var2, 0, this.s, 0, var3);
         } else {
            if (var1 != 20 && var1 != 12 || !(var2 instanceof byte[])) {
               throw new IllegalArgumentException("valueType invalid");
            }

            if ((var3 = ((byte[])var2).length) != 16) {
               throw new IllegalArgumentException("wrong array length: " + var3);
            }

            this.s = new byte[var3];
            System.arraycopy(var2, 0, this.s, 0, var3);
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
         ((Vector)this.s).addElement(var1);
      }
   }

   public void insertElementAt(DataElement var1, int var2) {
      if (var1 == null) {
         throw new NullPointerException("invalid DataElement");
      } else if (this.dataType != 56 && this.dataType != 48) {
         throw new ClassCastException("this object is not of type DATALT or DATSEQ");
      } else {
         ((Vector)this.s).insertElementAt(var1, var2);
      }
   }

   public boolean removeElement(DataElement var1) {
      if (var1 == null) {
         throw new NullPointerException("invalid DataElement");
      } else if (this.dataType != 56 && this.dataType != 48) {
         throw new ClassCastException("this object is not of type DATALT or DATSEQ");
      } else {
         return ((Vector)this.s).removeElement(var1);
      }
   }

   public int getDataType() {
      return this.dataType;
   }

   public boolean getBoolean() {
      if (this.dataType == 40) {
         return (Boolean)this.s;
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
         return (Long)this.s;
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
         return ((Vector)this.s).size();
      }
   }

   public Object getValue() {
      byte[] var2;
      switch(this.dataType) {
      case 11:
         var2 = new byte[8];
         System.arraycopy(this.s, 0, var2, 0, 8);
         return var2;
      case 12:
      case 20:
         var2 = new byte[16];
         System.arraycopy(this.s, 0, var2, 0, 16);
         return var2;
      case 24:
         String var1 = ((UUID)this.s).toString();
         return new UUID(var1, false);
      case 32:
      case 64:
         return new String((String)this.s);
      case 48:
      case 56:
         return ((Vector)this.s).elements();
      default:
         throw new ClassCastException("wrong data type: " + this.dataType);
      }
   }
}
