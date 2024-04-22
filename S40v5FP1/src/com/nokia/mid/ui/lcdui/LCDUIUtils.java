package com.nokia.mid.ui.lcdui;

import com.nokia.mid.impl.isa.ui.DisplayAccess;
import com.nokia.mid.impl.isa.ui.InitJALM;
import com.nokia.mid.impl.isa.ui.MIDletAccess;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Item;

public final class LCDUIUtils {
   private static MIDletAccess ma = InitJALM.s_getMIDletAccessor();

   private LCDUIUtils() {
      throw new IllegalStateException();
   }

   public static void setDisplayStateListener(Display display, DisplayStateListener listener) {
      DisplayAccess da = null;
      if (display == null) {
         throw new NullPointerException();
      } else {
         if (ma != null && (da = ma.getDisplayAccessor()) != null) {
            da.setDisplayStateListener(display, listener);
         }

      }
   }

   public static void setVisibilityListener(Displayable displayable, VisibilityListener listener) {
      DisplayAccess da = null;
      if (ma != null && (da = ma.getDisplayAccessor()) != null) {
         da.setVisibilityListener(displayable, listener);
      }

   }

   public static boolean isDisplayActive(Display display) {
      boolean displayActive = false;
      DisplayAccess da = null;
      if (display == null) {
         throw new NullPointerException();
      } else {
         if (ma != null && (da = ma.getDisplayAccessor()) != null) {
            displayActive = da.isDisplayActive(display);
         }

         return displayActive;
      }
   }

   public static void setCurrent(Display display, Displayable nextDisplayable, String promptText) {
      DisplayAccess da = null;
      if (display == null || nextDisplayable != null && promptText == null) {
         throw new NullPointerException();
      } else {
         if (ma != null && (da = ma.getDisplayAccessor()) != null) {
            try {
               da.setCurrent(display, nextDisplayable, false, promptText);
            } catch (ForegroundUnavailableException var5) {
            }
         }

      }
   }

   public static void setCurrent(Display display, Alert alert, Displayable nextDisplayable, String promptText) {
      DisplayAccess da = null;
      if (display != null && alert != null && nextDisplayable != null && promptText != null) {
         if (ma != null && (da = ma.getDisplayAccessor()) != null) {
            try {
               da.setCurrent(display, alert, nextDisplayable, false, promptText);
            } catch (ForegroundUnavailableException var6) {
            }
         }

      } else {
         throw new NullPointerException();
      }
   }

   public static void setCurrentItem(Display display, Item item, String promptText) {
      DisplayAccess da = null;
      if (display != null && item != null && promptText != null) {
         if (ma != null && (da = ma.getDisplayAccessor()) != null) {
            try {
               da.setCurrentItem(display, item, false, promptText);
            } catch (ForegroundUnavailableException var5) {
            }
         }

      } else {
         throw new NullPointerException();
      }
   }

   public static void setCurrentNoWaitForForeground(Display display, Displayable nextDisplayable) throws ForegroundUnavailableException {
      DisplayAccess da = null;
      if (display == null) {
         throw new NullPointerException();
      } else {
         if (ma != null && (da = ma.getDisplayAccessor()) != null) {
            try {
               da.setCurrent(display, nextDisplayable, true, (String)null);
            } catch (ForegroundUnavailableException var4) {
               throw new ForegroundUnavailableException();
            }
         }

      }
   }

   public static void setCurrentNoWaitForForeground(Display display, Alert alert, Displayable nextDisplayable) throws ForegroundUnavailableException {
      DisplayAccess da = null;
      if (display != null && alert != null && nextDisplayable != null) {
         if (ma != null && (da = ma.getDisplayAccessor()) != null) {
            try {
               da.setCurrent(display, alert, nextDisplayable, true, (String)null);
            } catch (ForegroundUnavailableException var5) {
               throw new ForegroundUnavailableException();
            }
         }

      } else {
         throw new NullPointerException();
      }
   }

   public static void setCurrentItemNoWaitForForeground(Display display, Item item) throws ForegroundUnavailableException {
      DisplayAccess da = null;
      if (display != null && item != null) {
         if (ma != null && (da = ma.getDisplayAccessor()) != null) {
            try {
               da.setCurrentItem(display, item, true, (String)null);
            } catch (ForegroundUnavailableException var4) {
               throw new ForegroundUnavailableException();
            }
         }

      } else {
         throw new NullPointerException();
      }
   }
}
