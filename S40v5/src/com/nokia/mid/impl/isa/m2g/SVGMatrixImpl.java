package com.nokia.mid.impl.isa.m2g;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGMatrix;

public class SVGMatrixImpl implements SVGMatrix {
   private float[] ab = new float[6];

   protected SVGMatrixImpl() {
   }

   protected SVGMatrixImpl(float var1, float var2, float var3, float var4, float var5, float var6) {
      this.ab[0] = var1;
      this.ab[1] = var2;
      this.ab[2] = var3;
      this.ab[3] = var4;
      this.ab[4] = var5;
      this.ab[5] = var6;
   }

   protected SVGMatrixImpl(SVGMatrixImpl var1) {
      this.ab[0] = var1.ab[0];
      this.ab[1] = var1.ab[1];
      this.ab[2] = var1.ab[2];
      this.ab[3] = var1.ab[3];
      this.ab[4] = var1.ab[4];
      this.ab[5] = var1.ab[5];
   }

   protected float[] getArray() {
      return this.ab;
   }

   protected void setArray(float[] var1) {
      this.ab = var1;
   }

   public float getComponent(int var1) throws DOMException {
      if (var1 >= 0 && var1 <= 5) {
         return this.ab[var1];
      } else {
         throw new DOMException((short)1, "Index Out of Bounds");
      }
   }

   public SVGMatrix mMultiply(SVGMatrix var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         SVGMatrixImpl var8 = (SVGMatrixImpl)var1;
         float var2 = this.ab[0];
         float var3 = this.ab[1];
         float var4 = this.ab[2];
         float var5 = this.ab[3];
         float var6 = this.ab[4];
         float var7 = this.ab[5];
         this.ab[0] = var2 * var8.ab[0] + var4 * var8.ab[1];
         this.ab[1] = var3 * var8.ab[0] + var5 * var8.ab[1];
         this.ab[2] = var2 * var8.ab[2] + var4 * var8.ab[3];
         this.ab[3] = var3 * var8.ab[2] + var5 * var8.ab[3];
         this.ab[4] = var2 * var8.ab[4] + var4 * var8.ab[5] + var6;
         this.ab[5] = var3 * var8.ab[4] + var5 * var8.ab[5] + var7;
         return this;
      }
   }

   public SVGMatrix inverse() throws SVGException {
      float var1;
      if ((var1 = this.ab[0] * this.ab[3] - this.ab[2] * this.ab[1]) == 0.0F) {
         throw new SVGException((short)2, "Matrix is not invertible");
      } else {
         return new SVGMatrixImpl(this.ab[3] / var1, -this.ab[1] / var1, -this.ab[2] / var1, this.ab[0] / var1, (this.ab[2] * this.ab[5] - this.ab[3] * this.ab[4]) / var1, (this.ab[1] * this.ab[4] - this.ab[0] * this.ab[5]) / var1);
      }
   }

   public SVGMatrix mTranslate(float var1, float var2) {
      if (var1 == 0.0F && var2 == 0.0F) {
         return this;
      } else {
         float var3 = this.ab[0];
         float var4 = this.ab[1];
         float var5 = this.ab[2];
         float var6 = this.ab[3];
         float var7 = this.ab[4];
         float var8 = this.ab[5];
         this.ab[0] = var3;
         this.ab[1] = var4;
         this.ab[2] = var5;
         this.ab[3] = var6;
         this.ab[4] = var3 * var1 + var5 * var2 + var7;
         this.ab[5] = var4 * var1 + var6 * var2 + var8;
         return this;
      }
   }

   public SVGMatrix mScale(float var1) {
      if (var1 == 1.0F) {
         return this;
      } else {
         float var2 = this.ab[0];
         float var3 = this.ab[1];
         float var4 = this.ab[2];
         float var5 = this.ab[3];
         float var6 = this.ab[4];
         float var7 = this.ab[5];
         this.ab[0] = var2 * var1;
         this.ab[1] = var3 * var1;
         this.ab[2] = var4 * var1;
         this.ab[3] = var5 * var1;
         this.ab[4] = var6;
         this.ab[5] = var7;
         return this;
      }
   }

   public SVGMatrix mRotate(float var1) {
      if (var1 % 360.0F == 0.0F) {
         return this;
      } else {
         var1 = var1 * 3.1415927F / 180.0F;
         float var2 = this.ab[0];
         float var3 = this.ab[1];
         float var4 = this.ab[2];
         float var5 = this.ab[3];
         float var6 = this.ab[4];
         float var7 = this.ab[5];
         this.ab[0] = var2 * (float)Math.cos((double)var1) + var4 * (float)Math.sin((double)var1);
         this.ab[1] = var3 * (float)Math.cos((double)var1) + var5 * (float)Math.sin((double)var1);
         this.ab[2] = var4 * (float)Math.cos((double)var1) - var2 * (float)Math.sin((double)var1);
         this.ab[3] = var5 * (float)Math.cos((double)var1) - var3 * (float)Math.sin((double)var1);
         this.ab[4] = var6;
         this.ab[5] = var7;
         return this;
      }
   }
}
