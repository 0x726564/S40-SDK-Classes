package com.nokia.mid.impl.isa.m2g;

import org.w3c.dom.svg.SVGRect;

public class SVGRectImpl implements SVGRect {
   private float[] xywh = new float[4];

   protected SVGRectImpl(float init_x, float init_y, float init_width, float init_height) {
      this.xywh[0] = init_x;
      this.xywh[1] = init_y;
      this.xywh[2] = init_width;
      this.xywh[3] = init_height;
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

   public void setX(float value) {
      this.xywh[0] = value;
   }

   public void setY(float value) {
      this.xywh[1] = value;
   }

   public void setWidth(float value) {
      this.xywh[2] = value;
   }

   public void setHeight(float value) {
      this.xywh[3] = value;
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
