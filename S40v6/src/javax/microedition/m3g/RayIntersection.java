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

   public float getTextureS(int index) {
      if (index >= 0 && index < this.textureS.length) {
         return this.textureS[index];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public float getTextureT(int index) {
      if (index >= 0 && index < this.textureT.length) {
         return this.textureT[index];
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

   public void getRay(float[] ray) {
      if (ray.length < 6) {
         throw new IllegalArgumentException();
      } else {
         ray[0] = this.ray[0];
         ray[1] = this.ray[1];
         ray[2] = this.ray[2];
         ray[3] = this.ray[3];
         ray[4] = this.ray[4];
         ray[5] = this.ray[5];
      }
   }

   static float[] createResult() {
      return new float[2 + 2 * Defs.NUM_TEXTURE_UNITS + 3 + 6];
   }

   void fill(int hIntersected, float[] result) {
      this.intersected = (Node)Object3D.getInstance(hIntersected);
      this.distance = result[0];
      this.submeshIndex = (int)result[1];
      this.textureS[0] = result[2];
      this.textureS[1] = result[3];
      this.textureT[0] = result[4];
      this.textureT[1] = result[5];
      this.normal[0] = result[6];
      this.normal[1] = result[7];
      this.normal[2] = result[8];
      this.ray[0] = result[9];
      this.ray[1] = result[10];
      this.ray[2] = result[11];
      this.ray[3] = result[12];
      this.ray[4] = result[13];
      this.ray[5] = result[14];
   }
}
