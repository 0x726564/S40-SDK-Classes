package javax.microedition.m3g;

public class Mesh extends Node {
   private VertexBuffer vertices;
   private Appearance[] appearances;
   private IndexBuffer[] triangles;

   Mesh(int handle) {
      super(handle);
      this.updateReferences();
   }

   public Mesh(VertexBuffer vertices, IndexBuffer[] triangles, Appearance[] appearances) {
      super(createHandle(vertices, triangles, appearances));
      this.updateReferences();
   }

   public Mesh(VertexBuffer vertices, IndexBuffer triangles, Appearance appearance) {
      super(createHandle(vertices, triangles, appearance));
      this.updateReferences();
   }

   public void setAppearance(int index, Appearance appearance) {
      _setAppearance(this.handle, index, appearance != null ? appearance.handle : 0);
      this.appearances[index] = appearance;
   }

   public Appearance getAppearance(int index) {
      return this.appearances[index];
   }

   public IndexBuffer getIndexBuffer(int index) {
      return this.triangles[index];
   }

   public VertexBuffer getVertexBuffer() {
      return this.vertices;
   }

   public int getSubmeshCount() {
      return _getSubmeshCount(this.handle);
   }

   static void verifyParams(VertexBuffer vertices, IndexBuffer[] triangles, Appearance[] appearances) {
      if (vertices != null && triangles != null) {
         if (triangles.length != 0 && (appearances == null || appearances.length >= triangles.length)) {
            if (triangles.length == 0) {
               throw new IllegalArgumentException();
            } else {
               for(int i = 0; i < triangles.length; ++i) {
                  if (triangles[i] == null) {
                     throw new NullPointerException();
                  }
               }

            }
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         throw new NullPointerException();
      }
   }

   static void verifyParams(VertexBuffer vertices, IndexBuffer triangles) {
      if (vertices == null || triangles == null) {
         throw new NullPointerException();
      }
   }

   void updateReferences() {
      this.triangles = new IndexBuffer[_getSubmeshCount(this.handle)];
      this.appearances = new Appearance[this.triangles.length];
      this.vertices = (VertexBuffer)getInstance(_getVertexBuffer(this.handle));

      for(int i = 0; i < this.triangles.length; ++i) {
         this.triangles[i] = (IndexBuffer)getInstance(_getIndexBuffer(this.handle, i));
         this.appearances[i] = (Appearance)getInstance(_getAppearance(this.handle, i));
      }

   }

   static int createHandle(VertexBuffer vertices, IndexBuffer[] triangles, Appearance[] appearances) {
      verifyParams(vertices, triangles, appearances);
      int[] hTriangles = new int[triangles.length];
      int[] hAppearances = null;
      if (appearances != null) {
         hAppearances = new int[appearances.length];
      }

      for(int i = 0; i < triangles.length; ++i) {
         hTriangles[i] = triangles[i].handle;
         if (appearances != null) {
            hAppearances[i] = appearances[i] != null ? appearances[i].handle : 0;
         }
      }

      return _ctor(Interface.getHandle(), vertices.handle, hTriangles, hAppearances);
   }

   static int createHandle(VertexBuffer vertices, IndexBuffer triangles, Appearance appearance) {
      verifyParams(vertices, triangles);
      int[] hTriangles = new int[1];
      int[] hAppearances = null;
      hTriangles[0] = triangles.handle;
      if (appearance != null) {
         hAppearances = new int[]{appearance.handle};
      }

      return _ctor(Interface.getHandle(), vertices.handle, hTriangles, hAppearances);
   }

   private static native int _ctor(int var0, int var1, int[] var2, int[] var3);

   private static native void _setAppearance(int var0, int var1, int var2);

   private static native int _getAppearance(int var0, int var1);

   private static native int _getIndexBuffer(int var0, int var1);

   private static native int _getVertexBuffer(int var0);

   private static native int _getSubmeshCount(int var0);
}
