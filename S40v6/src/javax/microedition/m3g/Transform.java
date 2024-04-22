package javax.microedition.m3g;

public class Transform {
   byte[] matrix = new byte[72];

   public Transform() {
      this.setIdentity();
   }

   public Transform(Transform other) {
      this.set(other);
   }

   public void setIdentity() {
      _setIdentity(this.matrix);
   }

   public void set(Transform transform) {
      System.arraycopy(transform.matrix, 0, this.matrix, 0, this.matrix.length);
   }

   public void set(float[] matrix) {
      _setMatrix(this.matrix, matrix);
   }

   public void get(float[] matrix) {
      _getMatrix(this.matrix, matrix);
   }

   public void invert() {
      _invert(this.matrix);
   }

   public void transpose() {
      _transpose(this.matrix);
   }

   public void postMultiply(Transform transform) {
      _mul(this.matrix, this.matrix, transform.matrix);
   }

   public void postScale(float sx, float sy, float sz) {
      _scale(this.matrix, sx, sy, sz);
   }

   public void postRotate(float angle, float ax, float ay, float az) {
      _rotate(this.matrix, angle, ax, ay, az);
   }

   public void postRotateQuat(float qx, float qy, float qz, float qw) {
      _rotateQuat(this.matrix, qx, qy, qz, qw);
   }

   public void postTranslate(float tx, float ty, float tz) {
      _translate(this.matrix, tx, ty, tz);
   }

   public void transform(float[] v) {
      if (v.length % 4 != 0) {
         throw new IllegalArgumentException();
      } else {
         if (v.length != 0) {
            _transformTable(this.matrix, v);
         }

      }
   }

   public void transform(VertexArray in, float[] out, boolean W) {
      if (in != null && out != null) {
         _transformArray(this.matrix, in.handle, out, W);
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
