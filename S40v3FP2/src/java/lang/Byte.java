package java.lang;

public final class Byte {
   public static final byte MIN_VALUE = -128;
   public static final byte MAX_VALUE = 127;
   private byte value;

   public static byte parseByte(String var0) throws NumberFormatException {
      return parseByte(var0, 10);
   }

   public static byte parseByte(String var0, int var1) throws NumberFormatException {
      int var2 = Integer.parseInt(var0, var1);
      if (var2 >= -128 && var2 <= 127) {
         return (byte)var2;
      } else {
         throw new NumberFormatException();
      }
   }

   public Byte(byte var1) {
      this.value = var1;
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

   public boolean equals(Object var1) {
      if (var1 instanceof Byte) {
         return this.value == (Byte)var1;
      } else {
         return false;
      }
   }
}
