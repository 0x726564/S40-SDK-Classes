package java.lang;

public final class Character {
   public static final int MIN_RADIX = 2;
   public static final int MAX_RADIX = 36;
   public static final char MIN_VALUE = '\u0000';
   public static final char MAX_VALUE = '\uffff';
   private char value;

   public Character(char value) {
      this.value = value;
   }

   public char charValue() {
      return this.value;
   }

   public int hashCode() {
      return this.value;
   }

   public boolean equals(Object obj) {
      if (obj instanceof Character) {
         return this.value == (Character)obj;
      } else {
         return false;
      }
   }

   public String toString() {
      char[] buf = new char[]{this.value};
      return String.valueOf(buf);
   }

   public static native boolean isLowerCase(char var0);

   public static native boolean isUpperCase(char var0);

   public static native boolean isDigit(char var0);

   public static native char toLowerCase(char var0);

   public static native char toUpperCase(char var0);

   private static final boolean isLatinLetter(char c) {
      return 'A' <= c && c <= 'Z' || 'a' <= c && c <= 'z' || 'Ａ' <= c && c <= 'Ｚ' || 'ａ' <= c && c <= 'ｚ';
   }

   public static int digit(char ch, int radix) {
      int value = -1;
      if (radix >= 2 && radix <= 36) {
         if (isLatinLetter(ch)) {
            value = (ch & 31) + 9;
         } else if (isDigit(ch)) {
            value = get_decimal_value(ch);
         }
      }

      return value < radix ? value : -1;
   }

   private static native int get_decimal_value(char var0);
}
