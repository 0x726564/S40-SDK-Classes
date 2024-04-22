package com.nokia.mid.ui.lcdui;

import com.nokia.mid.impl.isa.ui.DisplayAccess;
import com.nokia.mid.impl.isa.ui.InitJALM;
import com.nokia.mid.impl.isa.ui.MIDletAccess;
import com.nokia.mid.impl.isa.ui.style.UIStyle;
import com.nokia.mid.impl.isa.ui.style.Zone;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;

public final class LCDUIUtils {
   public static final int INDICATOR_ICON = 4;
   private static MIDletAccess ma = InitJALM.s_getMIDletAccessor();

   private LCDUIUtils() {
      throw new IllegalStateException();
   }

   public static int getBestImageHeight(int imageType) {
      if (imageType != 4) {
         throw new IllegalArgumentException();
      } else {
         Zone zone = UIStyle.getUIStyle().getZone(50);
         return zone.height - zone.getMarginTop() - zone.getMarginBottom();
      }
   }

   public static int getBestImageWidth(int imageType) {
      if (imageType != 4) {
         throw new IllegalArgumentException();
      } else {
         Zone zone = UIStyle.getUIStyle().getZone(50);
         int width = (zone.width - zone.getMarginLeft() - zone.getMarginRight()) / UIStyle.getNumberOfDynamicStatusIndicators();
         return width - 2;
      }
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

   public static void setCurrent(Display display, Displayable nextDisplayable, String promptText, Image icon) {
      DisplayAccess da = null;
      if (display == null || nextDisplayable != null && promptText == null && icon == null) {
         throw new NullPointerException();
      } else {
         if (ma != null && (da = ma.getDisplayAccessor()) != null) {
            try {
               da.setCurrent(display, nextDisplayable, false, promptText, icon);
            } catch (ForegroundUnavailableException var6) {
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

   public static void setCurrent(Display display, Alert alert, Displayable nextDisplayable, String promptText, Image icon) {
      DisplayAccess da = null;
      if (display == null || alert == null || nextDisplayable == null || promptText == null && icon == null) {
         throw new NullPointerException();
      } else {
         if (ma != null && (da = ma.getDisplayAccessor()) != null) {
            try {
               da.setCurrent(display, alert, nextDisplayable, false, promptText, icon);
            } catch (ForegroundUnavailableException var7) {
            }
         }

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
