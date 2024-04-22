package java.lang;

public final class Math {
   public static final double E = 2.718281828459045D;
   public static final double PI = 3.141592653589793D;
   private static long negativeZeroFloatBits = (long)Float.floatToIntBits(-0.0F);
   private static long negativeZeroDoubleBits = Double.doubleToLongBits(-0.0D);

   private strictfp Math() {
   }

   public static native double sin(double var0);

   public static native double cos(double var0);

   public static native double tan(double var0);

   public static strictfp double toRadians(double var0) {
      return var0 / 180.0D * 3.141592653589793D;
   }

   public static strictfp double toDegrees(double var0) {
      return var0 * 180.0D / 3.141592653589793D;
   }

   public static native double sqrt(double var0);

   public static native double ceil(double var0);

   public static native double floor(double var0);

   public static strictfp int abs(int var0) {
      return var0 < 0 ? -var0 : var0;
   }

   public static strictfp long abs(long var0) {
      return var0 < 0L ? -var0 : var0;
   }

   public static strictfp float abs(float var0) {
      return var0 <= 0.0F ? 0.0F - var0 : var0;
   }

   public static strictfp double abs(double var0) {
      return var0 <= 0.0D ? 0.0D - var0 : var0;
   }

   public static strictfp int max(int var0, int var1) {
      return var0 >= var1 ? var0 : var1;
   }

   public static strictfp long max(long var0, long var2) {
      return var0 >= var2 ? var0 : var2;
   }

   public static strictfp float max(float var0, float var1) {
      if (var0 != var0) {
         return var0;
      } else if (var0 == 0.0F && var1 == 0.0F && (long)Float.floatToIntBits(var0) == negativeZeroFloatBits) {
         return var1;
      } else {
         return var0 >= var1 ? var0 : var1;
      }
   }

   public static strictfp double max(double var0, double var2) {
      if (var0 != var0) {
         return var0;
      } else if (var0 == 0.0D && var2 == 0.0D && Double.doubleToLongBits(var0) == negativeZeroDoubleBits) {
         return var2;
      } else {
         return var0 >= var2 ? var0 : var2;
      }
   }

   public static strictfp int min(int var0, int var1) {
      return var0 <= var1 ? var0 : var1;
   }

   public static strictfp long min(long var0, long var2) {
      return var0 <= var2 ? var0 : var2;
   }

   public static strictfp float min(float var0, float var1) {
      if (var0 != var0) {
         return var0;
      } else if (var0 == 0.0F && var1 == 0.0F && (long)Float.floatToIntBits(var1) == negativeZeroFloatBits) {
         return var1;
      } else {
         return var0 <= var1 ? var0 : var1;
      }
   }

   public static strictfp double min(double var0, double var2) {
      if (var0 != var0) {
         return var0;
      } else if (var0 == 0.0D && var2 == 0.0D && Double.doubleToLongBits(var2) == negativeZeroDoubleBits) {
         return var2;
      } else {
         return var0 <= var2 ? var0 : var2;
      }
   }
}
