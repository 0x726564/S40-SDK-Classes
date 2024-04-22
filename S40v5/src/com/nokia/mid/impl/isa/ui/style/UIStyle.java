package com.nokia.mid.impl.isa.ui.style;

import com.nokia.mid.impl.isa.ui.ReinitialiseListener;
import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.gdi.Font;
import com.nokia.mid.impl.isa.ui.gdi.Graphics;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.isa.ui.gdi.TextLine;
import com.nokia.mid.impl.isa.util.SharedObjects;
import java.util.Vector;

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
   public static int COLOUR_NOTE_TEXT;
   public static int COLOUR_LABEL_TEXT;
   public static int FORM_MAX_SCROLL;
   public static int DEFAULT_TEXT_LEADING;
   public static int EDITABLE_TEXT_BOTTOM_MARGIN;
   public static int CHOICE_ITEMS_TOP_MARGIN;
   public static int BUTTON_BORDER_WIDTH;
   public static int BUTTON_BORDER_HEIGHT;
   public static int HYPERLINK_CUSTOMITEM_BORDER_WIDTH;
   public static int HYPERLINK_CUSTOMITEM_BORDER_HEIGHT;
   public static int CELL_SPACING;
   public static int LABEL_PAD_BEFORE;
   public static int LABEL_PAD_AFTER;
   public static int CUSTOMITEM_BORDER_PAD;
   public static int DEFAULT_FONT_TYPE;
   public static int BUTTON_FONT_TYPE;
   public static int MAX_BUTTON_WIDTH;
   public static int BORDER_NONE = 0;
   public static int BORDER_ALERT = 2;
   public static int BORDER_LIST_HIGHLIGHT = 4;
   public static int BORDER_IMAGE_HIGHLIGHT = 7;
   public static int BORDER_BUTTON = 10;
   public static int BORDER_ITEM = 12;
   public static final int TICKER_FONT = 0;
   public static final int MAX_SPECIAL_FONT_ID = 0;
   public static final int SMALL_FONT = 1;
   public static final int MEDIUM_FONT = 2;
   public static final int LARGE_FONT = 3;
   public static final int MEDIUM_BOLD_FONT = 4;
   public static final int DEFAULT_FONT = 5;
   public static boolean isAlignedLeftToRight;
   private static Font[] fq = new Font[1];
   private static volatile UIStyle fr;
   private static Vector fs = new Vector();
   private static Zone[] ft;
   private static Object fu = SharedObjects.getLock("com.nokia.mid.impl.isa.ui.style.uistyle");

   private UIStyle() {
      ft = new Zone[55];
   }

   public static UIStyle getUIStyle() {
      if (fr == null) {
         fr = new UIStyle();
      }

      return fr;
   }

   public Zone getZone(int var1) {
      Zone var2 = null;
      if (var1 >= 0 && var1 <= 54) {
         synchronized(this) {
            if ((var2 = ft[var1]) == null) {
               var2 = new Zone();
               if (assignZoneFields(var1, var2)) {
                  ft[var1] = var2;
               } else {
                  var2 = null;
               }
            }
         }
      }

      return var2;
   }

   public Font getDefaultFont() {
      return this.getFont(DEFAULT_FONT_TYPE);
   }

   public Font getButtonFont() {
      return this.getFont(BUTTON_FONT_TYPE);
   }

   public void drawScrollbar(Graphics var1, Zone var2, int var3, int var4, int var5, int var6, boolean var7) {
      this.nativeDrawScrollBar(var1, var2.x, var2.y, var2.width, var2.height, var3, var4, var5, var6, var7);
   }

   public void drawBorder(Graphics var1, int var2, int var3, int var4, int var5, int var6, boolean var7) {
      if (var6 != BORDER_IMAGE_HIGHLIGHT || var7) {
         ColorCtrl var8;
         int var9 = (var8 = var1.getColorCtrl()).getFgColor();
         if (var6 == BORDER_IMAGE_HIGHLIGHT && var7) {
            var8.setFgColor(COLOUR_SCHEME_HIGHLIGHT);
            if (HYPERLINK_CUSTOMITEM_BORDER_HEIGHT > 1 && HYPERLINK_CUSTOMITEM_BORDER_WIDTH > 1) {
               int var10 = var3 + var5 - 1;
               int var11 = var2 + var4 - 1;
               int var12 = var3 + HYPERLINK_CUSTOMITEM_BORDER_HEIGHT - 1;

               int var13;
               for(var13 = 0; var13 < HYPERLINK_CUSTOMITEM_BORDER_HEIGHT - 1; ++var13) {
                  var1.drawLine(var2, var3 + var13, var11, var3 + var13);
                  var1.drawLine(var2, var10 - var13, var11, var10 - var13);
               }

               var10 = var12 + var5 - (HYPERLINK_CUSTOMITEM_BORDER_HEIGHT << 1) + 2 - 1;

               for(var13 = 0; var13 < HYPERLINK_CUSTOMITEM_BORDER_WIDTH - 1; ++var13) {
                  var1.drawLine(var2 + var13, var12, var2 + var13, var10);
                  var1.drawLine(var11 - var13, var12, var11 - var13, var10);
               }
            } else {
               var1.drawRect((short)var2, (short)var3, (short)var4, (short)var5);
            }
         } else {
            if (var6 == BORDER_BUTTON) {
               ;
            }

            this.nativeDrawBorder(var1, var2, var3, var4, var5, var6, var7);
         }

         var8.setFgColor(var9);
      }
   }

   public void drawBorder(Graphics var1, Zone var2) {
      int var3;
      if ((var3 = var2.getBorderType()) != BORDER_NONE) {
         this.drawBorder(var1, var2.x, var2.y, var2.width, var2.height, var3, false);
      }

   }

   public void setIndex(int var1) {
      this.nativeDisplayIndex(var1);
   }

   public void hideIndex() {
      this.nativeDisplayIndex(-1);
   }

   public void drawGauge(Graphics var1, Zone var2, int var3, int var4, int var5, int var6, boolean var7, boolean var8) {
      int var9 = var2.getMarginLeft();
      int var10 = var2.getMarginRight();
      int var11 = var2.getMarginTop();
      int var12 = var2.getMarginBottom();
      this.nativeDrawGauge(var1, (short)(var3 + var2.x + var9), (short)(var4 + var2.y + var11 + 1), (short)(var2.width - var9 - var10 - 2), (short)(var2.height - var11 - var12 - 2), var5, var6, var7, var8);
   }

   public void drawHighlightBar(Graphics var1, int var2, int var3, int var4, int var5, boolean var6) {
      if (var6) {
         this.drawBorder(var1, var2, var3, var4, var5, BORDER_LIST_HIGHLIGHT, true);
      } else {
         ColorCtrl var7;
         int var8 = (var7 = var1.getColorCtrl()).getFgColor();
         var7.setFgColor(COLOUR_HIGHLIGHT);
         var1.fillRect((short)var2, (short)var3, (short)var4, (short)var5);
         var7.setFgColor(var8);
      }
   }

   public void drawPixmapInArea(Graphics var1, int var2, int var3, int var4, int var5, Pixmap var6) {
      int var12 = var1.getClipX();
      int var7 = var1.getClipY();
      int var8 = var1.getClipWidth();
      int var9 = var1.getClipHeight();
      int var10 = Math.max(var2, var12);
      int var11 = Math.max(var3, var7);
      var1.setClip(var10, var11, Math.min(var2 + var4, var12 + var8) - var10, Math.min(var3 + var5, var7 + var9) - var11);
      if (var6.getWidth() < var4) {
         var2 += (var4 - var6.getWidth()) / 2;
      }

      if (var6.getHeight() < var5) {
         var3 += (var5 - var6.getHeight()) / 2;
      }

      var1.drawPixmap(var6, var2, var3);
      var1.setClip(var12, var7, var8, var9);
   }

   public void drawPixmapInZone(Graphics var1, Zone var2, int var3, int var4, Pixmap var5) {
      this.drawPixmapInArea(var1, var3 + var2.x + var2.getMarginLeft(), var4 + var2.y + var2.getMarginTop(), var2.width - var2.getMarginLeft() - var2.getMarginRight(), var2.height - var2.getMarginTop() - var2.getMarginBottom(), var5);
   }

   public void drawEditableTextWithBorder(Graphics var1, Zone var2, int var3, int var4, TextLine var5, int var6, int var7, boolean var8) {
      ColorCtrl var9;
      int var10 = (var9 = var1.getColorCtrl()).getFgColor();
      int var11 = var9.getBgColor();
      Font var12 = var1.getFont();
      Font var13 = var2.getFont();
      var3 += var2.x;
      var4 += var2.y;
      int var14 = var2.getMarginLeft();
      int var15 = var2.getMarginRight();
      int var16 = var2.getMarginTop();
      int var17 = var3 + var14 + 1;
      int var18 = var4 + var16 + 1;
      int var19 = var2.width - 2 - var14 - var15;
      int var20 = var3;
      if (var5.getTextLineWidth() < var19) {
         var19 = var5.getTextLineWidth();
      }

      switch(var7) {
      case 1:
         var5.setAlignment(1);
         break;
      case 2:
         var17 = var3 + var2.width / 2;
         var5.setAlignment(2);
         break;
      case 3:
         var20 = (var17 = var3 + var2.width - var15 - 1) - var19 - var14 - 1;
         var5.setAlignment(3);
      }

      var1.setFont(var13);
      this.drawBorder(var1, var20, var4, var2.width, var2.height, var6, var8);
      if (var8) {
         if (var7 == 2) {
            var20 = var17 - var19 / 2 - var14 - 1;
         }

         getUIStyle().drawHighlightBar(var1, var20 + var14, var4 + var16, var19 + 2, var2.height - var16 - var16, false);
         var9.setFgColor(COLOUR_EDITOR_FOCUSED);
         var9.setBgColor(COLOUR_SCHEME_HIGHLIGHT);
      } else {
         var9.setFgColor(COLOUR_EDITOR_UNFOCUSED);
         var9.setBgColor(COLOUR_BACKGROUND);
      }

      var1.drawText(var5, (short)var17, (short)var18, (short)var19);
      var9.setFgColor(var10);
      var9.setBgColor(var11);
      var1.setFont(var12);
   }

   public static int getNumberOfSoftKeys() {
      return 0;
   }

   public static boolean isColourUI() {
      return false;
   }

   public static boolean isFourWayScroll() {
      return false;
   }

   public static boolean isInline() {
      return false;
   }

   public static boolean isRotator() {
      return false;
   }

   public static boolean isCanvasHasBgImage() {
      return false;
   }

   public static boolean isListEndToneOn() {
      return false;
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

   public static void registerReinitialiseListener(ReinitialiseListener var0) {
      synchronized(fs) {
         if (!fs.contains(var0)) {
            fs.addElement(var0);
         }

      }
   }

   public static void unregisterReinitialiseListener(ReinitialiseListener var0) {
      fs.removeElement(var0);
   }

   public static void reinitialiseAllForForeground() {
      i(false);
      synchronized(fs) {
         int var1 = fs.size();

         for(int var2 = 0; var2 < var1; ++var2) {
            ((ReinitialiseListener)fs.elementAt(var2)).reinitialiseForForeground();
         }

      }
   }

   static Font getStyleSpecificFont(int var0) {
      return getUIStyle().getFont(var0);
   }

   Font getFont(int var1) {
      Font var2 = null;
      synchronized(this) {
         switch(var1) {
         case 1:
            var2 = new Font(8, 0, false);
            break;
         case 2:
            var2 = new Font(0, 0, false);
            break;
         case 3:
            var2 = new Font(16, 0, false);
            break;
         case 4:
            var2 = new Font(0, 1, false);
            break;
         default:
            if (var1 >= 0 && var1 <= 0) {
               if ((var2 = fq[var1]) == null) {
                  int[] var4 = new int[2];
                  if (this.nativeGetFontParameters(var1, var4)) {
                     var2 = new Font(var4[0], var4[1]);
                     fq[var1] = var2;
                  } else {
                     var2 = this.getDefaultFont();
                  }
               }
            } else {
               var2 = this.getDefaultFont();
            }
         }

         return var2;
      }
   }

   private static void i(boolean var0) {
      isAlignedLeftToRight = true;
      String var1;
      if ((var1 = System.getProperty("microedition.locale")) != null && (var1.equals("he-IL") || var1.startsWith("ar") || var1.startsWith("fa"))) {
         isAlignedLeftToRight = false;
      }

      synchronized(fu) {
         nativeInitialiser(var0, isAlignedLeftToRight);
      }

      if (DEFAULT_FONT_TYPE <= 0 || DEFAULT_FONT_TYPE >= 5) {
         throw new Error("Invalid default Font type");
      }
   }

   static native boolean assignZoneFields(int var0, Zone var1);

   private static native void nativeInitialiser(boolean var0, boolean var1);

   private native boolean nativeGetFontParameters(int var1, int[] var2);

   private native void nativeDrawBorder(Graphics var1, int var2, int var3, int var4, int var5, int var6, boolean var7);

   private native void nativeDrawScrollBar(Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, boolean var10);

   private native void nativeDrawGauge(Graphics var1, short var2, short var3, short var4, short var5, int var6, int var7, boolean var8, boolean var9);

   private native void nativeDisplayIndex(int var1);

   static {
      i(true);
   }
}
