package com.nokia.mid.ui;

import com.nokia.mid.impl.isa.ui.DisplayAccess;
import com.nokia.mid.impl.isa.ui.InitJALM;
import com.nokia.mid.impl.isa.ui.MIDletAccess;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class DirectUtils {
   private DirectUtils() {
   }

   public static DirectGraphics getDirectGraphics(Graphics g) {
      return (DirectGraphics)g;
   }

   public static Image createImage(byte[] imageData, int imageOffset, int imageLength) {
      DisplayAccess da = null;
      Image image = null;
      MIDletAccess ma = InitJALM.s_getMIDletAccessor();
      if (ma != null) {
         da = ma.getDisplayAccessor();
         if (da != null) {
            image = da.createImage(Pixmap.createPixmap(imageData, imageOffset, imageLength, true));
         }
      }

      return image;
   }

   public static Image createImage(int w, int h, int color) {
      DisplayAccess da = null;
      Image image = null;
      MIDletAccess ma = InitJALM.s_getMIDletAccessor();
      if (ma != null) {
         da = ma.getDisplayAccessor();
         if (da != null) {
            image = da.createImage(Pixmap.createPixmap(w, h, true, color));
         }
      }

      return image;
   }

   public static Font getFont(int identifier) {
      DisplayAccess da = null;
      Font font = null;
      MIDletAccess ma = InitJALM.s_getMIDletAccessor();
      if (ma != null) {
         da = ma.getDisplayAccessor();
         if (da != null) {
            font = da.newFont(UIStyle.getUIStyle().getIdleScreenFont(identifier));
         }
      }

      return font;
   }
}
