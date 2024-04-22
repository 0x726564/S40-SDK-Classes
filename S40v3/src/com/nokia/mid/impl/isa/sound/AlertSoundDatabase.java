package com.nokia.mid.impl.isa.sound;

import com.nokia.mid.sound.Sound;
import javax.microedition.lcdui.AlertType;

public final class AlertSoundDatabase {
   private Sound sAlaram;
   private Sound sConfirmation;
   private Sound sError;
   private Sound sInfo;
   private Sound sWarning;
   private static AlertSoundDatabase s_singletonInstance;

   public static AlertSoundDatabase getAlertSoundDatabase() {
      if (s_singletonInstance == null) {
         s_singletonInstance = new AlertSoundDatabase();
         Sound.getConcurrentSoundCount(1);
      }

      return s_singletonInstance;
   }

   public void addAlertSound(Sound var1, AlertType var2) {
      if (var2.equals(AlertType.ALARM)) {
         this.sAlaram = var1;
      } else if (var2.equals(AlertType.CONFIRMATION)) {
         this.sConfirmation = var1;
      } else if (var2.equals(AlertType.ERROR)) {
         this.sError = var1;
      } else if (var2.equals(AlertType.INFO)) {
         this.sInfo = var1;
      } else if (var2.equals(AlertType.WARNING)) {
         this.sWarning = var1;
      }

   }

   public Sound getAlertSound(AlertType var1) {
      if (var1.equals(AlertType.ALARM)) {
         return this.sAlaram;
      } else if (var1.equals(AlertType.CONFIRMATION)) {
         return this.sConfirmation;
      } else if (var1.equals(AlertType.ERROR)) {
         return this.sError;
      } else if (var1.equals(AlertType.INFO)) {
         return this.sInfo;
      } else {
         return var1.equals(AlertType.WARNING) ? this.sWarning : null;
      }
   }
}
