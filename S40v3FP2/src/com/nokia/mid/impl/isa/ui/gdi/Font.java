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
   private static int midpSmallData = 0;
   private static int midpSmallType = 0;
   private static int midpMediumData = 0;
   private static int midpMediumType = 0;
   private static int midpLargeData = 0;
   private static int midpLargeType = 0;
   private static int midpSmallBoldData = 0;
   private static int midpSmallBoldType = 0;
   private static int midpMediumBoldData = 0;
   private static int midpMediumBoldType = 0;
   private static int midpLargeBoldData = 0;
   private static int midpLargeBoldType = 0;
   private static int midpSmallItalicData = 0;
   private static int midpSmallItalicType = 0;
   private static int midpMediumItalicData = 0;
   private static int midpMediumItalicType = 0;
   private static int midpLargeItalicData = 0;
   private static int midpLargeItalicType = 0;
   private static int midpSmallBoldItalicData = 0;
   private static int midpSmallBoldItalicType = 0;
   private static int midpMediumBoldItalicData = 0;
   private static int midpMediumBoldItalicType = 0;
   private static int midpLargeBoldItalicData = 0;
   private static int midpLargeBoldItalicType = 0;
   private int fontType;
   private int fontName;
   private int nativeData;

   public Font(int var1, int var2, boolean var3) {
      int var6 = 0;
      byte var7 = -3;
      int[] var4;
      int[] var5;
      switch(var1) {
      case 8:
         var4 = new int[]{midpSmallType, midpSmallBoldType, midpSmallItalicType, midpSmallBoldItalicType};
         var5 = new int[]{midpSmallData, midpSmallBoldData, midpSmallItalicData, midpSmallBoldItalicData};
         break;
      case 16:
         var4 = new int[]{midpLargeType, midpLargeBoldType, midpLargeItalicType, midpLargeBoldItalicType};
         var5 = new int[]{midpLargeData, midpLargeBoldData, midpLargeItalicData, midpLargeBoldItalicData};
         break;
      default:
         var4 = new int[]{midpMediumType, midpMediumBoldType, midpMediumItalicType, midpMediumBoldItalicType};
         var5 = new int[]{midpMediumData, midpMediumBoldData, midpMediumItalicData, midpMediumBoldItalicData};
      }

      if ((var2 & 1) != 0) {
         ++var6;
         this.fontType |= 2;
         var7 = 2;
      }

      if ((var2 & 2) != 0) {
         var6 += 2;
         this.fontType |= 4;
      }

      this.fontType |= var4[var6] & var7;
      this.nativeData = var5[var6];
      if ((var2 & 4) != 0) {
         this.fontType |= 8;
      }

   }

   public Font(int var1, int var2) {
      this.nativeInit(var1, var2, false);
      this.fontName = var1;
   }

   public native int getCharWidth(char var1);

   public int getDefaultCharHeight() {
      int var1 = this.getDefaultCharHeightInt();
      if ((this.fontType & 8) != 0) {
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
      return this.fontType;
   }

   public int getFontName() {
      return this.fontName;
   }

   public int getMIDPStyle() {
      int var1 = 0;
      if ((this.fontType & 2) != 0) {
         var1 |= 1;
      }

      if ((this.fontType & 4) != 0) {
         var1 |= 2;
      }

      if ((this.fontType & 8) != 0) {
         var1 |= 4;
      }

      return var1;
   }

   public int getMIDPSize() {
      byte var1 = 0;
      if (this.nativeData != midpMediumData && this.nativeData != midpMediumBoldData && this.nativeData != midpMediumItalicData && this.nativeData != midpMediumBoldItalicData) {
         if (this.nativeData != midpSmallData && this.nativeData != midpSmallBoldData && this.nativeData != midpSmallItalicData && this.nativeData != midpSmallBoldItalicData) {
            if (this.nativeData != midpLargeData && this.nativeData != midpLargeBoldData && this.nativeData != midpLargeItalicData && this.nativeData != midpLargeBoldItalicData) {
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
         int var6 = var3.getTextLineWidth();
         var4 = var6 > var4 ? var6 : var4;
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
      var3 = var2.getTextLine(32767);
      if (var3 != null) {
         var4 = var3.getTextLineHeight();
      }

      var2.destroyBreaker();
      return var4;
   }

   public String getStringWithCompatibleFont(String var1) {
      return var1;
   }

   private native char[] getCharsWithCompatibleFont(String var1);

   private native void nativeInit(int var1, int var2, boolean var3);

   private native int getDefaultCharHeightInt();

   private static native void nativeStaticInitialiser();

   static {
      nativeStaticInitialiser();
   }
}
