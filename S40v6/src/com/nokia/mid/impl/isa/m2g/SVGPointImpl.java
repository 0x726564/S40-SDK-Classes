package com.nokia.mid.impl.isa.m2g;

import org.w3c.dom.svg.SVGPoint;

public class SVGPointImpl implements SVGPoint {
   private SVGSVGElementImpl myElement;
   private float x;
   private float y;

   protected SVGPointImpl(SVGSVGElementImpl el) {
      this.myElement = el;
      this.x = 0.0F;
      this.y = 0.0F;
   }

   public void setX(float value) {
      if (!Float.isNaN(value)) {
         String zoomAndPan = this.myElement.getTrait("zoomAndPan");
         if (zoomAndPan == null || !zoomAndPan.equals("disable")) {
            this.x = value;
            this.myElement.updateTransformMatrix();
         }
      }
   }

   public void setY(float value) {
      if (!Float.isNaN(value)) {
         String zoomAndPan = this.myElement.getTrait("zoomAndPan");
         if (zoomAndPan == null || !zoomAndPan.equals("disable")) {
            this.y = value;
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
