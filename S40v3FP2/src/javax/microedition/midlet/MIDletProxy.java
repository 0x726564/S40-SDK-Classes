package javax.microedition.midlet;

import com.nokia.mid.impl.isa.ui.MIDletState;

class MIDletProxy extends MIDletState {
   MIDletProxy(MIDlet var1) {
      super(var1);
   }

   protected void startApp() throws MIDletStateChangeException {
      this.midlet.startApp();
   }

   protected void pauseApp() {
      this.midlet.pauseApp();
   }

   protected void destroyApp(boolean var1) throws MIDletStateChangeException {
      this.midlet.destroyApp(var1);
   }
}
