package javax.microedition.m3g;

public class SkinnedMesh extends Mesh {
   private Group skeleton;

   public SkinnedMesh(VertexBuffer vertices, IndexBuffer[] triangles, Appearance[] appearances, Group skeleton) {
      super(createHandle(vertices, triangles, appearances, skeleton));
      skeleton.setParent(this);
      this.skeleton = skeleton;
   }

   public SkinnedMesh(VertexBuffer vertices, IndexBuffer triangles, Appearance appearance, Group skeleton) {
      super(createHandle(vertices, triangles, appearance, skeleton));
      skeleton.setParent(this);
      this.skeleton = skeleton;
   }

   SkinnedMesh(int handle) {
      super(handle);
      this.skeleton = (Group)getInstance(_getSkeleton(handle));
   }

   public void addTransform(Node bone, int weight, int firstVertex, int numVertices) {
      _addTransform(this.handle, bone != null ? bone.handle : 0, weight, firstVertex, numVertices);
   }

   public Group getSkeleton() {
      return this.skeleton;
   }

   public void getBoneTransform(Node bone, Transform transform) {
      _getBoneTransform(this.handle, bone.handle, transform.matrix);
   }

   public int getBoneVertices(Node bone, int[] indices, float[] weights) {
      return _getBoneVertices(this.handle, bone.handle, indices, weights);
   }

   static int createHandle(VertexBuffer vertices, IndexBuffer[] triangles, Appearance[] appearances, Group skeleton) {
      verifyParams(vertices, triangles, appearances);
      if (skeleton == null) {
         throw new NullPointerException();
      } else if (skeleton.getParent() == null && !(skeleton instanceof World)) {
         int[] hTri = new int[triangles.length];
         int[] hApp = new int[triangles.length];

         for(int i = 0; i < triangles.length; ++i) {
            hTri[i] = triangles[i].handle;
            if (appearances != null && i < appearances.length) {
               hApp[i] = appearances[i] != null ? appearances[i].handle : 0;
            }
         }

         return _ctor(Interface.getHandle(), vertices.handle, hTri, hApp, skeleton.handle);
      } else {
         throw new IllegalArgumentException();
      }
   }

   static int createHandle(VertexBuffer vertices, IndexBuffer triangles, Appearance appearance, Group skeleton) {
      verifyParams(vertices, triangles);
      if (skeleton == null) {
         throw new NullPointerException();
      } else if (skeleton.getParent() == null && !(skeleton instanceof World)) {
         int[] hTri = new int[]{triangles.handle};
         int[] hApp = new int[]{appearance != null ? appearance.handle : 0};
         return _ctor(Interface.getHandle(), vertices.handle, hTri, hApp, skeleton.handle);
      } else {
         throw new IllegalArgumentException();
      }
   }

   private static native int _ctor(int var0, int var1, int[] var2, int[] var3, int var4);

   private static native void _addTransform(int var0, int var1, int var2, int var3, int var4);

   private static native int _getSkeleton(int var0);

   private static native void _getBoneTransform(int var0, int var1, byte[] var2);

   private static native int _getBoneVertices(int var0, int var1, int[] var2, float[] var3);
}
