package java.lang;

public final class Float {
   public static final float POSITIVE_INFINITY = POSITIVE_INFINITY;
   public static final float NEGATIVE_INFINITY = NEGATIVE_INFINITY;
   public static final float NaN = NaN;
   public static final float MAX_VALUE = MAX_VALUE;
   public static final float MIN_VALUE = MIN_VALUE;
   private float value;

   public static String toString(float f) {
      return (new FloatingDecimal(f)).toJavaFormatString();
   }

   public static Float valueOf(String s) throws NumberFormatException {
      return new Float(FloatingDecimal.readJavaFormatString(s).floatValue());
   }

   public static float parseFloat(String s) throws NumberFormatException {
      return FloatingDecimal.readJavaFormatString(s).floatValue();
   }

   public static boolean isNaN(float v) {
      return v != v;
   }

   public static boolean isInfinite(float v) {
      return v == POSITIVE_INFINITY || v == NEGATIVE_INFINITY;
   }

   public Float(float value) {
      this.value = value;
   }

   public Float(double value) {
      this.value = (float)value;
   }

   public boolean isNaN() {
      return isNaN(this.value);
   }

   public boolean isInfinite() {
      return isInfinite(this.value);
   }

   public String toString() {
      return String.valueOf(this.value);
   }

   public byte byteValue() {
      return (byte)((int)this.value);
   }

   public short shortValue() {
      return (short)((int)this.value);
   }

   public int intValue() {
      return (int)this.value;
   }

   public long longValue() {
      return (long)this.value;
   }

   public float floatValue() {
      return this.value;
   }

   public double doubleValue() {
      return (double)this.value;
   }

   public int hashCode() {
      return floatToIntBits(this.value);
   }

   public boolean equals(Object obj) {
      return obj instanceof Float && floatToIntBits(((Float)obj).value) == floatToIntBits(this.value);
   }

   public static native int floatToIntBits(float var0);

   public static native float intBitsToFloat(int var0);
}
