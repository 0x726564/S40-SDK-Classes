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
      this.addControl("TempoControl", new TempoCtrlImpl(this, (AudioOutImpl)this.mediaOut));
      this.addControl("StopTimeControl", this.stopCtrl);
      this.addControl("VolumeControl", this.volCtrl);
      RateCtrlImpl var1 = new RateCtrlImpl(this, (RateOut)this.mediaOut);
      if (var1.isValid()) {
         this.addControl("RateControl", var1);
      }

      if (MediaPrefs.nIsFeatureSupported(2)) {
         this.addControl("PitchControl", new PitchCtrlImpl(this, (AudioOutImpl)this.mediaOut));
      }

      this.playerId = eventConsumer.register((byte)0, this);
   }

   public void setParsedLocator(ParsedLocator var1) throws MediaException {
      super.setParsedLocator(var1);
      if (this.locator.isToneDeviceLocator()) {
         this.addControl("ToneControl", new TonePlayer.ToneCtrlImpl(this));
      }

   }

   protected void doPrefetch() throws MediaException {
      if (this.mediaData != null) {
         this.mediaData = nConvertToneSequenceToMIDI(this.mediaData);
         this.dataSource.setData(this.mediaData);
         super.doPrefetch();
      }

   }

   protected void doStart() throws MediaException {
      if (this.mediaData == null) {
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
      if (this.mediaData == null) {
         eventConsumer.serializeEvent(this.playerId, 8, 0L);
      } else {
         super.doStop();
      }

   }

   protected void doDeallocate() {
      if (this.mediaData != null) {
         super.doDeallocate();
      }

   }

   public void dispatchEvent(String var1, Object var2) {
      if (var1 == "stoppedAtTime") {
         this.stopCtrl.resetStopTime();
      }

      super.dispatchEvent(var1, var2);
   }

   private static native byte[] nConvertToneSequenceToMIDI(byte[] var0);

   private class ToneCtrlImpl extends Switchable implements ToneControl {
      ToneCtrlImpl(BasicPlayer var2) {
         this.player = var2;
      }

      public void setSequence(byte[] var1) {
         if (this.player.getState() >= 300) {
            throw new IllegalStateException();
         } else {
            try {
               Hashtable var2 = new Hashtable();
               boolean var3 = false;
               byte var4 = 0;
               int var5 = 0;
               int var6 = 2;
               if (var1[0] != -2 || var1[1] != 1) {
                  this.reportErr(6);
               }

               if (var1[var6] == -3) {
                  if (var1[var6 + 1] < 5) {
                     this.reportErr(5);
                  }

                  var6 += 2;
               }

               if (var1[var6] == -4) {
                  if (var1[var6 + 1] <= 0) {
                     this.reportErr(8);
                  }

                  var6 += 2;
               }

               int var8 = var6;

               while(true) {
                  if (var8 >= var1.length) {
                     if (var3) {
                        this.reportErr(1);
                     }
                     break;
                  }

                  byte var7 = var1[var8];
                  if (var7 < -9 || (var7 >= 0 || var7 == -1) && var1[var8 + 1] <= 0) {
                     this.reportErr(4);
                  }

                  switch(var7) {
                  case -9:
                     if (var1[var8 + 1] < 2) {
                        this.reportErr(9);
                     }

                     var7 = var1[var8 + 2];
                     if (var7 != -1 && var7 < 0) {
                        this.reportErr(11);
                     }
                     break;
                  case -8:
                     if (var1[var8 + 1] < 0 || var1[var8 + 1] > 100) {
                        this.reportErr(10);
                     }
                     break;
                  case -7:
                     if (var2.get(new Integer(var1[var8 + 1])) == null) {
                        this.reportErr(2);
                     }

                     if (var3) {
                        var5 += (Integer)var2.get(new Integer(var1[var8 + 1]));
                     }
                     break;
                  case -6:
                     if (var3) {
                        if (var1[var8 + 1] == var4) {
                           var3 = false;
                           var2.put(new Integer(var4), new Integer(var5));
                        } else {
                           this.reportErr(1);
                        }
                     } else {
                        this.reportErr(1);
                     }
                     break;
                  case -5:
                     if (!var3) {
                        if (var1[var8 + 1] < 0) {
                           this.reportErr(7);
                        }

                        var4 = var1[var8 + 1];
                        var3 = true;
                        var5 = 0;
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
                     if (var3) {
                        var5 += 2;
                     }
                  }

                  var8 += 2;
               }
            } catch (IllegalArgumentException var9) {
               throw var9;
            } catch (Exception var10) {
               throw new IllegalArgumentException(var10.getMessage());
            }

            TonePlayer.this.mediaData = var1;
         }
      }

      private void reportErr(int var1) {
         throw new IllegalArgumentException("Bad tone param: err code " + var1);
      }
   }
}
