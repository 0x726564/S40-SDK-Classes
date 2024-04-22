package com.nokia.mid.impl.isa.mmedia.video;

import com.nokia.mid.impl.isa.mmedia.MediaPlayer;
import com.nokia.mid.impl.isa.mmedia.ParsedLocator;
import javax.microedition.media.MediaException;

public class CameraPlayer extends MediaPlayer {
   private RecordCtrlImpl at;
   private String encoding = null;
   private int width = -1;
   private int height = -1;
   private int dL;

   public CameraPlayer() {
      this.mediaOut = new VideoOutImpl(this);
      this.playerId = eventConsumer.register((byte)2, this);
   }

   public void setParsedLocator(ParsedLocator var1) throws MediaException {
      super.setParsedLocator(var1);
      this.width = var1.getParamValueAsInt("width", 1, 32767, -1);
      this.height = var1.getParamValueAsInt("height", 1, 32767, -1);
      this.encoding = var1.getParamValueAsString("encoding");
      if ((this.width != -1 || this.height <= 0) && (this.width <= 0 || this.height != -1)) {
         if (this.encoding != null) {
            String var2 = System.getProperty("video.encodings") + " ";
            boolean var3 = false;
            boolean var4 = false;
            int var5 = 0;

            do {
               int var6;
               int var10000 = (var6 = var2.indexOf(61, var5)) > var5 ? var2.indexOf(32, var6) : -1;
               var5 = var10000;
               String var7;
               if (var10000 > 0 && ((var7 = var2.substring(var6 + 1, var5)).equalsIgnoreCase(this.encoding) || var7.equalsIgnoreCase("video/" + this.encoding))) {
                  this.encoding = var7;
                  var3 = true;
               }
            } while(var5 > 0 && !var3);

            if (!var3) {
               throw new MediaException("Invalid encoding: " + this.encoding);
            }
         }

         this.dL = var1.getLocatorType();
      } else {
         throw new MediaException("Both parameters (width and height) required.");
      }
   }

   protected void doRealize() throws MediaException {
      VideoCtrlImpl var1 = new VideoCtrlImpl(this, (VideoOutImpl)this.mediaOut);
      String var2 = (var2 = System.getProperty("video.encodings")).substring(var2.lastIndexOf(61) + 1);
      this.at = new RecordCtrlImpl(this, true, var2, 1);
      this.addControl("GUIControl", var1);
      this.addControl("VideoControl", var1);
      if (this.dL == 6) {
         this.addControl("RecordControl", this.at);
      } else {
         var1.setSnapshotSupport(this.at);
      }
   }

   protected void doPrefetch() throws MediaException {
      if (!nOpenCameraSession(this.getPlayerId(), this.width, this.height, this.dL, this.encoding)) {
         throw new MediaException("device error");
      } else {
         this.setActiveState(true);
      }
   }

   protected void doStart() throws MediaException {
      super.doStart();
      this.at.setStandbyMode(false);
   }

   protected void doStop() throws MediaException {
      this.at.setStandbyMode(true);
      super.doStop();
   }

   public String getDeviceName() {
      return "CAMERA";
   }

   private static native boolean nOpenCameraSession(int var0, int var1, int var2, int var3, String var4);
}
