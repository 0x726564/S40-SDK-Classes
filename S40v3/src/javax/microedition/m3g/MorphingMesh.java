package javax.microedition.m3g;

public class MorphingMesh extends Mesh {
   private VertexBuffer[] targets;

   public MorphingMesh(VertexBuffer var1, VertexBuffer[] var2, IndexBuffer var3, Appearance var4) {
      super(createHandle(var1, var2, var3, var4));
      this.targets = new VertexBuffer[var2.length];
      System.arraycopy(var2, 0, this.targets, 0, var2.length);
   }

   public MorphingMesh(VertexBuffer var1, VertexBuffer[] var2, IndexBuffer[] var3, Appearance[] var4) {
      super(createHandle(var1, var2, var3, var4));
      this.targets = new VertexBuffer[var2.length];
      System.arraycopy(var2, 0, this.targets, 0, var2.length);
   }

   MorphingMesh(int var1) {
      super(var1);
      this.targets = new VertexBuffer[_getMorphTargetCount(var1)];

      for(int var2 = 0; var2 < this.targets.length; ++var2) {
         this.targets[var2] = (VertexBuffer)getInstance(_getMorphTarget(var1, var2));
      }

   }

   public VertexBuffer getMorphTarget(int var1) {
      return this.targets[var1];
   }

   public int getMorphTargetCount() {
      return _getMorphTargetCount(this.handle);
   }

   public void setWeights(float[] var1) {
      _setWeights(this.handle, var1);
   }

   public void getWeights(float[] var1) {
      _getWeights(this.handle, var1);
   }

   static int createHandle(VertexBuffer var0, VertexBuffer[] var1, IndexBuffer var2, Appearance var3) {
      verifyParams(var0, var2);
      int[] var4 = new int[var1.length];
      Object var5 = null;
      int[] var6 = null;

      for(int var7 = 0; var7 < var1.length; ++var7) {
         var4[var7] = var1[var7].handle;
      }

      int[] var8 = new int[]{var2.handle};
      if (var3 != null) {
         var6 = new int[]{var3.handle};
      }

      return _ctor(Interface.getHandle(), var0.handle, var4, var8, var6);
   }

   static int createHandle(VertexBuffer var0, VertexBuffer[] var1, IndexBuffer[] var2, Appearance[] var3) {
      verifyParams(var0, var2, var3);
      int[] var4 = new int[var1.length];
      Object var5 = null;
      int[] var6 = null;

      int var7;
      for(var7 = 0; var7 < var1.length; ++var7) {
         var4[var7] = var1[var7].handle;
      }

      int[] var8 = new int[var2.length];
      if (var3 != null) {
         var6 = new int[var3.length];
      }

      for(var7 = 0; var7 < var2.length; ++var7) {
         var8[var7] = var2[var7].handle;
         if (var6 != null) {
            var6[var7] = var3[var7] != null ? var3[var7].handle : 0;
         }
      }

      return _ctor(Interface.getHandle(), var0.handle, var4, var8, var6);
   }

   private static native int _ctor(int var0, int var1, int[] var2, int[] var3, int[] var4);

   private static native void _setWeights(int var0, float[] var1);

   private static native void _getWeights(int var0, float[] var1);

   private static native int _getMorphTarget(int var0, int var1);

   private static native int _getMorphTargetCount(int var0);
}
