package com.nokia.mid.s40;

import com.nokia.mid.impl.isa.util.SharedObjects;
import com.nokia.mid.ui.lcdui.LCDUIUtils;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

public final class EmailClientUtils {
   public static final int IDLE_PROMPT_ICON_NONE = 0;
   public static final int IDLE_PROMPT_ICON_YAHOO = 1;
   public static final int IDLE_PROMPT_ICON_AOL = 2;
   public static final int IDLE_PROMPT_ICON_ICQ = 3;
   public static final int IDLE_PROMPT_ICON_WLIVE = 4;
   public static final int IDLE_PROMPT_EMAIL_ICON_GENERIC = 5;
   public static final int IDLE_PROMPT_EMAIL_ICON_AOL = 6;
   public static final int IDLE_PROMPT_EMAIL_ICON_YAHOO = 7;
   public static final int IDLE_PROMPT_EMAIL_ICON_WLIVE = 8;
   public static final int STATUS_ICON_YAHOO_IM_ONLINE = 101;
   public static final int STATUS_ICON_YAHOO_IM_OFFLINE = 102;
   public static final int STATUS_ICON_YAHOO_IM_BUSY = 103;
   public static final int STATUS_ICON_YAHOO_IM_NEW_MESSAGE = 104;
   public static final int STATUS_ICON_YAHOO_EMAIL_NEW_MESSAGE = 105;
   public static final int STATUS_ICON_AOL_IM_ONLINE = 106;
   public static final int STATUS_ICON_AOL_IM_OFFLINE = 107;
   public static final int STATUS_ICON_AOL_IM_AWAY = 108;
   public static final int STATUS_ICON_AOL_IM_NEW_MESSAGE = 109;
   public static final int STATUS_ICON_AOL_EMAIL_NEW_MESSAGE = 110;
   public static final int STATUS_ICON_ICQ_IM_ONLINE = 111;
   public static final int STATUS_ICON_ICQ_IM_OFFLINE = 112;
   public static final int STATUS_ICON_ICQ_IM_AWAY = 113;
   public static final int STATUS_ICON_ICQ_IM_NEW_MESSAGE = 114;
   public static final int STATUS_ICON_WLIVE_IM_ONLINE = 115;
   public static final int STATUS_ICON_WLIVE_IM_OFFLINE = 116;
   public static final int STATUS_ICON_WLIVE_IM_BUSY = 117;
   public static final int STATUS_ICON_WLIVE_IM_AWAY = 118;
   public static final int STATUS_ICON_WLIVE_IM_NEW_MESSAGE = 119;
   public static final int STATUS_ICON_WLIVE_EMAIL_NEW_MESSAGE = 120;
   public static final int STATUS_ICON_GENERIC_EMAIL_NEW_MESSAGE = 121;
   public static final int MESSAGE_TONE_YAHOO = 201;
   public static final int MESSAGE_TONE_AOL = 202;
   public static final int MESSAGE_TONE_ICQ = 203;
   public static final int MESSAGE_TONE_MSN = 204;
   public static final int MAIL_TONE_YAHOO = 301;
   public static final int MAIL_TONE_AOL = 302;
   public static final int MAIL_TONE_ICQ = 303;
   public static final int MAIL_TONE_MSN = 304;
   public static final int GENERIC_TONE = 401;
   static final Object emailClientLock = SharedObjects.getLock("com.nokia.mid.s40.EmailClientUtils.emailClientLock");

   private EmailClientUtils() {
      throw new IllegalStateException();
   }

   public static void setCurrent(Display display, Displayable nextDisplayable, String promptText, int iconID) {
      if (iconID >= 0 && iconID <= 8) {
         synchronized(emailClientLock) {
            nativeBindIconToCurrentThread(iconID);

            try {
               LCDUIUtils.setCurrent(display, nextDisplayable, promptText);
            } finally {
               nativeBindIconToCurrentThread(0);
            }

         }
      } else {
         throw new IllegalArgumentException("Invalid iconID");
      }
   }

   public static void setCurrent(Display display, Alert alert, Displayable nextDisplayable, String promptText, int iconID) {
      if (iconID >= 0 && iconID <= 8) {
         synchronized(emailClientLock) {
            nativeBindIconToCurrentThread(iconID);

            try {
               LCDUIUtils.setCurrent(display, alert, nextDisplayable, promptText);
            } finally {
               nativeBindIconToCurrentThread(0);
            }

         }
      } else {
         throw new IllegalArgumentException("Invalid iconID");
      }
   }

   public static void playAlertTone(int toneID) {
      if (toneID != 201 && toneID != 202 && toneID != 203 && toneID != 204 && toneID != 301 && toneID != 302 && toneID != 303 && toneID != 304 && toneID != 401) {
         throw new IllegalArgumentException("Invalid toneID");
      } else {
         nativePlayAlertTone(toneID);
      }
   }

   public static void setStatusIndicator(int indicatorID, boolean status) {
      if (indicatorID >= 101 && indicatorID <= 121) {
         nativeSetStatusIndicator(indicatorID, status);
      } else {
         throw new IllegalArgumentException("Invalid indicatorID");
      }
   }

   private static native void nativePlayAlertTone(int var0);

   private static native void nativeBindIconToCurrentThread(int var0);

   private static native void nativeSetStatusIndicator(int var0, boolean var1);
}
