package com.nokia.mid.impl.isa.ui;

public final class ITUKeyMap extends KeyMap {
   public static final int GAME_ACTION_MIN_VALUE = 1;
   public static final int GAME_ACTION_MAX_VALUE = 12;
   public static final int GAME_ACTION_VALUE_RANGE_SIZE = 12;
   public static final int KEY_CODE_MIN_VALUE = -23;
   public static final int KEY_CODE_MAX_VALUE = 57;
   public static final int KEY_CODE_VALUE_RANGE_SIZE = 81;

   ITUKeyMap() {
   }

   public int getGameAction(int keyCode) {
      int gameAction = -127;
      if (keyCode >= -23 && keyCode <= 57) {
         gameAction = nativeGetGameAction(keyCode - -23);
      }

      return gameAction;
   }

   public int getKeyCode(int gameAction) {
      int keyCode = Integer.MIN_VALUE;
      if (gameAction >= 1 && gameAction <= 12) {
         keyCode = nativeGetKeyCode(gameAction - 1);
      }

      return keyCode;
   }

   private static native int nativeGetGameAction(int var0);

   private static native int nativeGetKeyCode(int var0);

   private static native void nativeStaticInitializer();

   static {
      nativeStaticInitializer();
   }
}
