package com.nokia.mid.ui;

import com.nokia.mid.impl.isa.ui.style.UIStyle;
import javax.microedition.lcdui.Font;

public final class IdleStyle {
   public static final int IDLE_SCREEN_BACKGROUND = 0;
   public static final int IDLE_SCREEN_FOREGROUND = 1;
   public static final int IDLE_SCREEN_HIGHLIGHT_BACKGROUND = 2;
   public static final int IDLE_SCREEN_HIGHLIGHT_FOREGROUND = 3;
   public static final int IDLE_SCREEN_FONT = 6;
   public static final int IDLE_SCREEN_FOCUSED_FONT = 7;

   private IdleStyle() {
   }

   public static int getIdleScreenColour(int identifier) {
      int requestedColour = UIStyle.COLOUR_BLACK;
      switch(identifier) {
      case 0:
         requestedColour = UIStyle.COLOUR_IDLE_SCREEN_BACKGROUND;
         break;
      case 1:
         requestedColour = UIStyle.COLOUR_IDLE_SCREEN_FOREGROUND;
         break;
      case 2:
         requestedColour = UIStyle.COLOUR_IDLE_SCREEN_HIGHLIGHT_BACKGROUND;
         break;
      case 3:
         requestedColour = UIStyle.COLOUR_IDLE_SCREEN_HIGHLIGHT_FOREGROUND;
         break;
      default:
         throw new IllegalArgumentException();
      }

      return requestedColour;
   }

   public static Font getIdleScreenFont(int identifier) {
      if (identifier != 6 && identifier != 7) {
         throw new IllegalArgumentException();
      } else {
         return DirectUtils.getFont(identifier);
      }
   }
}
