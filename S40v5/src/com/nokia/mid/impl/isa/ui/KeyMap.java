package com.nokia.mid.impl.isa.ui;

public abstract class KeyMap {
   public static final int KEY_CODE_NOT_MAPPED = 0;
   public static final int KEY_CODE_ILLEGAL = -127;
   public static final int GAME_ACTION_ILLEGAL = Integer.MIN_VALUE;

   protected KeyMap() {
   }

   public static KeyMap getKeyMap() {
      KeyMap var0 = null;

      try {
         var0 = (KeyMap)Class.forName(getKeyMapClassName()).newInstance();
      } catch (Exception var2) {
      }

      return var0;
   }

   private static native String getKeyMapClassName();

   public abstract int getGameAction(int var1);

   public abstract int getKeyCode(int var1);
}
