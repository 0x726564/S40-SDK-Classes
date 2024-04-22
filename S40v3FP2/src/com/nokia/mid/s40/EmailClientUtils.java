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
   public static final int IDLE_PROMPT_ICON_MSN = 4;
   public static final int STATUS_ICON_YAHOO_IM_ONLINE = 101;
   public static final int STATUS_ICON_YAHOO_IM_OFFLINE = 102;
   public static final int STATUS_ICON_YAHOO_IM_BUSY = 103;
   public static final int STATUS_ICON_YAHOO_IM_NEW_MESSAGE = 104;
   public static final int STATUS_ICON_YAHOO_EMAIL_NEW_MESSAGE = 105;
   public static final int STATUS_ICON_AOL_IM_ONLINE = 106;
   public static final int STATUS_ICON_AOL_IM_OFFLINE = 107;
   public static final int STATUS_ICON_AOL_IM_BUSY = 108;
   public static final int STATUS_ICON_AOL_IM_NEW_MESSAGE = 109;
   public static final int STATUS_ICON_AOL_EMAIL_NEW_MESSAGE = 110;
   public static final int STATUS_ICON_ICQ_IM_ONLINE = 111;
   public static final int STATUS_ICON_ICQ_IM_OFFLINE = 112;
   public static final int STATUS_ICON_ICQ_IM_BUSY = 113;
   public static final int STATUS_ICON_ICQ_IM_NEW_MESSAGE = 114;
   public static final int STATUS_ICON_ICQ_EMAIL_NEW_MESSAGE = 115;
   public static final int STATUS_ICON_MSN_IM_ONLINE = 116;
   public static final int STATUS_ICON_MSN_IM_OFFLINE = 117;
   public static final int STATUS_ICON_MSN_IM_BUSY = 118;
   public static final int STATUS_ICON_MSN_IM_NEW_MESSAGE = 119;
   public static final int STATUS_ICON_MSN_EMAIL_NEW_MESSAGE = 120;
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

   public static void setCurrent(Display var0, Displayable var1, String var2, int var3) {
      if (var3 >= 0 && var3 <= 4) {
         synchronized(emailClientLock) {
            nativeBindIconToCurrentThread(var3);

            try {
               LCDUIUtils.setCurrent(var0, var1, var2);
            } finally {
               nativeBindIconToCurrentThread(0);
            }

         }
      } else {
         throw new IllegalArgumentException("Invalid iconID");
      }
   }

   public static void setCurrent(Display var0, Alert var1, Displayable var2, String var3, int var4) {
      if (var4 >= 0 && var4 <= 4) {
         synchronized(emailClientLock) {
            nativeBindIconToCurrentThread(var4);

            try {
               LCDUIUtils.setCurrent(var0, var1, var2, var3);
            } finally {
               nativeBindIconToCurrentThread(0);
            }

         }
      } else {
         throw new IllegalArgumentException("Invalid iconID");
      }
   }

   public static void playAlertTone(int var0) {
      if (var0 != 201 && var0 != 202 && var0 != 203 && var0 != 204 && var0 != 301 && var0 != 302 && var0 != 303 && var0 != 304) {
         throw new IllegalArgumentException("Invalid toneID");
      } else {
         nativePlayAlertTone(var0);
      }
   }

   public static void setStatusIndicator(int var0, boolean var1) {
      if (var0 >= 101 && var0 <= 121) {
         nativeSetStatusIndicator(var0, var1);
      } else {
         throw new IllegalArgumentException("Invalid indicatorID");
      }
   }

   private static native void nativePlayAlertTone(int var0);

   private static native void nativeBindIconToCurrentThread(int var0);

   private static native void nativeSetStatusIndicator(int var0, boolean var1);
}
