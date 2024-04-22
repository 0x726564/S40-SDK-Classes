package javax.microedition.lcdui;

import com.nokia.mid.impl.isa.sound.SoundDatabase;
import com.nokia.mid.impl.isa.ui.gdi.Pixmap;
import com.nokia.mid.sound.Sound;
import java.lang.ref.WeakReference;

public class AlertType {
   public static final AlertType INFO = new AlertType(3);
   public static final AlertType WARNING = new AlertType(4);
   public static final AlertType ERROR = new AlertType(2);
   public static final AlertType ALARM = new AlertType(0);
   public static final AlertType CONFIRMATION = new AlertType(1);
   private static WeakReference[] hF = new WeakReference[5];
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
         Sound var3;
         if ((var3 = this.getSound()) != null) {
            var3.play(1);
            var2 = true;
         }

         return var2;
      }
   }

   final void h(Display var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         Sound var2;
         if ((var2 = this.getSound()) != null) {
            var2.stop();
         }

      }
   }

   Pixmap getPixmap() {
      Pixmap var1 = null;
      if (hF[this.type] != null) {
         var1 = (Pixmap)hF[this.type].get();
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

         if ((var1 = Pixmap.createPixmap(var2)) != null) {
            hF[this.type] = new WeakReference(var1);
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
