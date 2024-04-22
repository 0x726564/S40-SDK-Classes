package javax.microedition.m3g;

public class Fog extends Object3D {
   public static final int EXPONENTIAL = 80;
   public static final int LINEAR = 81;

   public Fog() {
      super(_ctor(Interface.getHandle()));
   }

   Fog(int var1) {
      super(var1);
   }

   public void setMode(int var1) {
      _setMode(this.handle, var1);
   }

   public int getMode() {
      return _getMode(this.handle);
   }

   public void setLinear(float var1, float var2) {
      _setLinear(this.handle, var1, var2);
   }

   public float getNearDistance() {
      return _getDistance(this.handle, 0);
   }

   public float getFarDistance() {
      return _getDistance(this.handle, 1);
   }

   public void setDensity(float var1) {
      _setDensity(this.handle, var1);
   }

   public float getDensity() {
      return _getDensity(this.handle);
   }

   public void setColor(int var1) {
      _setColor(this.handle, var1);
   }

   public int getColor() {
      return _getColor(this.handle);
   }

   private static native int _ctor(int var0);

   private static native void _setMode(int var0, int var1);

   private static native int _getMode(int var0);

   private static native void _setLinear(int var0, float var1, float var2);

   private static native float _getDistance(int var0, int var1);

   private static native void _setDensity(int var0, float var1);

   private static native float _getDensity(int var0);

   private static native void _setColor(int var0, int var1);

   private static native int _getColor(int var0);
}
