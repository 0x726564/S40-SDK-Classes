package com.nokia.mid.impl.isa.amms.video;

import com.nokia.mid.impl.isa.amms.control.ImageFormatCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.video.CameraPlayer;
import javax.microedition.media.MediaException;

public class AdvancedCameraPlayer extends CameraPlayer {
   protected void doRealize() throws MediaException {
      super.doRealize();
      if (this.locator.getLocatorType() == 7) {
         ImageFormatCtrlImpl ici = new ImageFormatCtrlImpl(this, 1, "image/jpeg");
         CameraCtrlImpl cci = new CameraCtrlImpl();
         SnapshotCtrlImpl sci = new SnapshotCtrlImpl(this, this.recordCtrl, ici, cci);
         this.controlManager.addControl("javax.microedition.amms.control.", "ImageFormatControl", ici);
         this.controlManager.addControl("javax.microedition.amms.control.camera.", "SnapshotControl", sci);
         this.controlManager.addControl("javax.microedition.amms.control.camera.", "CameraControl", cci);
      }

   }
}
