package javax.microedition.m3g;

public class VertexArray extends Object3D {
   public VertexArray(int numVertices, int numComponents, int componentSize) {
      super(createHandle(numVertices, numComponents, componentSize));
   }

   VertexArray(int handle) {
      super(handle);
   }

   public void set(int startIndex, int length, short[] values) {
      _setShort(this.handle, startIndex, length, values);
   }

   public void set(int startIndex, int length, byte[] values) {
      _setByte(this.handle, startIndex, length, values);
   }

   public void get(int firstVertex, int numVertices, byte[] values) {
      _getByte(this.handle, firstVertex, numVertices, values);
   }

   public void get(int firstVertex, int numVertices, short[] values) {
      _getShort(this.handle, firstVertex, numVertices, values);
   }

   public int getComponentCount() {
      return _getComponentCount(this.handle);
   }

   public int getComponentType() {
      return _getComponentType(this.handle);
   }

   public int getVertexCount() {
      return _getVertexCount(this.handle);
   }

   private static int createHandle(int numVertices, int numComponents, int componentSize) {
      Platform.gc();
      return _ctor(Interface.getHandle(), numVertices, numComponents, componentSize);
   }

   private static native int _ctor(int var0, int var1, int var2, int var3);

   private static native void _setByte(int var0, int var1, int var2, byte[] var3);

   private static native void _setShort(int var0, int var1, int var2, short[] var3);

   private static native void _getByte(int var0, int var1, int var2, byte[] var3);

   private static native void _getShort(int var0, int var1, int var2, short[] var3);

   private static native int _getComponentCount(int var0);

   private static native int _getComponentType(int var0);

   private static native int _getVertexCount(int var0);
}
