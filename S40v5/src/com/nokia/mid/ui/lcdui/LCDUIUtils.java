package com.nokia.mid.ui.lcdui;

import com.nokia.mid.impl.isa.ui.DisplayAccess;
import com.nokia.mid.impl.isa.ui.InitJALM;
import com.nokia.mid.impl.isa.ui.MIDletAccess;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

public final class LCDUIUtils {
   private static MIDletAccess a = InitJALM.s_getMIDletAccessor();

   private LCDUIUtils() {
      throw new IllegalStateException();
   }

   public static void setDisplayStateListener(Display var0, DisplayStateListener var1) {
      DisplayAccess var2 = null;
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         if (a != null && (var2 = a.getDisplayAccessor()) != null) {
            var2.setDisplayStateListener(var0, var1);
         }

      }
   }

   public static void setVisibilityListener(Displayable var0, VisibilityListener var1) {
      DisplayAccess var2 = null;
      if (a != null && (var2 = a.getDisplayAccessor()) != null) {
         var2.setVisibilityListener(var0, var1);
      }

   }

   public static boolean isDisplayActive(Display var0) {
      boolean var1 = false;
      DisplayAccess var2 = null;
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         if (a != null && (var2 = a.getDisplayAccessor()) != null) {
            var1 = var2.isDisplayActive(var0);
         }

         return var1;
      }
   }

   public static void setCurrent(Display var0, Displayable var1, String var2) {
      DisplayAccess var3 = null;
      if (var0 != null && (var1 == null || var2 != null)) {
         if (a != null && (var3 = a.getDisplayAccessor()) != null) {
            var3.setCurrent(var0, var1, var2);
         }

      } else {
         throw new NullPointerException();
      }
   }

   public static void setCurrent(Display var0, Alert var1, Displayable var2, String var3) {
      DisplayAccess var4 = null;
      if (var0 != null && var3 != null && var0 != null && var3 != null) {
         if (a != null && (var4 = a.getDisplayAccessor()) != null) {
            var4.setCurrent(var0, var1, var2, var3);
         }

      } else {
         throw new NullPointerException();
      }
   }
}
