package javax.microedition.m3g;

public class RayIntersection {
   private Node intersected = null;
   private float distance = 0.0F;
   private int submeshIndex = 0;
   private float[] textureS;
   private float[] textureT;
   private float[] normal;
   private float[] ray;

   public RayIntersection() {
      this.textureS = new float[Defs.NUM_TEXTURE_UNITS];
      this.textureT = new float[Defs.NUM_TEXTURE_UNITS];
      this.normal = new float[3];
      this.ray = new float[6];
      this.normal[0] = 0.0F;
      this.normal[1] = 0.0F;
      this.normal[2] = 1.0F;
      this.ray[0] = 0.0F;
      this.ray[1] = 0.0F;
      this.ray[2] = 0.0F;
      this.ray[3] = 0.0F;
      this.ray[4] = 0.0F;
      this.ray[5] = 1.0F;
   }

   public Node getIntersected() {
      return this.intersected;
   }

   public float getDistance() {
      return this.distance;
   }

   public int getSubmeshIndex() {
      return this.submeshIndex;
   }

   public float getTextureS(int var1) {
      if (var1 >= 0 && var1 < this.textureS.length) {
         return this.textureS[var1];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public float getTextureT(int var1) {
      if (var1 >= 0 && var1 < this.textureT.length) {
         return this.textureT[var1];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public float getNormalX() {
      return this.normal[0];
   }

   public float getNormalY() {
      return this.normal[1];
   }

   public float getNormalZ() {
      return this.normal[2];
   }

   public void getRay(float[] var1) {
      if (var1.length < 6) {
         throw new IllegalArgumentException();
      } else {
         var1[0] = this.ray[0];
         var1[1] = this.ray[1];
         var1[2] = this.ray[2];
         var1[3] = this.ray[3];
         var1[4] = this.ray[4];
         var1[5] = this.ray[5];
      }
   }

   static float[] createResult() {
      return new float[2 + 2 * Defs.NUM_TEXTURE_UNITS + 3 + 6];
   }

   void fill(int var1, float[] var2) {
      this.intersected = (Node)Object3D.getInstance(var1);
      this.distance = var2[0];
      this.submeshIndex = (int)var2[1];
      this.textureS[0] = var2[2];
      this.textureS[1] = var2[3];
      this.textureT[0] = var2[4];
      this.textureT[1] = var2[5];
      this.normal[0] = var2[6];
      this.normal[1] = var2[7];
      this.normal[2] = var2[8];
      this.ray[0] = var2[9];
      this.ray[1] = var2[10];
      this.ray[2] = var2[11];
      this.ray[3] = var2[12];
      this.ray[4] = var2[13];
      this.ray[5] = var2[14];
   }
}
