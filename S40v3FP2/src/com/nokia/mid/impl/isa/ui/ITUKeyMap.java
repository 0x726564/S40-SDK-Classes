package com.nokia.mid.impl.isa.ui;

public final class ITUKeyMap extends KeyMap {
   public static final int GAME_ACTION_MIN_VALUE = 1;
   public static final int GAME_ACTION_MAX_VALUE = 12;
   public static final int GAME_ACTION_VALUE_RANGE_SIZE = 12;
   public static final int KEY_CODE_MIN_VALUE = -11;
   public static final int KEY_CODE_MAX_VALUE = 57;
   public static final int KEY_CODE_VALUE_RANGE_SIZE = 69;

   ITUKeyMap() {
   }

   public int getGameAction(int var1) {
      int var2 = -127;
      if (var1 >= -11 && var1 <= 57) {
         var2 = nativeGetGameAction(var1 - -11);
      }

      return var2;
   }

   public int getKeyCode(int var1) {
      int var2 = Integer.MIN_VALUE;
      if (var1 >= 1 && var1 <= 12) {
         var2 = nativeGetKeyCode(var1 - 1);
      }

      return var2;
   }

   private static native int nativeGetGameAction(int var0);

   private static native int nativeGetKeyCode(int var0);

   private static native void nativeStaticInitializer();

   static {
      nativeStaticInitializer();
   }
}
