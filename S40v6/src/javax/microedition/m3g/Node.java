package javax.microedition.m3g;

public abstract class Node extends Transformable {
   public static final int NONE = 144;
   public static final int ORIGIN = 145;
   public static final int X_AXIS = 146;
   public static final int Y_AXIS = 147;
   public static final int Z_AXIS = 148;
   private Node parent;
   private Node zRef;
   private Node yRef;

   Node(int handle) {
      super(handle);
      this.parent = (Node)getInstance(_getParent(handle));
      this.zRef = (Node)getInstance(_getZRef(handle));
      this.yRef = (Node)getInstance(_getYRef(handle));
   }

   public Node getParent() {
      return this.parent;
   }

   public boolean getTransformTo(Node target, Transform transform) {
      return _getTransformTo(this.handle, target.handle, transform != null ? transform.matrix : null);
   }

   public void setAlignment(Node zReference, int zTarget, Node yReference, int yTarget) {
      _setAlignment(this.handle, zReference != null ? zReference.handle : 0, zTarget, yReference != null ? yReference.handle : 0, yTarget);
      this.zRef = zReference;
      this.yRef = yReference;
   }

   public void setAlphaFactor(float alphaFactor) {
      _setAlphaFactor(this.handle, alphaFactor);
   }

   public float getAlphaFactor() {
      return _getAlphaFactor(this.handle);
   }

   public void setRenderingEnable(boolean enable) {
      _enable(this.handle, 0, enable);
   }

   public boolean isRenderingEnabled() {
      return _isEnabled(this.handle, 0);
   }

   public void setPickingEnable(boolean enable) {
      _enable(this.handle, 1, enable);
   }

   public boolean isPickingEnabled() {
      return _isEnabled(this.handle, 1);
   }

   public void setScope(int id) {
      _setScope(this.handle, id);
   }

   public int getScope() {
      return _getScope(this.handle);
   }

   public final void align(Node reference) {
      _align(this.handle, reference != null ? reference.handle : 0);
   }

   public Node getAlignmentReference(int axis) {
      switch(axis) {
      case 147:
         return this.yRef;
      case 148:
         return this.zRef;
      default:
         throw new IllegalArgumentException();
      }
   }

   public int getAlignmentTarget(int axis) {
      return _getAlignmentTarget(this.handle, axis);
   }

   void setParent(Node parent) {
      this.parent = parent;
   }

   private static native boolean _getTransformTo(int var0, int var1, byte[] var2);

   private static native void _align(int var0, int var1);

   private static native void _setAlignment(int var0, int var1, int var2, int var3, int var4);

   private static native void _setAlphaFactor(int var0, float var1);

   private static native float _getAlphaFactor(int var0);

   private static native void _enable(int var0, int var1, boolean var2);

   private static native boolean _isEnabled(int var0, int var1);

   private static native void _setScope(int var0, int var1);

   private static native int _getScope(int var0);

   private static native int _getParent(int var0);

   private static native int _getZRef(int var0);

   private static native int _getYRef(int var0);

   static native int _getSubtreeSize(int var0);

   private static native int _getAlignmentTarget(int var0, int var1);
}
