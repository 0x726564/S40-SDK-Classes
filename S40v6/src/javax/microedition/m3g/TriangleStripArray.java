package javax.microedition.m3g;

public class TriangleStripArray extends IndexBuffer {
   public TriangleStripArray(int firstIndex, int[] stripLengths) {
      super(_createImplicit(Interface.getHandle(), firstIndex, stripLengths));
   }

   public TriangleStripArray(int[] indices, int[] stripLengths) {
      super(_createExplicit(Interface.getHandle(), indices, stripLengths));
   }

   TriangleStripArray(int handle) {
      super(handle);
   }

   public int getIndexCount() {
      return _getIndexCount(this.handle);
   }

   public void getIndices(int[] indices) {
      if (indices.length < _getIndexCount(this.handle)) {
         throw new IllegalArgumentException();
      } else {
         _getIndices(this.handle, indices);
      }
   }

   private static native int _createImplicit(int var0, int var1, int[] var2);

   private static native int _createExplicit(int var0, int[] var1, int[] var2);

   private static native int _getIndexCount(int var0);

   private static native void _getIndices(int var0, int[] var1);
}
