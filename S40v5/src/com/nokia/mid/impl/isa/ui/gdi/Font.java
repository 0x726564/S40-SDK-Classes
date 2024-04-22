package com.nokia.mid.impl.isa.ui.gdi;

import com.nokia.mid.impl.isa.ui.style.UIStyle;

public class Font {
   public static final int TYPE_PLAIN = 1;
   public static final int TYPE_BOLD = 2;
   public static final int TYPE_ITALIC = 4;
   public static final int TYPE_UNDERLINED = 8;
   public static final int TYPE_STRIKE_THROUGH = 16;
   public static final int TYPE_DIGI = 32;
   public static final int TYPE_LIGHT = 64;
   public static final int TYPE_NORM = 128;
   private static int hO = 0;
   private static int hP = 0;
   private static int hQ = 0;
   private static int hR = 0;
   private static int hS = 0;
   private static int hT = 0;
   private static int hU = 0;
   private static int hV = 0;
   private static int hW = 0;
   private static int hX = 0;
   private static int hY = 0;
   private static int hZ = 0;
   private static int ia = 0;
   private static int ib = 0;
   private static int ic = 0;
   private static int ie = 0;
   private static int if = 0;
   private static int ig = 0;
   private static int ih = 0;
   private static int ii = 0;
   private static int ij = 0;
   private static int ik = 0;
   private static int il = 0;
   private static int im = 0;
   private int io;
   private int ip;
   private int iq;

   public Font(int var1, int var2, boolean var3) {
      int var4 = 0;
      byte var5 = -3;
      int[] var6;
      int[] var7;
      switch(var1) {
      case 8:
         var6 = new int[]{0, 0, 0, 0};
         var7 = new int[]{0, 0, 0, 0};
         break;
      case 16:
         var6 = new int[]{0, 0, 0, 0};
         var7 = new int[]{0, 0, 0, 0};
         break;
      default:
         var6 = new int[]{0, 0, 0, 0};
         var7 = new int[]{0, 0, 0, 0};
      }

      if ((var2 & 1) != 0) {
         ++var4;
         this.io |= 2;
         var5 = 2;
      }

      if ((var2 & 2) != 0) {
         var4 += 2;
         this.io |= 4;
      }

      this.io |= var6[var4] & var5;
      this.iq = var7[var4];
      if ((var2 & 4) != 0) {
         this.io |= 8;
      }

   }

   public Font(int var1, int var2) {
      this.nativeInit(var1, var2, false);
      this.ip = var1;
   }

   public native int getCharWidth(char var1);

   public int getDefaultCharHeight() {
      int var1 = this.getDefaultCharHeightInt();
      if ((this.io & 8) != 0) {
         var1 += 2;
      }

      return var1;
   }

   public native int getBaselinePositionImpl();

   public native int getCharASpace(char var1);

   public native int getCharCSpace(char var1);

   public native int getAbove();

   public native int getBelow();

   public int getFontType() {
      return this.io;
   }

   public int getFontName() {
      return this.ip;
   }

   public int getMIDPStyle() {
      int var1 = 0;
      if ((this.io & 2) != 0) {
         var1 = 1;
      }

      if ((this.io & 4) != 0) {
         var1 |= 2;
      }

      if ((this.io & 8) != 0) {
         var1 |= 4;
      }

      return var1;
   }

   public int getMIDPSize() {
      byte var1 = 0;
      if (this.iq != 0 && this.iq != 0 && this.iq != 0 && this.iq != 0) {
         if (this.iq != 0 && this.iq != 0 && this.iq != 0 && this.iq != 0) {
            if (this.iq != 0 && this.iq != 0 && this.iq != 0 && this.iq != 0) {
               switch(UIStyle.DEFAULT_FONT_TYPE) {
               case 1:
                  var1 = 8;
                  break;
               case 2:
               case 4:
                  var1 = 0;
                  break;
               case 3:
                  var1 = 16;
               }
            } else {
               var1 = 16;
            }
         } else {
            var1 = 8;
         }
      } else {
         var1 = 0;
      }

      return var1;
   }

   public int stringWidth(String var1) {
      TextBreaker var2 = TextBreaker.getBreaker();
      TextLine var3 = null;
      int var4 = 0;
      boolean var5 = false;
      var2.enableWordWrapping(false);
      var2.setFont(this);
      var2.setText(var1);

      while((var3 = var2.getTextLine(32767)) != null) {
         int var6;
         var4 = (var6 = var3.getTextLineWidth()) > var4 ? var6 : var4;
      }

      var2.destroyBreaker();
      return var4;
   }

   public int stringHeight(String var1) {
      TextBreaker var2 = TextBreaker.getBreaker();
      TextLine var3 = null;
      int var4 = 0;
      var2.enableWordWrapping(false);
      var2.setFont(this);
      var2.setText(var1);
      if ((var3 = var2.getTextLine(32767)) != null) {
         var4 = var3.getTextLineHeight();
      }

      var2.destroyBreaker();
      return var4;
   }

   public String getStringWithCompatibleFont(String var1) {
      return var1;
   }

   private native void nativeInit(int var1, int var2, boolean var3);

   private native int getDefaultCharHeightInt();

   private static native void nativeStaticInitialiser();

   static {
      nativeStaticInitialiser();
   }
}
