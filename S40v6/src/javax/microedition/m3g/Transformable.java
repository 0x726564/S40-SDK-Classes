package javax.microedition.m3g;

public abstract class Transformable extends Object3D {
   Transformable(int handle) {
      super(handle);
   }

   public void setOrientation(float angle, float ax, float ay, float az) {
      _setOrientation(this.handle, angle, ax, ay, az, true);
   }

   public void postRotate(float angle, float ax, float ay, float az) {
      _setOrientation(this.handle, angle, ax, ay, az, false);
   }

   public void preRotate(float angle, float ax, float ay, float az) {
      _preRotate(this.handle, angle, ax, ay, az);
   }

   public void getOrientation(float[] angleAxis) {
      _getOrientation(this.handle, angleAxis);
   }

   public void setScale(float sx, float sy, float sz) {
      _setScale(this.handle, sx, sy, sz, true);
   }

   public void scale(float sx, float sy, float sz) {
      _setScale(this.handle, sx, sy, sz, false);
   }

   public void getScale(float[] xyz) {
      _getScale(this.handle, xyz);
   }

   public void setTranslation(float tx, float ty, float tz) {
      _setTranslation(this.handle, tx, ty, tz, true);
   }

   public void translate(float tx, float ty, float tz) {
      _setTranslation(this.handle, tx, ty, tz, false);
   }

   public void getTranslation(float[] xyz) {
      _getTranslation(this.handle, xyz);
   }

   public void setTransform(Transform transform) {
      _setTransform(this.handle, transform != null ? transform.matrix : null);
   }

   public void getTransform(Transform transform) {
      _getTransform(this.handle, transform.matrix);
   }

   public void getCompositeTransform(Transform transform) {
      _getComposite(this.handle, transform.matrix);
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
