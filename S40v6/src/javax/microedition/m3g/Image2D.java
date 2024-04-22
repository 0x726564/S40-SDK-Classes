package javax.microedition.m3g;

import javax.microedition.lcdui.Image;

public class Image2D extends Object3D {
   public static final int ALPHA = 96;
   public static final int LUMINANCE = 97;
   public static final int LUMINANCE_ALPHA = 98;
   public static final int RGB = 99;
   public static final int RGBA = 100;

   public Image2D(int format, Object image) {
      super(checkAndCreate(format, image));
   }

   public Image2D(int format, int width, int height, byte[] image) {
      super(createHandle(format, width, height, image));
   }

   public Image2D(int format, int width, int height, byte[] image, byte[] palette) {
      super(createHandle(format, width, height, image, palette));
   }

   public Image2D(int format, int width, int height) {
      super(createHandle(format, width, height));
   }

   Image2D(int handle) {
      super(handle);
   }

   public void set(int x, int y, int width, int height, byte[] image) {
      if (image == null) {
         throw new NullPointerException();
      } else {
         _set(this.handle, x, y, width, height, image);
      }
   }

   public boolean isMutable() {
      return _isMutable(this.handle);
   }

   public int getFormat() {
      return _getFormat(this.handle);
   }

   public int getWidth() {
      return _getWidth(this.handle);
   }

   public int getHeight() {
      return _getHeight(this.handle);
   }

   private static int checkAndCreate(int format, Object image) {
      if (image == null) {
         throw new NullPointerException();
      } else if (!(image instanceof Image)) {
         throw new IllegalArgumentException();
      } else {
         Platform.gc();
         Image i = (Image)image;
         int width = i.getWidth();
         int height = i.getHeight();
         int i2d = _ctorSize(Interface.getHandle(), format, width, height, 0);
         int[] pixels = new int[width];
         boolean trueAlpha = !i.isMutable() || format != 96;

         for(int y = 0; y < height; ++y) {
            i.getRGB(pixels, 0, width, 0, y, width, 1);
            _setScanline(i2d, y, trueAlpha, pixels);
         }

         _commit(i2d);
         return i2d;
      }
   }

   private static int createHandle(int format, int width, int height, byte[] image) {
      Platform.gc();
      return _ctorSizePixels(Interface.getHandle(), format, width, height, image);
   }

   private static int createHandle(int format, int width, int height, byte[] image, byte[] palette) {
      Platform.gc();
      return _ctorSizePixelsPalette(Interface.getHandle(), format, width, height, image, palette);
   }

   private static int createHandle(int format, int width, int height) {
      Platform.gc();
      return _ctorSize(Interface.getHandle(), format, width, height, 5);
   }

   private static native int _ctorSizePixels(int var0, int var1, int var2, int var3, byte[] var4);

   private static native int _ctorSizePixelsPalette(int var0, int var1, int var2, int var3, byte[] var4, byte[] var5);

   private static native int _ctorSize(int var0, int var1, int var2, int var3, int var4);

   private static native void _set(int var0, int var1, int var2, int var3, int var4, byte[] var5);

   private static native boolean _isMutable(int var0);

   private static native int _getFormat(int var0);

   private static native int _getWidth(int var0);

   private static native int _getHeight(int var0);

   private static native void _setScanline(int var0, int var1, boolean var2, int[] var3);

   private static native void _commit(int var0);
}
