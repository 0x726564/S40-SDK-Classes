package javax.microedition.m3g;

public class SkinnedMesh extends Mesh {
   private Group skeleton;

   public SkinnedMesh(VertexBuffer var1, IndexBuffer[] var2, Appearance[] var3, Group var4) {
      super(createHandle(var1, var2, var3, var4));
      var4.setParent(this);
      this.skeleton = var4;
   }

   public SkinnedMesh(VertexBuffer var1, IndexBuffer var2, Appearance var3, Group var4) {
      super(createHandle(var1, var2, var3, var4));
      var4.setParent(this);
      this.skeleton = var4;
   }

   SkinnedMesh(int var1) {
      super(var1);
      this.skeleton = (Group)getInstance(_getSkeleton(var1));
   }

   public void addTransform(Node var1, int var2, int var3, int var4) {
      _addTransform(this.handle, var1 != null ? var1.handle : 0, var2, var3, var4);
   }

   public Group getSkeleton() {
      return this.skeleton;
   }

   public void getBoneTransform(Node var1, Transform var2) {
      _getBoneTransform(this.handle, var1.handle, var2.matrix);
   }

   public int getBoneVertices(Node var1, int[] var2, float[] var3) {
      return _getBoneVertices(this.handle, var1.handle, var2, var3);
   }

   static int createHandle(VertexBuffer var0, IndexBuffer[] var1, Appearance[] var2, Group var3) {
      verifyParams(var0, var1, var2);
      if (var3 == null) {
         throw new NullPointerException();
      } else if (var3.getParent() == null && !(var3 instanceof World)) {
         int[] var4 = new int[var1.length];
         int[] var5 = new int[var1.length];

         for(int var6 = 0; var6 < var1.length; ++var6) {
            var4[var6] = var1[var6].handle;
            if (var2 != null && var6 < var2.length) {
               var5[var6] = var2[var6] != null ? var2[var6].handle : 0;
            }
         }

         return _ctor(Interface.getHandle(), var0.handle, var4, var5, var3.handle);
      } else {
         throw new IllegalArgumentException();
      }
   }

   static int createHandle(VertexBuffer var0, IndexBuffer var1, Appearance var2, Group var3) {
      verifyParams(var0, var1);
      if (var3 == null) {
         throw new NullPointerException();
      } else if (var3.getParent() == null && !(var3 instanceof World)) {
         int[] var4 = new int[]{var1.handle};
         int[] var5 = new int[]{var2 != null ? var2.handle : 0};
         return _ctor(Interface.getHandle(), var0.handle, var4, var5, var3.handle);
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
