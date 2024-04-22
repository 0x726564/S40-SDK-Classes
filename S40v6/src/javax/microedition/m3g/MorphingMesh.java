package javax.microedition.m3g;

public class MorphingMesh extends Mesh {
   private VertexBuffer[] targets;

   public MorphingMesh(VertexBuffer base, VertexBuffer[] targets, IndexBuffer triangles, Appearance appearance) {
      super(createHandle(base, targets, triangles, appearance));
      this.targets = new VertexBuffer[targets.length];
      System.arraycopy(targets, 0, this.targets, 0, targets.length);
   }

   public MorphingMesh(VertexBuffer base, VertexBuffer[] targets, IndexBuffer[] triangles, Appearance[] appearances) {
      super(createHandle(base, targets, triangles, appearances));
      this.targets = new VertexBuffer[targets.length];
      System.arraycopy(targets, 0, this.targets, 0, targets.length);
   }

   MorphingMesh(int handle) {
      super(handle);
      this.targets = new VertexBuffer[_getMorphTargetCount(handle)];

      for(int i = 0; i < this.targets.length; ++i) {
         this.targets[i] = (VertexBuffer)getInstance(_getMorphTarget(handle, i));
      }

   }

   public VertexBuffer getMorphTarget(int index) {
      return this.targets[index];
   }

   public int getMorphTargetCount() {
      return _getMorphTargetCount(this.handle);
   }

   public void setWeights(float[] weights) {
      _setWeights(this.handle, weights);
   }

   public void getWeights(float[] weights) {
      _getWeights(this.handle, weights);
   }

   static int createHandle(VertexBuffer base, VertexBuffer[] targets, IndexBuffer triangles, Appearance appearance) {
      verifyParams(base, triangles);
      int[] hTargets = new int[targets.length];
      int[] hTriangles = null;
      int[] hAppearances = null;

      for(int i = 0; i < targets.length; ++i) {
         hTargets[i] = targets[i].handle;
      }

      int[] hTriangles = new int[]{triangles.handle};
      if (appearance != null) {
         hAppearances = new int[]{appearance.handle};
      }

      return _ctor(Interface.getHandle(), base.handle, hTargets, hTriangles, hAppearances);
   }

   static int createHandle(VertexBuffer base, VertexBuffer[] targets, IndexBuffer[] triangles, Appearance[] appearances) {
      verifyParams(base, triangles, appearances);
      int[] hTargets = new int[targets.length];
      int[] hTriangles = null;
      int[] hAppearances = null;

      int i;
      for(i = 0; i < targets.length; ++i) {
         hTargets[i] = targets[i].handle;
      }

      int[] hTriangles = new int[triangles.length];
      if (appearances != null) {
         hAppearances = new int[appearances.length];
      }

      for(i = 0; i < triangles.length; ++i) {
         hTriangles[i] = triangles[i].handle;
         if (hAppearances != null) {
            hAppearances[i] = appearances[i] != null ? appearances[i].handle : 0;
         }
      }

      return _ctor(Interface.getHandle(), base.handle, hTargets, hTriangles, hAppearances);
   }

   private static native int _ctor(int var0, int var1, int[] var2, int[] var3, int[] var4);

   private static native void _setWeights(int var0, float[] var1);

   private static native void _getWeights(int var0, float[] var1);

   private static native int _getMorphTarget(int var0, int var1);

   private static native int _getMorphTargetCount(int var0);
}
