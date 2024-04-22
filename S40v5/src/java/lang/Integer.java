package java.lang;

public final class Integer {
   public static final int MIN_VALUE = MIN_VALUE;
   public static final int MAX_VALUE = MAX_VALUE;
   static final char[] digits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
   private int value;

   public static String toString(int var0, int var1) {
      if (var1 < 2 || var1 > 36) {
         var1 = 10;
      }

      char[] var2 = new char[33];
      boolean var3 = var0 < 0;
      int var4 = 32;
      if (!var3) {
         var0 = -var0;
      }

      while(var0 <= -var1) {
         var2[var4--] = digits[-(var0 % var1)];
         var0 /= var1;
      }

      var2[var4] = digits[-var0];
      if (var3) {
         --var4;
         var2[var4] = '-';
      }

      return new String(var2, var4, 33 - var4);
   }

   public static String toHexString(int var0) {
      return b(var0, 4);
   }

   public static String toOctalString(int var0) {
      return b(var0, 3);
   }

   public static String toBinaryString(int var0) {
      return b(var0, 1);
   }

   private static String b(int var0, int var1) {
      char[] var2 = new char[32];
      int var3 = 32;
      int var4 = (var4 = 1 << var1) - 1;

      do {
         --var3;
         var2[var3] = digits[var0 & var4];
      } while((var0 >>>= var1) != 0);

      return new String(var2, var3, 32 - var3);
   }

   public static String toString(int var0) {
      return toString(var0, 10);
   }

   public static int parseInt(String var0, int var1) throws NumberFormatException {
      if (var0 == null) {
         throw new NumberFormatException("null");
      } else if (var1 < 2) {
         throw new NumberFormatException("radix " + var1 + " less than Character.MIN_RADIX");
      } else if (var1 > 36) {
         throw new NumberFormatException("radix " + var1 + " greater than Character.MAX_RADIX");
      } else {
         int var2 = 0;
         boolean var3 = false;
         int var4 = 0;
         int var5;
         if ((var5 = var0.length()) > 0) {
            int var6;
            if (var0.charAt(0) == '-') {
               var3 = true;
               var6 = MIN_VALUE;
               ++var4;
            } else {
               var6 = -2147483647;
            }

            int var7 = var6 / var1;
            int var8;
            if (var4 < var5) {
               if ((var8 = Character.digit(var0.charAt(var4++), var1)) < 0) {
                  throw new NumberFormatException(var0);
               }

               var2 = -var8;
            }

            while(var4 < var5) {
               if ((var8 = Character.digit(var0.charAt(var4++), var1)) < 0) {
                  throw new NumberFormatException(var0);
               }

               if (var2 < var7) {
                  throw new NumberFormatException(var0);
               }

               if ((var2 *= var1) < var6 + var8) {
                  throw new NumberFormatException(var0);
               }

               var2 -= var8;
            }

            if (var3) {
               if (var4 > 1) {
                  return var2;
               } else {
                  throw new NumberFormatException(var0);
               }
            } else {
               return -var2;
            }
         } else {
            throw new NumberFormatException(var0);
         }
      }
   }

   public static int parseInt(String var0) throws NumberFormatException {
      return parseInt(var0, 10);
   }

   public static Integer valueOf(String var0, int var1) throws NumberFormatException {
      return new Integer(parseInt(var0, var1));
   }

   public static Integer valueOf(String var0) throws NumberFormatException {
      return new Integer(parseInt(var0, 10));
   }

   public Integer(int var1) {
      this.value = var1;
   }

   public final byte byteValue() {
      return (byte)this.value;
   }

   public final short shortValue() {
      return (short)this.value;
   }

   public final int intValue() {
      return this.value;
   }

   public final long longValue() {
      return (long)this.value;
   }

   public final float floatValue() {
      return (float)this.value;
   }

   public final double doubleValue() {
      return (double)this.value;
   }

   public final String toString() {
      return String.valueOf(this.value);
   }

   public final int hashCode() {
      return this.value;
   }

   public final boolean equals(Object var1) {
      if (var1 instanceof Integer) {
         return this.value == (Integer)var1;
      } else {
         return false;
      }
   }
}
