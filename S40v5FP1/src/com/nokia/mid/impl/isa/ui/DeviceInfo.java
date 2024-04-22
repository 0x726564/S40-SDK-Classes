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
   private static boolean isColor;
   private static int numColorS;
   private static int numAlphaLevelS;
   private static boolean hasRepeatEvents;
   private static boolean hasPointerEvents;
   private static boolean hasPointerMotionEvents;
   private static int numSoftButtons;
   private static String dateFormat;
   private static String timeFormat;

   public static native int getDisplayWidth(int var0);

   public static native int getDisplayHeight(int var0);

   public static boolean isColor() {
      return isColor;
   }

   public static int numColors() {
      return numColorS;
   }

   public static int numAlphaLevels() {
      return numAlphaLevelS;
   }

   public static boolean hasRepeatEvents() {
      return hasRepeatEvents;
   }

   public static boolean hasPointerEvents() {
      return hasPointerEvents;
   }

   public static boolean hasPointerMotionEvents() {
      return hasPointerMotionEvents;
   }

   public static int getNumSoftButtons() {
      return numSoftButtons;
   }

   public static int getKeyCodeForSB(int buttonNum) {
      if (buttonNum == 0) {
         return -6;
      } else if (numSoftButtons == 2 && buttonNum == 1) {
         return -5;
      } else if (buttonNum == 2) {
         return -7;
      } else {
         throw new IllegalArgumentException("Invalid softkey number: " + buttonNum);
      }
   }

   public static boolean isSoftkey(int keyCode) {
      return keyCode == -6 || keyCode == -7 || keyCode == -5;
   }

   public static boolean isKeypadKey(int keyCode) {
      return -11 != keyCode && -10 != keyCode && -7 != keyCode && -6 != keyCode && -5 != keyCode && -4 != keyCode && -3 != keyCode && -2 != keyCode && -1 != keyCode;
   }

   public static boolean isCharAddingKey(int keyCode) {
      return 10 == keyCode || 32 == keyCode || 42 == keyCode || 48 == keyCode || 49 == keyCode || 50 == keyCode || 51 == keyCode || 52 == keyCode || 53 == keyCode || 54 == keyCode || 55 == keyCode || 56 == keyCode || 57 == keyCode;
   }

   public static native int getTickerPollInterval();

   public static native int getTickerIncrement();

   private static native void updateDeviceColors();

   private static native void updateDeviceEvents();

   public static String getDateFormatString() {
      return dateFormat;
   }

   public static String getTimeFormatString() {
      return timeFormat;
   }

   public static boolean is24HoursClock() {
      return nativeIs24HoursClock();
   }

   public static native boolean nativeIs24HoursClock();

   static {
      updateDeviceColors();
      updateDeviceEvents();
      dateFormat = System.getProperty("com.nokia.mid.dateformat");
      timeFormat = System.getProperty("com.nokia.mid.timeformat");
   }
}
