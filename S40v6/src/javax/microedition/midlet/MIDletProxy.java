package javax.microedition.midlet;

import com.nokia.mid.impl.isa.ui.MIDletState;

class MIDletProxy extends MIDletState {
   MIDletProxy(MIDlet m) {
      super(m);
   }

   protected void startApp() throws MIDletStateChangeException {
      this.midlet.startApp();
   }

   protected void pauseApp() {
      this.midlet.pauseApp();
   }

   protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
      this.midlet.destroyApp(unconditional);
   }
}
