package javax.microedition.m3g;

public class RayIntersection {
   private Node v = null;
   private float w = 0.0F;
   private int x = 0;
   private float[] y = new float[0];
   private float[] z = new float[0];
   private float[] A = new float[3];
   private float[] B = new float[6];

   public RayIntersection() {
      this.A[0] = 0.0F;
      this.A[1] = 0.0F;
      this.A[2] = 1.0F;
      this.B[0] = 0.0F;
      this.B[1] = 0.0F;
      this.B[2] = 0.0F;
      this.B[3] = 0.0F;
      this.B[4] = 0.0F;
      this.B[5] = 1.0F;
   }

   public Node getIntersected() {
      return this.v;
   }

   public float getDistance() {
      return this.w;
   }

   public int getSubmeshIndex() {
      return this.x;
   }

   public float getTextureS(int var1) {
      if (var1 >= 0 && var1 < this.y.length) {
         return this.y[var1];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public float getTextureT(int var1) {
      if (var1 >= 0 && var1 < this.z.length) {
         return this.z[var1];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public float getNormalX() {
      return this.A[0];
   }

   public float getNormalY() {
      return this.A[1];
   }

   public float getNormalZ() {
      return this.A[2];
   }

   public void getRay(float[] var1) {
      if (var1.length < 6) {
         throw new IllegalArgumentException();
      } else {
         var1[0] = this.B[0];
         var1[1] = this.B[1];
         var1[2] = this.B[2];
         var1[3] = this.B[3];
         var1[4] = this.B[4];
         var1[5] = this.B[5];
      }
   }

   final void a(int var1, float[] var2) {
      this.v = (Node)Object3D.getInstance(var1);
      this.w = var2[0];
      this.x = (int)var2[1];
      this.y[0] = var2[2];
      this.y[1] = var2[3];
      this.z[0] = var2[4];
      this.z[1] = var2[5];
      this.A[0] = var2[6];
      this.A[1] = var2[7];
      this.A[2] = var2[8];
      this.B[0] = var2[9];
      this.B[1] = var2[10];
      this.B[2] = var2[11];
      this.B[3] = var2[12];
      this.B[4] = var2[13];
      this.B[5] = var2[14];
   }
}
