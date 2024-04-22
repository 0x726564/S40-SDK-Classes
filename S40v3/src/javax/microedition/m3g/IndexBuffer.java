package javax.microedition.m3g;

public abstract class IndexBuffer extends Object3D {
   IndexBuffer(int var1) {
      super(var1);
   }

   public abstract int getIndexCount();

   public abstract void getIndices(int[] var1);
}
