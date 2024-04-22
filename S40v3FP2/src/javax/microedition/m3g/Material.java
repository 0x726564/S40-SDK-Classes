package javax.microedition.m3g;

public class Material extends Object3D {
   public static final int AMBIENT = 1024;
   public static final int DIFFUSE = 2048;
   public static final int EMISSIVE = 4096;
   public static final int SPECULAR = 8192;

   public Material() {
      super(_ctor(Interface.getHandle()));
   }

   Material(int var1) {
      super(var1);
   }

   public void setColor(int var1, int var2) {
      _setColor(this.handle, var1, var2);
   }

   public int getColor(int var1) {
      return _getColor(this.handle, var1);
   }

   public void setShininess(float var1) {
      _setShininess(this.handle, var1);
   }

   public float getShininess() {
      return _getShininess(this.handle);
   }

   public void setVertexColorTrackingEnable(boolean var1) {
      _setVertexColorTrackingEnable(this.handle, var1);
   }

   public boolean isVertexColorTrackingEnabled() {
      return _isVertexColorTrackingEnabled(this.handle);
   }

   private static native int _ctor(int var0);

   private static native void _setColor(int var0, int var1, int var2);

   private static native int _getColor(int var0, int var1);

   private static native void _setShininess(int var0, float var1);

   private static native float _getShininess(int var0);

   private static native void _setVertexColorTrackingEnable(int var0, boolean var1);

   private static native boolean _isVertexColorTrackingEnabled(int var0);
}
