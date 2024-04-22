package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.sound.SoundDatabase;
import com.nokia.mid.sound.Sound;
import java.lang.ref.WeakReference;

public class AlertType {
   static final int ALERT_NO_TYPE = -1;
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
   static WeakReference[] pixmapReferences = new WeakReference[5];
   int type;

   protected AlertType() {
   }

   private AlertType(int type) {
      this.type = type;
   }

   public boolean playSound(Display display) {
      boolean bRetValue = false;
      if (display == null) {
         throw new NullPointerException();
      } else {
         Sound _s = this.getSound();
         if (_s != null) {
            _s.play(1);
            bRetValue = true;
         }

         return bRetValue;
      }
   }

   private Sound getSound() {
      int soundId = -1;
      switch(this.type) {
      case 0:
         soundId = 0;
         break;
      case 1:
         soundId = 1;
         break;
      case 2:
         soundId = 2;
         break;
      case 3:
         soundId = 3;
         break;
      case 4:
         soundId = 4;
      }

      return soundId < 0 ? null : SoundDatabase.getSound(soundId);
   }
}
