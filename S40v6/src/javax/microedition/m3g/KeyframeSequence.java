package javax.microedition.m3g;

public class KeyframeSequence extends Object3D {
   public static final int LINEAR = 176;
   public static final int SLERP = 177;
   public static final int SPLINE = 178;
   public static final int SQUAD = 179;
   public static final int STEP = 180;
   public static final int CONSTANT = 192;
   public static final int LOOP = 193;

   public KeyframeSequence(int numKeyframes, int numComponents, int interpolation) {
      super(_ctor(Interface.getHandle(), numKeyframes, numComponents, interpolation));
   }

   KeyframeSequence(int handle) {
      super(handle);
   }

   public void setKeyframe(int index, int time, float[] value) {
      _setKeyframe(this.handle, index, time, value);
   }

   public void setValidRange(int first, int last) {
      _setValidRange(this.handle, first, last);
   }

   public void setDuration(int duration) {
      _setDuration(this.handle, duration);
   }

   public int getDuration() {
      return _getDuration(this.handle);
   }

   public void setRepeatMode(int mode) {
      _setRepeatMode(this.handle, mode);
   }

   public int getRepeatMode() {
      return _getRepeatMode(this.handle);
   }

   public int getComponentCount() {
      return _getComponentCount(this.handle);
   }

   public int getInterpolationType() {
      return _getInterpolationType(this.handle);
   }

   public int getKeyframe(int index, float[] value) {
      return _getKeyframe(this.handle, index, value);
   }

   public int getKeyframeCount() {
      return _getKeyframeCount(this.handle);
   }

   public int getValidRangeFirst() {
      return _getValidRangeFirst(this.handle);
   }

   public int getValidRangeLast() {
      return _getValidRangeLast(this.handle);
   }

   private static native int _ctor(int var0, int var1, int var2, int var3);

   private static native void _setValidRange(int var0, int var1, int var2);

   private static native void _setKeyframe(int var0, int var1, int var2, float[] var3);

   private static native void _setDuration(int var0, int var1);

   private static native int _getDuration(int var0);

   private static native void _setRepeatMode(int var0, int var1);

   private static native int _getRepeatMode(int var0);

   private static native int _getComponentCount(int var0);

   private static native int _getInterpolationType(int var0);

   private static native int _getKeyframe(int var0, int var1, float[] var2);

   private static native int _getKeyframeCount(int var0);

   private static native int _getValidRangeFirst(int var0);

   private static native int _getValidRangeLast(int var0);
}
