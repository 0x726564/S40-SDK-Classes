package com.nokia.mid.impl.isa.m2g;

import org.w3c.dom.svg.SVGRect;

public class SVGRectImpl implements SVGRect {
   private float[] xywh = new float[4];

   protected SVGRectImpl(float var1, float var2, float var3, float var4) {
      this.xywh[0] = var1;
      this.xywh[1] = var2;
      this.xywh[2] = var3;
      this.xywh[3] = var4;
   }

   protected SVGRectImpl() {
      this.xywh[0] = 0.0F;
      this.xywh[1] = 0.0F;
      this.xywh[2] = 0.0F;
      this.xywh[3] = 0.0F;
   }

   protected float[] getArray() {
      return this.xywh;
   }

   public void setX(float var1) {
      this.xywh[0] = var1;
   }

   public void setY(float var1) {
      this.xywh[1] = var1;
   }

   public void setWidth(float var1) {
      this.xywh[2] = var1;
   }

   public void setHeight(float var1) {
      this.xywh[3] = var1;
   }

   public float getX() {
      return this.xywh[0];
   }

   public float getY() {
      return this.xywh[1];
   }

   public float getWidth() {
      return this.xywh[2];
   }

   public float getHeight() {
      return this.xywh[3];
   }
}
