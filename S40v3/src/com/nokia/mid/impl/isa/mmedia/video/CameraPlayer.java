package com.nokia.mid.impl.isa.mmedia.video;

import com.nokia.mid.impl.isa.mmedia.MediaPlayer;
import com.nokia.mid.impl.isa.mmedia.ParsedLocator;
import javax.microedition.media.MediaException;

public class CameraPlayer extends MediaPlayer {
   private static boolean sessionActive;
   private RecordCtrlImpl recordCtrl;
   private static final String LOCATOR_WIDTH = "width";
   private static final String LOCATOR_HEIGHT = "height";
   private static final String LOCATOR_ENCODING = "encoding";
   private static final int MAX_UNSIGNED_INT = 32767;
   private int width = -1;
   private int height = -1;
   private int locatorType;
   private static final Object sessionLock = new Object();

   public CameraPlayer() {
      this.mediaOut = new VideoOutImpl(this);
      this.playerId = eventConsumer.register((byte)2, this);
   }

   public void setParsedLocator(ParsedLocator var1) throws MediaException {
      super.setParsedLocator(var1);
      this.width = var1.getParamValueAsInt("width", 1, 32767, -1);
      this.height = var1.getParamValueAsInt("height", 1, 32767, -1);
      String var2 = var1.getParamValueAsString("encoding");
      if ((this.width != -1 || this.height <= 0) && (this.width <= 0 || this.height != -1)) {
         if (var2 != null) {
            String var3 = System.getProperty("video.encodings");
            var3 = var3.substring(var3.lastIndexOf(61) + 1);
            if (!var2.equals(var3)) {
               throw new MediaException("Invalid encoding: " + var2);
            }
         }

         this.locatorType = var1.getLocatorType();
      } else {
         throw new MediaException("Both parameters (width and height) required.");
      }
   }

   protected void doRealize() throws MediaException {
      VideoCtrlImpl var1 = new VideoCtrlImpl(this, (VideoOutImpl)this.mediaOut);
      String var2 = System.getProperty("video.encodings");
      String var3 = "video/" + var2.substring(var2.lastIndexOf(61) + 1);
      this.recordCtrl = new RecordCtrlImpl(this, true, var3, 1);
      this.addControl("GUIControl", var1);
      this.addControl("VideoControl", var1);
      if (this.locatorType == 6) {
         this.addControl("RecordControl", this.recordCtrl);
      } else {
         var1.setSnapshotSupport(this.recordCtrl);
      }

   }

   protected void doPrefetch() throws MediaException {
      synchronized(sessionLock) {
         if (!sessionActive) {
            if (!nOpenCameraSession(this.getPlayerId(), this.width, this.height, this.locatorType)) {
               throw new MediaException("device error");
            } else {
               this.setActiveState(true);
               sessionActive = true;
            }
         } else {
            throw new MediaException("Only one camera player can be active");
         }
      }
   }

   protected void doDeallocate() {
      super.doDeallocate();
      synchronized(sessionLock) {
         sessionActive = false;
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

   public void dispatchEvent(String var1, Object var2) {
      if (var1 == "error") {
         synchronized(sessionLock) {
            sessionActive = false;
         }
      }

      super.dispatchEvent(var1, var2);
   }

   private static native boolean nOpenCameraSession(int var0, int var1, int var2, int var3);
}
