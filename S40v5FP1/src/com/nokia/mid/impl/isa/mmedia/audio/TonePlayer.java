package com.nokia.mid.impl.isa.mmedia.audio;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import com.nokia.mid.impl.isa.mmedia.MediaPlayer;
import com.nokia.mid.impl.isa.mmedia.MediaPrefs;
import com.nokia.mid.impl.isa.mmedia.ParsedLocator;
import com.nokia.mid.impl.isa.mmedia.RateCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.StopTimeCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.VolumeCtrlImpl;
import com.nokia.mid.impl.isa.mmedia.control.RateOut;
import com.nokia.mid.impl.isa.mmedia.control.StopTimeOut;
import com.nokia.mid.impl.isa.mmedia.control.Switchable;
import java.util.Hashtable;
import javax.microedition.media.MediaException;
import javax.microedition.media.control.ToneControl;

public class TonePlayer extends MediaPlayer {
   StopTimeCtrlImpl stopCtrl;

   public TonePlayer() {
      this.mediaOut = new AudioOutImpl(this);
      this.volCtrl = new VolumeCtrlImpl(this, this.mediaOut);
      this.stopCtrl = new StopTimeCtrlImpl(this, (StopTimeOut)this.mediaOut);
      this.addControl("StopTimeControl", this.stopCtrl);
      this.addControl("VolumeControl", this.volCtrl);
      if (MediaPrefs.nIsFeatureSupported(2)) {
         this.addControl("PitchControl", new PitchCtrlImpl(this, (AudioOutImpl)this.mediaOut));
      }

      this.playerId = eventConsumer.register((byte)0, this);
   }

   public void setParsedLocator(ParsedLocator loc) throws MediaException {
      super.setParsedLocator(loc);
      RateCtrlImpl rateCtrl = new RateCtrlImpl(this, (RateOut)this.mediaOut);
      if (rateCtrl.isValid()) {
         this.addControl("RateControl", rateCtrl);
      }

      this.addControl("TempoControl", new TempoCtrlImpl(this, (AudioOutImpl)this.mediaOut));
      if (this.locator.isToneDeviceLocator()) {
         this.addControl("ToneControl", new TonePlayer.ToneCtrlImpl(this));
      }

   }

   protected void doPrefetch() throws MediaException {
      if (this.locator.isToneDeviceLocator()) {
         if (this.mediaData != null) {
            this.mediaData = nConvertToneSequenceToMIDI(this.mediaData);
            this.dataSource.setData(this.mediaData);
            super.doPrefetch();
         }
      } else {
         super.doPrefetch();
      }

   }

   protected void doStart() throws MediaException {
      if (this.mediaData == null && this.locator.isToneDeviceLocator()) {
         eventConsumer.serializeEvent(this.playerId, 7, 0L);
         eventConsumer.serializeEvent(this.playerId, 4, 0L);
      } else {
         super.doStart();
      }

   }

   protected long doGetDuration() {
      return this.locator.isToneDeviceLocator() ? 0L : super.doGetDuration();
   }

   protected void doStop() throws MediaException {
      if (this.mediaData == null && this.locator.isToneDeviceLocator()) {
         eventConsumer.serializeEvent(this.playerId, 8, 0L);
      } else {
         super.doStop();
      }

   }

   public void dispatchEvent(String evt, Object evtData) {
      if (evt == "stoppedAtTime") {
         this.stopCtrl.resetStopTime();
      }

      super.dispatchEvent(evt, evtData);
   }

   private static native byte[] nConvertToneSequenceToMIDI(byte[] var0);

   private class ToneCtrlImpl extends Switchable implements ToneControl {
      ToneCtrlImpl(BasicPlayer player) {
         this.player = player;
      }

      public void setSequence(byte[] sequence) {
         if (this.player.getState() >= 300) {
            throw new IllegalStateException();
         } else {
            try {
               Hashtable blens = new Hashtable();
               boolean inblk = false;
               int found = 0;
               int thisblen = 0;
               int startseq = 2;
               if (sequence[0] != -2 || sequence[1] != 1) {
                  this.reportErr(6);
               }

               if (sequence[startseq] == -3) {
                  if (sequence[startseq + 1] < 5) {
                     this.reportErr(5);
                  }

                  startseq += 2;
               }

               if (sequence[startseq] == -4) {
                  if (sequence[startseq + 1] <= 0) {
                     this.reportErr(8);
                  }

                  startseq += 2;
               }

               int i = startseq;

               while(true) {
                  if (i >= sequence.length) {
                     if (inblk) {
                        this.reportErr(1);
                     }
                     break;
                  }

                  byte note = sequence[i];
                  if (note < -9 || (note >= 0 || note == -1) && sequence[i + 1] <= 0) {
                     this.reportErr(4);
                  }

                  switch(note) {
                  case -9:
                     if (sequence[i + 1] < 2) {
                        this.reportErr(9);
                     }

                     note = sequence[i + 2];
                     if (note != -1 && note < 0) {
                        this.reportErr(11);
                     }
                     break;
                  case -8:
                     if (sequence[i + 1] < 0 || sequence[i + 1] > 100) {
                        this.reportErr(10);
                     }
                     break;
                  case -7:
                     if (blens.get(new Integer(sequence[i + 1])) == null) {
                        this.reportErr(2);
                     }

                     if (inblk) {
                        thisblen += (Integer)blens.get(new Integer(sequence[i + 1]));
                     }
                     break;
                  case -6:
                     if (inblk) {
                        if (sequence[i + 1] == found) {
                           inblk = false;
                           blens.put(new Integer(found), new Integer(thisblen));
                        } else {
                           this.reportErr(1);
                        }
                     } else {
                        this.reportErr(1);
                     }
                     break;
                  case -5:
                     if (!inblk) {
                        if (sequence[i + 1] < 0) {
                           this.reportErr(7);
                        }

                        found = sequence[i + 1];
                        inblk = true;
                        thisblen = 0;
                     } else {
                        this.reportErr(3);
                     }
                     break;
                  case -4:
                  case -3:
                  case -2:
                     this.reportErr(12);
                     break;
                  default:
                     if (inblk) {
                        thisblen += 2;
                     }
                  }

                  i += 2;
               }
            } catch (IllegalArgumentException var9) {
               throw var9;
            } catch (Exception var10) {
               throw new IllegalArgumentException(var10.getMessage());
            }

            TonePlayer.this.mediaData = sequence;
         }
      }

      private void reportErr(int code) {
         throw new IllegalArgumentException("Bad tone param: err code " + code);
      }
   }
}
