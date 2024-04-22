package javax.microedition.m3g;

public class VertexBuffer extends Object3D {
   private VertexArray positions;
   private VertexArray normals;
   private VertexArray colors;
   private VertexArray[] texCoords;

   public VertexBuffer() {
      super(_ctor(Interface.getHandle()));
   }

   VertexBuffer(int var1) {
      super(var1);
      this.positions = (VertexArray)getInstance(_getArray(var1, 0, (float[])null));
      this.normals = (VertexArray)getInstance(_getArray(var1, 1, (float[])null));
      this.colors = (VertexArray)getInstance(_getArray(var1, 2, (float[])null));
      this.texCoords = new VertexArray[Defs.NUM_TEXTURE_UNITS];

      for(int var2 = 0; var2 < Defs.NUM_TEXTURE_UNITS; ++var2) {
         this.texCoords[var2] = (VertexArray)getInstance(_getArray(var1, 3 + var2, (float[])null));
      }

   }

   public int getVertexCount() {
      return _getVertexCount(this.handle);
   }

   public void setPositions(VertexArray var1, float var2, float[] var3) {
      _setVertices(this.handle, var1 != null ? var1.handle : 0, var2, var3);
      this.positions = var1;
   }

   public void setTexCoords(int var1, VertexArray var2, float var3, float[] var4) {
      _setTexCoords(this.handle, var1, var2 != null ? var2.handle : 0, var3, var4);
      if (this.texCoords == null) {
         this.texCoords = new VertexArray[Defs.NUM_TEXTURE_UNITS];
      }

      this.texCoords[var1] = var2;
   }

   public void setNormals(VertexArray var1) {
      _setNormals(this.handle, var1 != null ? var1.handle : 0);
      this.normals = var1;
   }

   public void setColors(VertexArray var1) {
      _setColors(this.handle, var1 != null ? var1.handle : 0);
      this.colors = var1;
   }

   public VertexArray getPositions(float[] var1) {
      _getArray(this.handle, 0, var1);
      return this.positions;
   }

   public VertexArray getTexCoords(int var1, float[] var2) {
      if (var1 >= 0 && var1 < Defs.NUM_TEXTURE_UNITS) {
         _getArray(this.handle, 3 + var1, var2);
         return this.texCoords != null ? this.texCoords[var1] : null;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public VertexArray getNormals() {
      return this.normals;
   }

   public VertexArray getColors() {
      return this.colors;
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
