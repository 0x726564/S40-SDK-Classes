package javax.microedition.m3g;

import javax.microedition.lcdui.Image;

public class Image2D extends Object3D {
   public static final int ALPHA = 96;
   public static final int LUMINANCE = 97;
   public static final int LUMINANCE_ALPHA = 98;
   public static final int RGB = 99;
   public static final int RGBA = 100;

   public Image2D(int var1, Object var2) {
      super(checkAndCreate(var1, var2));
   }

   public Image2D(int var1, int var2, int var3, byte[] var4) {
      super(createHandle(var1, var2, var3, var4));
   }

   public Image2D(int var1, int var2, int var3, byte[] var4, byte[] var5) {
      super(createHandle(var1, var2, var3, var4, var5));
   }

   public Image2D(int var1, int var2, int var3) {
      super(createHandle(var1, var2, var3));
   }

   Image2D(int var1) {
      super(var1);
   }

   public void set(int var1, int var2, int var3, int var4, byte[] var5) {
      if (var5 == null) {
         throw new NullPointerException();
      } else {
         _set(this.handle, var1, var2, var3, var4, var5);
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

   private static int checkAndCreate(int var0, Object var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (!(var1 instanceof Image)) {
         throw new IllegalArgumentException();
      } else {
         Platform.gc();
         Image var2 = (Image)var1;
         int var3 = var2.getWidth();
         int var4 = var2.getHeight();
         int var5 = _ctorSize(Interface.getHandle(), var0, var3, var4, 0);
         int[] var6 = new int[var3];
         boolean var7 = !var2.isMutable() || var0 != 96;

         for(int var8 = 0; var8 < var4; ++var8) {
            var2.getRGB(var6, 0, var3, 0, var8, var3, 1);
            _setScanline(var5, var8, var7, var6);
         }

         _commit(var5);
         return var5;
      }
   }

   private static int createHandle(int var0, int var1, int var2, byte[] var3) {
      Platform.gc();
      return _ctorSizePixels(Interface.getHandle(), var0, var1, var2, var3);
   }

   private static int createHandle(int var0, int var1, int var2, byte[] var3, byte[] var4) {
      Platform.gc();
      return _ctorSizePixelsPalette(Interface.getHandle(), var0, var1, var2, var3, var4);
   }

   private static int createHandle(int var0, int var1, int var2) {
      Platform.gc();
      return _ctorSize(Interface.getHandle(), var0, var1, var2, 5);
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
