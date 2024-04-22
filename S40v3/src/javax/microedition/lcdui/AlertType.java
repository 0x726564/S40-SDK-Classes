package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.sound.AlertSoundDatabase;
import com.nokia.mid.sound.Sound;

public class AlertType {
   static final int ALERT_ALARM = 0;
   static final int ALERT_CONFIRMATION = 1;
   static final int ALERT_ERROR = 2;
   static final int ALERT_INFO = 3;
   static final int ALERT_WARNING = 4;
   public static final AlertType INFO = new AlertType(3);
   public static final AlertType WARNING = new AlertType(4);
   public static final AlertType ERROR = new AlertType(2);
   public static final AlertType ALARM = new AlertType(0);
   public static final AlertType CONFIRMATION = new AlertType(1);
   int type;

   protected AlertType() {
   }

   private AlertType(int var1) {
      this.type = var1;
   }

   public boolean playSound(Display var1) {
      boolean var2 = false;
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         Sound var3 = this.getSound();
         if (var3 != null) {
            var3.play(1);
            var2 = true;
         }

         return var2;
      }
   }

   void stopSound(Display var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         Sound var2 = this.getSound();
         if (var2 != null) {
            var2.stop();
         }

      }
   }

   private Sound getSound() {
      AlertSoundDatabase var1 = AlertSoundDatabase.getAlertSoundDatabase();
      return var1.getAlertSound(this);
   }
}
