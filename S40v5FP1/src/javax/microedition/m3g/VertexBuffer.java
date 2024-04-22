package javax.microedition.m3g;

public class VertexBuffer extends Object3D {
   private VertexArray H;
   private VertexArray I;
   private VertexArray J;
   private VertexArray[] K;

   public VertexBuffer() {
      super(_ctor(Interface.getHandle()));
   }

   VertexBuffer(int var1) {
      super(var1);
      this.H = (VertexArray)getInstance(_getArray(var1, 0, (float[])null));
      this.I = (VertexArray)getInstance(_getArray(var1, 1, (float[])null));
      this.J = (VertexArray)getInstance(_getArray(var1, 2, (float[])null));
      this.K = new VertexArray[0];
   }

   public int getVertexCount() {
      return _getVertexCount(this.handle);
   }

   public void setPositions(VertexArray var1, float var2, float[] var3) {
      _setVertices(this.handle, var1 != null ? var1.handle : 0, var2, var3);
      this.H = var1;
   }

   public void setTexCoords(int var1, VertexArray var2, float var3, float[] var4) {
      _setTexCoords(this.handle, var1, var2 != null ? var2.handle : 0, var3, var4);
      if (this.K == null) {
         this.K = new VertexArray[0];
      }

      this.K[var1] = var2;
   }

   public void setNormals(VertexArray var1) {
      _setNormals(this.handle, var1 != null ? var1.handle : 0);
      this.I = var1;
   }

   public void setColors(VertexArray var1) {
      _setColors(this.handle, var1 != null ? var1.handle : 0);
      this.J = var1;
   }

   public VertexArray getPositions(float[] var1) {
      _getArray(this.handle, 0, var1);
      return this.H;
   }

   public VertexArray getTexCoords(int var1, float[] var2) {
      if (var1 >= 0 && var1 < 0) {
         _getArray(this.handle, 3 + var1, var2);
         return this.K != null ? this.K[var1] : null;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public VertexArray getNormals() {
      return this.I;
   }

   public VertexArray getColors() {
      return this.J;
   }

   public void setDefaultColor(int var1) {
      _setDefaultColor(this.handle, var1);
   }

   public int getDefaultColor() {
      return _getDefaultColor(this.handle);
   }

   private static native int _ctor(int var0);

   private static native void _setColors(int var0, int var1);

   private static native void _setNormals(int var0, int var1);

   private static native void _setTexCoords(int var0, int var1, int var2, float var3, float[] var4);

   private static native void _setVertices(int var0, int var1, float var2, float[] var3);

   private static native void _setDefaultColor(int var0, int var1);

   private static native int _getDefaultColor(int var0);

   private static native int _getArray(int var0, int var1, float[] var2);

   private static native int _getVertexCount(int var0);
}
