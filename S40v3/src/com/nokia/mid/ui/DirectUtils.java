package com.nokia.mid.ui;

import com.nokia.mid.impl.isa.ui.DisplayAccess;
import com.nokia.mid.impl.isa.ui.InitJALM;
import com.nokia.mid.impl.isa.ui.MIDletAccess;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class DirectUtils {
   private DirectUtils() {
   }

   public static DirectGraphics getDirectGraphics(Graphics var0) {
      return (DirectGraphics)var0;
   }

   public static Image createImage(byte[] var0, int var1, int var2) {
      DisplayAccess var3 = null;
      Image var4 = null;
      MIDletAccess var5 = InitJALM.s_getMIDletAccessor();
      if (var5 != null) {
         var3 = var5.getDisplayAccessor();
         if (var3 != null) {
            var4 = var3.createImage(Pixmap.createPixmap(var0, var1, var2, true));
         }
      }

      return var4;
   }

   public static Image createImage(int var0, int var1, int var2) {
      DisplayAccess var3 = null;
      Image var4 = null;
      MIDletAccess var5 = InitJALM.s_getMIDletAccessor();
      if (var5 != null) {
         var3 = var5.getDisplayAccessor();
         if (var3 != null) {
            var4 = var3.createImage(Pixmap.createPixmap(var0, var1, true, var2));
         }
      }

      return var4;
   }
}
