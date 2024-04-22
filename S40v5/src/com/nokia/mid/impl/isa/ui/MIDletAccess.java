package com.nokia.mid.impl.isa.ui;

import java.util.Timer;
import javax.microedition.midlet.MIDlet;

public interface MIDletAccess {
   void registerTimer(MIDlet var1, Timer var2);

   void deregisterTimer(MIDlet var1, Timer var2);

   void destroyMIDlet(MIDlet var1);

   String getMIDletName(MIDlet var1);

   DisplayAccess getDisplayAccessor();
}
