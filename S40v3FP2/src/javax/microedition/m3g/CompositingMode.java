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

   CompositingMode(int var1) {
      super(var1);
   }

   public void setBlending(int var1) {
      _setBlending(this.handle, var1);
   }

   public int getBlending() {
      return _getBlending(this.handle);
   }

   public void setAlphaThreshold(float var1) {
      _setAlphaThreshold(this.handle, var1);
   }

   public float getAlphaThreshold() {
      return _getAlphaThreshold(this.handle);
   }

   public void setAlphaWriteEnable(boolean var1) {
      _setAlphaWriteEnable(this.handle, var1);
   }

   public boolean isAlphaWriteEnabled() {
      return _isAlphaWriteEnabled(this.handle);
   }

   public void setColorWriteEnable(boolean var1) {
      _enableColorWrite(this.handle, var1);
   }

   public boolean isColorWriteEnabled() {
      return _isColorWriteEnabled(this.handle);
   }

   public void setDepthWriteEnable(boolean var1) {
      _enableDepthWrite(this.handle, var1);
   }

   public boolean isDepthWriteEnabled() {
      return _isDepthWriteEnabled(this.handle);
   }

   public void setDepthTestEnable(boolean var1) {
      _enableDepthTest(this.handle, var1);
   }

   public boolean isDepthTestEnabled() {
      return _isDepthTestEnabled(this.handle);
   }

   public void setDepthOffset(float var1, float var2) {
      _setDepthOffset(this.handle, var1, var2);
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
