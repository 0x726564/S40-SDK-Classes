package com.nokia.mid.impl.isa.mmedia.audio;

import com.nokia.mid.impl.isa.mmedia.MediaPlayer;
import com.nokia.mid.impl.isa.mmedia.ParsedLocator;
import com.nokia.mid.impl.isa.mmedia.video.RecordCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.video.VideoOutImpl;
import javax.microedition.media.MediaException;

public class AudioCapturePlayer extends MediaPlayer {
   private RecordCtrlImpl at;

   public AudioCapturePlayer() {
      this.mediaOut = new VideoOutImpl(this);
      this.playerId = eventConsumer.register((byte)2, this);
   }

   public String getDeviceName() {
      return "AUDIO CAPTURE";
   }

   public void setParsedLocator(ParsedLocator var1) throws MediaException {
      super.setParsedLocator(var1);
      String var7;
      if ((var7 = var1.getParamValueAsString("encoding")) != null) {
         var1 = null;
         String var2 = null;
         String var3 = System.getProperty("audio.encodings");
         int var4 = 0;
         boolean var5 = false;
         boolean var6 = false;
         int var9;
         if ((var9 = var3.indexOf(32)) == -1) {
            var9 = var3.length() - 1;
         }

         do {
            String var8 = var3.substring(var3.indexOf(61, var4) + 1, var9);
            if (var3.indexOf(47, var4) > 0) {
               var2 = var3.substring(var3.indexOf(47, var9) + 1);
            }

            if (!var7.equals(var8) || !var7.equals(var2)) {
               throw new MediaException("Invalid encoding: " + var7);
            }

            var4 = var9;
            if (var6) {
               break;
            }

            if ((var9 = var3.indexOf(32, var9)) == -1) {
               var9 = var3.length() - 1;
               var6 = true;
            }
         } while(var9 > 0);
      }

   }

   protected void doRealize() throws MediaException {
      String var1 = (var1 = System.getProperty("audio.encodings")).substring(var1.lastIndexOf(61) + 1);
      this.at = new RecordCtrlImpl(this, false, var1, 2);
      this.addControl("RecordControl", this.at);
   }

   protected void doPrefetch() throws MediaException {
      if (!nOpenAudioCaptureSession(this.getPlayerId())) {
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

   private static native boolean nOpenAudioCaptureSession(int var0);
}
