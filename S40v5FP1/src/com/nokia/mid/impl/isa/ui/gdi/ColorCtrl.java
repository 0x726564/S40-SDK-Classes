package com.nokia.mid.impl.isa.ui.gdi;

public class ColorCtrl {
   private int fgColor;
   private int bgColor = -1;
   private int blendFactor = 0;

   private static native void nativeStaticInitialiser();

   public int getFgColor() {
      return this.fgColor;
   }

   public void setFgColor(int fgColor) {
      this.fgColor = fgColor;
      this.setBlendFactor(fgColor >>> 24 & 255);
   }

   public void setFgColorOnly(int fgColor) {
      this.fgColor = fgColor;
   }

   public int getBgColor() {
      return this.bgColor;
   }

   public void setBgColor(int bgColor) {
      this.bgColor = bgColor;
   }

   public int getBlendFactor() {
      return this.blendFactor;
   }

   public void setBlendFactor(int alpha) {
      this.blendFactor = alpha >= 255 ? 100 : (alpha <= 0 ? 0 : alpha * 100 / 255);
   }

   static {
      nativeStaticInitialiser();
   }
}
