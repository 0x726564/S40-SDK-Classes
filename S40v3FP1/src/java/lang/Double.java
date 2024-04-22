package java.lang;

public final class Double {
   public static final double POSITIVE_INFINITY = POSITIVE_INFINITY;
   public static final double NEGATIVE_INFINITY = NEGATIVE_INFINITY;
   public static final double NaN = NaN;
   public static final double MAX_VALUE = MAX_VALUE;
   public static final double MIN_VALUE = longBitsToDouble(1L);
   private double value;

   public static String toString(double var0) {
      return (new FloatingDecimal(var0)).toJavaFormatString();
   }

   public static Double valueOf(String var0) throws NumberFormatException {
      return new Double(FloatingDecimal.readJavaFormatString(var0).doubleValue());
   }

   public static double parseDouble(String var0) throws NumberFormatException {
      return FloatingDecimal.readJavaFormatString(var0).doubleValue();
   }

   public static boolean isNaN(double var0) {
      return var0 != var0;
   }

   public static boolean isInfinite(double var0) {
      return var0 == POSITIVE_INFINITY || var0 == NEGATIVE_INFINITY;
   }

   public Double(double var1) {
      this.value = var1;
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
      long var1 = doubleToLongBits(this.value);
      return (int)(var1 ^ var1 >>> 32);
   }

   public boolean equals(Object var1) {
      return var1 instanceof Double && doubleToLongBits(((Double)var1).value) == doubleToLongBits(this.value);
   }

   public static native long doubleToLongBits(double var0);

   public static native double longBitsToDouble(long var0);
}
