package javax.microedition.m3g;

public class PolygonMode extends Object3D {
   public static final int CULL_BACK = 160;
   public static final int CULL_FRONT = 161;
   public static final int CULL_NONE = 162;
   public static final int SHADE_FLAT = 164;
   public static final int SHADE_SMOOTH = 165;
   public static final int WINDING_CCW = 168;
   public static final int WINDING_CW = 169;

   public PolygonMode() {
      super(_ctor(Interface.getHandle()));
   }

   PolygonMode(int var1) {
      super(var1);
   }

   public void setCulling(int var1) {
      _setCulling(this.handle, var1);
   }

   public int getCulling() {
      return _getCulling(this.handle);
   }

   public void setWinding(int var1) {
      _setWinding(this.handle, var1);
   }

   public int getWinding() {
      return _getWinding(this.handle);
   }

   public void setShading(int var1) {
      _setShading(this.handle, var1);
   }

   public int getShading() {
      return _getShading(this.handle);
   }

   public void setTwoSidedLightingEnable(boolean var1) {
      _setTwoSidedLightingEnable(this.handle, var1);
   }

   public boolean isTwoSidedLightingEnabled() {
      return _isTwoSidedLightingEnabled(this.handle);
   }

   public void setLocalCameraLightingEnable(boolean var1) {
      _setLocalCameraLightingEnable(this.handle, var1);
   }

   public void setPerspectiveCorrectionEnable(boolean var1) {
      _setPerspectiveCorrectionEnable(this.handle, var1);
   }

   public boolean isLocalCameraLightingEnabled() {
      return _isLocalCameraLightingEnabled(this.handle);
   }

   public boolean isPerspectiveCorrectionEnabled() {
      return _isPerspectiveCorrectionEnabled(this.handle);
   }

   private static native int _ctor(int var0);

   private static native void _setLocalCameraLightingEnable(int var0, boolean var1);

   private static native void _setPerspectiveCorrectionEnable(int var0, boolean var1);

   private static native void _setCulling(int var0, int var1);

   private static native int _getCulling(int var0);

   private static native void _setWinding(int var0, int var1);

   private static native int _getWinding(int var0);

   private static native void _setShading(int var0, int var1);

   private static native int _getShading(int var0);

   private static native void _setTwoSidedLightingEnable(int var0, boolean var1);

   private static native boolean _isTwoSidedLightingEnabled(int var0);

   private static native boolean _isLocalCameraLightingEnabled(int var0);

   private static native boolean _isPerspectiveCorrectionEnabled(int var0);
}
