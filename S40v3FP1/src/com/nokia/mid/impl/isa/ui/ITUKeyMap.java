package com.nokia.mid.impl.isa.ui;

public final class ITUKeyMap extends KeyMap {
   public static final int GAME_ACTION_MIN_VALUE = 1;
   public static final int GAME_ACTION_MAX_VALUE = 12;
   public static final int GAME_ACTION_VALUE_RANGE_SIZE = 12;
   public static final int KEY_CODE_MIN_VALUE = -11;
   public static final int KEY_CODE_MAX_VALUE = 57;
   public static final int KEY_CODE_VALUE_RANGE_SIZE = 69;
   private static final byte[] keyToGameActionMap = createDeviceKeyToGameKeyMap();
   private static final int[] gameActionToKeyMap = createGameKeyToDeviceKeyMap();

   ITUKeyMap() {
   }

   public int getGameAction(int var1) {
      byte var2 = -127;
      if (var1 >= -11 && var1 <= 57) {
         var2 = keyToGameActionMap[var1 - -11];
      }

      return var2;
   }

   public int getKeyCode(int var1) {
      int var2 = Integer.MIN_VALUE;
      if (var1 >= 1 && var1 <= 12) {
         var2 = gameActionToKeyMap[var1 - 1];
      }

      return var2;
   }

   private static native int[] createGameKeyToDeviceKeyMap();

   private static native byte[] createDeviceKeyToGameKeyMap();

   private static native void init();

   static {
      init();
   }
}
