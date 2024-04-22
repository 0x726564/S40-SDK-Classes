package java.lang;

public final class Long {
   public static final long MIN_VALUE = MIN_VALUE;
   public static final long MAX_VALUE = MAX_VALUE;
   private long value;

   public static String toString(long var0, int var2) {
      if (var2 < 2 || var2 > 36) {
         var2 = 10;
      }

      char[] var3 = new char[65];
      int var4 = 64;
      boolean var5 = var0 < 0L;
      if (!var5) {
         var0 = -var0;
      }

      while(var0 <= (long)(-var2)) {
         var3[var4--] = Integer.digits[(int)(-(var0 % (long)var2))];
         var0 /= (long)var2;
      }

      var3[var4] = Integer.digits[(int)(-var0)];
      if (var5) {
         --var4;
         var3[var4] = '-';
      }

      return new String(var3, var4, 65 - var4);
   }

   public static String toString(long var0) {
      return toString(var0, 10);
   }

   public static long parseLong(String var0, int var1) throws NumberFormatException {
      if (var0 == null) {
         throw new NumberFormatException("null");
      } else if (var1 < 2) {
         throw new NumberFormatException("radix " + var1 + " less than Character.MIN_RADIX");
      } else if (var1 > 36) {
         throw new NumberFormatException("radix " + var1 + " greater than Character.MAX_RADIX");
      } else {
         long var2 = 0L;
         boolean var4 = false;
         int var5 = 0;
         int var6 = var0.length();
         if (var6 > 0) {
            long var7;
            if (var0.charAt(0) == '-') {
               var4 = true;
               var7 = MIN_VALUE;
               ++var5;
            } else {
               var7 = -9223372036854775807L;
            }

            long var9 = var7 / (long)var1;
            int var11;
            if (var5 < var6) {
               var11 = Character.digit(var0.charAt(var5++), var1);
               if (var11 < 0) {
                  throw new NumberFormatException(var0);
               }

               var2 = (long)(-var11);
            }

            while(var5 < var6) {
               var11 = Character.digit(var0.charAt(var5++), var1);
               if (var11 < 0) {
                  throw new NumberFormatException(var0);
               }

               if (var2 < var9) {
                  throw new NumberFormatException(var0);
               }

               var2 *= (long)var1;
               if (var2 < var7 + (long)var11) {
                  throw new NumberFormatException(var0);
               }

               var2 -= (long)var11;
            }

            if (var4) {
               if (var5 > 1) {
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

   public static long parseLong(String var0) throws NumberFormatException {
      return parseLong(var0, 10);
   }

   public Long(long var1) {
      this.value = var1;
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

   public boolean equals(Object var1) {
      if (var1 instanceof Long) {
         return this.value == (Long)var1;
      } else {
         return false;
      }
   }
}
