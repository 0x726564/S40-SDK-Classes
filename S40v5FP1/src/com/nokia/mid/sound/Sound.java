package com.nokia.mid.sound;

import com.nokia.mid.impl.isa.sound.SoundDatabase;
import java.io.ByteArrayInputStream;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VolumeControl;

public class Sound {
   public static final int SOUND_PLAYING = 0;
   public static final int SOUND_STOPPED = 1;
   public static final int SOUND_UNINITIALIZED = 3;
   public static final int FORMAT_TONE = 1;
   public static final int FORMAT_WAV = 5;
   private static final int DEFAULT_VOLUME = 200;
   private static final int FREQUENCY_LOW = 0;
   private static final int FREQUENCY_HIGH = 7692;
   private static boolean isHwInitialized = false;
   private static Sound currentPlaying;
   private static final int RET_BAD_PHONE_STATE = 0;
   private static final int RET_BAD_STATE = 1;
   private static final int RET_BAD_SEQUENCE = 2;
   private static final int RET_PLAY_FAIL = 3;
   private static final int RET_OK = 4;
   private static final int RET_NOT_READY = 5;
   private static final int RET_OUT_OF_MEMORY = 6;
   private static final int RET_COULD_NOT_SEND = 7;
   private static final int TYPE_BUZZER = 8;
   private static final int TYPE_TONE = 9;
   private static final int TYPE_ALERT = 10;
   private SoundListener listener;
   private int freq;
   private int format;
   private int type;
   private int status = 3;
   private int gain;
   private int loop;
   private byte[] data;
   private long duration;
   private boolean already_played = false;
   private Sound.SoundPlayListener listenerSoundThread;
   private byte[] data_midi;
   private Player midiPlayer;
   private VolumeControl volCol;
   private byte note;

   public Sound(int freq, long duration) {
      this.initToneBuzzer(freq, duration);
   }

   public Sound(byte[] rcvData, int type) {
      this.initRingingTone(rcvData, type);
   }

   private Sound(int soundId) {
      this.format = soundId;
      this.listenerSoundThread = new Sound.SoundPlayListener();
      this.type = 10;
      this.gain = 200;
      this.status = 1;
      if (!isHwInitialized) {
         isHwInitialized = true;
         this.init0();
      }

   }

   public static int getConcurrentSoundCount(int type) {
      if (type != 1) {
         throw new IllegalArgumentException();
      } else {
         return 1;
      }
   }

   public int getGain() {
      return this.gain;
   }

   public int getState() {
      return this.status;
   }

   public static int[] getSupportedFormats() {
      return new int[]{1};
   }

   public void init(int freq, long duration) {
      this.initToneBuzzer(freq, duration);
      this.already_played = false;
   }

   public void init(byte[] rcvData, int type) {
      this.initRingingTone(rcvData, type);
      this.already_played = false;
   }

   public void play(int loop) {
      this.loop = loop;
      if (this.loop < 0) {
         throw new IllegalArgumentException();
      } else {
         if (this.loop < 255) {
            this.playInternal(loop);
         }

      }
   }

   public void release() {
      this.releaseInternal();
   }

   public void resume() {
      if (this.already_played) {
         this.playInternal(this.loop);
      }

   }

   public void setGain(int gain) {
      if (gain < 0) {
         this.gain = 0;
      } else if (gain > 255) {
         this.gain = 255;
      } else {
         this.gain = gain;
      }

   }

   public void setSoundListener(SoundListener listener) {
      this.listener = listener;
   }

   public void stop() {
      if (this.status == 0) {
         this.stopInternal();
      }

   }

   private void initRingingTone(byte[] rcvData, int type) throws IllegalArgumentException, NullPointerException {
      if (rcvData == null) {
         this.status = 3;
         throw new NullPointerException("Data is null.");
      } else if (type != 1) {
         this.status = 3;
         throw new IllegalArgumentException("Type unsupported or unknown.");
      } else {
         if (this.status != 3) {
            this.releaseInternal();
         }

         this.format = type;
         this.type = 9;
         this.gain = 200;
         this.data = rcvData;
         this.data_midi = nConvertOTAToMIDI(this.data);
         if (this.data_midi == null) {
            throw new IllegalArgumentException("Error converting to MIDI format.");
         } else {
            this.initializeMidiPlayer(this.data_midi);
            this.status = 1;
         }
      }
   }

   private void initToneBuzzer(int freq, long duration) throws IllegalArgumentException {
      if (duration > 0L && freq <= 7692 && freq >= 0) {
         if (this.status != 3) {
            this.releaseInternal();
         }

         this.format = 1;
         this.type = 8;
         this.freq = freq;
         this.duration = duration * 1000L;
         this.gain = 200;
         this.note = (byte)nConvertFreqToJavaNote(this.freq);
         byte tempoLowbyte = (byte)((int)(this.duration & 255L));
         byte tempoMidByte = (byte)((int)((this.duration & 65280L) >>> 8));
         byte tempoHighByte = (byte)((int)((this.duration & 16711680L) >>> 16));
         byte[] dummy_midi = new byte[]{77, 84, 104, 100, 0, 0, 0, 6, 0, 1, 0, 2, 0, 96, 77, 84, 114, 107, 0, 0, 0, 19, 0, -1, 88, 4, 4, 2, 24, 8, 0, -1, 81, 3, tempoHighByte, tempoMidByte, tempoLowbyte, 0, -1, 47, 0, 77, 84, 114, 107, 0, 0, 0, 15, 0, -63, 77, 0, -111, this.note, 127, 96, -111, this.note, 0, 0, -1, 47, 0};
         this.initializeMidiPlayer(dummy_midi);
         this.status = 1;
      } else {
         this.status = 3;
         throw new IllegalArgumentException("Parameter values are illegal.");
      }
   }

   private void initializeMidiPlayer(byte[] rcvData) {
      try {
         this.midiPlayer = Manager.createPlayer(new ByteArrayInputStream(rcvData), "audio/midi");
         this.listenerSoundThread = new Sound.SoundPlayListener();
         this.midiPlayer.addPlayerListener(this.listenerSoundThread);
      } catch (Exception var3) {
      }

   }

   private void playInternal(int loop) {
      if (this.type == 9 && this.data == null) {
         throw new NullPointerException("Data is null.");
      } else {
         if (this.status != 3) {
            if (currentPlaying != null && currentPlaying != this) {
               currentPlaying.stop();
            }

            currentPlaying = this;
            switch(this.type) {
            case 8:
            case 9:
               if (this.status == 0) {
                  this.stopInternal();
               }

               try {
                  this.midiPlayer.prefetch();
                  this.volCol = (VolumeControl)this.midiPlayer.getControl("VolumeControl");
                  this.volCol.setLevel((int)((double)this.gain / 2.55D));
                  if (this.loop == 0) {
                     this.midiPlayer.setLoopCount(-1);
                  } else {
                     this.midiPlayer.setLoopCount(this.loop);
                  }

                  this.midiPlayer.setMediaTime(0L);
                  this.midiPlayer.start();
               } catch (Exception var3) {
               }
               break;
            case 10:
               this.play0(this.type, this.format, this.duration, this.freq, this.data, 255, loop);
            }

            this.status = 0;
            this.already_played = true;
         }

      }
   }

   private void releaseInternal() {
      if (this.status != 3) {
         if (this.status == 0) {
            this.stopInternal();
         }

         if (this.midiPlayer != null) {
            this.midiPlayer.close();
            this.data = null;
         }

         if (this.type == 8) {
            this.freq = 0;
            this.duration = 0L;
            this.note = 0;
         }

         this.type = 0;
         this.format = 0;
         this.loop = 0;
         this.gain = 200;
         this.already_played = false;
         this.status = 3;
         this.listenerSoundThread = null;
      }

   }

   private void stopInternal() {
      try {
         this.midiPlayer.deallocate();
      } catch (Exception var2) {
      }

      this.status = 1;
   }

   private native int play0(int var1, int var2, long var3, int var5, byte[] var6, int var7, int var8);

   private native void init0();

   public static native byte[] nConvertOTAToMIDI(byte[] var0);

   public static native int nConvertFreqToJavaNote(int var0);

   static {
      SoundDatabase.addSound(new Sound(0), 0);
      SoundDatabase.addSound(new Sound(1), 1);
      SoundDatabase.addSound(new Sound(2), 2);
      SoundDatabase.addSound(new Sound(3), 3);
      SoundDatabase.addSound(new Sound(4), 4);
      SoundDatabase.addSound(new Sound(5), 5);
   }

   private class SoundPlayListener implements PlayerListener {
      private SoundPlayListener() {
      }

      public void playerUpdate(Player player, String event, Object eventData) {
         if (event != "stopped" && event != "endOfMedia") {
            if (event == "closed") {
               if (Sound.this.listener != null) {
                  Sound.this.status = 3;
                  Sound.this.listener.soundStateChanged(Sound.this, 3);
               }
            } else if (event == "started" && Sound.this.listener != null) {
               Sound.this.status = 0;
               Sound.this.listener.soundStateChanged(Sound.this, 0);
            }
         } else if (Sound.this.listener != null) {
            Sound.this.status = 1;
            Sound.this.listener.soundStateChanged(Sound.this, 1);
         }

      }

      // $FF: synthetic method
      SoundPlayListener(Object x1) {
         this();
      }
   }
}
