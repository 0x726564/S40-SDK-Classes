package javax.microedition.m3g;

public class VertexBuffer extends Object3D {
   private VertexArray positions;
   private VertexArray normals;
   private VertexArray colors;
   private VertexArray[] texCoords;

   public VertexBuffer() {
      super(_ctor(Interface.getHandle()));
   }

   VertexBuffer(int handle) {
      super(handle);
      this.positions = (VertexArray)getInstance(_getArray(handle, 0, (float[])null));
      this.normals = (VertexArray)getInstance(_getArray(handle, 1, (float[])null));
      this.colors = (VertexArray)getInstance(_getArray(handle, 2, (float[])null));
      this.texCoords = new VertexArray[Defs.NUM_TEXTURE_UNITS];

      for(int i = 0; i < Defs.NUM_TEXTURE_UNITS; ++i) {
         this.texCoords[i] = (VertexArray)getInstance(_getArray(handle, 3 + i, (float[])null));
      }

   }

   public int getVertexCount() {
      return _getVertexCount(this.handle);
   }

   public void setPositions(VertexArray positions, float scale, float[] bias) {
      _setVertices(this.handle, positions != null ? positions.handle : 0, scale, bias);
      this.positions = positions;
   }

   public void setTexCoords(int index, VertexArray texCoords, float scale, float[] bias) {
      _setTexCoords(this.handle, index, texCoords != null ? texCoords.handle : 0, scale, bias);
      if (this.texCoords == null) {
         this.texCoords = new VertexArray[Defs.NUM_TEXTURE_UNITS];
      }

      this.texCoords[index] = texCoords;
   }

   public void setNormals(VertexArray normals) {
      _setNormals(this.handle, normals != null ? normals.handle : 0);
      this.normals = normals;
   }

   public void setColors(VertexArray colors) {
      _setColors(this.handle, colors != null ? colors.handle : 0);
      this.colors = colors;
   }

   public VertexArray getPositions(float[] scaleBias) {
      _getArray(this.handle, 0, scaleBias);
      return this.positions;
   }

   public VertexArray getTexCoords(int index, float[] scaleBias) {
      if (index >= 0 && index < Defs.NUM_TEXTURE_UNITS) {
         _getArray(this.handle, 3 + index, scaleBias);
         return this.texCoords != null ? this.texCoords[index] : null;
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

   public void setDefaultColor(int ARGB) {
      _setDefaultColor(this.handle, ARGB);
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
