package com.nokia.mid.impl.isa.amms.audio;

import com.nokia.mid.impl.isa.mmedia.ControlManager;
import javax.microedition.media.Control;
import javax.microedition.media.Controllable;

public class SpectatorImpl implements Controllable {
   private ControlManager controlManager = new ControlManager();
   private static final String AUDIO3D_PACKAGE = "javax.microedition.amms.control.audio3d";
   public static final Object specLock = new Object();

   public SpectatorImpl() {
      this.controlManager.addControlsFromEncodedStrings(this.nGetSupportedControls());
   }

   public Control getControl(String controlType) {
      if (controlType == null) {
         throw new IllegalArgumentException("null");
      } else if (controlType.startsWith("javax.microedition.amms.control.audio3d")) {
         controlType = controlType.substring("javax.microedition.amms.control.audio3d".length() + 1);
         return this.controlManager.getControl("javax.microedition.amms.control.audio3d", controlType);
      } else {
         return null;
      }
   }

   public Control[] getControls() {
      return this.controlManager.getControls();
   }

   private native String[] nGetSupportedControls();
}
