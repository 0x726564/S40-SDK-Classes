package java.lang;

public final class Short {
   public static final short MIN_VALUE = -32768;
   public static final short MAX_VALUE = 32767;
   private short value;

   public static short parseShort(String s) throws NumberFormatException {
      return parseShort(s, 10);
   }

   public static short parseShort(String s, int radix) throws NumberFormatException {
      int i = Integer.parseInt(s, radix);
      if (i >= -32768 && i <= 32767) {
         return (short)i;
      } else {
         throw new NumberFormatException();
      }
   }

   public Short(short value) {
      this.value = value;
   }

   public short shortValue() {
      return this.value;
   }

   public String toString() {
      return String.valueOf((int)this.value);
   }

   public int hashCode() {
      return this.value;
   }

   public boolean equals(Object obj) {
      if (obj instanceof Short) {
         return this.value == (Short)obj;
      } else {
         return false;
      }
   }
}
