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

   public Font(int size, int style, boolean is_lcduiFont) {
      int index = 0;
      int boldTypeStatus = -3;
      int[] fontTypes;
      int[] fontData;
      switch(size) {
      case 8:
         fontTypes = new int[]{midpSmallType, midpSmallBoldType, midpSmallItalicType, midpSmallBoldItalicType};
         fontData = new int[]{midpSmallData, midpSmallBoldData, midpSmallItalicData, midpSmallBoldItalicData};
         break;
      case 16:
         fontTypes = new int[]{midpLargeType, midpLargeBoldType, midpLargeItalicType, midpLargeBoldItalicType};
         fontData = new int[]{midpLargeData, midpLargeBoldData, midpLargeItalicData, midpLargeBoldItalicData};
         break;
      default:
         fontTypes = new int[]{midpMediumType, midpMediumBoldType, midpMediumItalicType, midpMediumBoldItalicType};
         fontData = new int[]{midpMediumData, midpMediumBoldData, midpMediumItalicData, midpMediumBoldItalicData};
      }

      if ((style & 1) != 0) {
         ++index;
         this.fontType |= 2;
         boldTypeStatus = 2;
      }

      if ((style & 2) != 0) {
         index += 2;
         this.fontType |= 4;
      }

      this.fontType |= fontTypes[index] & boldTypeStatus;
      this.nativeData = fontData[index];
      if ((style & 4) != 0) {
         this.fontType |= 8;
      }

   }

   public Font(int fontName, int fontType) {
      this.nativeInit(fontName, fontType, false);
      this.fontName = fontName;
   }

   public native int getCharWidth(char var1);

   public int getDefaultCharHeight() {
      int _chHeight = this.getDefaultCharHeightInt();
      if ((this.fontType & 8) != 0) {
         _chHeight += 2;
      }

      return _chHeight;
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
      int style = 0;
      if ((this.fontType & 2) != 0) {
         style |= 1;
      }

      if ((this.fontType & 4) != 0) {
         style |= 2;
      }

      if ((this.fontType & 8) != 0) {
         style |= 4;
      }

      return style;
   }

   public int getMIDPSize() {
      int size = 0;
      if (this.nativeData != midpMediumData && this.nativeData != midpMediumBoldData && this.nativeData != midpMediumItalicData && this.nativeData != midpMediumBoldItalicData) {
         if (this.nativeData != midpSmallData && this.nativeData != midpSmallBoldData && this.nativeData != midpSmallItalicData && this.nativeData != midpSmallBoldItalicData) {
            if (this.nativeData != midpLargeData && this.nativeData != midpLargeBoldData && this.nativeData != midpLargeItalicData && this.nativeData != midpLargeBoldItalicData) {
               switch(UIStyle.DEFAULT_FONT_TYPE) {
               case 1:
                  size = 8;
                  break;
               case 2:
               case 4:
                  size = 0;
                  break;
               case 3:
                  size = 16;
               }
            } else {
               size = 16;
            }
         } else {
            size = 8;
         }
      } else {
         size = 0;
      }

      return size;
   }

   public int stringWidth(String str) {
      TextBreaker breaker = TextBreaker.getBreaker();
      TextLine tline = null;
      int maxWidth = 0;
      int lineWidth = false;
      breaker.enableWordWrapping(false);
      breaker.setFont(this);
      breaker.setText(str);

      while((tline = breaker.getTextLine(32767)) != null) {
         int lineWidth = tline.getTextLineWidth();
         maxWidth = lineWidth > maxWidth ? lineWidth : maxWidth;
      }

      breaker.destroyBreaker();
      return maxWidth;
   }

   public int stringHeight(String str) {
      TextBreaker breaker = TextBreaker.getBreaker();
      TextLine tline = null;
      int ret = 0;
      breaker.enableWordWrapping(false);
      breaker.setFont(this);
      breaker.setText(str);
      tline = breaker.getTextLine(32767);
      if (tline != null) {
         ret = tline.getTextLineHeight();
      }

      breaker.destroyBreaker();
      return ret;
   }

   public String getStringWithCompatibleFont(String str) {
      return str;
   }

   private native char[] getCharsWithCompatibleFont(String var1);

   private native void nativeInit(int var1, int var2, boolean var3);

   private native int getDefaultCharHeightInt();

   private static native void nativeStaticInitialiser();

   static {
      nativeStaticInitialiser();
   }
}
