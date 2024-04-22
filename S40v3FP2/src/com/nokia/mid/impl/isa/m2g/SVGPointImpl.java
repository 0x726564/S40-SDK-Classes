package com.nokia.mid.impl.isa.m2g;

import org.w3c.dom.svg.SVGPoint;

public class SVGPointImpl implements SVGPoint {
   private SVGSVGElementImpl myElement;
   private float x;
   private float y;

   protected SVGPointImpl(SVGSVGElementImpl var1) {
      this.myElement = var1;
      this.x = 0.0F;
      this.y = 0.0F;
   }

   public void setX(float var1) {
      if (!Float.isNaN(var1)) {
         String var2 = this.myElement.getTrait("zoomAndPan");
         if (var2 == null || !var2.equals("disable")) {
            this.x = var1;
            this.myElement.updateTransformMatrix();
         }
      }
   }

   public void setY(float var1) {
      if (!Float.isNaN(var1)) {
         String var2 = this.myElement.getTrait("zoomAndPan");
         if (var2 == null || !var2.equals("disable")) {
            this.y = var1;
            this.myElement.updateTransformMatrix();
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
