package com.nokia.mid.impl.isa.amms.video;

import com.nokia.mid.impl.isa.amms.AdvancedMediaEventConsumer;
import com.nokia.mid.impl.isa.amms.AdvancedMediaEventHelper;
import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import com.nokia.mid.impl.isa.mmedia.MediaPrefs;

public class CameraMediaEventHelper implements AdvancedMediaEventHelper {
   public void processEvent(AdvancedMediaEventConsumer amvc, BasicPlayer player, int eventId, long eventTime) {
      SnapshotCtrlImpl snc;
      switch(eventId) {
      case 107:
         snc = (SnapshotCtrlImpl)player.getControl("javax.microedition.amms.control.camera.SnapshotControl");
         MediaPrefs.nTrace(" EVENT_STORAGE_ERROR");
         if (snc != null) {
            player.dispatchEvent("STORAGE_ERROR", snc.getLastFile());
         }
         break;
      case 108:
         snc = (SnapshotCtrlImpl)player.getControl("javax.microedition.amms.control.camera.SnapshotControl");
         MediaPrefs.nTrace(" EVENT_SHOOTING_STOPPED");
         if (snc != null) {
            player.dispatchEvent("SHOOTING_STOPPED", snc.getLastFile());
         }
         break;
      case 109:
         MediaPrefs.nTrace(" EVENT_WAITING_UNFREEZE");
         player.dispatchEvent("WAITING_UNFREEZE", (Object)null);
         break;
      case 201:
      case 202:
         snc = (SnapshotCtrlImpl)player.getControl("javax.microedition.amms.control.camera.SnapshotControl");
         MediaPrefs.nTrace(" FILE_SAVED_" + (eventId == 201 ? "OK" : "ERROR"));
         if (snc != null) {
            snc.fileSavedEvent(eventTime, eventId != 201);
         }
         break;
      default:
         throw new RuntimeException("internal error: bad event");
      }

   }
}
