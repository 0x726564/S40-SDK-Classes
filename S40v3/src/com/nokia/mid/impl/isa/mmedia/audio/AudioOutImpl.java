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
   private static final byte CONTENT_TYPE_MP3 = 0;
   private static final byte CONTENT_TYPE_AAC = 1;
   private static final long MICROSECS_PER_DECISEC = 100000L;
   private static final int DEFAULT_AUDIO_RATE = 100000;
   private static final int DEFAULT_AUDIO_MAX_RATE = 200000;
   private static final int DEFAULT_AUDIO_MIN_RATE = 50000;

   AudioOutImpl(BasicPlayer var1) {
      super(var1, (byte)0);
   }

   public long getProperty(byte var1) throws MediaException {
      return var1 != 1 && var1 != 2 ? (long)nGetProperty(this.player.getPlayerId(), var1, 0) : (long)nGetProperty(this.player.getPlayerId(), var1, 0) * 100000L;
   }

   public long setProperty(byte var1, long var2) throws MediaException {
      if (var1 == 1) {
         int var4 = microToDeciseconds(var2);
         return (long)nSetProperty(this.player.getPlayerId(), var1, var4) * 100000L;
      } else {
         return (long)nSetProperty(this.player.getPlayerId(), var1, (int)var2);
      }
   }

   public boolean pause() {
      return nPauseAudio(this.player.getPlayerId());
   }

   public boolean resume() {
      return nResumeAudio(this.player.getPlayerId());
   }

   public boolean openSession(byte[] var1, String var2, int var3, int var4) {
      return nOpenDataSession(this.player.getPlayerId(), var1, var2, var3, var4);
   }

   public void close() {
      nCloseAudioSession(this.player.getPlayerId());
   }

   protected String[] getMetaDataKeys() {
      return this.nGetMetaDataKeys(this.getCurrentContentType());
   }

   protected String getMetaDataKeyValue(String var1) throws IllegalArgumentException {
      return this.nGetMetaDataKeyValue(this.player.getPlayerId(), var1, this.getCurrentContentType());
   }

   protected int setPitch(int var1) throws MediaException {
      return nSetProperty(this.player.getPlayerId(), (byte)7, var1);
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

   public int setRate(int var1) throws MediaException {
      return MediaPrefs.nIsFeatureSupported(1) ? nSetProperty(this.player.getPlayerId(), (byte)6, var1) : 100000;
   }

   public int getRate() throws MediaException {
      return MediaPrefs.nIsFeatureSupported(1) ? nGetProperty(this.player.getPlayerId(), (byte)6, 2) : 100000;
   }

   public int getMaxRate() {
      int var1 = 200000;
      if (MediaPrefs.nIsFeatureSupported(1)) {
         try {
            var1 = nGetProperty(this.player.getPlayerId(), (byte)6, 3);
         } catch (MediaException var3) {
         }
      }

      return var1;
   }

   public int getMinRate() {
      int var1 = 50000;
      if (MediaPrefs.nIsFeatureSupported(1)) {
         try {
            var1 = nGetProperty(this.player.getPlayerId(), (byte)6, 1);
         } catch (MediaException var3) {
         }
      }

      return var1;
   }

   public int getDefaultMaxRate() {
      return MediaPrefs.nIsFeatureSupported(1) ? 200000 : 100000;
   }

   public int getDefaultMinRate() {
      return MediaPrefs.nIsFeatureSupported(1) ? '썐' : 100000;
   }

   protected int setTempo(int var1) throws MediaException {
      return nSetProperty(this.player.getPlayerId(), (byte)5, var1);
   }

   protected int getTempo() throws MediaException {
      return nGetProperty(this.player.getPlayerId(), (byte)5, 0);
   }

   protected void setMute(boolean var1) throws MediaException {
      nSetProperty(this.player.getPlayerId(), (byte)0, !var1 ? 0 : 1);
   }

   protected boolean isMuted() throws MediaException {
      return nGetProperty(this.player.getPlayerId(), (byte)0, 0) != 0;
   }

   protected int setLevel(int var1) throws MediaException {
      return nSetLevel(this.player.getPlayerId(), var1);
   }

   public long getStopTime() throws MediaException {
      return (long)nGetProperty(this.player.getPlayerId(), (byte)3, 0) * 100000L;
   }

   public long setStopTime(long var1) throws MediaException {
      int var3 = microToDeciseconds(var1);
      return (long)nSetProperty(this.player.getPlayerId(), (byte)3, var3) * 100000L;
   }

   int getChannelVolume(int var1) throws MediaException {
      return nGetProperty(this.player.getPlayerId(), (byte)8, var1);
   }

   int longMsg(byte[] var1, int var2, int var3) {
      return MediaPrefs.nIsFeatureSupported(4) ? nSendLongMidi(this.player.getPlayerId(), var1, var2, var3) : -1;
   }

   void shortMsg(int var1, int var2, int var3) {
      if (MediaPrefs.nIsFeatureSupported(4)) {
         nSendShortMidi(this.player.getPlayerId(), var1, var2, var3);
      }

   }

   boolean openMidiEventSession(int var1) {
      return nOpenMidiEventSession(this.player.getPlayerId(), var1);
   }

   private static int microToDeciseconds(long var0) {
      if (var0 == Long.MAX_VALUE) {
         var0 = 2147483647L;
      } else {
         long var2 = var0;
         var0 /= 100000L;
         if (var2 - var0 * 100000L > 0L) {
            ++var0;
         }

         if (var0 >= 2147483647L) {
            var0 = 2147483646L;
         }
      }

      return (int)var0;
   }

   private short getCurrentContentType() {
      boolean var1 = false;
      byte var2;
      if (!this.player.getContentType().equals("audio/mpeg3") && !this.player.getContentType().equals("audio/mpeg") && !this.player.getContentType().equals("audio/mp3")) {
         if (!this.player.getContentType().equals("audio/mp4") && !this.player.getContentType().equals("audio/mpeg4")) {
            throw new IllegalArgumentException("Content type not supported");
         }

         var2 = 1;
      } else {
         var2 = 0;
      }

      return var2;
   }

   public static native boolean nPlayTone(int var0, int var1, int var2);

   private static native boolean nPauseAudio(int var0);

   private static native boolean nResumeAudio(int var0);

   private static native void nCloseAudioSession(int var0);

   private static native void nSendShortMidi(int var0, int var1, int var2, int var3);

   private static native int nSendLongMidi(int var0, byte[] var1, int var2, int var3);

   private static native void nInitAudio();

   private static native boolean nOpenMidiEventSession(int var0, int var1);

   private static native boolean nOpenDataSession(int var0, byte[] var1, String var2, int var3, int var4);

   private static native int nGetProperty(int var0, byte var1, int var2) throws MediaException;

   private static native int nSetProperty(int var0, byte var1, int var2) throws MediaException;

   private static native int nSetLevel(int var0, int var1) throws MediaException;

   private native String nGetMetaDataKeyValue(int var1, String var2, short var3) throws IllegalArgumentException;

   private native String[] nGetMetaDataKeys(short var1);

   static {
      nInitAudio();
   }
}
