package java.lang;

public final class Byte {
   public static final byte MIN_VALUE = -128;
   public static final byte MAX_VALUE = 127;
   private byte value;

   public static byte parseByte(String s) throws NumberFormatException {
      return parseByte(s, 10);
   }

   public static byte parseByte(String s, int radix) throws NumberFormatException {
      int i = Integer.parseInt(s, radix);
      if (i >= -128 && i <= 127) {
         return (byte)i;
      } else {
         throw new NumberFormatException();
      }
   }

   public Byte(byte value) {
      this.value = value;
   }

   public byte byteValue() {
      return this.value;
   }

   public String toString() {
      return String.valueOf((int)this.value);
   }

   public int hashCode() {
      return this.value;
   }

   public boolean equals(Object obj) {
      if (obj instanceof Byte) {
         return this.value == (Byte)obj;
      } else {
         return false;
      }
   }
}
