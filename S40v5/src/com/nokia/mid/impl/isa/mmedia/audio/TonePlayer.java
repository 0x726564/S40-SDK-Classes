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
   private StopTimeCtrlImpl am;

   public TonePlayer() {
      this.mediaOut = new AudioOutImpl(this);
      this.volCtrl = new VolumeCtrlImpl(this, this.mediaOut);
      this.am = new StopTimeCtrlImpl(this, (StopTimeOut)this.mediaOut);
      this.addControl("StopTimeControl", this.am);
      this.addControl("VolumeControl", this.volCtrl);
      if (MediaPrefs.nIsFeatureSupported(2)) {
         this.addControl("PitchControl", new PitchCtrlImpl(this, (AudioOutImpl)this.mediaOut));
      }

      this.playerId = eventConsumer.register((byte)0, this);
   }

   public void setParsedLocator(ParsedLocator var1) throws MediaException {
      super.setParsedLocator(var1);
      RateCtrlImpl var2;
      if ((var2 = new RateCtrlImpl(this, (RateOut)this.mediaOut)).isValid()) {
         this.addControl("RateControl", var2);
      }

      this.addControl("TempoControl", new TempoCtrlImpl(this, (AudioOutImpl)this.mediaOut));
      if (this.locator.isToneDeviceLocator()) {
         this.addControl("ToneControl", new TonePlayer.ToneCtrlImpl(this, this));
      }

   }

   protected void doPrefetch() throws MediaException {
      if (this.locator.isToneDeviceLocator()) {
         if (this.mediaData == null) {
            return;
         }

         this.dataSource.setData(this.mediaData);
      }

      super.doPrefetch();
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

   public void dispatchEvent(String var1, Object var2) {
      if (var1 == "stoppedAtTime") {
         this.am.resetStopTime();
      }

      super.dispatchEvent(var1, var2);
   }

   static byte[] a(TonePlayer var0, byte[] var1) {
      return var0.mediaData = var1;
   }

   private class ToneCtrlImpl extends Switchable implements ToneControl {
      private final TonePlayer dM;

      ToneCtrlImpl(TonePlayer var1, BasicPlayer var2) {
         this.dM = var1;
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
                  h(6);
               }

               if (var1[2] == -3) {
                  if (var1[3] < 5) {
                     h(5);
                  }

                  var6 += 2;
               }

               if (var1[var6] == -4) {
                  if (var1[var6 + 1] <= 0) {
                     h(8);
                  }

                  var6 += 2;
               }

               int var7 = var6;

               while(true) {
                  if (var7 >= var1.length) {
                     if (var3) {
                        h(1);
                     }
                     break;
                  }

                  byte var10;
                  if ((var10 = var1[var7]) < -9 || (var10 >= 0 || var10 == -1) && var1[var7 + 1] <= 0) {
                     h(4);
                  }

                  switch(var10) {
                  case -9:
                     if (var1[var7 + 1] < 2) {
                        h(9);
                     }

                     if ((var10 = var1[var7 + 2]) != -1 && var10 < 0) {
                        h(11);
                     }
                     break;
                  case -8:
                     if (var1[var7 + 1] < 0 || var1[var7 + 1] > 100) {
                        h(10);
                     }
                     break;
                  case -7:
                     if (var2.get(new Integer(var1[var7 + 1])) == null) {
                        h(2);
                     }

                     if (var3) {
                        var5 += (Integer)var2.get(new Integer(var1[var7 + 1]));
                     }
                     break;
                  case -6:
                     if (var3) {
                        if (var1[var7 + 1] == var4) {
                           var3 = false;
                           var2.put(new Integer(var4), new Integer(var5));
                        } else {
                           h(1);
                        }
                     } else {
                        h(1);
                     }
                     break;
                  case -5:
                     if (!var3) {
                        if (var1[var7 + 1] < 0) {
                           h(7);
                        }

                        var4 = var1[var7 + 1];
                        var3 = true;
                        var5 = 0;
                     } else {
                        h(3);
                     }
                     break;
                  case -4:
                  case -3:
                  case -2:
                     h(12);
                     break;
                  default:
                     if (var3) {
                        var5 += 2;
                     }
                  }

                  var7 += 2;
               }
            } catch (IllegalArgumentException var8) {
               throw var8;
            } catch (Exception var9) {
               throw new IllegalArgumentException(var9.getMessage());
            }

            TonePlayer.a(this.dM, var1);
         }
      }

      private static void h(int var0) {
         throw new IllegalArgumentException("Bad tone param: err code " + var0);
      }
   }
}
