package javax.microedition.m3g;

public class CompositingMode extends Object3D {
   public static final int ALPHA = 64;
   public static final int ALPHA_ADD = 65;
   public static final int MODULATE = 66;
   public static final int MODULATE_X2 = 67;
   public static final int REPLACE = 68;

   public CompositingMode() {
      super(_ctor(Interface.getHandle()));
   }

   CompositingMode(int handle) {
      super(handle);
   }

   public void setBlending(int mode) {
      _setBlending(this.handle, mode);
   }

   public int getBlending() {
      return _getBlending(this.handle);
   }

   public void setAlphaThreshold(float threshold) {
      _setAlphaThreshold(this.handle, threshold);
   }

   public float getAlphaThreshold() {
      return _getAlphaThreshold(this.handle);
   }

   public void setAlphaWriteEnable(boolean enable) {
      _setAlphaWriteEnable(this.handle, enable);
   }

   public boolean isAlphaWriteEnabled() {
      return _isAlphaWriteEnabled(this.handle);
   }

   public void setColorWriteEnable(boolean enable) {
      _enableColorWrite(this.handle, enable);
   }

   public boolean isColorWriteEnabled() {
      return _isColorWriteEnabled(this.handle);
   }

   public void setDepthWriteEnable(boolean enable) {
      _enableDepthWrite(this.handle, enable);
   }

   public boolean isDepthWriteEnabled() {
      return _isDepthWriteEnabled(this.handle);
   }

   public void setDepthTestEnable(boolean enable) {
      _enableDepthTest(this.handle, enable);
   }

   public boolean isDepthTestEnabled() {
      return _isDepthTestEnabled(this.handle);
   }

   public void setDepthOffset(float factor, float units) {
      _setDepthOffset(this.handle, factor, units);
   }

   public float getDepthOffsetFactor() {
      return _getDepthOffsetFactor(this.handle);
   }

   public float getDepthOffsetUnits() {
      return _getDepthOffsetUnits(this.handle);
   }

   private static native int _ctor(int var0);

   private static native void _setBlending(int var0, int var1);

   private static native int _getBlending(int var0);

   private static native void _setAlphaThreshold(int var0, float var1);

   private static native float _getAlphaThreshold(int var0);

   private static native void _setAlphaWriteEnable(int var0, boolean var1);

   private static native boolean _isAlphaWriteEnabled(int var0);

   private static native void _enableDepthTest(int var0, boolean var1);

   private static native boolean _isDepthTestEnabled(int var0);

   private static native void _enableDepthWrite(int var0, boolean var1);

   private static native boolean _isDepthWriteEnabled(int var0);

   private static native void _enableColorWrite(int var0, boolean var1);

   private static native boolean _isColorWriteEnabled(int var0);

   private static native void _setDepthOffset(int var0, float var1, float var2);

   private static native float _getDepthOffsetFactor(int var0);

   private static native float _getDepthOffsetUnits(int var0);
}
