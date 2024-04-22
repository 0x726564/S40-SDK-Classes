package com.nokia.mid.impl.isa.sound;

import com.nokia.mid.sound.Sound;

public final class SoundDatabase {
   public static final int SID_ALERT_ALARM = 0;
   public static final int SID_ALERT_CONFIRMATION = 1;
   public static final int SID_ALERT_ERROR = 2;
   public static final int SID_ALERT_INFO = 3;
   public static final int SID_ALERT_WARNING = 4;
   public static final int SID_LIST_WRAP_TONE = 5;
   public static final int SID_NBR_SOUND_IDS = 6;
   private static Sound[] sounds = new Sound[6];

   private SoundDatabase() {
   }

   public static void addSound(Sound var0, int var1) {
      if (var1 >= 0 && var1 < 6) {
         sounds[var1] = var0;
      }

   }

   public static Sound getSound(int var0) {
      return var0 >= 0 && var0 < 6 ? sounds[var0] : null;
   }

   static {
      Sound.getConcurrentSoundCount(1);
   }
}
