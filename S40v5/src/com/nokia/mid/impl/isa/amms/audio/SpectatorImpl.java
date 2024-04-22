package com.nokia.mid.impl.isa.amms.audio;

import com.nokia.mid.impl.isa.mmedia.ControlManager;
import javax.microedition.media.Control;
import javax.microedition.media.Controllable;

public class SpectatorImpl implements Controllable {
   private ControlManager controlManager = new ControlManager();
   public static final Object specLock = new Object();

   public SpectatorImpl() {
      this.controlManager.addControlsFromEncodedStrings(this.nGetSupportedControls());
   }

   public Control getControl(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("null");
      } else if (var1.startsWith("javax.microedition.amms.control.audio3d")) {
         var1 = var1.substring("javax.microedition.amms.control.audio3d".length() + 1);
         return this.controlManager.getControl("javax.microedition.amms.control.audio3d", var1);
      } else {
         return null;
      }
   }

   public Control[] getControls() {
      return this.controlManager.getControls();
   }

   private native String[] nGetSupportedControls();
}
