package java.lang;

public final class Long {
   public static final long MIN_VALUE = MIN_VALUE;
   public static final long MAX_VALUE = MAX_VALUE;
   private long value;

   public static String toString(long i, int radix) {
      if (radix < 2 || radix > 36) {
         radix = 10;
      }

      char[] buf = new char[65];
      int charPos = 64;
      boolean negative = i < 0L;
      if (!negative) {
         i = -i;
      }

      while(i <= (long)(-radix)) {
         buf[charPos--] = Integer.digits[(int)(-(i % (long)radix))];
         i /= (long)radix;
      }

      buf[charPos] = Integer.digits[(int)(-i)];
      if (negative) {
         --charPos;
         buf[charPos] = '-';
      }

      return new String(buf, charPos, 65 - charPos);
   }

   public static String toString(long i) {
      return toString(i, 10);
   }

   public static long parseLong(String s, int radix) throws NumberFormatException {
      if (s == null) {
         throw new NumberFormatException("null");
      } else if (radix < 2) {
         throw new NumberFormatException("radix " + radix + " less than Character.MIN_RADIX");
      } else if (radix > 36) {
         throw new NumberFormatException("radix " + radix + " greater than Character.MAX_RADIX");
      } else {
         long result = 0L;
         boolean negative = false;
         int i = 0;
         int max = s.length();
         if (max > 0) {
            long limit;
            if (s.charAt(0) == '-') {
               negative = true;
               limit = MIN_VALUE;
               ++i;
            } else {
               limit = -9223372036854775807L;
            }

            long multmin = limit / (long)radix;
            int digit;
            if (i < max) {
               digit = Character.digit(s.charAt(i++), radix);
               if (digit < 0) {
                  throw new NumberFormatException(s);
               }

               result = (long)(-digit);
            }

            while(i < max) {
               digit = Character.digit(s.charAt(i++), radix);
               if (digit < 0) {
                  throw new NumberFormatException(s);
               }

               if (result < multmin) {
                  throw new NumberFormatException(s);
               }

               result *= (long)radix;
               if (result < limit + (long)digit) {
                  throw new NumberFormatException(s);
               }

               result -= (long)digit;
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

   public static long parseLong(String s) throws NumberFormatException {
      return parseLong(s, 10);
   }

   public Long(long value) {
      this.value = value;
   }

   public long longValue() {
      return this.value;
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
      return (int)(this.value ^ this.value >> 32);
   }

   public boolean equals(Object obj) {
      if (obj instanceof Long) {
         return this.value == (Long)obj;
      } else {
         return false;
      }
   }
}
