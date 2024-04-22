package javax.microedition.m3g;

public class VertexArray extends Object3D {
   public VertexArray(int var1, int var2, int var3) {
      super(_ctor(Interface.getHandle(), var1, var2, var3));
   }

   VertexArray(int var1) {
      super(var1);
   }

   public void set(int var1, int var2, short[] var3) {
      _setShort(this.handle, var1, var2, var3);
   }

   public void set(int var1, int var2, byte[] var3) {
      _setByte(this.handle, var1, var2, var3);
   }

   public void get(int var1, int var2, byte[] var3) {
      _getByte(this.handle, var1, var2, var3);
   }

   public void get(int var1, int var2, short[] var3) {
      _getShort(this.handle, var1, var2, var3);
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

   private static native int _ctor(int var0, int var1, int var2, int var3);

   private static native void _setByte(int var0, int var1, int var2, byte[] var3);

   private static native void _setShort(int var0, int var1, int var2, short[] var3);

   private static native void _getByte(int var0, int var1, int var2, byte[] var3);

   private static native void _getShort(int var0, int var1, int var2, short[] var3);

   private static native int _getComponentCount(int var0);

   private static native int _getComponentType(int var0);

   private static native int _getVertexCount(int var0);
}
