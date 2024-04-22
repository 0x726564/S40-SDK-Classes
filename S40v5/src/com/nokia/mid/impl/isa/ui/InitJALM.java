package com.nokia.mid.impl.isa.ui;

import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;

public final class InitJALM {
   private static boolean mn;

   public static MIDletAccess s_getMIDletAccessor() {
      return null;
   }

   public static EventProducer s_getEventProducer() {
      return EventDispatcher.s_getEventProducer();
   }

   public static ExitManager s_getExitManager() {
      if (!mn) {
         mn = true;
         return null;
      } else {
         throw new SecurityException();
      }
   }

   public static void s_setDisplayAccessor(DisplayAccess var0) {
      MIDletManager.s_setDisplayAccessor(var0);
   }

   public static void s_setMIDletList(List var0) {
      MIDletExplorer.s_setMIDletList(var0);
   }

   public static MIDlet getCurrentMIDlet() {
      return MIDletManager.getCurrentMIDlet();
   }

   private InitJALM() {
   }
}
