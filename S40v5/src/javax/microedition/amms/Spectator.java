package javax.microedition.amms;

import javax.microedition.media.Control;
import javax.microedition.media.Controllable;

public class Spectator implements Controllable {
   private Controllable a;

   Spectator(Controllable var1) {
      this.a = var1;
   }

   public Control getControl(String var1) {
      return this.a.getControl(var1);
   }

   public Control[] getControls() {
      return this.a.getControls();
   }
}
