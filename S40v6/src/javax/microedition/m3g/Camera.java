package javax.microedition.m3g;

public class Camera extends Node {
   public static final int GENERIC = 48;
   public static final int PARALLEL = 49;
   public static final int PERSPECTIVE = 50;

   public Camera() {
      super(_ctor(Interface.getHandle()));
   }

   Camera(int handle) {
      super(handle);
   }

   public void setParallel(float height, float aspectRatio, float near, float far) {
      _setParallel(this.handle, height, aspectRatio, near, far);
   }

   public void setPerspective(float fovy, float aspectRatio, float near, float far) {
      _setPerspective(this.handle, fovy, aspectRatio, near, far);
   }

   public void setGeneric(Transform transform) {
      _setGeneric(this.handle, transform.matrix);
   }

   public int getProjection(Transform transform) {
      return _getProjectionAsTransform(this.handle, transform != null ? transform.matrix : null);
   }

   public int getProjection(float[] params) {
      return _getProjectionAsParams(this.handle, params);
   }

   private static native int _ctor(int var0);

   private static native void _setParallel(int var0, float var1, float var2, float var3, float var4);

   private static native void _setPerspective(int var0, float var1, float var2, float var3, float var4);

   private static native void _setGeneric(int var0, byte[] var1);

   private static native int _getProjectionAsTransform(int var0, byte[] var1);

   private static native int _getProjectionAsParams(int var0, float[] var1);
}
