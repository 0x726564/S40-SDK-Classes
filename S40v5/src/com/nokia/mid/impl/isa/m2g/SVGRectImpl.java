package com.nokia.mid.impl.isa.m2g;

import org.w3c.dom.svg.SVGRect;

public class SVGRectImpl implements SVGRect {
   private float[] fX = new float[4];

   protected SVGRectImpl(float var1, float var2, float var3, float var4) {
      this.fX[0] = var1;
      this.fX[1] = var2;
      this.fX[2] = var3;
      this.fX[3] = var4;
   }

   protected SVGRectImpl() {
      this.fX[0] = 0.0F;
      this.fX[1] = 0.0F;
      this.fX[2] = 0.0F;
      this.fX[3] = 0.0F;
   }

   protected float[] getArray() {
      return this.fX;
   }

   public void setX(float var1) {
      this.fX[0] = var1;
   }

   public void setY(float var1) {
      this.fX[1] = var1;
   }

   public void setWidth(float var1) {
      this.fX[2] = var1;
   }

   public void setHeight(float var1) {
      this.fX[3] = var1;
   }

   public float getX() {
      return this.fX[0];
   }

   public float getY() {
      return this.fX[1];
   }

   public float getWidth() {
      return this.fX[2];
   }

   public float getHeight() {
      return this.fX[3];
   }
}
