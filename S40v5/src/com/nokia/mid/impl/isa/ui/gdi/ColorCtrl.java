package com.nokia.mid.impl.isa.ui.gdi;

public class ColorCtrl {
   private int gg;
   private int gh = -1;
   private int gi = 0;

   private static native void nativeStaticInitialiser();

   public int getFgColor() {
      return this.gg;
   }

   public void setFgColor(int var1) {
      this.gg = var1;
      this.setBlendFactor(var1 >>> 24 & 255);
   }

   public void setFgColorOnly(int var1) {
      this.gg = var1;
   }

   public int getBgColor() {
      return this.gh;
   }

   public void setBgColor(int var1) {
      this.gh = var1;
   }

   public int getBlendFactor() {
      return this.gi;
   }

   public void setBlendFactor(int var1) {
      this.gi = var1 >= 255 ? 100 : (var1 <= 0 ? 0 : var1 * 100 / 255);
   }

   static {
      nativeStaticInitialiser();
   }
}
