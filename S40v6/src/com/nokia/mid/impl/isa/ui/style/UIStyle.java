package com.nokia.mid.impl.isa.ui.style;

import com.nokia.mid.impl.isa.ui.ReinitialiseListener;
import com.nokia.mid.impl.isa.ui.gdi.ColorCtrl;
import com.nokia.mid.impl.isa.ui.gdi.Font;
import com.nokia.mid.impl.isa.ui.gdi.Graphics;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.isa.ui.gdi.TextLine;
import com.nokia.mid.impl.isa.util.SharedObjects;
import java.util.Vector;
import javax.microedition.lcdui.Displayable;

public class UIStyle {
   public static int COLOUR_WHITE;
   public static int COLOUR_BLACK;
   public static int COLOUR_TEXT;
   public static int COLOUR_BACKGROUND;
   public static int COLOUR_HIGHLIGHT;
   public static int COLOUR_HIGHLIGHT_TEXT;
   public static int COLOUR_GRAY;
   public static int COLOUR_SCHEME_HIGHLIGHT;
   public static int COLOUR_EDITOR_FOCUSED;
   public static int COLOUR_EDITOR_UNFOCUSED;
   public static int COLOUR_NOTE_TEXT;
   public static int COLOUR_LABEL_TEXT;
   public static int COLOUR_IDLE_SCREEN_BACKGROUND;
   public static int COLOUR_IDLE_SCREEN_FOREGROUND;
   public static int COLOUR_IDLE_SCREEN_HIGHLIGHT_BACKGROUND;
   public static int COLOUR_IDLE_SCREEN_HIGHLIGHT_FOREGROUND;
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
   public static final int SMALL_FONT = 1;
   public static final int MEDIUM_FONT = 2;
   public static final int LARGE_FONT = 3;
   public static final int MEDIUM_BOLD_FONT = 4;
   public static final int DEFAULT_FONT = 5;
   public static final int IDLE_SCREEN_FONT = 6;
   public static final int IDLE_SCREEN_FOCUSED_FONT = 7;
   public static final int MAX_SPECIAL_FONT_ID = 7;
   static final int EDITABLE_TEXT_X_PAD = 1;
   static final int EDITABLE_TEXT_Y_PAD = 1;
   private static int num_dynamic_status_indicators;
   private static int num_dynamic_power_saver_status_indicators;
   public static boolean isAlignedLeftToRight;
   private static int num_soft_keys;
   private static boolean ui_colour;
   private static boolean four_way_scroll;
   private static boolean inLine;
   private static Font[] fonts = new Font[2];
   private static volatile UIStyle instance;
   private static boolean canvasHasBgImage;
   private static boolean listEndToneOn;
   private static Vector reinitListenerList = new Vector();
   static Zone[] zones;
   static Object sharedUIStyleLock = SharedObjects.getLock("com.nokia.mid.impl.isa.ui.style.uistyle");
   private static Displayable curDisp;

   private UIStyle() {
      zones = new Zone[51];
   }

   public static UIStyle getUIStyle() {
      if (instance == null) {
         instance = new UIStyle();
      }

      return instance;
   }

   public Zone getZone(int javaZoneID) {
      Zone zone = null;
      if (javaZoneID >= 0 && javaZoneID <= 50) {
         synchronized(this) {
            zone = zones[javaZoneID];
            if (zone == null) {
               zone = new Zone();
               if (assignZoneFields(javaZoneID, zone)) {
                  zones[javaZoneID] = zone;
               } else {
                  zone = null;
               }
            }
         }
      }

      return zone;
   }

   public Font getDefaultFont() {
      return this.getFont(DEFAULT_FONT_TYPE);
   }

   public Font getButtonFont() {
      return this.getFont(BUTTON_FONT_TYPE);
   }

   public Font getIdleScreenFont(int identifier) {
      return this.getFont(identifier);
   }

   public void drawScrollbar(Graphics graphics, Zone zone, int minimum, int maximum, int thumb_height, int value, boolean rangeIncluesThumb) {
      this.nativeDrawScrollBar(graphics, zone.x, zone.y, zone.width, zone.height, minimum, maximum, thumb_height, value, rangeIncluesThumb);
   }

   public void drawBorder(Graphics graphics, int x, int y, int w, int h, int borderType, boolean isFocused) {
      if (borderType != BORDER_IMAGE_HIGHLIGHT || isFocused) {
         ColorCtrl colorControl = graphics.getColorCtrl();
         int fg_colour = colorControl.getFgColor();
         if (borderType == BORDER_IMAGE_HIGHLIGHT && isFocused) {
            colorControl.setFgColor(COLOUR_SCHEME_HIGHLIGHT);
            if (HYPERLINK_CUSTOMITEM_BORDER_HEIGHT > 1 && HYPERLINK_CUSTOMITEM_BORDER_WIDTH > 1) {
               int yEndOffset = y + h - 1;
               int xEndOffset = x + w - 1;
               int yStartOffset = y + HYPERLINK_CUSTOMITEM_BORDER_HEIGHT - 1;

               int i;
               for(i = 0; i < HYPERLINK_CUSTOMITEM_BORDER_HEIGHT - 1; ++i) {
                  graphics.drawLine(x, y + i, xEndOffset, y + i);
                  graphics.drawLine(x, yEndOffset - i, xEndOffset, yEndOffset - i);
               }

               yEndOffset = yStartOffset + h - HYPERLINK_CUSTOMITEM_BORDER_HEIGHT * 2 + 2 - 1;

               for(i = 0; i < HYPERLINK_CUSTOMITEM_BORDER_WIDTH - 1; ++i) {
                  graphics.drawLine(x + i, yStartOffset, x + i, yEndOffset);
                  graphics.drawLine(xEndOffset - i, yStartOffset, xEndOffset - i, yEndOffset);
               }
            } else {
               graphics.drawRect((short)x, (short)y, (short)w, (short)h);
            }
         } else if (borderType == BORDER_BUTTON && isFocused) {
            this.nativeDrawBorder(graphics, x, y, w, h, borderType, isFocused);
         } else {
            this.nativeDrawBorder(graphics, x, y, w, h, borderType, isFocused);
         }

         colorControl.setFgColor(fg_colour);
      }
   }

   public void drawBorder(Graphics graphics, Zone zone) {
      int borderType = zone.getBorderType();
      if (borderType != BORDER_NONE) {
         this.drawBorder(graphics, zone.x, zone.y, zone.width, zone.height, borderType, false);
      }

   }

   public void setIndex(Displayable displayable, int index) {
      curDisp = displayable;
      this.nativeDisplayIndex(index);
   }

   public void hideIndex(Displayable displayable) {
      if (curDisp == null || curDisp == displayable) {
         this.nativeDisplayIndex(-1);
         curDisp = null;
      }

   }

   public void drawGauge(Graphics graphics, Zone zone, int translateX, int translateY, int currentValue, int maxValue, boolean hasFocus, boolean aForm) {
      int ml = zone.getMarginLeft();
      int mr = zone.getMarginRight();
      int mt = zone.getMarginTop();
      int mb = zone.getMarginBottom();
      this.nativeDrawGauge(graphics, (short)(translateX + zone.x + ml), (short)(translateY + zone.y + mt + 1), (short)(zone.width - ml - mr - 2), (short)(zone.height - mt - mb - 2), currentValue, maxValue, hasFocus, aForm);
   }

   public void drawHighlightBar(Graphics graphics, int x, int y, int w, int h, boolean useNative) {
      if (useNative) {
         this.drawBorder(graphics, x, y, w, h, BORDER_LIST_HIGHLIGHT, true);
      } else {
         ColorCtrl colorCtrl = graphics.getColorCtrl();
         int oldFgColor = colorCtrl.getFgColor();
         colorCtrl.setFgColor(COLOUR_HIGHLIGHT);
         graphics.fillRect((short)x, (short)y, (short)w, (short)h);
         colorCtrl.setFgColor(oldFgColor);
      }

   }

   public void drawPixmapInArea(Graphics graphics, int x, int y, int w, int h, Pixmap pixmap) {
      int clx = graphics.getClipX();
      int cly = graphics.getClipY();
      int clw = graphics.getClipWidth();
      int clh = graphics.getClipHeight();
      int newClipX = Math.max(x, clx);
      int newClipY = Math.max(y, cly);
      graphics.setClip(newClipX, newClipY, Math.min(x + w, clx + clw) - newClipX, Math.min(y + h, cly + clh) - newClipY);
      if (pixmap.getWidth() < w) {
         x += (w - pixmap.getWidth()) / 2;
      }

      if (pixmap.getHeight() < h) {
         y += (h - pixmap.getHeight()) / 2;
      }

      graphics.drawPixmap(pixmap, x, y);
      graphics.setClip(clx, cly, clw, clh);
   }

   public void drawPixmapInZone(Graphics graphics, Zone zone, int translateX, int translateY, Pixmap pixmap) {
      this.drawPixmapInArea(graphics, translateX + zone.x + zone.getMarginLeft(), translateY + zone.y + zone.getMarginTop(), zone.width - zone.getMarginLeft() - zone.getMarginRight(), zone.height - zone.getMarginTop() - zone.getMarginBottom(), pixmap);
   }

   public void drawEditableTextWithBorder(Graphics graphics, Zone zone, int translateX, int translateY, TextLine text, int borderType, int alignment, boolean hasFocus) {
      ColorCtrl color_ctrl = graphics.getColorCtrl();
      int orig_fg_colour = color_ctrl.getFgColor();
      int orig_bg_colour = color_ctrl.getBgColor();
      Font orig_font = graphics.getFont();
      Font font = zone.getFont();
      int offsetX = translateX + zone.x;
      int offsetY = translateY + zone.y;
      int zoneMarginLeft = zone.getMarginLeft();
      int zoneMarginRight = zone.getMarginRight();
      int zoneMarginTop = zone.getMarginTop();
      int textOffsetX = offsetX + zoneMarginLeft + 1;
      int textOffsetY = offsetY + zoneMarginTop + 1;
      int textWidth = zone.width - 2 - zoneMarginLeft - zoneMarginRight;
      int borderX = offsetX;
      if (text.getTextLineWidth() < textWidth) {
         textWidth = text.getTextLineWidth();
      }

      switch(alignment) {
      case 1:
         text.setAlignment(1);
         break;
      case 2:
         textOffsetX = offsetX + zone.width / 2;
         text.setAlignment(2);
         break;
      case 3:
         textOffsetX = offsetX + zone.width - zoneMarginRight - 1;
         borderX = textOffsetX - textWidth - zoneMarginLeft - 1;
         text.setAlignment(3);
      }

      graphics.setFont(font);
      this.drawBorder(graphics, borderX, offsetY, zone.width, zone.height, borderType, hasFocus);
      if (hasFocus) {
         if (alignment == 2) {
            borderX = textOffsetX - textWidth / 2 - zoneMarginLeft - 1;
         }

         getUIStyle().drawHighlightBar(graphics, borderX + zoneMarginLeft, offsetY + zoneMarginTop, textWidth + 2, zone.height - zoneMarginTop - zoneMarginTop, false);
         color_ctrl.setFgColor(COLOUR_EDITOR_FOCUSED);
         color_ctrl.setBgColor(COLOUR_SCHEME_HIGHLIGHT);
      } else {
         color_ctrl.setFgColor(COLOUR_EDITOR_UNFOCUSED);
         color_ctrl.setBgColor(COLOUR_BACKGROUND);
      }

      graphics.drawText(text, (short)textOffsetX, (short)textOffsetY, (short)textWidth, true);
      color_ctrl.setFgColor(orig_fg_colour);
      color_ctrl.setBgColor(orig_bg_colour);
      graphics.setFont(orig_font);
   }

   public static int getNumberOfSoftKeys() {
      return num_soft_keys;
   }

   public static int getNumberOfDynamicStatusIndicators() {
      return num_dynamic_status_indicators;
   }

   public static int getNumberOfDynamicPowSaverStatusIndicators() {
      return num_dynamic_power_saver_status_indicators;
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

   public static boolean isCanvasHasBgImage() {
      return canvasHasBgImage;
   }

   public static boolean isListEndToneOn() {
      return listEndToneOn;
   }

   public int getEditableTextHeight(TextLine text) {
      Zone itemZone = this.getZone(20);
      return text.getTextLineHeight() + itemZone.getMarginBottom() + itemZone.getMarginTop() + 2;
   }

   public int getEditableTextWidth(Zone zone, TextLine text, int alignment) {
      int textWidth = zone.width - zone.getMarginLeft() - zone.getMarginRight() - 2;
      if (text.getTextLineWidth() < textWidth) {
         textWidth = text.getTextLineWidth();
      }

      return textWidth + 2;
   }

   public int getEditableTextX(Zone zone, TextLine text, int alignment) {
      int offsetX = zone.x;
      int textBoxWidth = this.getEditableTextWidth(zone, text, alignment);
      int textBoxLeft;
      switch(alignment) {
      case 2:
         textBoxLeft = offsetX + zone.width / 2 - textBoxWidth / 2;
         break;
      case 3:
         textBoxLeft = offsetX + zone.width - zone.getMarginRight() - textBoxWidth;
         break;
      default:
         textBoxLeft = offsetX + zone.getMarginLeft();
      }

      return textBoxLeft;
   }

   public static void registerReinitialiseListener(ReinitialiseListener rl) {
      synchronized(reinitListenerList) {
         if (!reinitListenerList.contains(rl)) {
            reinitListenerList.addElement(rl);
         }

      }
   }

   public static void unregisterReinitialiseListener(ReinitialiseListener rl) {
      reinitListenerList.removeElement(rl);
   }

   public static void reinitialiseAllForForeground() {
      initialiseStyle(false);
      synchronized(reinitListenerList) {
         int nbrOfElementsInList = reinitListenerList.size();

         for(int i = 0; i < nbrOfElementsInList; ++i) {
            ((ReinitialiseListener)reinitListenerList.elementAt(i)).reinitialiseForForeground();
         }

      }
   }

   static Font getStyleSpecificFont(int index) {
      return getUIStyle().getFont(index);
   }

   Font getFont(int index) {
      Font font = null;
      synchronized(this) {
         switch(index) {
         case 1:
            font = new Font(8, 0, false);
            break;
         case 2:
            font = new Font(0, 0, false);
            break;
         case 3:
            font = new Font(16, 0, false);
            break;
         case 4:
            font = new Font(0, 1, false);
            break;
         default:
            if (index > 5 && index <= 7) {
               int arrayindex = index - 5 - 1;
               font = fonts[arrayindex];
               if (font == null) {
                  int[] f_params = new int[2];
                  if (this.nativeGetFontParameters(arrayindex, f_params)) {
                     font = new Font(f_params[0], f_params[1]);
                     fonts[arrayindex] = font;
                  } else {
                     font = this.getDefaultFont();
                  }
               }
            } else {
               font = this.getDefaultFont();
            }
         }

         return font;
      }
   }

   private static void initialiseStyle(boolean isFirstClassLoad) {
      synchronized(sharedUIStyleLock) {
         nativeInitialiser(isFirstClassLoad);
      }

      if (DEFAULT_FONT_TYPE >= 5) {
         throw new Error("Invalid default Font type");
      }
   }

   static native boolean assignZoneFields(int var0, Zone var1);

   private static native void nativeInitialiser(boolean var0);

   private native boolean nativeGetFontParameters(int var1, int[] var2);

   private native void nativeDrawBorder(Graphics var1, int var2, int var3, int var4, int var5, int var6, boolean var7);

   private native void nativeDrawScrollBar(Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, boolean var10);

   private native void nativeDrawGauge(Graphics var1, short var2, short var3, short var4, short var5, int var6, int var7, boolean var8, boolean var9);

   private native void nativeDisplayIndex(int var1);

   static {
      initialiseStyle(true);
   }
}
