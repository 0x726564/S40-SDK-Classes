package com.nokia.mid.impl.isa.ui;

import javax.microedition.midlet.MIDlet;

public final class InitJALM {
   private static boolean s_getExitManagerCalled;

   public static MIDletAccess s_getMIDletAccessor() {
      return MIDletManager.s_getMIDletManager();
   }

   public static EventProducer s_getEventProducer() {
      return EventDispatcher.s_getEventProducer();
   }

   public static ExitManager s_getExitManager() {
      if (!s_getExitManagerCalled) {
         s_getExitManagerCalled = true;
         return MIDletManager.s_getMIDletManager();
      } else {
         throw new SecurityException();
      }
   }

   public static void s_setDisplayAccessor(DisplayAccess da) {
      MIDletManager.s_setDisplayAccessor(da);
   }

   public static MIDlet getCurrentMIDlet() {
      return MIDletManager.getCurrentMIDlet();
   }

   private InitJALM() {
   }
}
