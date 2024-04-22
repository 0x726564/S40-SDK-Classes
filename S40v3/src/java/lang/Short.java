package java.lang;

public final class Short {
   public static final short MIN_VALUE = -32768;
   public static final short MAX_VALUE = 32767;
   private short value;

   public static short parseShort(String var0) throws NumberFormatException {
      return parseShort(var0, 10);
   }

   public static short parseShort(String var0, int var1) throws NumberFormatException {
      int var2 = Integer.parseInt(var0, var1);
      if (var2 >= -32768 && var2 <= 32767) {
         return (short)var2;
      } else {
         throw new NumberFormatException();
      }
   }

   public Short(short var1) {
      this.value = var1;
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

   public boolean equals(Object var1) {
      if (var1 instanceof Short) {
         return this.value == (Short)var1;
      } else {
         return false;
      }
   }
}
