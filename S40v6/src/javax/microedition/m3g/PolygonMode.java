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

   PolygonMode(int handle) {
      super(handle);
   }

   public void setCulling(int mode) {
      _setCulling(this.handle, mode);
   }

   public int getCulling() {
      return _getCulling(this.handle);
   }

   public void setWinding(int mode) {
      _setWinding(this.handle, mode);
   }

   public int getWinding() {
      return _getWinding(this.handle);
   }

   public void setShading(int mode) {
      _setShading(this.handle, mode);
   }

   public int getShading() {
      return _getShading(this.handle);
   }

   public void setTwoSidedLightingEnable(boolean enable) {
      _setTwoSidedLightingEnable(this.handle, enable);
   }

   public boolean isTwoSidedLightingEnabled() {
      return _isTwoSidedLightingEnabled(this.handle);
   }

   public void setLocalCameraLightingEnable(boolean enable) {
      _setLocalCameraLightingEnable(this.handle, enable);
   }

   public void setPerspectiveCorrectionEnable(boolean enable) {
      _setPerspectiveCorrectionEnable(this.handle, enable);
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
