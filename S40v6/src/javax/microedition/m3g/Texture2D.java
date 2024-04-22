package javax.microedition.m3g;

public class Texture2D extends Transformable {
   public static final int FILTER_BASE_LEVEL = 208;
   public static final int FILTER_LINEAR = 209;
   public static final int FILTER_NEAREST = 210;
   public static final int FUNC_ADD = 224;
   public static final int FUNC_BLEND = 225;
   public static final int FUNC_DECAL = 226;
   public static final int FUNC_MODULATE = 227;
   public static final int FUNC_REPLACE = 228;
   public static final int WRAP_CLAMP = 240;
   public static final int WRAP_REPEAT = 241;
   private Image2D image;

   public Texture2D(Image2D image) {
      super(_ctor(Interface.getHandle(), image != null ? image.handle : 0));
      this.image = image;
   }

   Texture2D(int handle) {
      super(handle);
      this.image = (Image2D)getInstance(_getImage(handle));
   }

   public void setImage(Image2D image) {
      _setImage(this.handle, image != null ? image.handle : 0);
      this.image = image;
   }

   public Image2D getImage() {
      return this.image;
   }

   public void setFiltering(int levelFilter, int imageFilter) {
      _setFiltering(this.handle, levelFilter, imageFilter);
   }

   public void setWrapping(int wrapS, int wrapT) {
      _setWrapping(this.handle, wrapS, wrapT);
   }

   public int getWrappingS() {
      return _getWrappingS(this.handle);
   }

   public int getWrappingT() {
      return _getWrappingT(this.handle);
   }

   public void setBlending(int func) {
      _setBlending(this.handle, func);
   }

   public int getBlending() {
      return _getBlending(this.handle);
   }

   public void setBlendColor(int RGB) {
      _setBlendColor(this.handle, RGB);
   }

   public int getBlendColor() {
      return _getBlendColor(this.handle);
   }

   public int getImageFilter() {
      return _getImageFilter(this.handle);
   }

   public int getLevelFilter() {
      return _getLevelFilter(this.handle);
   }

   private static native int _ctor(int var0, int var1);

   private static native void _setImage(int var0, int var1);

   private static native int _getImage(int var0);

   private static native void _setFiltering(int var0, int var1, int var2);

   private static native void _setWrapping(int var0, int var1, int var2);

   private static native int _getWrappingS(int var0);

   private static native int _getWrappingT(int var0);

   private static native void _setBlending(int var0, int var1);

   private static native int _getBlending(int var0);

   private static native void _setBlendColor(int var0, int var1);

   private static native int _getBlendColor(int var0);

   private static native int _getImageFilter(int var0);

   private static native int _getLevelFilter(int var0);
}
