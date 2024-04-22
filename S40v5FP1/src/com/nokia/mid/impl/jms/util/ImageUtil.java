package com.nokia.mid.impl.jms.util;

public class ImageUtil {
   public static final int IMAGE_FORMAT_JPG = 0;
   public static final int IMAGE_FORMAT_GIF = 1;
   public static final int IMAGE_FORMAT_PNG = 2;
   public static final int IMAGE_FORMAT_BMP = 3;

   public static byte[] convert(byte[] imgData, int w, int h, int imgType) throws ImageException, IllegalArgumentException, OutOfMemoryError {
      if ((imgType == 0 || imgType == 1 || imgType == 2 || imgType == 3) && w >= 0 && h >= 0 && imgData != null && imgData.length > 0) {
         byte[] data = Convert0(imgData, w, h, imgType);
         return data;
      } else {
         throw new IllegalArgumentException("Invalid argument");
      }
   }

   private static native byte[] Convert0(byte[] var0, int var1, int var2, int var3);
}
