package javax.microedition.m3g;

public abstract class Transformable extends Object3D {
   Transformable(int var1) {
      super(var1);
   }

   public void setOrientation(float var1, float var2, float var3, float var4) {
      _setOrientation(this.handle, var1, var2, var3, var4, true);
   }

   public void postRotate(float var1, float var2, float var3, float var4) {
      _setOrientation(this.handle, var1, var2, var3, var4, false);
   }

   public void preRotate(float var1, float var2, float var3, float var4) {
      _preRotate(this.handle, var1, var2, var3, var4);
   }

   public void getOrientation(float[] var1) {
      _getOrientation(this.handle, var1);
   }

   public void setScale(float var1, float var2, float var3) {
      _setScale(this.handle, var1, var2, var3, true);
   }

   public void scale(float var1, float var2, float var3) {
      _setScale(this.handle, var1, var2, var3, false);
   }

   public void getScale(float[] var1) {
      _getScale(this.handle, var1);
   }

   public void setTranslation(float var1, float var2, float var3) {
      _setTranslation(this.handle, var1, var2, var3, true);
   }

   public void translate(float var1, float var2, float var3) {
      _setTranslation(this.handle, var1, var2, var3, false);
   }

   public void getTranslation(float[] var1) {
      _getTranslation(this.handle, var1);
   }

   public void setTransform(Transform var1) {
      _setTransform(this.handle, var1 != null ? var1.s : null);
   }

   public void getTransform(Transform var1) {
      _getTransform(this.handle, var1.s);
   }

   public void getCompositeTransform(Transform var1) {
      _getComposite(this.handle, var1.s);
   }

   private static native void _setOrientation(int var0, float var1, float var2, float var3, float var4, boolean var5);

   private static native void _preRotate(int var0, float var1, float var2, float var3, float var4);

   private static native void _getOrientation(int var0, float[] var1);

   private static native void _setScale(int var0, float var1, float var2, float var3, boolean var4);

   private static native void _getScale(int var0, float[] var1);

   private static native void _setTranslation(int var0, float var1, float var2, float var3, boolean var4);

   private static native void _getTranslation(int var0, float[] var1);

   private static native void _setTransform(int var0, byte[] var1);

   private static native void _getTransform(int var0, byte[] var1);

   private static native void _getComposite(int var0, byte[] var1);
}
