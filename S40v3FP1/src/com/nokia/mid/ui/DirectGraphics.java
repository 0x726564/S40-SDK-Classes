package com.nokia.mid.ui;

import javax.microedition.lcdui.Image;

public interface DirectGraphics {
   int FLIP_HORIZONTAL = 8192;
   int FLIP_VERTICAL = 16384;
   int ROTATE_90 = 90;
   int ROTATE_180 = 180;
   int ROTATE_270 = 270;
   int TYPE_BYTE_1_GRAY_VERTICAL = -1;
   int TYPE_BYTE_1_GRAY = 1;
   int TYPE_BYTE_2_GRAY = 2;
   int TYPE_BYTE_4_GRAY = 4;
   int TYPE_BYTE_8_GRAY = 8;
   int TYPE_BYTE_332_RGB = 332;
   int TYPE_USHORT_4444_ARGB = 4444;
   int TYPE_USHORT_444_RGB = 444;
   int TYPE_USHORT_555_RGB = 555;
   int TYPE_USHORT_1555_ARGB = 1555;
   int TYPE_USHORT_565_RGB = 565;
   int TYPE_INT_888_RGB = 888;
   int TYPE_INT_8888_ARGB = 8888;

   int getAlphaComponent();

   void setARGBColor(int var1);

   void drawImage(Image var1, int var2, int var3, int var4, int var5);

   void drawTriangle(int var1, int var2, int var3, int var4, int var5, int var6, int var7);

   void fillTriangle(int var1, int var2, int var3, int var4, int var5, int var6, int var7);

   void drawPolygon(int[] var1, int var2, int[] var3, int var4, int var5, int var6);

   void fillPolygon(int[] var1, int var2, int[] var3, int var4, int var5, int var6);

   void drawPixels(int[] var1, boolean var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10);

   void getPixels(int[] var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8);

   void getPixels(byte[] var1, byte[] var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9);

   void getPixels(short[] var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8);

   void drawPixels(byte[] var1, byte[] var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10);

   void drawPixels(short[] var1, boolean var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10);

   int getNativePixelFormat();
}
