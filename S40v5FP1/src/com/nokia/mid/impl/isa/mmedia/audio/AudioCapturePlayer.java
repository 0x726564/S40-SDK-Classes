package com.nokia.mid.impl.isa.mmedia.audio;

import com.nokia.mid.impl.isa.mmedia.MediaPlayer;
import com.nokia.mid.impl.isa.mmedia.ParsedLocator;
import com.nokia.mid.impl.isa.mmedia.video.RecordCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.video.VideoOutImpl;
import javax.microedition.media.MediaException;

public class AudioCapturePlayer extends MediaPlayer {
   private RecordCtrlImpl recordCtrl;
   private static final String LOCATOR_ENCODING = "encoding";

   public AudioCapturePlayer() {
      this.mediaOut = new VideoOutImpl(this);
      this.playerId = eventConsumer.register((byte)2, this);
   }

   public String getDeviceName() {
      return "AUDIO CAPTURE";
   }

   public void setParsedLocator(ParsedLocator loc) throws MediaException {
      super.setParsedLocator(loc);
      String encoding = loc.getParamValueAsString("encoding");
      if (encoding != null) {
         String sEncFull = null;
         String sEncPart = null;
         String sEnc = System.getProperty("audio.encodings");
         int pos = 0;
         int sepIndex = true;
         boolean isEndOfString = false;
         int sepIndex;
         if ((sepIndex = sEnc.indexOf(32)) == -1) {
            sepIndex = sEnc.length() - 1;
         }

         do {
            sEncFull = sEnc.substring(sEnc.indexOf(61, pos) + 1, sepIndex);
            if (sEnc.indexOf(47, pos) > 0) {
               sEncPart = sEnc.substring(sEnc.indexOf(47, sepIndex) + 1);
            }

            if (!encoding.equals(sEncFull) || !encoding.equals(sEncPart)) {
               throw new MediaException("Invalid encoding: " + encoding);
            }

            pos = sepIndex;
            if (isEndOfString) {
               break;
            }

            if ((sepIndex = sEnc.indexOf(32, sepIndex)) == -1) {
               sepIndex = sEnc.length() - 1;
               isEndOfString = true;
            }
         } while(sepIndex > 0);
      }

   }

   protected void doRealize() throws MediaException {
      String sEnc = System.getProperty("audio.encodings");
      String contentType = sEnc.substring(sEnc.lastIndexOf(61) + 1);
      this.recordCtrl = new RecordCtrlImpl(this, false, contentType, 2);
      this.addControl("RecordControl", this.recordCtrl);
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
      this.recordCtrl.setStandbyMode(false);
   }

   protected void doStop() throws MediaException {
      this.recordCtrl.setStandbyMode(true);
      super.doStop();
   }

   private static native boolean nOpenAudioCaptureSession(int var0);
}
