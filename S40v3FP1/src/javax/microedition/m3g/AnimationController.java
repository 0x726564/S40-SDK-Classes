package javax.microedition.m3g;

public class AnimationController extends Object3D {
   public AnimationController() {
      super(_ctor(Interface.getHandle()));
   }

   AnimationController(int var1) {
      super(var1);
   }

   public void setActiveInterval(int var1, int var2) {
      _setActiveInterval(this.handle, var1, var2);
   }

   public int getActiveIntervalStart() {
      return _getActiveIntervalStart(this.handle);
   }

   public int getActiveIntervalEnd() {
      return _getActiveIntervalEnd(this.handle);
   }

   public void setSpeed(float var1, int var2) {
      _setSpeed(this.handle, var1, var2);
   }

   public float getSpeed() {
      return _getSpeed(this.handle);
   }

   public void setPosition(float var1, int var2) {
      _setPosition(this.handle, var1, var2);
   }

   public float getPosition(int var1) {
      return _getPosition(this.handle, var1);
   }

   public void setWeight(float var1) {
      _setWeight(this.handle, var1);
   }

   public float getWeight() {
      return _getWeight(this.handle);
   }

   public int getRefWorldTime() {
      return _getRefWorldTime(this.handle);
   }

   private static native int _ctor(int var0);

   private static native void _setActiveInterval(int var0, int var1, int var2);

   private static native int _getActiveIntervalStart(int var0);

   private static native int _getActiveIntervalEnd(int var0);

   private static native void _setSpeed(int var0, float var1, int var2);

   private static native float _getSpeed(int var0);

   private static native void _setPosition(int var0, float var1, int var2);

   private static native float _getPosition(int var0, int var1);

   private static native void _setWeight(int var0, float var1);

   private static native float _getWeight(int var0);

   private static native int _getRefWorldTime(int var0);
}
