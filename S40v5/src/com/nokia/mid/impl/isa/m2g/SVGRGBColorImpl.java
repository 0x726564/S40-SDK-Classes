package com.nokia.mid.impl.isa.m2g;

import org.w3c.dom.svg.SVGRGBColor;

public class SVGRGBColorImpl implements SVGRGBColor {
   private int[] ad = new int[3];

   protected SVGRGBColorImpl(int var1, int var2, int var3) {
      this.ad[0] = var1;
      this.ad[1] = var2;
      this.ad[2] = var3;
   }

   public int getRed() {
      return this.ad[0];
   }

   public int getGreen() {
      return this.ad[1];
   }

   public int getBlue() {
      return this.ad[2];
   }

   public final String toString() {
      return "SVGRGBColor( r = " + this.ad[0] + ", g = " + this.ad[1] + ", b = " + this.ad[2] + " )";
   }

   protected int[] getArray() {
      return this.ad;
   }
}
