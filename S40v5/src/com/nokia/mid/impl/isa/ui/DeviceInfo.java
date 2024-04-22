package com.nokia.mid.impl.isa.ui;

import com.nokia.mid.impl.isa.ui.style.UIStyle;

public class DeviceInfo {
   public static final int KEY_STOP = -23;
   public static final int KEY_NEXT_FORWARD = -22;
   public static final int KEY_PREVIOUS_REWIND = -21;
   public static final int KEY_PLAY = -20;
   public static final int KEY_END = -11;
   public static final int KEY_SEND = -10;
   public static final int KEY_SOFT_RIGHT = -7;
   public static final int KEY_SOFT_LEFT = -6;
   public static final int KEY_SOFT_MIDDLE = -5;
   public static final int KEY_SCROLL_RIGHT = -4;
   public static final int KEY_SCROLL_LEFT = -3;
   public static final int KEY_SCROLL_DOWN = -2;
   public static final int KEY_SCROLL_UP = -1;
   public static final int KEY_BACKSPACE = 8;
   public static final int KEY_LINE_FEED = 10;
   public static final int KEY_SPACE = 32;
   public static final int KEY_POUND = 35;
   public static final int KEY_STAR = 42;
   public static final int KEY_NUM0 = 48;
   public static final int KEY_NUM1 = 49;
   public static final int KEY_NUM2 = 50;
   public static final int KEY_NUM3 = 51;
   public static final int KEY_NUM4 = 52;
   public static final int KEY_NUM5 = 53;
   public static final int KEY_NUM6 = 54;
   public static final int KEY_NUM7 = 55;
   public static final int KEY_NUM8 = 56;
   public static final int KEY_NUM9 = 57;
   public static final int KEY_SOFT_SELECT = UIStyle.getNumberOfSoftKeys() > 2 ? -5 : -6;
   public static final int JAM_DISPLAY_MODE_SCREEN = 0;
   public static final int JAM_DISPLAY_MODE_TIMED_ALERT = 1;
   public static final int JAM_DISPLAY_MODE_CANVAS = 2;
   public static final int JAM_DISPLAY_MODE_FULL_CANVAS = 3;
   private static String eH;
   private static String eI;

   public static native int getDisplayWidth(int var0);

   public static native int getDisplayHeight(int var0);

   public static boolean isColor() {
      return false;
   }

   public static int numColors() {
      return 0;
   }

   public static int numAlphaLevels() {
      return 0;
   }

   public static boolean hasRepeatEvents() {
      return false;
   }

   public static boolean hasPointerEvents() {
      return false;
   }

   public static boolean hasPointerMotionEvents() {
      return false;
   }

   public static int getNumSoftButtons() {
      return 0;
   }

   public static int getKeyCodeForSB(int var0) {
      if (var0 == 0) {
         return -6;
      } else if (var0 == 2) {
         return -7;
      } else {
         throw new IllegalArgumentException("Invalid softkey number: " + var0);
      }
   }

   public static boolean isSoftkey(int var0) {
      return var0 == -6 || var0 == -7 || var0 == -5;
   }

   public static boolean isKeypadKey(int var0) {
      return -11 != var0 && -10 != var0 && -7 != var0 && -6 != var0 && -5 != var0 && -4 != var0 && -3 != var0 && -2 != var0 && -1 != var0;
   }

   public static boolean isCharAddingKey(int var0) {
      return 10 == var0 || 32 == var0 || 42 == var0 || 48 == var0 || 49 == var0 || 50 == var0 || 51 == var0 || 52 == var0 || 53 == var0 || 54 == var0 || 55 == var0 || 56 == var0 || 57 == var0;
   }

   public static native int getTickerPollInterval();

   public static native int getTickerIncrement();

   private static native void updateDeviceColors();

   private static native void updateDeviceEvents();

   public static String getDateFormatString() {
      return eH;
   }

   public static String getTimeFormatString() {
      return eI;
   }

   public static boolean is24HoursClock() {
      return nativeIs24HoursClock();
   }

   public static native boolean nativeIs24HoursClock();

   static {
      updateDeviceColors();
      updateDeviceEvents();
      eH = System.getProperty("com.nokia.mid.dateformat");
      eI = System.getProperty("com.nokia.mid.timeformat");
   }
}
