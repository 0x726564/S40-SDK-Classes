package java.lang;

public final class Character {
   public static final int MIN_RADIX = 2;
   public static final int MAX_RADIX = 36;
   public static final char MIN_VALUE = '\u0000';
   public static final char MAX_VALUE = '\uffff';
   private char value;

   public Character(char var1) {
      this.value = var1;
   }

   public char charValue() {
      return this.value;
   }

   public int hashCode() {
      return this.value;
   }

   public boolean equals(Object var1) {
      if (var1 instanceof Character) {
         return this.value == (Character)var1;
      } else {
         return false;
      }
   }

   public String toString() {
      char[] var1 = new char[]{this.value};
      return String.valueOf(var1);
   }

   public static native boolean isLowerCase(char var0);

   public static native boolean isUpperCase(char var0);

   public static native boolean isDigit(char var0);

   public static native char toLowerCase(char var0);

   public static native char toUpperCase(char var0);

   private static final boolean isLatinLetter(char var0) {
      return 'A' <= var0 && var0 <= 'Z' || 'a' <= var0 && var0 <= 'z' || 'Ａ' <= var0 && var0 <= 'Ｚ' || 'ａ' <= var0 && var0 <= 'ｚ';
   }

   public static int digit(char var0, int var1) {
      int var2 = -1;
      if (var1 >= 2 && var1 <= 36) {
         if (isLatinLetter(var0)) {
            var2 = (var0 & 31) + 9;
         } else if (isDigit(var0)) {
            var2 = get_decimal_value(var0);
         }
      }

      return var2 < var1 ? var2 : -1;
   }

   private static native int get_decimal_value(char var0);
}
