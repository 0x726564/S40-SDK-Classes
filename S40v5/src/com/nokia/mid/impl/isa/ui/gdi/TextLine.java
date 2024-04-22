package com.nokia.mid.impl.isa.ui.gdi;

public class TextLine {
   public static final int ALIGNMENT_UNDEFINED = 0;
   public static final int ALIGNMENT_LEFT = 1;
   public static final int ALIGNMENT_CENTER = 2;
   public static final int ALIGNMENT_RIGHT = 3;
   String lv;
   private int lw;
   private int lx;
   private int ly;
   private int lz;
   private int lA;
   int lB;
   int lC;
   private Font ck = null;
   private int lD = 0;

   TextLine(String var1, int[] var2, Font var3, int var4, int var5) {
      this.lv = var1;
      this.ck = var3;
      this.lw = var2[0];
      this.lx = var2[1];
      this.ly = var2[2];
      this.lz = var2[3];
      this.lA = var2[4];
      this.lB = var4;
      this.lC = var5;
   }

   public int getTextLineWidth() {
      return this.lw;
   }

   public int getTextLineHeight() {
      return this.lx;
   }

   public int getTextLineBase() {
      return this.ly;
   }

   public int getTextLineLeading() {
      return this.lz;
   }

   public Font getTextLineFont() {
      return this.ck;
   }

   public int getAlignment() {
      return this.lD;
   }

   public boolean isTextFlowLTR() {
      return this.lA <= 0;
   }

   public boolean isTruncated() {
      return this.lC + this.lB < this.lv.length();
   }

   public void setAlignment(int var1) {
      if (var1 != 0 && var1 != 1 && var1 != 2 && var1 != 3) {
         throw new IllegalArgumentException();
      } else {
         this.lD = var1;
      }
   }

   public void setEllipsis(String var1) {
      if (var1 != null) {
         String var2;
         int var3 = (var2 = this.lv.substring(this.lC, this.lC + this.lB)).indexOf(10);
         int var4 = var2.indexOf(13);
         if (var3 == -1 || var4 < var3 && var4 > -1) {
            var3 = var4;
         }

         if (var3 > -1) {
            var2 = var2.substring(0, var3);
         }

         this.lv = var2.concat(var1);
         this.lC = 0;
         this.lB = this.lv.length();
      }
   }
}
