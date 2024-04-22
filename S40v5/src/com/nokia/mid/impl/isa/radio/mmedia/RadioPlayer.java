package com.nokia.mid.impl.isa.radio.mmedia;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import com.nokia.mid.impl.isa.mmedia.ParsedLocator;
import com.nokia.mid.impl.isa.mmedia.VolumeCtrlImplVideo;
import java.io.IOException;
import javax.microedition.media.MediaException;

public class RadioPlayer extends BasicPlayer {
   public static final String RADIO_LOCATOR_PREFIX = "capture://radio";
   private int aD = -1;
   private int aE = 50;
   private static boolean aF = isStereoSupported();
   private static final Object aG = new Object();
   private VolumeCtrlImplVideo aH;
   private static boolean aI = false;

   public RadioPlayer() throws IOException {
      synchronized(aG) {
         if (eventConsumer.getNumPlayers((byte)1) == 0) {
            short var2;
            if ((var2 = this.getRadioStatus()) == 52 || var2 == 53) {
               aI = var2 == 52;
            }

            this.setRadioStatusOnJavaStart(aI);
         }
      }

      this.aH = new VolumeCtrlImplVideo(this, 0);
      this.addControl("VolumeControl", this.aH);
      this.playerId = eventConsumer.register((byte)1, this);
   }

   public void setParsedLocator(ParsedLocator var1) throws MediaException {
      super.setParsedLocator(var1);
      String var2;
      if ((var2 = var1.getParamValueAsString("f")) != null) {
         this.aD = d(var2);
         if (this.aD == -1) {
            throw new MediaException("Invalid frequency " + var2);
         }
      }

      if ((var2 = var1.getParamValueAsString("mod")) != null && !var2.equals("fm")) {
         throw new MediaException("Invalid modulation " + var2);
      } else {
         if ((var2 = var1.getParamValueAsString("st")) != null) {
            if (var2.equals("auto")) {
               this.aE = aF ? 51 : 50;
            } else if (var2.equals("mono")) {
               this.aE = 50;
            } else {
               if (!var2.equals("stereo")) {
                  throw new MediaException("Invalid stereo mode " + var2);
               }

               if (!aF) {
                  throw new MediaException("Stereo mode not supported");
               }

               this.aE = 51;
            }
         }

         if ((var2 = var1.getParamValueAsString("id")) != null && var2.length() == 0) {
            throw new MediaException("Invalid id " + var2);
         } else {
            if ((var2 = var1.getParamValueAsString("preset")) != null) {
               boolean var4 = false;

               try {
                  var4 = Integer.parseInt(var2) < 0;
               } catch (NumberFormatException var3) {
                  var4 = true;
               }

               if (var4) {
                  throw new MediaException("Invalid preset value " + var2);
               }
            }

         }
      }
   }

   private static int d(String var0) {
      byte var1 = 0;
      if (var0.endsWith("M")) {
         var1 = 6;
      } else if (var0.endsWith("k")) {
         var1 = 3;
      }

      int var2;
      int var7;
      if ((var2 = var0.indexOf(46)) == -1) {
         try {
            var7 = Integer.parseInt(var1 == 0 ? var0 : var0.substring(0, var0.length() - 1));
            if (var1 == 6) {
               var7 *= 1000000;
            } else if (var1 == 3) {
               var7 *= 1000;
            }

            return var7;
         } catch (NumberFormatException var6) {
            return -1;
         }
      } else if (var1 == 0) {
         return -1;
      } else {
         String var3 = var0.substring(var2 + 1, var0.length() - 1);
         StringBuffer var4;
         (var4 = new StringBuffer()).append(var0.substring(0, var2));
         if ((var2 = var3.length()) > var1) {
            return -1;
         } else {
            var4.append(var3);

            for(var7 = 0; var7 < var1 - var2; ++var7) {
               var4.append('0');
            }

            try {
               return Integer.parseInt(var4.toString());
            } catch (NumberFormatException var5) {
               return -1;
            }
         }
      }
   }

   protected void doSetLoopCount(int var1) {
      if (var1 == 0) {
         throw new IllegalArgumentException("Loop count is not valid");
      }
   }

   protected void doRealize() throws MediaException {
      synchronized(aG) {
         if (this.isInFlightMode()) {
            throw new MediaException("Unable to realize Radio Player when in flight mode");
         } else if (eventConsumer.getNumPlayers((byte)1) == 0 && this.registerRadio() == -1) {
            throw new MediaException("Unable to initialize the radio");
         }
      }
   }

   protected void doPrefetch() throws MediaException {
      this.setActiveState(true);
   }

   protected void doStart() throws MediaException {
      synchronized(aG) {
         if (this.switchOnRadio(true) < 0) {
            throw new MediaException("Headset unplugged or radio is unavailable.");
         } else if (this.setRadioFrequency(this.aD) < 0) {
            if (!aI) {
               this.switchOnRadio(false);
            }

            throw new MediaException("Cannot set the frequency " + this.aD);
         } else {
            short var2;
            if ((var2 = this.getRadioAudioMode()) == -1) {
               if (!aI) {
                  this.switchOnRadio(false);
               }

               throw new MediaException("Unable to get the audio mode");
            } else if (this.aE != var2 && this.setRadioAudioMode(this.aE) == -1) {
               if (!aI) {
                  this.switchOnRadio(false);
               }

               throw new MediaException("Unable to set the audio mode");
            } else {
               eventConsumer.serializeEvent(this.playerId, 7, -1L);
            }
         }
      }
   }

   protected void doStop() throws MediaException {
      synchronized(aG) {
         if (eventConsumer.getNumPlayers((byte)1) == 1) {
            this.switchOnRadio(false);
         }

         if (this.getState() != 300) {
            eventConsumer.serializeEvent(this.playerId, 8, -1L);
         }

      }
   }

   protected void doDeallocate() {
      synchronized(aG) {
         if (eventConsumer.getNumPlayers((byte)1) == 1) {
            if (aI) {
               this.aH.setMute(true);
            } else {
               this.switchOnRadio(false);
            }
         }

      }
   }

   public void doDeactivate() {
   }

   protected void doClose() {
      RadioPlayer var3 = this;
      synchronized(aG) {
         if (eventConsumer.getNumPlayers((byte)1) != 0) {
            if (eventConsumer.getNumPlayers((byte)1) == 0) {
               var3.unregisterRadio();
            }

         }
      }
   }

   public String getDeviceName() {
      return "RADIO";
   }

   protected long doSetMediaTime(long var1) throws MediaException {
      throw new MediaException("Not supported");
   }

   protected long doGetMediaTime() {
      return -1L;
   }

   protected long doGetDuration() {
      return -1L;
   }

   public String getContentType() {
      this.chkIllegalState(true);
      return "radio";
   }

   private native short getRadioStatus();

   private native short switchOnRadio(boolean var1);

   private native short setRadioFrequency(int var1);

   private native short getRadioAudioMode();

   private native short setRadioAudioMode(int var1);

   private native short registerRadio();

   private native short unregisterRadio();

   private static native boolean isStereoSupported();

   private native short setRadioStatusOnJavaStart(boolean var1);

   private native boolean isInFlightMode();
}
