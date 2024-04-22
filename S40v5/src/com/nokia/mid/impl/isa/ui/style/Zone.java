package com.nokia.mid.impl.isa.ui.style;

import com.nokia.mid.impl.isa.ui.gdi.Font;

public final class Zone {
   public int x = 0;
   public int y = 0;
   public int width = 0;
   public int height = 0;
   private int flags = 0;
   public static final int TRANSLATED_MASK = -268435456;
   public static final int TRANSLATED_FOR_SCREEN = 0;
   public static final int TRANSLATED_FOR_CANVAS = 268435456;
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

   public final boolean isHorizontallyCentered() {
      return false;
   }

   public final Font getFont() {
      return UIStyle.getStyleSpecificFont(0);
   }

   public final int getBorderType() {
      return 0;
   }

   public final int getMarginTop() {
      return 0;
   }

   public final int getMarginBottom() {
      return 0;
   }

   public final int getMarginLeft() {
      return 0;
   }

   public final int getMarginRight() {
      return 0;
   }
}
