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

   public static strictfp double toRadians(double angdeg) {
      return angdeg / 180.0D * 3.141592653589793D;
   }

   public static strictfp double toDegrees(double angrad) {
      return angrad * 180.0D / 3.141592653589793D;
   }

   public static native double sqrt(double var0);

   public static native double ceil(double var0);

   public static native double floor(double var0);

   public static strictfp int abs(int a) {
      return a < 0 ? -a : a;
   }

   public static strictfp long abs(long a) {
      return a < 0L ? -a : a;
   }

   public static strictfp float abs(float a) {
      return a <= 0.0F ? 0.0F - a : a;
   }

   public static strictfp double abs(double a) {
      return a <= 0.0D ? 0.0D - a : a;
   }

   public static strictfp int max(int a, int b) {
      return a >= b ? a : b;
   }

   public static strictfp long max(long a, long b) {
      return a >= b ? a : b;
   }

   public static strictfp float max(float a, float b) {
      if (a != a) {
         return a;
      } else if (a == 0.0F && b == 0.0F && (long)Float.floatToIntBits(a) == negativeZeroFloatBits) {
         return b;
      } else {
         return a >= b ? a : b;
      }
   }

   public static strictfp double max(double a, double b) {
      if (a != a) {
         return a;
      } else if (a == 0.0D && b == 0.0D && Double.doubleToLongBits(a) == negativeZeroDoubleBits) {
         return b;
      } else {
         return a >= b ? a : b;
      }
   }

   public static strictfp int min(int a, int b) {
      return a <= b ? a : b;
   }

   public static strictfp long min(long a, long b) {
      return a <= b ? a : b;
   }

   public static strictfp float min(float a, float b) {
      if (a != a) {
         return a;
      } else if (a == 0.0F && b == 0.0F && (long)Float.floatToIntBits(b) == negativeZeroFloatBits) {
         return b;
      } else {
         return a <= b ? a : b;
      }
   }

   public static strictfp double min(double a, double b) {
      if (a != a) {
         return a;
      } else if (a == 0.0D && b == 0.0D && Double.doubleToLongBits(b) == negativeZeroDoubleBits) {
         return b;
      } else {
         return a <= b ? a : b;
      }
   }
}
