package javax.microedition.m3g;

public class AnimationController extends Object3D {
   public AnimationController() {
      super(_ctor(Interface.getHandle()));
   }

   AnimationController(int handle) {
      super(handle);
   }

   public void setActiveInterval(int worldTimeMin, int worldTimeMax) {
      _setActiveInterval(this.handle, worldTimeMin, worldTimeMax);
   }

   public int getActiveIntervalStart() {
      return _getActiveIntervalStart(this.handle);
   }

   public int getActiveIntervalEnd() {
      return _getActiveIntervalEnd(this.handle);
   }

   public void setSpeed(float factor, int worldTime) {
      _setSpeed(this.handle, factor, worldTime);
   }

   public float getSpeed() {
      return _getSpeed(this.handle);
   }

   public void setPosition(float time, int worldTime) {
      _setPosition(this.handle, time, worldTime);
   }

   public float getPosition(int worldTime) {
      return _getPosition(this.handle, worldTime);
   }

   public void setWeight(float weight) {
      _setWeight(this.handle, weight);
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
