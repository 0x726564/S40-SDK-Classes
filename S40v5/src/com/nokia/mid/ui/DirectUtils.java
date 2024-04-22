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
      MIDletAccess var3 = null;
      Image var4 = null;
      DisplayAccess var5;
      if ((var3 = InitJALM.s_getMIDletAccessor()) != null && (var5 = var3.getDisplayAccessor()) != null) {
         var4 = var5.createImage(Pixmap.createPixmap(var0, var1, var2, true));
      }

      return var4;
   }

   public static Image createImage(int var0, int var1, int var2) {
      MIDletAccess var3 = null;
      Image var4 = null;
      DisplayAccess var5;
      if ((var3 = InitJALM.s_getMIDletAccessor()) != null && (var5 = var3.getDisplayAccessor()) != null) {
         var4 = var5.createImage(Pixmap.createPixmap(var0, var1, true, var2));
      }

      return var4;
   }
}
