package com.nokia.mid.impl.isa.m2g;

import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGMatrix;

public class SVGMatrixImpl implements SVGMatrix {
   private float[] myMatrix = new float[6];

   protected SVGMatrixImpl() {
   }

   protected SVGMatrixImpl(float a, float b, float c, float d, float e, float f) {
      this.myMatrix[0] = a;
      this.myMatrix[1] = b;
      this.myMatrix[2] = c;
      this.myMatrix[3] = d;
      this.myMatrix[4] = e;
      this.myMatrix[5] = f;
   }

   protected SVGMatrixImpl(SVGMatrixImpl original) {
      this.myMatrix[0] = original.myMatrix[0];
      this.myMatrix[1] = original.myMatrix[1];
      this.myMatrix[2] = original.myMatrix[2];
      this.myMatrix[3] = original.myMatrix[3];
      this.myMatrix[4] = original.myMatrix[4];
      this.myMatrix[5] = original.myMatrix[5];
   }

   protected float[] getArray() {
      return this.myMatrix;
   }

   protected void setArray(float[] newArray) {
      this.myMatrix = newArray;
   }

   public float getComponent(int index) throws DOMException {
      if (index >= 0 && index <= 5) {
         return this.myMatrix[index];
      } else {
         throw new DOMException((short)1, "Index Out of Bounds");
      }
   }

   public SVGMatrix mMultiply(SVGMatrix mtx) {
      if (mtx == null) {
         throw new NullPointerException();
      } else {
         SVGMatrixImpl secondMatrix = (SVGMatrixImpl)mtx;
         float a = this.myMatrix[0];
         float b = this.myMatrix[1];
         float c = this.myMatrix[2];
         float d = this.myMatrix[3];
         float e = this.myMatrix[4];
         float f = this.myMatrix[5];
         this.myMatrix[0] = a * secondMatrix.myMatrix[0] + c * secondMatrix.myMatrix[1];
         this.myMatrix[1] = b * secondMatrix.myMatrix[0] + d * secondMatrix.myMatrix[1];
         this.myMatrix[2] = a * secondMatrix.myMatrix[2] + c * secondMatrix.myMatrix[3];
         this.myMatrix[3] = b * secondMatrix.myMatrix[2] + d * secondMatrix.myMatrix[3];
         this.myMatrix[4] = a * secondMatrix.myMatrix[4] + c * secondMatrix.myMatrix[5] + e;
         this.myMatrix[5] = b * secondMatrix.myMatrix[4] + d * secondMatrix.myMatrix[5] + f;
         return this;
      }
   }

   public SVGMatrix inverse() throws SVGException {
      float det = this.myMatrix[0] * this.myMatrix[3] - this.myMatrix[2] * this.myMatrix[1];
      if (det == 0.0F) {
         throw new SVGException((short)2, "Matrix is not invertible");
      } else {
         return new SVGMatrixImpl(this.myMatrix[3] / det, -this.myMatrix[1] / det, -this.myMatrix[2] / det, this.myMatrix[0] / det, (this.myMatrix[2] * this.myMatrix[5] - this.myMatrix[3] * this.myMatrix[4]) / det, (this.myMatrix[1] * this.myMatrix[4] - this.myMatrix[0] * this.myMatrix[5]) / det);
      }
   }

   public SVGMatrix mTranslate(float x, float y) {
      if (x == 0.0F && y == 0.0F) {
         return this;
      } else {
         float a = this.myMatrix[0];
         float b = this.myMatrix[1];
         float c = this.myMatrix[2];
         float d = this.myMatrix[3];
         float e = this.myMatrix[4];
         float f = this.myMatrix[5];
         this.myMatrix[0] = a;
         this.myMatrix[1] = b;
         this.myMatrix[2] = c;
         this.myMatrix[3] = d;
         this.myMatrix[4] = a * x + c * y + e;
         this.myMatrix[5] = b * x + d * y + f;
         return this;
      }
   }

   public SVGMatrix mScale(float scaleFactor) {
      if (scaleFactor == 1.0F) {
         return this;
      } else {
         float a = this.myMatrix[0];
         float b = this.myMatrix[1];
         float c = this.myMatrix[2];
         float d = this.myMatrix[3];
         float e = this.myMatrix[4];
         float f = this.myMatrix[5];
         this.myMatrix[0] = a * scaleFactor;
         this.myMatrix[1] = b * scaleFactor;
         this.myMatrix[2] = c * scaleFactor;
         this.myMatrix[3] = d * scaleFactor;
         this.myMatrix[4] = e;
         this.myMatrix[5] = f;
         return this;
      }
   }

   public SVGMatrix mRotate(float angle) {
      if (angle % 360.0F == 0.0F) {
         return this;
      } else {
         angle = angle * 3.1415927F / 180.0F;
         float a = this.myMatrix[0];
         float b = this.myMatrix[1];
         float c = this.myMatrix[2];
         float d = this.myMatrix[3];
         float e = this.myMatrix[4];
         float f = this.myMatrix[5];
         this.myMatrix[0] = a * (float)Math.cos((double)angle) + c * (float)Math.sin((double)angle);
         this.myMatrix[1] = b * (float)Math.cos((double)angle) + d * (float)Math.sin((double)angle);
         this.myMatrix[2] = c * (float)Math.cos((double)angle) - a * (float)Math.sin((double)angle);
         this.myMatrix[3] = d * (float)Math.cos((double)angle) - b * (float)Math.sin((double)angle);
         this.myMatrix[4] = e;
         this.myMatrix[5] = f;
         return this;
      }
   }
}
