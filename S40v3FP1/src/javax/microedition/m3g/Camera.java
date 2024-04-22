package javax.microedition.m3g;

public class Camera extends Node {
   public static final int GENERIC = 48;
   public static final int PARALLEL = 49;
   public static final int PERSPECTIVE = 50;

   public Camera() {
      super(_ctor(Interface.getHandle()));
   }

   Camera(int var1) {
      super(var1);
   }

   public void setParallel(float var1, float var2, float var3, float var4) {
      _setParallel(this.handle, var1, var2, var3, var4);
   }

   public void setPerspective(float var1, float var2, float var3, float var4) {
      _setPerspective(this.handle, var1, var2, var3, var4);
   }

   public void setGeneric(Transform var1) {
      _setGeneric(this.handle, var1.matrix);
   }

   public int getProjection(Transform var1) {
      return _getProjectionAsTransform(this.handle, var1 != null ? var1.matrix : null);
   }

   public int getProjection(float[] var1) {
      return _getProjectionAsParams(this.handle, var1);
   }

   private static native int _ctor(int var0);

   private static native void _setParallel(int var0, float var1, float var2, float var3, float var4);

   private static native void _setPerspective(int var0, float var1, float var2, float var3, float var4);

   private static native void _setGeneric(int var0, byte[] var1);

   private static native int _getProjectionAsTransform(int var0, byte[] var1);

   private static native int _getProjectionAsParams(int var0, float[] var1);
}
