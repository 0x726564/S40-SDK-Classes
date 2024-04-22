package com.nokia.mid.impl.isa.mmedia.video;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import com.nokia.mid.impl.isa.mmedia.MediaOut;
import com.nokia.mid.impl.isa.mmedia.control.FramePositioningOut;
import com.nokia.mid.impl.isa.mmedia.control.RateOut;
import com.nokia.mid.impl.isa.mmedia.control.StopTimeOut;
import javax.microedition.media.MediaException;

public class VideoOutImpl extends MediaOut implements RateOut, StopTimeOut, FramePositioningOut {
   static final int DEFAULT_IMG_MAX_RATE = 100000;
   static final int DEFAULT_IMG_MIN_RATE = 33333;
   static final int DEFAULT_VIDEO_MAX_RATE = 200000;
   static final int DEFAULT_VIDEO_MIN_RATE = 50000;
   static final int STOP_TIME_CTRL_RESET = -2;
   private static final long MICROSECS_PER_MILLISEC = 1000L;

   public VideoOutImpl(BasicPlayer player) {
      super(player, (byte)2);
   }

   public long getProperty(byte prop) throws MediaException {
      if (prop != 1 && prop != 2 && prop != 3) {
         return (long)nGetProperty(this.player.getPlayerId(), prop);
      } else {
         int val = nGetProperty(this.player.getPlayerId(), prop);
         return val > 0 ? (long)val * 1000L : (long)val;
      }
   }

   public long setProperty(byte prop, long value) throws MediaException {
      long result = -1L;
      if (prop == 3 || prop == 1) {
         value = value > 0L ? value / 1000L : value;
      }

      result = (long)nSetProperty(this.player.getPlayerId(), prop, (int)value);
      return result;
   }

   public long getStopTime() throws MediaException {
      long time = this.getProperty((byte)3);
      if (time == -2L) {
         time = Long.MAX_VALUE;
      }

      return time;
   }

   public long setStopTime(long time) throws MediaException {
      if (time == Long.MAX_VALUE) {
         time = -2L;
      }

      return this.setProperty((byte)3, time);
   }

   public int setRate(int milliRate) throws MediaException {
      return nSetRate(this.player.getPlayerId(), milliRate);
   }

   public int getRate() throws MediaException {
      return nGetRate(this.player.getPlayerId());
   }

   public int getMaxRate() {
      int[] rate = new int[2];
      return getRateControlRange(this.player.getPlayerId(), rate) ? rate[1] : 100000;
   }

   public int getMinRate() {
      int[] rate = new int[2];
      return getRateControlRange(this.player.getPlayerId(), rate) ? rate[0] : 100000;
   }

   public int getDefaultMaxRate() {
      return this.player.locator.contentType.toLowerCase().compareTo("image/gif") != 0 && !this.player.locator.contentType.toLowerCase().startsWith("image") ? 200000 : 100000;
   }

   public int getDefaultMinRate() {
      if (this.player.locator.contentType.toLowerCase().compareTo("image/gif") == 0) {
         return 33333;
      } else {
         return this.player.locator.contentType.toLowerCase().startsWith("image") ? 100000 : '썐';
      }
   }

   public int seek(int frameNumber) {
      if (frameNumber < 0) {
         frameNumber = 0;
      }

      return nSeek(this.player.getPlayerId(), frameNumber);
   }

   public int skip(int framesToSkip) {
      return nSkip(this.player.getPlayerId(), framesToSkip);
   }

   public long mapFrameToTime(int frameNumber) {
      return nMapFrameToTime(this.player.getPlayerId(), frameNumber);
   }

   public int mapTimeToFrame(long time) {
      return nMapTimeToFrame(this.player.getPlayerId(), time);
   }

   public boolean pause() {
      return nPause(this.player.getPlayerId());
   }

   public boolean resume() {
      return nResume(this.player.getPlayerId());
   }

   public boolean openSession(byte[] sourceId, String contentType, int loopCount, int vol, boolean previewMode) {
      return nOpenDataSession(this.player.getPlayerId(), sourceId, contentType, loopCount, vol, this.player.locator.previewMode);
   }

   public void close() {
      nClose(this.player.getPlayerId());
   }

   protected boolean isMuted() throws MediaException {
      return nIsMuted(this.player.getPlayerId());
   }

   protected int setLevel(int newVol) throws MediaException {
      return nSetLevel(this.player.getPlayerId(), newVol);
   }

   protected void setMute(boolean mute) throws MediaException {
      nSetMute(this.player.getPlayerId(), mute);
   }

   public String[] getMetaDataKeys() {
      return this.nGetMetaDataKeys(this.player.getPlayerId());
   }

   public String getMetaDataKeyValue(String key) throws IllegalArgumentException {
      return this.nGetMetaDataKey(this.player.getPlayerId(), key);
   }

   static native boolean nPause(int var0);

   static native boolean nResume(int var0);

   static native void nClose(int var0);

   private static native void nCommonInit();

   private static native boolean nOpenDataSession(int var0, byte[] var1, String var2, int var3, int var4, boolean var5);

   private static native int nGetProperty(int var0, byte var1) throws MediaException;

   private static native int nSetProperty(int var0, byte var1, int var2) throws MediaException;

   private native String[] nGetMetaDataKeys(int var1);

   private native String nGetMetaDataKey(int var1, String var2) throws IllegalArgumentException;

   static native boolean getRateControlRange(int var0, int[] var1);

   static native int nSetRate(int var0, int var1);

   static native int nGetRate(int var0);

   private static native int nSetLevel(int var0, int var1) throws MediaException;

   private static native void nSetMute(int var0, boolean var1) throws MediaException;

   private static native boolean nIsMuted(int var0) throws MediaException;

   private static native int nSeek(int var0, int var1);

   private static native int nSkip(int var0, int var1);

   private static native long nMapFrameToTime(int var0, int var1);

   private static native int nMapTimeToFrame(int var0, long var1);

   static {
      nCommonInit();
   }
}
