package com.nokia.mid.impl.isa.m2g;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGMatrix;

public class SVGMatrixImpl implements SVGMatrix {
   private float[] myMatrix = new float[6];

   protected SVGMatrixImpl() {
   }

   protected SVGMatrixImpl(float var1, float var2, float var3, float var4, float var5, float var6) {
      this.myMatrix[0] = var1;
      this.myMatrix[1] = var2;
      this.myMatrix[2] = var3;
      this.myMatrix[3] = var4;
      this.myMatrix[4] = var5;
      this.myMatrix[5] = var6;
   }

   protected SVGMatrixImpl(SVGMatrixImpl var1) {
      this.myMatrix[0] = var1.myMatrix[0];
      this.myMatrix[1] = var1.myMatrix[1];
      this.myMatrix[2] = var1.myMatrix[2];
      this.myMatrix[3] = var1.myMatrix[3];
      this.myMatrix[4] = var1.myMatrix[4];
      this.myMatrix[5] = var1.myMatrix[5];
   }

   protected float[] getArray() {
      return this.myMatrix;
   }

   protected void setArray(float[] var1) {
      this.myMatrix = var1;
   }

   public float getComponent(int var1) throws DOMException {
      if (var1 >= 0 && var1 <= 5) {
         return this.myMatrix[var1];
      } else {
         throw new DOMException((short)1, "Index Out of Bounds");
      }
   }

   public SVGMatrix mMultiply(SVGMatrix var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         SVGMatrixImpl var2 = (SVGMatrixImpl)var1;
         float var3 = this.myMatrix[0];
         float var4 = this.myMatrix[1];
         float var5 = this.myMatrix[2];
         float var6 = this.myMatrix[3];
         float var7 = this.myMatrix[4];
         float var8 = this.myMatrix[5];
         this.myMatrix[0] = var3 * var2.myMatrix[0] + var5 * var2.myMatrix[1];
         this.myMatrix[1] = var4 * var2.myMatrix[0] + var6 * var2.myMatrix[1];
         this.myMatrix[2] = var3 * var2.myMatrix[2] + var5 * var2.myMatrix[3];
         this.myMatrix[3] = var4 * var2.myMatrix[2] + var6 * var2.myMatrix[3];
         this.myMatrix[4] = var3 * var2.myMatrix[4] + var5 * var2.myMatrix[5] + var7;
         this.myMatrix[5] = var4 * var2.myMatrix[4] + var6 * var2.myMatrix[5] + var8;
         return this;
      }
   }

   public SVGMatrix inverse() throws SVGException {
      float var1 = this.myMatrix[0] * this.myMatrix[3] - this.myMatrix[2] * this.myMatrix[1];
      if (var1 == 0.0F) {
         throw new SVGException((short)2, "Matrix is not invertible");
      } else {
         return new SVGMatrixImpl(this.myMatrix[3] / var1, -this.myMatrix[1] / var1, -this.myMatrix[2] / var1, this.myMatrix[0] / var1, (this.myMatrix[2] * this.myMatrix[5] - this.myMatrix[3] * this.myMatrix[4]) / var1, (this.myMatrix[1] * this.myMatrix[4] - this.myMatrix[0] * this.myMatrix[5]) / var1);
      }
   }

   public SVGMatrix mTranslate(float var1, float var2) {
      if (var1 == 0.0F && var2 == 0.0F) {
         return this;
      } else {
         float var3 = this.myMatrix[0];
         float var4 = this.myMatrix[1];
         float var5 = this.myMatrix[2];
         float var6 = this.myMatrix[3];
         float var7 = this.myMatrix[4];
         float var8 = this.myMatrix[5];
         this.myMatrix[0] = var3;
         this.myMatrix[1] = var4;
         this.myMatrix[2] = var5;
         this.myMatrix[3] = var6;
         this.myMatrix[4] = var3 * var1 + var5 * var2 + var7;
         this.myMatrix[5] = var4 * var1 + var6 * var2 + var8;
         return this;
      }
   }

   public SVGMatrix mScale(float var1) {
      if (var1 == 1.0F) {
         return this;
      } else {
         float var2 = this.myMatrix[0];
         float var3 = this.myMatrix[1];
         float var4 = this.myMatrix[2];
         float var5 = this.myMatrix[3];
         float var6 = this.myMatrix[4];
         float var7 = this.myMatrix[5];
         this.myMatrix[0] = var2 * var1;
         this.myMatrix[1] = var3 * var1;
         this.myMatrix[2] = var4 * var1;
         this.myMatrix[3] = var5 * var1;
         this.myMatrix[4] = var6;
         this.myMatrix[5] = var7;
         return this;
      }
   }

   public SVGMatrix mRotate(float var1) {
      if (var1 % 360.0F == 0.0F) {
         return this;
      } else {
         var1 = var1 * 3.1415927F / 180.0F;
         float var2 = this.myMatrix[0];
         float var3 = this.myMatrix[1];
         float var4 = this.myMatrix[2];
         float var5 = this.myMatrix[3];
         float var6 = this.myMatrix[4];
         float var7 = this.myMatrix[5];
         this.myMatrix[0] = var2 * (float)Math.cos((double)var1) + var4 * (float)Math.sin((double)var1);
         this.myMatrix[1] = var3 * (float)Math.cos((double)var1) + var5 * (float)Math.sin((double)var1);
         this.myMatrix[2] = var4 * (float)Math.cos((double)var1) - var2 * (float)Math.sin((double)var1);
         this.myMatrix[3] = var5 * (float)Math.cos((double)var1) - var3 * (float)Math.sin((double)var1);
         this.myMatrix[4] = var6;
         this.myMatrix[5] = var7;
         return this;
      }
   }
}
