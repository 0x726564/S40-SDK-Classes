package com.nokia.mid.impl.isa.m2g;

import org.w3c.dom.svg.SVGPoint;

public class SVGPointImpl implements SVGPoint {
   private SVGSVGElementImpl bk;
   private float x;
   private float y;

   protected SVGPointImpl(SVGSVGElementImpl var1) {
      this.bk = var1;
      this.x = 0.0F;
      this.y = 0.0F;
   }

   public void setX(float var1) {
      if (!Float.isNaN(var1)) {
         String var2;
         if ((var2 = this.bk.getTrait("zoomAndPan")) == null || !var2.equals("disable")) {
            this.x = var1;
            this.bk.updateTransformMatrix();
         }
      }
   }

   public void setY(float var1) {
      if (!Float.isNaN(var1)) {
         String var2;
         if ((var2 = this.bk.getTrait("zoomAndPan")) == null || !var2.equals("disable")) {
            this.y = var1;
            this.bk.updateTransformMatrix();
         }
      }
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }
}
