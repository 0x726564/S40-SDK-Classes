package javax.microedition.m3g;

public class TriangleStripArray extends IndexBuffer {
   public TriangleStripArray(int var1, int[] var2) {
      super(_createImplicit(Interface.getHandle(), var1, var2));
   }

   public TriangleStripArray(int[] var1, int[] var2) {
      super(_createExplicit(Interface.getHandle(), var1, var2));
   }

   TriangleStripArray(int var1) {
      super(var1);
   }

   public int getIndexCount() {
      return _getIndexCount(this.handle);
   }

   public void getIndices(int[] var1) {
      if (var1.length < _getIndexCount(this.handle)) {
         throw new IllegalArgumentException();
      } else {
         _getIndices(this.handle, var1);
      }
   }

   private static native int _createImplicit(int var0, int var1, int[] var2);

   private static native int _createExplicit(int var0, int[] var1, int[] var2);

   private static native int _getIndexCount(int var0);

   private static native void _getIndices(int var0, int[] var1);
}
