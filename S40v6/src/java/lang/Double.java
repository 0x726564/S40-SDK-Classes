package java.lang;

public final class Double {
   public static final double POSITIVE_INFINITY = POSITIVE_INFINITY;
   public static final double NEGATIVE_INFINITY = NEGATIVE_INFINITY;
   public static final double NaN = NaN;
   public static final double MAX_VALUE = MAX_VALUE;
   public static final double MIN_VALUE = longBitsToDouble(1L);
   private double value;

   public static String toString(double d) {
      return (new FloatingDecimal(d)).toJavaFormatString();
   }

   public static Double valueOf(String s) throws NumberFormatException {
      return new Double(FloatingDecimal.readJavaFormatString(s).doubleValue());
   }

   public static double parseDouble(String s) throws NumberFormatException {
      return FloatingDecimal.readJavaFormatString(s).doubleValue();
   }

   public static boolean isNaN(double v) {
      return v != v;
   }

   public static boolean isInfinite(double v) {
      return v == POSITIVE_INFINITY || v == NEGATIVE_INFINITY;
   }

   public Double(double value) {
      this.value = value;
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
      return (float)this.value;
   }

   public double doubleValue() {
      return this.value;
   }

   public int hashCode() {
      long bits = doubleToLongBits(this.value);
      return (int)(bits ^ bits >>> 32);
   }

   public boolean equals(Object obj) {
      return obj instanceof Double && doubleToLongBits(((Double)obj).value) == doubleToLongBits(this.value);
   }

   public static native long doubleToLongBits(double var0);

   public static native double longBitsToDouble(long var0);
}
