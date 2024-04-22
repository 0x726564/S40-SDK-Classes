package com.nokia.mid.impl.isa.mmedia.audio;

import com.nokia.mid.impl.isa.mmedia.MediaPlayer;
import com.nokia.mid.impl.isa.mmedia.ParsedLocator;
import com.nokia.mid.impl.isa.mmedia.video.RecordCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.video.VideoOutImpl;
import javax.microedition.media.MediaException;

public class AudioCapturePlayer extends MediaPlayer {
   private static boolean sessionActive;
   private RecordCtrlImpl recordCtrl;
   private static final String LOCATOR_ENCODING = "encoding";
   private static final Object sessionLock = new Object();

   public AudioCapturePlayer() {
      this.mediaOut = new VideoOutImpl(this);
      this.playerId = eventConsumer.register((byte)2, this);
   }

   public String getDeviceName() {
      return "AUDIO CAPTURE";
   }

   public void dispatchEvent(String var1, Object var2) {
      if (var1 == "error") {
         synchronized(sessionLock) {
            sessionActive = false;
         }
      }

      super.dispatchEvent(var1, var2);
   }

   public void setParsedLocator(ParsedLocator var1) throws MediaException {
      super.setParsedLocator(var1);
      String var2 = var1.getParamValueAsString("encoding");
      if (var2 != null) {
         String var3 = null;
         String var4 = null;
         String var5 = System.getProperty("audio.encodings");
         int var6 = 0;
         boolean var7 = true;
         boolean var8 = false;
         int var9;
         if ((var9 = var5.indexOf(32)) == -1) {
            var9 = var5.length() - 1;
         }

         do {
            var3 = var5.substring(var5.indexOf(61, var6) + 1, var9);
            if (var5.indexOf(47, var6) > 0) {
               var4 = var5.substring(var5.indexOf(47, var9) + 1);
            }

            if (!var2.equals(var3) || !var2.equals(var4)) {
               throw new MediaException("Invalid encoding: " + var2);
            }

            var6 = var9;
            if (var8) {
               break;
            }

            if ((var9 = var5.indexOf(32, var9)) == -1) {
               var9 = var5.length() - 1;
               var8 = true;
            }
         } while(var9 > 0);
      }

   }

   protected void doRealize() throws MediaException {
      String var1 = System.getProperty("audio.encodings");
      String var2 = var1.substring(var1.lastIndexOf(61) + 1);
      this.recordCtrl = new RecordCtrlImpl(this, false, var2, 2);
      this.addControl("RecordControl", this.recordCtrl);
   }

   protected void doPrefetch() throws MediaException {
      synchronized(sessionLock) {
         if (!sessionActive) {
            if (!nOpenAudioCaptureSession(this.getPlayerId())) {
               throw new MediaException("device error");
            } else {
               this.setActiveState(true);
               sessionActive = true;
            }
         } else {
            throw new MediaException("Only one audio capture player can be active");
         }
      }
   }

   protected void doDeallocate() {
      synchronized(sessionLock) {
         super.doDeallocate();
      }

      sessionActive = false;
   }

   protected void doStart() throws MediaException {
      super.doStart();
      this.recordCtrl.setStandbyMode(false);
   }

   protected void doStop() throws MediaException {
      this.recordCtrl.setStandbyMode(true);
      super.doStop();
   }

   private static native boolean nOpenAudioCaptureSession(int var0);
}
