package com.nokia.mid.impl.isa.ui.gdi;

public class ColorCtrl {
   private int fgColor;
   private int bgColor = -1;
   private int blendFactor = 0;

   private static native void nativeStaticInitialiser();

   public int getFgColor() {
      return this.fgColor;
   }

   public void setFgColor(int var1) {
      this.fgColor = var1;
      this.setBlendFactor(var1 >>> 24 & 255);
   }

   public void setFgColorOnly(int var1) {
      this.fgColor = var1;
   }

   public int getBgColor() {
      return this.bgColor;
   }

   public void setBgColor(int var1) {
      this.bgColor = var1;
   }

   public int getBlendFactor() {
      return this.blendFactor;
   }

   public void setBlendFactor(int var1) {
      this.blendFactor = var1 >= 255 ? 100 : (var1 <= 0 ? 0 : var1 * 100 / 255);
   }

   static {
      nativeStaticInitialiser();
   }
}
