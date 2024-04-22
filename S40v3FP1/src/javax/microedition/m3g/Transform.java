package javax.microedition.m3g;

public class Transform {
   byte[] matrix = new byte[72];

   public Transform() {
      this.setIdentity();
   }

   public Transform(Transform var1) {
      this.set(var1);
   }

   public void setIdentity() {
      _setIdentity(this.matrix);
   }

   public void set(Transform var1) {
      System.arraycopy(var1.matrix, 0, this.matrix, 0, this.matrix.length);
   }

   public void set(float[] var1) {
      _setMatrix(this.matrix, var1);
   }

   public void get(float[] var1) {
      _getMatrix(this.matrix, var1);
   }

   public void invert() {
      _invert(this.matrix);
   }

   public void transpose() {
      _transpose(this.matrix);
   }

   public void postMultiply(Transform var1) {
      _mul(this.matrix, this.matrix, var1.matrix);
   }

   public void postScale(float var1, float var2, float var3) {
      _scale(this.matrix, var1, var2, var3);
   }

   public void postRotate(float var1, float var2, float var3, float var4) {
      _rotate(this.matrix, var1, var2, var3, var4);
   }

   public void postRotateQuat(float var1, float var2, float var3, float var4) {
      _rotateQuat(this.matrix, var1, var2, var3, var4);
   }

   public void postTranslate(float var1, float var2, float var3) {
      _translate(this.matrix, var1, var2, var3);
   }

   public void transform(float[] var1) {
      if (var1.length % 4 != 0) {
         throw new IllegalArgumentException();
      } else {
         if (var1.length != 0) {
            _transformTable(this.matrix, var1);
         }

      }
   }

   public void transform(VertexArray var1, float[] var2, boolean var3) {
      if (var1 != null && var2 != null) {
         _transformArray(this.matrix, var1.handle, var2, var3);
      } else {
         throw new NullPointerException();
      }
   }

   private static native void _mul(byte[] var0, byte[] var1, byte[] var2);

   private static native void _setIdentity(byte[] var0);

   private static native void _setMatrix(byte[] var0, float[] var1);

   private static native void _getMatrix(byte[] var0, float[] var1);

   private static native void _invert(byte[] var0);

   private static native void _transpose(byte[] var0);

   private static native void _rotate(byte[] var0, float var1, float var2, float var3, float var4);

   private static native void _rotateQuat(byte[] var0, float var1, float var2, float var3, float var4);

   private static native void _scale(byte[] var0, float var1, float var2, float var3);

   private static native void _translate(byte[] var0, float var1, float var2, float var3);

   private static native void _transformTable(byte[] var0, float[] var1);

   private static native void _transformArray(byte[] var0, int var1, float[] var2, boolean var3);
}
