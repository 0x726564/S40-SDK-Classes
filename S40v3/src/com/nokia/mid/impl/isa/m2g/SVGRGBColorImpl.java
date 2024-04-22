package com.nokia.mid.impl.isa.m2g;

import org.w3c.dom.svg.SVGRGBColor;

public class SVGRGBColorImpl implements SVGRGBColor {
   private int[] rgb = new int[3];

   protected SVGRGBColorImpl(int var1, int var2, int var3) {
      this.rgb[0] = var1;
      this.rgb[1] = var2;
      this.rgb[2] = var3;
   }

   public int getRed() {
      return this.rgb[0];
   }

   public int getGreen() {
      return this.rgb[1];
   }

   public int getBlue() {
      return this.rgb[2];
   }

   public String toString() {
      return "SVGRGBColor( r = " + this.rgb[0] + ", g = " + this.rgb[1] + ", b = " + this.rgb[2] + " )";
   }

   protected int[] getArray() {
      return this.rgb;
   }
}
