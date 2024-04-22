package com.nokia.mid.impl.isa.radio.mmedia;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import com.nokia.mid.impl.isa.mmedia.ParsedLocator;
import com.nokia.mid.impl.isa.mmedia.VolumeCtrlImplVideo;
import java.io.IOException;
import javax.microedition.media.MediaException;
import javax.microedition.media.PlayerListener;

public class RadioPlayer extends BasicPlayer {
   public static final String RADIO_LOCATOR_PREFIX = "capture://radio";
   private static final String LOCATOR_FREQUENCY = "f";
   private static final String LOCATOR_MODULATION = "mod";
   private static final String MODULATION_AM = "am";
   private static final String MODULATION_FM = "fm";
   private static final String LOCATOR_STEREO_MODE = "st";
   private static final String AUDIO_MODE_MONO = "mono";
   private static final String AUDIO_MODE_STEREO = "stereo";
   private static final String AUDIO_MODE_AUTO = "auto";
   private static final String LOCATOR_ID = "id";
   private static final String LOCATOR_PRESET = "preset";
   private int frequency = -1;
   private int audioMode = 50;
   private static boolean supportedStereoMode = isStereoSupported();
   private static final Object nativeLock = new Object();
   private static PlayerListener eventHandler;
   private VolumeCtrlImplVideo volumeCtl;
   private static boolean radioOnBeforeJavaStarted = false;
   private static final short RETURN_CODE_ERROR = -1;
   private static final short RETURN_CODE_OK = 0;
   private static final short RETURN_CODE_FALSE = 1;
   private static final short RETURN_CODE_TRUE = 2;
   private static final short MODE_MONO = 50;
   private static final short MODE_STEREO = 51;
   private static final short RADIO_ON = 52;
   private static final short RADIO_OFF = 53;
   private static final short RADIO_MUTED = 54;
   private static final short RADIO_UNMUTED = 55;

   public RadioPlayer() throws IOException {
      synchronized(nativeLock) {
         if (eventConsumer.getNumPlayers((byte)1) == 0) {
            short var2 = this.getRadioStatus();
            if (var2 == 52 || var2 == 53) {
               radioOnBeforeJavaStarted = var2 == 52;
            }

            this.setRadioStatusOnJavaStart(radioOnBeforeJavaStarted);
         }
      }

      this.volumeCtl = new VolumeCtrlImplVideo(this, 0);
      this.addControl("VolumeControl", this.volumeCtl);
      this.playerId = eventConsumer.register((byte)1, this);
   }

   public void setParsedLocator(ParsedLocator var1) throws MediaException {
      super.setParsedLocator(var1);
      String var2 = var1.getParamValueAsString("f");
      if (var2 != null) {
         this.frequency = this.parseFrequency(var2);
         if (this.frequency == -1) {
            throw new MediaException("Invalid frequency " + var2);
         }
      }

      var2 = var1.getParamValueAsString("mod");
      if (var2 != null && !var2.equals("fm")) {
         throw new MediaException("Invalid modulation " + var2);
      } else {
         var2 = var1.getParamValueAsString("st");
         if (var2 != null) {
            if (var2.equals("auto")) {
               this.audioMode = supportedStereoMode ? 51 : 50;
            } else if (var2.equals("mono")) {
               this.audioMode = 50;
            } else {
               if (!var2.equals("stereo")) {
                  throw new MediaException("Invalid stereo mode " + var2);
               }

               if (!supportedStereoMode) {
                  throw new MediaException("Stereo mode not supported");
               }

               this.audioMode = 51;
            }
         }

         var2 = var1.getParamValueAsString("id");
         if (var2 != null && var2.length() == 0) {
            throw new MediaException("Invalid id " + var2);
         } else {
            var2 = var1.getParamValueAsString("preset");
            if (var2 != null) {
               boolean var3 = false;

               try {
                  var3 = Integer.parseInt(var2) < 0;
               } catch (NumberFormatException var5) {
                  var3 = true;
               }

               if (var3) {
                  throw new MediaException("Invalid preset value " + var2);
               }
            }

         }
      }
   }

   private int parseFrequency(String var1) {
      byte var2 = 0;
      if (var1.endsWith("M")) {
         var2 = 6;
      } else if (var1.endsWith("k")) {
         var2 = 3;
      }

      int var3 = var1.indexOf(46);
      if (var3 == -1) {
         try {
            int var10 = Integer.parseInt(var2 == 0 ? var1 : var1.substring(0, var1.length() - 1));
            if (var2 == 6) {
               var10 *= 1000000;
            } else if (var2 == 3) {
               var10 *= 1000;
            }

            return var10;
         } catch (NumberFormatException var9) {
            return -1;
         }
      } else if (var2 == 0) {
         return -1;
      } else {
         String var4 = var1.substring(var3 + 1, var1.length() - 1);
         StringBuffer var5 = new StringBuffer();
         var5.append(var1.substring(0, var3));
         int var6 = var4.length();
         if (var6 > var2) {
            return -1;
         } else {
            var5.append(var4);

            for(int var7 = 0; var7 < var2 - var6; ++var7) {
               var5.append('0');
            }

            try {
               return Integer.parseInt(var5.toString());
            } catch (NumberFormatException var8) {
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
      synchronized(nativeLock) {
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
      synchronized(nativeLock) {
         if (this.switchOnRadio(true) < 0) {
            throw new MediaException("Headset unplugged or radio is unavailable.");
         } else if (this.setRadioFrequency(this.frequency) < 0) {
            if (!radioOnBeforeJavaStarted) {
               this.switchOnRadio(false);
            }

            throw new MediaException("Cannot set the frequency " + this.frequency);
         } else {
            short var2 = this.getRadioAudioMode();
            if (var2 == -1) {
               if (!radioOnBeforeJavaStarted) {
                  this.switchOnRadio(false);
               }

               throw new MediaException("Unable to get the audio mode");
            } else if (this.audioMode != var2 && this.setRadioAudioMode(this.audioMode) == -1) {
               if (!radioOnBeforeJavaStarted) {
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
      synchronized(nativeLock) {
         if (eventConsumer.getNumPlayers((byte)1) == 1) {
            this.switchOnRadio(false);
         }

         if (this.getState() != 300) {
            eventConsumer.serializeEvent(this.playerId, 8, -1L);
         }

      }
   }

   protected void doDeallocate() {
      synchronized(nativeLock) {
         if (eventConsumer.getNumPlayers((byte)1) == 1) {
            if (radioOnBeforeJavaStarted) {
               this.volumeCtl.setMute(true);
            } else {
               this.switchOnRadio(false);
            }
         }

      }
   }

   public void doDeactivate() {
   }

   protected void doClose() {
      this.unregisterPlayer();
   }

   public String getDeviceName() {
      return "RADIO";
   }

   private void unregisterPlayer() {
      synchronized(nativeLock) {
         if (eventConsumer.getNumPlayers((byte)1) != 0) {
            if (eventConsumer.getNumPlayers((byte)1) == 0) {
               this.unregisterRadio();
            }

         }
      }
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
