package javax.microedition.m3g;

public class Light extends Node {
   public static final int AMBIENT = 128;
   public static final int DIRECTIONAL = 129;
   public static final int OMNI = 130;
   public static final int SPOT = 131;

   public Light() {
      super(_ctor(Interface.getHandle()));
   }

   Light(int handle) {
      super(handle);
   }

   public void setIntensity(float intensity) {
      _setIntensity(this.handle, intensity);
   }

   public float getIntensity() {
      return _getIntensity(this.handle);
   }

   public void setColor(int RGB) {
      _setColor(this.handle, RGB);
   }

   public int getColor() {
      return _getColor(this.handle);
   }

   public void setMode(int mode) {
      _setMode(this.handle, mode);
   }

   public int getMode() {
      return _getMode(this.handle);
   }

   public void setSpotAngle(float angle) {
      _setSpotAngle(this.handle, angle);
   }

   public float getSpotAngle() {
      return _getSpotAngle(this.handle);
   }

   public void setSpotExponent(float exponent) {
      _setSpotExponent(this.handle, exponent);
   }

   public float getSpotExponent() {
      return _getSpotExponent(this.handle);
   }

   public void setAttenuation(float constant, float linear, float quadratic) {
      _setAttenuation(this.handle, constant, linear, quadratic);
   }

   public float getConstantAttenuation() {
      return _getAttenuation(this.handle, 0);
   }

   public float getLinearAttenuation() {
      return _getAttenuation(this.handle, 1);
   }

   public float getQuadraticAttenuation() {
      return _getAttenuation(this.handle, 2);
   }

   private static native int _ctor(int var0);

   private static native void _setIntensity(int var0, float var1);

   private static native float _getIntensity(int var0);

   private static native void _setColor(int var0, int var1);

   private static native int _getColor(int var0);

   private static native void _setMode(int var0, int var1);

   private static native int _getMode(int var0);

   private static native void _setSpotAngle(int var0, float var1);

   private static native float _getSpotAngle(int var0);

   private static native void _setSpotExponent(int var0, float var1);

   private static native float _getSpotExponent(int var0);

   private static native void _setAttenuation(int var0, float var1, float var2, float var3);

   private static native float _getAttenuation(int var0, int var1);
}
