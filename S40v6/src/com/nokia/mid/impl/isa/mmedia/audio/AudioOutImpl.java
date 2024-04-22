package com.nokia.mid.impl.isa.mmedia.audio;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import com.nokia.mid.impl.isa.mmedia.MediaOut;
import com.nokia.mid.impl.isa.mmedia.MediaPrefs;
import com.nokia.mid.impl.isa.mmedia.control.RateOut;
import com.nokia.mid.impl.isa.mmedia.control.StopTimeOut;
import javax.microedition.media.MediaException;

public class AudioOutImpl extends MediaOut implements RateOut, StopTimeOut {
   private static final byte MIN = 1;
   private static final byte CURRENT = 2;
   private static final byte MAX = 3;
   private static final long MICROSECS_PER_DECISEC = 100000L;
   private static final int DEFAULT_AUDIO_RATE = 100000;
   private static final int DEFAULT_AUDIO_MAX_RATE = 200000;
   private static final int DEFAULT_AUDIO_MIN_RATE = 50000;

   AudioOutImpl(BasicPlayer player) {
      super(player, (byte)0);
   }

   public long getProperty(byte prop) throws MediaException {
      return prop != 1 && prop != 2 ? (long)nGetProperty(this.player.getPlayerId(), prop, 0) : (long)nGetProperty(this.player.getPlayerId(), prop, 0) * 100000L;
   }

   public long setProperty(byte prop, long value) throws MediaException {
      if (prop == 1) {
         int ds = microToDeciseconds(value);
         return (long)nSetProperty(this.player.getPlayerId(), prop, ds) * 100000L;
      } else {
         return (long)nSetProperty(this.player.getPlayerId(), prop, (int)value);
      }
   }

   public boolean pause() {
      return nPauseAudio(this.player.getPlayerId());
   }

   public boolean resume() {
      return nResumeAudio(this.player.getPlayerId());
   }

   public boolean openSession(byte[] sourceId, String contentType, int loopCount, int vol, boolean previewMode) {
      return nOpenDataSession(this.player.getPlayerId(), sourceId, contentType, loopCount, vol, this.player.locator.previewMode);
   }

   public void close() {
      nCloseAudioSession(this.player.getPlayerId());
   }

   protected String[] getMetaDataKeys() {
      return this.nGetMetaDataKeys(this.player.getContentType());
   }

   protected String getMetaDataKeyValue(String key) throws IllegalArgumentException {
      return this.nGetMetaDataKeyValue(this.player.getPlayerId(), key, this.player.getContentType());
   }

   protected int setPitch(int mst) throws MediaException {
      return nSetProperty(this.player.getPlayerId(), (byte)7, mst);
   }

   protected int getPitch() throws MediaException {
      return nGetProperty(this.player.getPlayerId(), (byte)7, 2);
   }

   protected int getMaxPitch() throws MediaException {
      return nGetProperty(this.player.getPlayerId(), (byte)7, 3);
   }

   protected int getMinPitch() throws MediaException {
      return nGetProperty(this.player.getPlayerId(), (byte)7, 1);
   }

   public int setRate(int milliRate) throws MediaException {
      return MediaPrefs.nIsFeatureSupported(1) && !this.player.locator.contentType.equals("audio/amr") ? nSetProperty(this.player.getPlayerId(), (byte)6, milliRate) : 100000;
   }

   public int getRate() throws MediaException {
      return MediaPrefs.nIsFeatureSupported(1) && !this.player.locator.contentType.equals("audio/amr") ? nGetProperty(this.player.getPlayerId(), (byte)6, 2) : 100000;
   }

   public int getMaxRate() {
      int maxRate = 200000;
      if (this.player.locator.contentType.equals("audio/amr")) {
         return 100000;
      } else {
         if (MediaPrefs.nIsFeatureSupported(1)) {
            try {
               maxRate = nGetProperty(this.player.getPlayerId(), (byte)6, 3);
            } catch (MediaException var3) {
            }
         }

         return maxRate;
      }
   }

   public int getMinRate() {
      int minRate = 50000;
      if (this.player.locator.contentType.equals("audio/amr")) {
         return 100000;
      } else {
         if (MediaPrefs.nIsFeatureSupported(1)) {
            try {
               minRate = nGetProperty(this.player.getPlayerId(), (byte)6, 1);
            } catch (MediaException var3) {
            }
         }

         return minRate;
      }
   }

   public int getDefaultMaxRate() {
      return MediaPrefs.nIsFeatureSupported(1) && !this.player.locator.contentType.equals("audio/amr") ? 200000 : 100000;
   }

   public int getDefaultMinRate() {
      return MediaPrefs.nIsFeatureSupported(1) && !this.player.locator.contentType.equals("audio/amr") ? '썐' : 100000;
   }

   protected int setTempo(int milliTempo) throws MediaException {
      return nSetProperty(this.player.getPlayerId(), (byte)5, milliTempo);
   }

   protected int getTempo() throws MediaException {
      return nGetProperty(this.player.getPlayerId(), (byte)5, 0);
   }

   protected void setMute(boolean mute) throws MediaException {
      this.nSetMute(this.player.getPlayerId(), mute);
   }

   protected boolean isMuted() throws MediaException {
      return this.nIsMuted(this.player.getPlayerId());
   }

   protected int setLevel(int level) throws MediaException {
      return nSetLevel(this.player.getPlayerId(), level);
   }

   public long getStopTime() throws MediaException {
      return (long)nGetProperty(this.player.getPlayerId(), (byte)3, 0) * 100000L;
   }

   public long setStopTime(long time) throws MediaException {
      int ds = microToDeciseconds(time);
      return (long)nSetProperty(this.player.getPlayerId(), (byte)3, ds) * 100000L;
   }

   int getChannelVolume(int channel) throws MediaException {
      return nGetProperty(this.player.getPlayerId(), (byte)8, channel);
   }

   int longMsg(byte[] data, int offset, int count) {
      return MediaPrefs.nIsFeatureSupported(4) ? nSendLongMidi(this.player.getPlayerId(), data, offset, count) : -1;
   }

   void shortMsg(int type, int data1, int data2) {
      if (MediaPrefs.nIsFeatureSupported(4)) {
         nSendShortMidi(this.player.getPlayerId(), type, data1, data2);
      }

   }

   boolean openMidiEventSession(int vol) {
      return nOpenMidiEventSession(this.player.getPlayerId(), vol);
   }

   private static int microToDeciseconds(long value) {
      if (value == Long.MAX_VALUE) {
         value = 2147483647L;
      } else {
         long tmp = value;
         value /= 100000L;
         if (tmp - value * 100000L > 0L) {
            ++value;
         }

         if (value >= 2147483647L) {
            value = 2147483646L;
         }
      }

      return (int)value;
   }

   public static native boolean nPlayTone(int var0, int var1, int var2);

   private static native boolean nPauseAudio(int var0);

   private static native boolean nResumeAudio(int var0);

   private static native void nCloseAudioSession(int var0);

   private static native void nSendShortMidi(int var0, int var1, int var2, int var3);

   private static native int nSendLongMidi(int var0, byte[] var1, int var2, int var3);

   private static native void nInitAudio();

   private static native boolean nOpenMidiEventSession(int var0, int var1);

   private static native boolean nOpenDataSession(int var0, byte[] var1, String var2, int var3, int var4, boolean var5);

   private static native int nGetProperty(int var0, byte var1, int var2) throws MediaException;

   private static native int nSetProperty(int var0, byte var1, int var2) throws MediaException;

   private static native int nSetLevel(int var0, int var1) throws MediaException;

   private native String nGetMetaDataKeyValue(int var1, String var2, String var3) throws IllegalArgumentException;

   private native String[] nGetMetaDataKeys(String var1);

   private native void nSetMute(int var1, boolean var2);

   private native boolean nIsMuted(int var1);

   public native boolean nIsMetaDataSupported(String var1);

   static {
      nInitAudio();
   }
}
