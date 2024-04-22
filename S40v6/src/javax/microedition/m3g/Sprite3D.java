package javax.microedition.m3g;

public class Sprite3D extends Node {
   private Image2D image;
   private Appearance appearance;

   public Sprite3D(boolean scaled, Image2D image, Appearance appearance) {
      super(_ctor(Interface.getHandle(), scaled, image != null ? image.handle : 0, appearance != null ? appearance.handle : 0));
      this.image = image;
      this.appearance = appearance;
   }

   Sprite3D(int handle) {
      super(handle);
      this.image = (Image2D)getInstance(_getImage(handle));
      this.appearance = (Appearance)getInstance(_getAppearance(handle));
   }

   public boolean isScaled() {
      return _isScaled(this.handle);
   }

   public void setAppearance(Appearance appearance) {
      _setAppearance(this.handle, appearance != null ? appearance.handle : 0);
      this.appearance = appearance;
   }

   public Appearance getAppearance() {
      return this.appearance;
   }

   public void setImage(Image2D image) {
      _setImage(this.handle, image != null ? image.handle : 0);
      this.image = image;
   }

   public Image2D getImage() {
      return this.image;
   }

   public void setCrop(int cropX, int cropY, int width, int height) {
      _setCrop(this.handle, cropX, cropY, width, height);
   }

   public int getCropX() {
      return _getCrop(this.handle, 0);
   }

   public int getCropY() {
      return _getCrop(this.handle, 1);
   }

   public int getCropWidth() {
      return _getCrop(this.handle, 2);
   }

   public int getCropHeight() {
      return _getCrop(this.handle, 3);
   }

   private static native int _ctor(int var0, boolean var1, int var2, int var3);

   private static native boolean _isScaled(int var0);

   private static native void _setAppearance(int var0, int var1);

   private static native void _setImage(int var0, int var1);

   private static native void _setCrop(int var0, int var1, int var2, int var3, int var4);

   private static native int _getCrop(int var0, int var1);

   private static native int _getAppearance(int var0);

   private static native int _getImage(int var0);
}
