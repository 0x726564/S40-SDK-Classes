package javax.microedition.m3g;

public class Background extends Object3D {
   public static final int BORDER = 32;
   public static final int REPEAT = 33;
   private Image2D t;

   public Background() {
      super(_ctor(Interface.getHandle()));
   }

   Background(int var1) {
      super(var1);
      this.t = (Image2D)getInstance(_getImage(var1));
   }

   public void setColor(int var1) {
      _setColor(this.handle, var1);
   }

   public int getColor() {
      return _getColor(this.handle);
   }

   public void setImage(Image2D var1) {
      _setImage(this.handle, var1 != null ? var1.handle : 0);
      this.t = var1;
   }

   public Image2D getImage() {
      return this.t;
   }

   public void setImageMode(int var1, int var2) {
      _setImageMode(this.handle, var1, var2);
   }

   public int getImageModeX() {
      return _getImageMode(this.handle, 0);
   }

   public int getImageModeY() {
      return _getImageMode(this.handle, 1);
   }

   public void setColorClearEnable(boolean var1) {
      _enable(this.handle, 0, var1);
   }

   public void setDepthClearEnable(boolean var1) {
      _enable(this.handle, 1, var1);
   }

   public boolean isColorClearEnabled() {
      return _isEnabled(this.handle, 0);
   }

   public boolean isDepthClearEnabled() {
      return _isEnabled(this.handle, 1);
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
