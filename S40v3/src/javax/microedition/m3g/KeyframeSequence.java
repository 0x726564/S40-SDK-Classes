package javax.microedition.m3g;

public class KeyframeSequence extends Object3D {
   public static final int LINEAR = 176;
   public static final int SLERP = 177;
   public static final int SPLINE = 178;
   public static final int SQUAD = 179;
   public static final int STEP = 180;
   public static final int CONSTANT = 192;
   public static final int LOOP = 193;

   public KeyframeSequence(int var1, int var2, int var3) {
      super(_ctor(Interface.getHandle(), var1, var2, var3));
   }

   KeyframeSequence(int var1) {
      super(var1);
   }

   public void setKeyframe(int var1, int var2, float[] var3) {
      _setKeyframe(this.handle, var1, var2, var3);
   }

   public void setValidRange(int var1, int var2) {
      _setValidRange(this.handle, var1, var2);
   }

   public void setDuration(int var1) {
      _setDuration(this.handle, var1);
   }

   public int getDuration() {
      return _getDuration(this.handle);
   }

   public void setRepeatMode(int var1) {
      _setRepeatMode(this.handle, var1);
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

   public int getKeyframe(int var1, float[] var2) {
      return _getKeyframe(this.handle, var1, var2);
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
