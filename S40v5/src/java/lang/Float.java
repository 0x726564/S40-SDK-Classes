package java.lang;

public final class Float {
   public static final float POSITIVE_INFINITY = POSITIVE_INFINITY;
   public static final float NEGATIVE_INFINITY = NEGATIVE_INFINITY;
   public static final float NaN = NaN;
   public static final float MAX_VALUE = MAX_VALUE;
   public static final float MIN_VALUE = MIN_VALUE;
   private float value;

   public static String toString(float var0) {
      return (new FloatingDecimal(var0)).toJavaFormatString();
   }

   public static Float valueOf(String var0) throws NumberFormatException {
      return new Float(FloatingDecimal.K(var0).floatValue());
   }

   public static float parseFloat(String var0) throws NumberFormatException {
      return FloatingDecimal.K(var0).floatValue();
   }

   public static boolean isNaN(float var0) {
      return var0 != var0;
   }

   public static boolean isInfinite(float var0) {
      return var0 == POSITIVE_INFINITY || var0 == NEGATIVE_INFINITY;
   }

   public Float(float var1) {
      this.value = var1;
   }

   public Float(double var1) {
      this.value = (float)var1;
   }

   public final boolean isNaN() {
      return isNaN(this.value);
   }

   public final boolean isInfinite() {
      return isInfinite(this.value);
   }

   public final String toString() {
      return String.valueOf(this.value);
   }

   public final byte byteValue() {
      return (byte)((int)this.value);
   }

   public final short shortValue() {
      return (short)((int)this.value);
   }

   public final int intValue() {
      return (int)this.value;
   }

   public final long longValue() {
      return (long)this.value;
   }

   public final float floatValue() {
      return this.value;
   }

   public final double doubleValue() {
      return (double)this.value;
   }

   public final int hashCode() {
      return floatToIntBits(this.value);
   }

   public final boolean equals(Object var1) {
      return var1 instanceof Float && floatToIntBits(((Float)var1).value) == floatToIntBits(this.value);
   }

   public static native int floatToIntBits(float var0);

   public static native float intBitsToFloat(int var0);
}
