package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.sound.SoundDatabase;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.sound.Sound;
import java.lang.ref.WeakReference;

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
   static WeakReference[] pixmapReferences = new WeakReference[5];
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

   Pixmap getPixmap() {
      Pixmap var1 = null;
      if (pixmapReferences[this.type] != null) {
         var1 = (Pixmap)pixmapReferences[this.type].get();
      }

      if (var1 == null) {
         byte var2 = 2;
         switch(this.type) {
         case 0:
            var2 = 1;
            break;
         case 1:
            var2 = 3;
            break;
         case 2:
            var2 = 4;
         case 3:
         default:
            break;
         case 4:
            var2 = 5;
         }

         var1 = Pixmap.createPixmap(var2);
         if (var1 != null) {
            pixmapReferences[this.type] = new WeakReference(var1);
         }
      }

      return var1;
   }

   private Sound getSound() {
      byte var1 = -1;
      switch(this.type) {
      case 0:
         var1 = 0;
         break;
      case 1:
         var1 = 1;
         break;
      case 2:
         var1 = 2;
         break;
      case 3:
         var1 = 3;
         break;
      case 4:
         var1 = 4;
      }

      return var1 < 0 ? null : SoundDatabase.getSound(var1);
   }
}
