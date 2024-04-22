package com.nokia.mid.impl.isa.ui.style;

import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.gdi.Font;
import com.nokia.mid.impl.isa.ui.gdi.Graphics;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.isa.ui.gdi.TextLine;

public class UIStyle {
   public static int COLOUR_WHITE;
   public static int COLOUR_BLACK;
   public static int COLOUR_TEXT;
   public static int COLOUR_BACKGROUND;
   public static int COLOUR_HIGHLIGHT;
   public static int COLOUR_HIGHLIGHT_TEXT;
   public static int COLOUR_TICKER_BACKGROUND;
   public static int COLOUR_TICKER_TEXT;
   public static int COLOUR_GRAY;
   public static int COLOUR_SCHEME_HIGHLIGHT;
   public static int COLOUR_EDITOR_FOCUSED;
   public static int COLOUR_EDITOR_UNFOCUSED;
   public static int FORM_MAX_SCROLL;
   public static int DEFAULT_TEXT_LEADING;
   public static int EDITABLE_TEXT_BOTTOM_MARGIN;
   public static int CHOICE_ITEMS_TOP_MARGIN;
   public static int BUTTON_BORDER_WIDTH;
   public static int BUTTON_BORDER_HEIGHT;
   public static int HYPERLINK_CUSTOMITEM_BORDER_WIDTH;
   public static int HYPERLINK_CUSTOMITEM_BORDER_HEIGHT;
   public static int CELL_SPACING;
   public static int LABEL_PAD;
   public static int CUSTOMITEM_BORDER_PAD;
   public static int DEFAULT_FONT_TYPE;
   public static int BORDER_NONE = 0;
   public static int BORDER_TYPE_1 = 1;
   public static int BORDER_TYPE_2 = 2;
   public static int BORDER_TYPE_HIGHLIGHT = 3;
   public static int BORDER_TYPE_3 = 4;
   public static int BORDER_TYPE_4 = 5;
   public static int BORDER_TYPE_5 = 6;
   public static int BORDER_TYPE_8 = 7;
   public static int BORDER_TYPE_9 = 8;
   public static int BORDER_TYPE_10 = 9;
   public static final int BORDER_TYPE_FORM_ITEM = 99;
   public static final int SMALL_FONT = 0;
   public static final int MEDIUM_FONT = 1;
   public static final int LARGE_FONT = 2;
   public static final int MEDIUM_BOLD_FONT = 3;
   public static final int TICKER_FONT = 4;
   public static final int DEFAULT_FONT = 5;
   public static final int MAX_FONT_ID = 5;
   private static int GAUGE_PAD_XY = 2;
   static final int EDITABLE_TEXT_X_PAD = 1;
   static final int EDITABLE_TEXT_Y_PAD = 1;
   public static boolean isAlignedLeftToRight;
   private static int num_soft_keys;
   private static boolean ui_colour;
   private static boolean four_way_scroll;
   private static boolean inLine;
   private static boolean rotator;
   private static boolean usesBackgroundsInsteadOfBorders;
   private static Font[] fonts = new Font[6];
   private static volatile UIStyle instance;
   private static boolean canvasHasBgImage;
   static Zone[] zones;
   private int editableTextHighlightedWidth;
   private int editableTextHighlightedX;

   private UIStyle() {
      zones = new Zone[50];
   }

   public static UIStyle getUIStyle() {
      if (instance == null) {
         instance = new UIStyle();
      }

      return instance;
   }

   public Zone getZone(int var1) {
      Zone var2 = null;
      if (var1 >= 0 && var1 <= 49) {
         synchronized(this) {
            var2 = zones[var1];
            if (var2 == null) {
               var2 = new Zone();
               if (assignZoneFields(var1, var2)) {
                  zones[var1] = var2;
               } else {
                  var2 = null;
               }
            }
         }
      }

      return var2;
   }

   public Font getDefaultFont() {
      return this.getFont(5);
   }

   public void drawScrollbar(Graphics var1, Zone var2, int var3, int var4, int var5, int var6, boolean var7) {
      this.nativeDrawScrollBar(var1, var2.x, var2.y, var2.width, var2.height, var3, var4, var5, var6, var7);
   }

   public void drawBorder(Graphics var1, int var2, int var3, int var4, int var5, int var6, boolean var7) {
      if (var6 != BORDER_TYPE_5 || var7) {
         if (usesBackgroundsInsteadOfBorders) {
            ColorCtrl var8 = var1.getColorCtrl();
            int var9 = var8.getFgColor();
            if (var6 == BORDER_TYPE_5 && var7) {
               var8.setFgColor(COLOUR_SCHEME_HIGHLIGHT);
               var1.drawRect((short)var2, (short)var3, (short)var4, (short)var5);
            } else if (var6 == BORDER_TYPE_8 && var7) {
               this.nativeDrawBorder(var1, var2, var3, var4, var5, var6, var7);
               var8.setFgColor(COLOUR_SCHEME_HIGHLIGHT);
               var1.drawRoundRect((short)var2, (short)var3, (short)var4, (short)var5, 2, 2);
            } else {
               this.nativeDrawBorder(var1, var2, var3, var4, var5, var6, var7);
            }

            var8.setFgColor(var9);
         } else {
            this.nativeDrawBorder(var1, var2, var3, var4, var5, var6, var7);
         }

      }
   }

   public void drawBorder(Graphics var1, Zone var2) {
      int var3 = var2.getBorderType();
      if (var3 != BORDER_NONE) {
         this.drawBorder(var1, var2.x, var2.y, var2.width, var2.height, var3, false);
      }

   }

   public void setIndex(int var1) {
      this.nativeDisplayIndex(var1);
   }

   public void hideIndex() {
      this.nativeDisplayIndex(-1);
   }

   public void drawGauge(Graphics var1, Zone var2, int var3, int var4, int var5, int var6, boolean var7, boolean var8, boolean var9) {
      int var10 = var2.getMarginLeft();
      int var11 = var2.getMarginRight();
      int var12 = var2.getMarginTop();
      int var13 = var2.getMarginBottom();
      this.nativeDrawGauge(var1, (short)(var3 + var2.x), (short)(var4 + var2.y), (short)var2.width, (short)var2.height, (short)(var3 + var2.x + var10), (short)(var4 + var2.y + var12), (short)(var2.width - var10 - var11 - 2), (short)(var2.height - var12 - var13 - 2), var5, var6, var8, var9);
   }

   public void drawHighlightBar(Graphics var1, int var2, int var3, int var4, int var5, boolean var6) {
      if (var6) {
         this.drawBorder(var1, var2, var3, var4, var5, BORDER_TYPE_HIGHLIGHT, true);
      } else {
         ColorCtrl var7 = var1.getColorCtrl();
         int var8 = var7.getFgColor();
         var7.setFgColor(COLOUR_HIGHLIGHT);
         var1.fillRect((short)var2, (short)var3, (short)var4, (short)var5);
         var7.setFgColor(var8);
      }

   }

   public void drawPixmapInArea(Graphics var1, int var2, int var3, int var4, int var5, Pixmap var6) {
      int var7 = var1.getClipX();
      int var8 = var1.getClipY();
      int var9 = var1.getClipWidth();
      int var10 = var1.getClipHeight();
      int var11 = Math.max(var2, var7);
      int var12 = Math.max(var3, var8);
      var1.setClip(var11, var12, Math.min(var2 + var4, var7 + var9) - var11, Math.min(var3 + var5, var8 + var10) - var12);
      if (var6.getWidth() < var4) {
         var2 += (var4 - var6.getWidth()) / 2;
      }

      if (var6.getHeight() < var5) {
         var3 += (var5 - var6.getHeight()) / 2;
      }

      var1.drawPixmap(var6, var2, var3);
      var1.setClip(var7, var8, var9, var10);
   }

   public void drawPixmapInZone(Graphics var1, Zone var2, int var3, int var4, Pixmap var5) {
      this.drawPixmapInArea(var1, var3 + var2.x + var2.getMarginLeft(), var4 + var2.y + var2.getMarginTop(), var2.width - var2.getMarginLeft() - var2.getMarginRight(), var2.height - var2.getMarginTop() - var2.getMarginBottom(), var5);
   }

   public void drawEditableTextWithBorder(Graphics var1, Zone var2, int var3, int var4, TextLine var5, int var6, boolean var7) {
      ColorCtrl var8 = var1.getColorCtrl();
      int var9 = var8.getFgColor();
      int var10 = var8.getBgColor();
      Font var11 = var1.getFont();
      Font var12 = var2.getFont();
      int var13 = var3 + var2.x;
      int var14 = var4 + var2.y;
      int var15 = var2.getMarginLeft();
      int var16 = var2.getMarginRight();
      int var17 = var2.getMarginBottom();
      int var18 = var2.getMarginTop();
      int var19 = var13 + var15 + 1;
      int var20 = var14 + var18 + 1;
      int var21 = var2.width - 2 - var15 - var16;
      int var22 = var13;
      if (var5.getTextLineWidth() < var21) {
         var21 = var5.getTextLineWidth();
      }

      switch(var6) {
      case 1:
         var5.setAlignment(1);
         break;
      case 2:
         var19 = var13 + var2.width / 2;
         var5.setAlignment(2);
         break;
      case 3:
         var19 = var13 + var2.width - var16 - 1;
         var22 = var19 - var21 - var15 - 1;
         var5.setAlignment(3);
      }

      var1.setFont(var12);
      if (usesBackgroundsInsteadOfBorders) {
         this.drawBorder(var1, var22, var14, var2.width, var2.height, 99, var7);
      } else {
         this.drawBorder(var1, var22, var14, var2.width, var2.height, var7 ? BORDER_TYPE_2 : BORDER_NONE, var7);
      }

      if (var7) {
         if (var6 == 2) {
            var22 = var19 - var21 / 2 - var15 - 1;
         }

         getUIStyle().drawHighlightBar(var1, var22 + var15, var14 + var18, var21 + 2, var2.height - var18 - var18, false);
         var8.setFgColor(COLOUR_EDITOR_FOCUSED);
         var8.setBgColor(COLOUR_SCHEME_HIGHLIGHT);
      } else {
         var8.setFgColor(COLOUR_EDITOR_UNFOCUSED);
         var8.setBgColor(COLOUR_BACKGROUND);
      }

      var1.drawText(var5, (short)var19, (short)var20, (short)var21);
      var8.setFgColor(var9);
      var8.setBgColor(var10);
      var1.setFont(var11);
   }

   public static int getNumberOfSoftKeys() {
      return num_soft_keys;
   }

   public static boolean isColourUI() {
      return ui_colour;
   }

   public static boolean isFourWayScroll() {
      return four_way_scroll;
   }

   public static boolean isInline() {
      return inLine;
   }

   public static boolean isRotator() {
      return rotator;
   }

   public static boolean usesBackgroundsInsteadOfBorders() {
      return usesBackgroundsInsteadOfBorders;
   }

   public static boolean isCanvasHasBgImage() {
      return canvasHasBgImage;
   }

   public int getEditableTextHeight(TextLine var1) {
      Zone var2 = this.getZone(21);
      return var1.getTextLineHeight() + var2.getMarginBottom() + var2.getMarginTop() + 2;
   }

   public int getEditableTextWidth(Zone var1, TextLine var2, int var3) {
      int var4 = var1.width - var1.getMarginLeft() - var1.getMarginRight() - 2;
      if (var2.getTextLineWidth() < var4) {
         var4 = var2.getTextLineWidth();
      }

      return var4 + 2;
   }

   public int getEditableTextX(Zone var1, TextLine var2, int var3) {
      int var4 = var1.x;
      int var5 = this.getEditableTextWidth(var1, var2, var3);
      int var6;
      switch(var3) {
      case 2:
         var6 = var4 + var1.width / 2 - var5 / 2;
         break;
      case 3:
         var6 = var4 + var1.width - var1.getMarginRight() - var5;
         break;
      default:
         var6 = var4 + var1.getMarginLeft();
      }

      return var6;
   }

   public static void initialiseStyle() {
      isAlignedLeftToRight = true;
      String var0 = System.getProperty("microedition.locale");
      if (var0 != null) {
         if (var0.equals("he-IL")) {
            isAlignedLeftToRight = false;
         } else if (var0.startsWith("ar")) {
            isAlignedLeftToRight = false;
         } else if (var0.startsWith("fa")) {
            isAlignedLeftToRight = false;
         }
      }

      nativeStaticInitialiser(isAlignedLeftToRight);
   }

   static Font getStyleSpecificFont(int var0) {
      return getUIStyle().getFont(var0);
   }

   Font getFont(int var1) {
      Font var2 = null;
      if (fonts == null) {
         fonts = new Font[6];
      }

      if (var1 < 0 || var1 >= fonts.length) {
         var1 = 5;
      }

      synchronized(this) {
         var2 = fonts[var1];
         if (var2 == null) {
            int[] var4 = new int[2];
            this.nativeGetFontParameters(var1, var4);
            var2 = new Font(var4[0], var4[1]);
            fonts[var1] = var2;
         }

         return var2;
      }
   }

   static native boolean assignZoneFields(int var0, Zone var1);

   private static native void nativeStaticInitialiser(boolean var0);

   private native void nativeGetFontParameters(int var1, int[] var2);

   private native void nativeDrawBorder(Graphics var1, int var2, int var3, int var4, int var5, int var6, boolean var7);

   private native void nativeDrawScrollBar(Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, boolean var10);

   private native void nativeDrawGauge(Graphics var1, short var2, short var3, short var4, short var5, short var6, short var7, short var8, short var9, int var10, int var11, boolean var12, boolean var13);

   private native void nativeDisplayIndex(int var1);

   static {
      initialiseStyle();
   }
}
