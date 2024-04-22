package javax.microedition.m3g;

public class Sprite3D extends Node {
   private Image2D t;
   private Appearance u;

   public Sprite3D(boolean var1, Image2D var2, Appearance var3) {
      super(_ctor(Interface.getHandle(), var1, var2 != null ? var2.handle : 0, var3 != null ? var3.handle : 0));
      this.t = var2;
      this.u = var3;
   }

   Sprite3D(int var1) {
      super(var1);
      this.t = (Image2D)getInstance(_getImage(var1));
      this.u = (Appearance)getInstance(_getAppearance(var1));
   }

   public boolean isScaled() {
      return _isScaled(this.handle);
   }

   public void setAppearance(Appearance var1) {
      _setAppearance(this.handle, var1 != null ? var1.handle : 0);
      this.u = var1;
   }

   public Appearance getAppearance() {
      return this.u;
   }

   public void setImage(Image2D var1) {
      _setImage(this.handle, var1 != null ? var1.handle : 0);
      this.t = var1;
   }

   public Image2D getImage() {
      return this.t;
   }

   public void setCrop(int var1, int var2, int var3, int var4) {
      _setCrop(this.handle, var1, var2, var3, var4);
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
