package java.lang;

public final class Integer {
   public static final int MIN_VALUE = MIN_VALUE;
   public static final int MAX_VALUE = MAX_VALUE;
   static final char[] digits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
   private int value;

   public static String toString(int i, int radix) {
      if (radix < 2 || radix > 36) {
         radix = 10;
      }

      char[] buf = new char[33];
      boolean negative = i < 0;
      int charPos = 32;
      if (!negative) {
         i = -i;
      }

      while(i <= -radix) {
         buf[charPos--] = digits[-(i % radix)];
         i /= radix;
      }

      buf[charPos] = digits[-i];
      if (negative) {
         --charPos;
         buf[charPos] = '-';
      }

      return new String(buf, charPos, 33 - charPos);
   }

   public static String toHexString(int i) {
      return toUnsignedString(i, 4);
   }

   public static String toOctalString(int i) {
      return toUnsignedString(i, 3);
   }

   public static String toBinaryString(int i) {
      return toUnsignedString(i, 1);
   }

   private static String toUnsignedString(int i, int shift) {
      char[] buf = new char[32];
      int charPos = 32;
      int radix = 1 << shift;
      int mask = radix - 1;

      do {
         --charPos;
         buf[charPos] = digits[i & mask];
         i >>>= shift;
      } while(i != 0);

      return new String(buf, charPos, 32 - charPos);
   }

   public static String toString(int i) {
      return toString(i, 10);
   }

   public static int parseInt(String s, int radix) throws NumberFormatException {
      if (s == null) {
         throw new NumberFormatException("null");
      } else if (radix < 2) {
         throw new NumberFormatException("radix " + radix + " less than Character.MIN_RADIX");
      } else if (radix > 36) {
         throw new NumberFormatException("radix " + radix + " greater than Character.MAX_RADIX");
      } else {
         int result = 0;
         boolean negative = false;
         int i = 0;
         int max = s.length();
         if (max > 0) {
            int limit;
            if (s.charAt(0) == '-') {
               negative = true;
               limit = MIN_VALUE;
               ++i;
            } else {
               limit = -2147483647;
            }

            int multmin = limit / radix;
            int digit;
            if (i < max) {
               digit = Character.digit(s.charAt(i++), radix);
               if (digit < 0) {
                  throw new NumberFormatException(s);
               }

               result = -digit;
            }

            while(i < max) {
               digit = Character.digit(s.charAt(i++), radix);
               if (digit < 0) {
                  throw new NumberFormatException(s);
               }

               if (result < multmin) {
                  throw new NumberFormatException(s);
               }

               result *= radix;
               if (result < limit + digit) {
                  throw new NumberFormatException(s);
               }

               result -= digit;
            }

            if (negative) {
               if (i > 1) {
                  return result;
               } else {
                  throw new NumberFormatException(s);
               }
            } else {
               return -result;
            }
         } else {
            throw new NumberFormatException(s);
         }
      }
   }

   public static int parseInt(String s) throws NumberFormatException {
      return parseInt(s, 10);
   }

   public static Integer valueOf(String s, int radix) throws NumberFormatException {
      return new Integer(parseInt(s, radix));
   }

   public static Integer valueOf(String s) throws NumberFormatException {
      return new Integer(parseInt(s, 10));
   }

   public Integer(int value) {
      this.value = value;
   }

   public byte byteValue() {
      return (byte)this.value;
   }

   public short shortValue() {
      return (short)this.value;
   }

   public int intValue() {
      return this.value;
   }

   public long longValue() {
      return (long)this.value;
   }

   public float floatValue() {
      return (float)this.value;
   }

   public double doubleValue() {
      return (double)this.value;
   }

   public String toString() {
      return String.valueOf(this.value);
   }

   public int hashCode() {
      return this.value;
   }

   public boolean equals(Object obj) {
      if (obj instanceof Integer) {
         return this.value == (Integer)obj;
      } else {
         return false;
      }
   }
}
