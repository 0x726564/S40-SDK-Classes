package javax.microedition.m3g;

public class Mesh extends Node {
   private VertexBuffer vertices;
   private Appearance[] appearances;
   private IndexBuffer[] triangles;

   Mesh(int var1) {
      super(var1);
      this.updateReferences();
   }

   public Mesh(VertexBuffer var1, IndexBuffer[] var2, Appearance[] var3) {
      super(createHandle(var1, var2, var3));
      this.updateReferences();
   }

   public Mesh(VertexBuffer var1, IndexBuffer var2, Appearance var3) {
      super(createHandle(var1, var2, var3));
      this.updateReferences();
   }

   public void setAppearance(int var1, Appearance var2) {
      _setAppearance(this.handle, var1, var2 != null ? var2.handle : 0);
      this.appearances[var1] = var2;
   }

   public Appearance getAppearance(int var1) {
      return this.appearances[var1];
   }

   public IndexBuffer getIndexBuffer(int var1) {
      return this.triangles[var1];
   }

   public VertexBuffer getVertexBuffer() {
      return this.vertices;
   }

   public int getSubmeshCount() {
      return _getSubmeshCount(this.handle);
   }

   static void verifyParams(VertexBuffer var0, IndexBuffer[] var1, Appearance[] var2) {
      if (var0 != null && var1 != null) {
         if (var1.length != 0 && (var2 == null || var2.length >= var1.length)) {
            if (var1.length == 0) {
               throw new IllegalArgumentException();
            } else {
               for(int var3 = 0; var3 < var1.length; ++var3) {
                  if (var1[var3] == null) {
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

   static void verifyParams(VertexBuffer var0, IndexBuffer var1) {
      if (var0 == null || var1 == null) {
         throw new NullPointerException();
      }
   }

   void updateReferences() {
      this.triangles = new IndexBuffer[_getSubmeshCount(this.handle)];
      this.appearances = new Appearance[this.triangles.length];
      this.vertices = (VertexBuffer)getInstance(_getVertexBuffer(this.handle));

      for(int var1 = 0; var1 < this.triangles.length; ++var1) {
         this.triangles[var1] = (IndexBuffer)getInstance(_getIndexBuffer(this.handle, var1));
         this.appearances[var1] = (Appearance)getInstance(_getAppearance(this.handle, var1));
      }

   }

   static int createHandle(VertexBuffer var0, IndexBuffer[] var1, Appearance[] var2) {
      verifyParams(var0, var1, var2);
      int[] var3 = new int[var1.length];
      int[] var4 = null;
      if (var2 != null) {
         var4 = new int[var2.length];
      }

      for(int var5 = 0; var5 < var1.length; ++var5) {
         var3[var5] = var1[var5].handle;
         if (var2 != null) {
            var4[var5] = var2[var5] != null ? var2[var5].handle : 0;
         }
      }

      return _ctor(Interface.getHandle(), var0.handle, var3, var4);
   }

   static int createHandle(VertexBuffer var0, IndexBuffer var1, Appearance var2) {
      verifyParams(var0, var1);
      int[] var3 = new int[1];
      int[] var4 = null;
      var3[0] = var1.handle;
      if (var2 != null) {
         var4 = new int[]{var2.handle};
      }

      return _ctor(Interface.getHandle(), var0.handle, var3, var4);
   }

   private static native int _ctor(int var0, int var1, int[] var2, int[] var3);

   private static native void _setAppearance(int var0, int var1, int var2);

   private static native int _getAppearance(int var0, int var1);

   private static native int _getIndexBuffer(int var0, int var1);

   private static native int _getVertexBuffer(int var0);

   private static native int _getSubmeshCount(int var0);
}
