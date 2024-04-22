package com.nokia.mid.impl.isa.ui.style;

import com.nokia.mid.impl.isa.ui.gdi.Font;

public final class Zone {
   public int x = 0;
   public int y = 0;
   public int width = 0;
   public int height = 0;
   int font = 0;
   int flags = 0;
   public static final int TRANSLATED_MASK = -268435456;
   public static final int TRANSLATED_FOR_SCREEN = 0;
   public static final int TRANSLATED_FOR_CANVAS = 268435456;
   public static final int TRANSLATED_FOR_TICKER = 536870912;
   public static final int NOT_TRANSLATED_BIT = Integer.MIN_VALUE;
   public static final int IS_HCENTERED_BIT = 16777216;
   public static final int FONT_SHIFT = 20;
   public static final int FONT_MASK = 15728640;
   public static final int BORDER_SHIFT = 16;
   public static final int BORDER_MASK = 983040;
   public static final int MARGIN_TOP_SHIFT = 12;
   public static final int MARGIN_TOP_MASK = 61440;
   public static final int MARGIN_BOTTOM_SHIFT = 8;
   public static final int MARGIN_BOTTOM_MASK = 3840;
   public static final int MARGIN_LEFT_SHIFT = 4;
   public static final int MARGIN_LEFT_MASK = 240;
   public static final int MARGIN_RIGHT_SHIFT = 0;
   public static final int MARGIN_RIGHT_MASK = 15;

   public boolean isHorizontallyCentered() {
      return (this.flags & 16777216) != 0;
   }

   public Font getFont() {
      return UIStyle.getStyleSpecificFont((this.flags & 15728640) >> 20);
   }

   public int getBorderType() {
      return (this.flags & 983040) >> 16;
   }

   public int getMarginTop() {
      return (this.flags & '\uf000') >> 12;
   }

   public int getMarginBottom() {
      return (this.flags & 3840) >> 8;
   }

   public int getMarginLeft() {
      return (this.flags & 240) >> 4;
   }

   public int getMarginRight() {
      return (this.flags & 15) >> 0;
   }
}
