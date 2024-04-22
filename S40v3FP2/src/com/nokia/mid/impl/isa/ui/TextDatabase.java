package com.nokia.mid.impl.isa.ui;

import com.nokia.mid.impl.isa.ui.style.UIStyle;

public final class TextDatabase {
   public static final int qtn_java_info_running_in_bg2 = 0;
   public static final int qtn_java_quest_app_error = 1;
   public static final int text_softkey_option = 2;
   public static final int text_softkey_back = 3;
   public static final int text_softkey_yes = 4;
   public static final int text_softkey_no = 5;
   public static final int text_softkey_ok = 6;
   public static final int text_softkey_more = 7;
   public static final int text_softkey_cancel = 8;
   public static final int text_softkey_select = 9;
   public static final int text_softkey_mark = 10;
   public static final int text_softkey_unmark = 11;
   public static final int text_softkey_exit = 12;
   public static final int text_softkey_quit = 13;
   public static final int text_softkey_clear = 14;
   public static final int text_date_input_format = 15;
   public static final int text_time_input_format = 16;
   public static final int qtn_soft_java_dismiss = 17;
   public static final int qtn_java_prmpt_date = 18;
   public static final int qtn_java_prmpt_time = 19;
   public static final int text_calendar_invalid_date = 20;
   public static final int qtn_alert_type_null = 21;
   public static final int qtn_alert_type_alarm = 22;
   public static final int qtn_alert_type_confirmation = 23;
   public static final int qtn_alert_type_error = 24;
   public static final int qtn_alert_type_information = 25;
   public static final int qtn_alert_type_warning = 26;
   public static final int text_header_rtc_time = 27;
   public static final int text_header_calendar_date = 28;
   public static final int text_softkey_edit = 29;
   public static final int text_softkey_close = 30;
   public static final int text_softkey_help = 31;
   public static final int text_softkey_stop = 32;
   public static final int text_folder_empty = 33;
   public static final int qtn_java_prmpt_ellipsis = 34;
   public static final int text_softkey_find = 35;
   public static final int qtn_java_label_separator = 36;
   public static final int qtn_mark_all = 37;
   public static final int qtn_unmark_all = 38;
   public static final int text_realtime_am = 39;
   public static final int text_realtime_pm = 40;
   public static final int text_header_rtc_am_pm = 41;
   public static final int text_softkey_open_folder = 42;
   public static final int text_softkey_view = 43;
   public static final int qtn_me_mm_minutes = 44;
   public static final int qtn_me_hh = 45;
   public static final int qtn_me_dd = 46;
   public static final int qtn_me_mm_month = 47;
   public static final int qtn_me_yyyy = 48;
   public static final int text_calc_decimal_separator = 49;
   public static final int qtn_key_send = 50;
   public static final int qtn_key_right_selkey = 51;
   public static final int qtn_key_left_selkey = 52;
   public static final int qtn_key_middle_selkey = 53;
   public static final int qtn_key_scroll_right = 54;
   public static final int qtn_key_scroll_left = 55;
   public static final int qtn_key_scroll_down = 56;
   public static final int qtn_key_scroll_up = 57;
   public static final int qtn_key_pound = 58;
   public static final int qtn_key_star = 59;
   public static final int qtn_key_num0 = 60;
   public static final int qtn_key_num1 = 61;
   public static final int qtn_key_num2 = 62;
   public static final int qtn_key_num3 = 63;
   public static final int qtn_key_num4 = 64;
   public static final int qtn_key_num5 = 65;
   public static final int qtn_key_num6 = 66;
   public static final int qtn_key_num7 = 67;
   public static final int qtn_key_num8 = 68;
   public static final int qtn_key_num9 = 69;
   public static final int qtn_key_backspace = 70;
   public static final int qtn_key_space = 71;
   public static final int qtn_key_enter = 72;
   public static final int text_softkey_detail = 73;
   public static final int text_kjava_last = 74;
   static final char PLACEHOLDER_BEGIN = '%';
   static final char PLACEHOLDER_STRING_END = 'U';
   static final char PLACEHOLDER_NUM_END = 'N';
   static String[] usedStrings = new String[74];
   static String lastUsedLocale = System.getProperty("microedition.locale");

   public static String getText(int var0) {
      String[] var1 = usedStrings;

      try {
         if (var1[var0] == null) {
            var1[var0] = getNativeText(var0);
         }
      } catch (Exception var3) {
         throw new IllegalArgumentException("TextDatabase: " + var3);
      }

      return var1[var0];
   }

   public static String getText(int var0, String var1) {
      return s_rePlaceHolder(getText(var0), var1);
   }

   public static String getText(int var0, String var1, String var2) {
      return s_rePlaceHolder(s_rePlaceHolder(getText(var0), var1), var2);
   }

   private static native String getNativeText(int var0);

   private static String s_rePlaceHolder(String var0, String var1) {
      int var2 = var0.indexOf(37);
      int var3 = var0.indexOf(85) + 1;
      int var4 = var0.indexOf(78) + 1;
      int var5 = 0;
      if (var2 < 0) {
         throw new IllegalArgumentException("TextDatabase: invalid placeholder sequence");
      } else {
         if (var3 > 0 && var4 > 0) {
            var5 = var3 > var4 ? var4 : var3;
         } else if (var4 > 0) {
            var5 = var4;
         } else if (var3 > 0) {
            var5 = var3;
         } else if (var3 < 0 && var4 < 0) {
            throw new IllegalArgumentException("TextDatabase: invalid placeholder sequence");
         }

         return var0.substring(0, var2) + var1 + var0.substring(var5);
      }
   }

   private TextDatabase() {
   }

   static {
      UIStyle.registerReinitialiseListener(new ReinitialiseListener() {
         public void reinitialiseForForeground() {
            String var1 = System.getProperty("microedition.locale");
            if (!var1.equals(TextDatabase.lastUsedLocale)) {
               TextDatabase.lastUsedLocale = var1;
               TextDatabase.usedStrings = new String[74];
            }

         }
      });
   }
}
