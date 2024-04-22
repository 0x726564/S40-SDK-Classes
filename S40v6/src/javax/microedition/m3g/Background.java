package javax.microedition.m3g;

public class Background extends Object3D {
   public static final int BORDER = 32;
   public static final int REPEAT = 33;
   private Image2D image;

   public Background() {
      super(_ctor(Interface.getHandle()));
   }

   Background(int handle) {
      super(handle);
      this.image = (Image2D)getInstance(_getImage(handle));
   }

   public void setColor(int ARGB) {
      _setColor(this.handle, ARGB);
   }

   public int getColor() {
      return _getColor(this.handle);
   }

   public void setImage(Image2D image) {
      _setImage(this.handle, image != null ? image.handle : 0);
      this.image = image;
   }

   public Image2D getImage() {
      return this.image;
   }

   public void setImageMode(int modeX, int modeY) {
      _setImageMode(this.handle, modeX, modeY);
   }

   public int getImageModeX() {
      return _getImageMode(this.handle, 0);
   }

   public int getImageModeY() {
      return _getImageMode(this.handle, 1);
   }

   public void setColorClearEnable(boolean enable) {
      _enable(this.handle, 0, enable);
   }

   public void setDepthClearEnable(boolean enable) {
      _enable(this.handle, 1, enable);
   }

   public boolean isColorClearEnabled() {
      return _isEnabled(this.handle, 0);
   }

   public boolean isDepthClearEnabled() {
      return _isEnabled(this.handle, 1);
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

   private static native int _ctor(int var0);

   private static native void _setColor(int var0, int var1);

   private static native int _getColor(int var0);

   private static native void _setImage(int var0, int var1);

   private static native int _getImage(int var0);

   private static native void _setImageMode(int var0, int var1, int var2);

   private static native int _getImageMode(int var0, int var1);

   private static native void _enable(int var0, int var1, boolean var2);

   private static native boolean _isEnabled(int var0, int var1);

   private static native void _setCrop(int var0, int var1, int var2, int var3, int var4);

   private static native int _getCrop(int var0, int var1);
}
