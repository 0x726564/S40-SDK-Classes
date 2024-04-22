package com.nokia.mid.s40.ui.simulation.keyboard;

import com.nokia.mid.impl.isa.util.SharedObjects;
import java.util.Hashtable;

public final class KeyPressSimulator {
   public static final int KEY_END = -11;
   public static final int KEY_SEND = -10;
   public static final int KEY_SOFT_RIGHT = -7;
   public static final int KEY_SOFT_LEFT = -6;
   public static final int KEY_SOFT_MIDDLE = -5;
   public static final int KEY_SCROLL_RIGHT = -4;
   public static final int KEY_SCROLL_LEFT = -3;
   public static final int KEY_SCROLL_DOWN = -2;
   public static final int KEY_SCROLL_UP = -1;
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
   public static final int KEY_STATE_SHORT = 257;
   public static final int KEY_STATE_LONG = 258;
   public static final int KEY_STATE_REPEAT = 259;
   private static final int KEY_STATE_NORMAL = 256;
   private static Hashtable keyCodeTranslationTable = new Hashtable();
   private static final Object keypressLock = SharedObjects.getLock("com.nokia.mid.s40.ui.simulation.keyboard.keypressLock");

   public static boolean simulateKeyPress(int keycode) {
      boolean result = false;
      Integer translatedkey = (Integer)keyCodeTranslationTable.get(new Integer(keycode));
      if (translatedkey != null) {
         synchronized(keypressLock) {
            result = native_simulateKeyPress(translatedkey, 256);
         }
      }

      return result;
   }

   public static boolean simulateKeyPress(int keycode, int keystate) {
      boolean result = false;
      if (keystate != 257 && keystate != 258 && keystate != 259) {
         throw new IllegalArgumentException("Illegal key state");
      } else {
         Integer translatedkey = (Integer)keyCodeTranslationTable.get(new Integer(keycode));
         if (translatedkey != null) {
            synchronized(keypressLock) {
               result = native_simulateKeyPress(translatedkey, keystate);
            }
         }

         return result;
      }
   }

   private static native boolean native_simulateKeyPress(int var0, int var1);

   static {
      keyCodeTranslationTable.put(new Integer(48), new Integer(0));
      keyCodeTranslationTable.put(new Integer(49), new Integer(1));
      keyCodeTranslationTable.put(new Integer(50), new Integer(2));
      keyCodeTranslationTable.put(new Integer(51), new Integer(3));
      keyCodeTranslationTable.put(new Integer(52), new Integer(4));
      keyCodeTranslationTable.put(new Integer(53), new Integer(5));
      keyCodeTranslationTable.put(new Integer(54), new Integer(6));
      keyCodeTranslationTable.put(new Integer(55), new Integer(7));
      keyCodeTranslationTable.put(new Integer(56), new Integer(8));
      keyCodeTranslationTable.put(new Integer(57), new Integer(9));
      keyCodeTranslationTable.put(new Integer(35), new Integer(10));
      keyCodeTranslationTable.put(new Integer(42), new Integer(11));
      keyCodeTranslationTable.put(new Integer(-4), new Integer(12));
      keyCodeTranslationTable.put(new Integer(-3), new Integer(13));
      keyCodeTranslationTable.put(new Integer(-2), new Integer(14));
      keyCodeTranslationTable.put(new Integer(-1), new Integer(15));
      keyCodeTranslationTable.put(new Integer(-6), new Integer(16));
      keyCodeTranslationTable.put(new Integer(-7), new Integer(17));
      keyCodeTranslationTable.put(new Integer(-5), new Integer(18));
      keyCodeTranslationTable.put(new Integer(-10), new Integer(19));
      keyCodeTranslationTable.put(new Integer(-11), new Integer(20));
   }
}
