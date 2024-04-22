package javax.microedition.m3g;

public class SkinnedMesh extends Mesh {
   private Group P;

   public SkinnedMesh(VertexBuffer var1, IndexBuffer[] var2, Appearance[] var3, Group var4) {
      super(a(var1, var2, var3, var4));
      var4.setParent(this);
      this.P = var4;
   }

   public SkinnedMesh(VertexBuffer var1, IndexBuffer var2, Appearance var3, Group var4) {
      a(var1 = var1, var2);
      if (var4 == null) {
         throw new NullPointerException();
      } else if (var4.getParent() == null && !(var4 instanceof World)) {
         int[] var6 = new int[]{var2.handle};
         int[] var7 = new int[]{var3 != null ? var3.handle : 0};
         super(_ctor(Interface.getHandle(), var1.handle, var6, var7, var4.handle));
         var4.setParent(this);
         this.P = var4;
      } else {
         throw new IllegalArgumentException();
      }
   }

   SkinnedMesh(int var1) {
      super(var1);
      this.P = (Group)getInstance(_getSkeleton(var1));
   }

   public void addTransform(Node var1, int var2, int var3, int var4) {
      _addTransform(this.handle, var1 != null ? var1.handle : 0, var2, var3, var4);
   }

   public Group getSkeleton() {
      return this.P;
   }

   public void getBoneTransform(Node var1, Transform var2) {
      _getBoneTransform(this.handle, var1.handle, var2.s);
   }

   public int getBoneVertices(Node var1, int[] var2, float[] var3) {
      return _getBoneVertices(this.handle, var1.handle, var2, var3);
   }

   private static int a(VertexBuffer var0, IndexBuffer[] var1, Appearance[] var2, Group var3) {
      a(var0, var1, var2);
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

   private static native int _ctor(int var0, int var1, int[] var2, int[] var3, int var4);

   private static native void _addTransform(int var0, int var1, int var2, int var3, int var4);

   private static native int _getSkeleton(int var0);

   private static native void _getBoneTransform(int var0, int var1, byte[] var2);

   private static native int _getBoneVertices(int var0, int var1, int[] var2, float[] var3);
}
