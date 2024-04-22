package com.nokia.mid.impl.jms.util;

public class ImageUtil {
   public static final int IMAGE_FORMAT_JPG = 0;
   public static final int IMAGE_FORMAT_GIF = 1;
   public static final int IMAGE_FORMAT_PNG = 2;
   public static final int IMAGE_FORMAT_BMP = 3;

   public static byte[] convert(byte[] var0, int var1, int var2, int var3) throws ImageException, IllegalArgumentException, OutOfMemoryError {
      if ((var3 == 0 || var3 == 1 || var3 == 2 || var3 == 3) && var1 >= 0 && var2 >= 0 && var0 != null && var0.length > 0) {
         byte[] var4 = Convert0(var0, var1, var2, var3);
         return var4;
      } else {
         throw new IllegalArgumentException("Invalid argument");
      }
   }

   private static native byte[] Convert0(byte[] var0, int var1, int var2, int var3);
}
