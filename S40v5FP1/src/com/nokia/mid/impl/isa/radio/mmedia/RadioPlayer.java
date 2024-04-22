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
            short status = this.getRadioStatus();
            if (status == 52 || status == 53) {
               radioOnBeforeJavaStarted = status == 52;
            }

            this.setRadioStatusOnJavaStart(radioOnBeforeJavaStarted);
         }
      }

      this.volumeCtl = new VolumeCtrlImplVideo(this, 0);
      this.addControl("VolumeControl", this.volumeCtl);
      this.playerId = eventConsumer.register((byte)1, this);
   }

   public void setParsedLocator(ParsedLocator loc) throws MediaException {
      super.setParsedLocator(loc);
      String value = loc.getParamValueAsString("f");
      if (value != null) {
         this.frequency = this.parseFrequency(value);
         if (this.frequency == -1) {
            throw new MediaException("Invalid frequency " + value);
         }
      }

      value = loc.getParamValueAsString("mod");
      if (value != null && !value.equals("fm")) {
         throw new MediaException("Invalid modulation " + value);
      } else {
         value = loc.getParamValueAsString("st");
         if (value != null) {
            if (value.equals("auto")) {
               this.audioMode = supportedStereoMode ? 51 : 50;
            } else if (value.equals("mono")) {
               this.audioMode = 50;
            } else {
               if (!value.equals("stereo")) {
                  throw new MediaException("Invalid stereo mode " + value);
               }

               if (!supportedStereoMode) {
                  throw new MediaException("Stereo mode not supported");
               }

               this.audioMode = 51;
            }
         }

         value = loc.getParamValueAsString("id");
         if (value != null && value.length() == 0) {
            throw new MediaException("Invalid id " + value);
         } else {
            value = loc.getParamValueAsString("preset");
            if (value != null) {
               boolean invalid = false;

               try {
                  invalid = Integer.parseInt(value) < 0;
               } catch (NumberFormatException var5) {
                  invalid = true;
               }

               if (invalid) {
                  throw new MediaException("Invalid preset value " + value);
               }
            }

         }
      }
   }

   private int parseFrequency(String freq) {
      int exponent = 0;
      if (freq.endsWith("M")) {
         exponent = 6;
      } else if (freq.endsWith("k")) {
         exponent = 3;
      }

      int index = freq.indexOf(46);
      if (index == -1) {
         try {
            int frequency = Integer.parseInt(exponent == 0 ? freq : freq.substring(0, freq.length() - 1));
            if (exponent == 6) {
               frequency *= 1000000;
            } else if (exponent == 3) {
               frequency *= 1000;
            }

            return frequency;
         } catch (NumberFormatException var9) {
            return -1;
         }
      } else if (exponent == 0) {
         return -1;
      } else {
         String decimal = freq.substring(index + 1, freq.length() - 1);
         StringBuffer buffer = new StringBuffer();
         buffer.append(freq.substring(0, index));
         int size = decimal.length();
         if (size > exponent) {
            return -1;
         } else {
            buffer.append(decimal);

            for(int i = 0; i < exponent - size; ++i) {
               buffer.append('0');
            }

            try {
               return Integer.parseInt(buffer.toString());
            } catch (NumberFormatException var8) {
               return -1;
            }
         }
      }
   }

   protected void doSetLoopCount(int count) {
      if (count == 0) {
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
            int mode = this.getRadioAudioMode();
            if (mode == -1) {
               if (!radioOnBeforeJavaStarted) {
                  this.switchOnRadio(false);
               }

               throw new MediaException("Unable to get the audio mode");
            } else if (this.audioMode != mode && this.setRadioAudioMode(this.audioMode) == -1) {
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

   protected long doSetMediaTime(long now) throws MediaException {
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
