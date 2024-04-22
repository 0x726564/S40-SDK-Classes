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

   Node(int var1) {
      super(var1);
      this.parent = (Node)getInstance(_getParent(var1));
      this.zRef = (Node)getInstance(_getZRef(var1));
      this.yRef = (Node)getInstance(_getYRef(var1));
   }

   public Node getParent() {
      return this.parent;
   }

   public boolean getTransformTo(Node var1, Transform var2) {
      return _getTransformTo(this.handle, var1.handle, var2 != null ? var2.matrix : null);
   }

   public void setAlignment(Node var1, int var2, Node var3, int var4) {
      _setAlignment(this.handle, var1 != null ? var1.handle : 0, var2, var3 != null ? var3.handle : 0, var4);
      this.zRef = var1;
      this.yRef = var3;
   }

   public void setAlphaFactor(float var1) {
      _setAlphaFactor(this.handle, var1);
   }

   public float getAlphaFactor() {
      return _getAlphaFactor(this.handle);
   }

   public void setRenderingEnable(boolean var1) {
      _enable(this.handle, 0, var1);
   }

   public boolean isRenderingEnabled() {
      return _isEnabled(this.handle, 0);
   }

   public void setPickingEnable(boolean var1) {
      _enable(this.handle, 1, var1);
   }

   public boolean isPickingEnabled() {
      return _isEnabled(this.handle, 1);
   }

   public void setScope(int var1) {
      _setScope(this.handle, var1);
   }

   public int getScope() {
      return _getScope(this.handle);
   }

   public final void align(Node var1) {
      _align(this.handle, var1 != null ? var1.handle : 0);
   }

   public Node getAlignmentReference(int var1) {
      switch(var1) {
      case 147:
         return this.yRef;
      case 148:
         return this.zRef;
      default:
         throw new IllegalArgumentException();
      }
   }

   public int getAlignmentTarget(int var1) {
      return _getAlignmentTarget(this.handle, var1);
   }

   void setParent(Node var1) {
      this.parent = var1;
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
