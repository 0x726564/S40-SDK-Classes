package com.nokia.mid.impl.isa.mmedia.video;

import com.nokia.mid.impl.isa.mmedia.MediaPlayer;
import com.nokia.mid.impl.isa.mmedia.ParsedLocator;
import javax.microedition.media.MediaException;

public class CameraPlayer extends MediaPlayer {
   protected RecordCtrlImpl recordCtrl;
   private static final String LOCATOR_WIDTH = "width";
   private static final String LOCATOR_HEIGHT = "height";
   private static final String LOCATOR_ENCODING = "encoding";
   private static final int MAX_UNSIGNED_INT = 32767;
   private String encoding = null;
   private int width = -1;
   private int height = -1;

   public CameraPlayer() {
      this.mediaOut = new VideoOutImpl(this);
      this.playerId = eventConsumer.register((byte)2, this);
   }

   public void setParsedLocator(ParsedLocator loc) throws MediaException {
      super.setParsedLocator(loc);
      this.width = loc.getParamValueAsInt("width", 1, 32767, -1);
      this.height = loc.getParamValueAsInt("height", 1, 32767, -1);
      this.encoding = loc.getParamValueAsString("encoding");
      if ((this.width != -1 || this.height <= 0) && (this.width <= 0 || this.height != -1)) {
         if (this.encoding != null) {
            String sEnc = System.getProperty("video.encodings") + " ";
            boolean isValidEncoding = false;
            int iniIdx = false;
            int endIdx = 0;

            do {
               int iniIdx = sEnc.indexOf(61, endIdx);
               endIdx = iniIdx > endIdx ? sEnc.indexOf(32, iniIdx) : -1;
               if (endIdx > 0) {
                  String enc = sEnc.substring(iniIdx + 1, endIdx);
                  if (enc.equalsIgnoreCase(this.encoding) || enc.equalsIgnoreCase("video/" + this.encoding)) {
                     this.encoding = enc;
                     isValidEncoding = true;
                  }
               }
            } while(endIdx > 0 && !isValidEncoding);

            if (!isValidEncoding) {
               throw new MediaException("Invalid encoding: " + this.encoding);
            }
         }

      } else {
         throw new MediaException("Both parameters (width and height) required.");
      }
   }

   protected void doRealize() throws MediaException {
      VideoCtrlImpl videoCtrl = new VideoCtrlImpl(this, (VideoOutImpl)this.mediaOut);
      String sEnc = System.getProperty("video.encodings");
      String contentType = sEnc.substring(sEnc.lastIndexOf(61) + 1);
      this.recordCtrl = new RecordCtrlImpl(this, true, contentType, 1);
      this.addControl("GUIControl", videoCtrl);
      this.addControl("VideoControl", videoCtrl);
      if (this.locator.getLocatorType() == 6) {
         this.addControl("RecordControl", this.recordCtrl);
      } else {
         videoCtrl.setSnapshotSupport(this.recordCtrl);
      }

   }

   protected void doPrefetch() throws MediaException {
      if (!nOpenCameraSession(this.getPlayerId(), this.width, this.height, this.locator.getLocatorType(), this.encoding)) {
         throw new MediaException("device error");
      } else {
         this.setActiveState(true);
      }
   }

   protected void doStart() throws MediaException {
      super.doStart();
      this.recordCtrl.setStandbyMode(false);
   }

   protected void doStop() throws MediaException {
      this.recordCtrl.setStandbyMode(true);
      super.doStop();
   }

   public String getDeviceName() {
      return "CAMERA";
   }

   private static native boolean nOpenCameraSession(int var0, int var1, int var2, int var3, String var4);
}
