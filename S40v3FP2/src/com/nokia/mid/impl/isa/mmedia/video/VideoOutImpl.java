package com.nokia.mid.impl.isa.mmedia.video;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import com.nokia.mid.impl.isa.mmedia.MediaOut;
import com.nokia.mid.impl.isa.mmedia.control.FramePositioningOut;
import com.nokia.mid.impl.isa.mmedia.control.RateOut;
import com.nokia.mid.impl.isa.mmedia.control.StopTimeOut;
import javax.microedition.media.MediaException;

public class VideoOutImpl extends MediaOut implements RateOut, StopTimeOut, FramePositioningOut {
   static final int DEFAULT_IMG_MAX_RATE = 300000;
   static final int DEFAULT_IMG_MIN_RATE = 33333;
   static final int DEFAULT_VIDEO_MAX_RATE = 200000;
   static final int DEFAULT_VIDEO_MIN_RATE = 50000;
   static final int STOP_TIME_CTRL_RESET = -2;
   private static final long MICROSECS_PER_MILLISEC = 1000L;
   private int seekFrame;

   public VideoOutImpl(BasicPlayer var1) {
      super(var1, (byte)2);
   }

   public long getProperty(byte var1) throws MediaException {
      if (var1 != 1 && var1 != 2 && var1 != 3) {
         return (long)nGetProperty(this.player.getPlayerId(), var1);
      } else {
         int var2 = nGetProperty(this.player.getPlayerId(), var1);
         return var2 > 0 ? (long)var2 * 1000L : (long)var2;
      }
   }

   public long setProperty(byte var1, long var2) throws MediaException {
      long var4 = -1L;
      if (var1 == 3 || var1 == 1) {
         var2 = var2 > 0L ? var2 / 1000L : var2;
      }

      var4 = (long)nSetProperty(this.player.getPlayerId(), var1, (int)var2);
      return var4;
   }

   public long getStopTime() throws MediaException {
      long var1 = this.getProperty((byte)3);
      if (var1 == -2L) {
         var1 = Long.MAX_VALUE;
      }

      return var1;
   }

   public long setStopTime(long var1) throws MediaException {
      if (var1 == Long.MAX_VALUE) {
         var1 = -2L;
      }

      return this.setProperty((byte)3, var1);
   }

   public int setRate(int var1) throws MediaException {
      return (int)this.setProperty((byte)11, (long)var1);
   }

   public int getRate() throws MediaException {
      return (int)this.getProperty((byte)11);
   }

   public int getMaxRate() {
      int[] var1 = new int[2];
      return getRateControlRange(this.player.getPlayerId(), var1) ? var1[1] : 100000;
   }

   public int getMinRate() {
      int[] var1 = new int[2];
      return getRateControlRange(this.player.getPlayerId(), var1) ? var1[0] : 100000;
   }

   public int getDefaultMaxRate() {
      return this.player.locator.contentType.toLowerCase().compareTo("image/gif") == 0 ? 300000 : 200000;
   }

   public int getDefaultMinRate() {
      return this.player.locator.contentType.toLowerCase().compareTo("image/gif") == 0 ? '舵' : '썐';
   }

   public int seek(int var1) {
      int var2 = this.player.getState();
      if (var1 < 0) {
         var1 = 0;
      } else if (var1 == Integer.MAX_VALUE) {
         var1 = 2147483646;
      }

      int var3;
      if (var2 != 200 && var1 != 2147483646) {
         var3 = nSeek(this.player.getPlayerId(), var1);
      } else {
         this.seekFrame = var1;
         var3 = var1;
      }

      return var3;
   }

   public int skip(int var1) {
      return nSkip(this.player.getPlayerId(), var1);
   }

   public long mapFrameToTime(int var1) {
      return nMapFrameToTime(this.player.getPlayerId(), var1);
   }

   public int mapTimeToFrame(long var1) {
      return nMapTimeToFrame(this.player.getPlayerId(), var1);
   }

   public boolean pause() {
      return nPause(this.player.getPlayerId());
   }

   public boolean resume() {
      boolean var1 = nResume(this.player.getPlayerId(), this.seekFrame);
      this.seekFrame = 0;
      return var1;
   }

   public boolean openSession(byte[] var1, String var2, int var3, int var4, boolean var5) {
      return nOpenDataSession(this.player.getPlayerId(), var1, var2, var3, var4, this.player.locator.previewMode);
   }

   public void close() {
      nClose(this.player.getPlayerId());
   }

   protected boolean isMuted() throws MediaException {
      return nIsMuted(this.player.getPlayerId());
   }

   protected int setLevel(int var1) throws MediaException {
      return nSetLevel(this.player.getPlayerId(), var1);
   }

   protected void setMute(boolean var1) throws MediaException {
      nSetMute(this.player.getPlayerId(), var1);
   }

   public String[] getMetaDataKeys() {
      return this.nGetMetaDataKeys(this.player.getPlayerId());
   }

   public String getMetaDataKeyValue(String var1) throws IllegalArgumentException {
      return this.nGetMetaDataKey(this.player.getPlayerId(), var1);
   }

   static native boolean nPause(int var0);

   static native boolean nResume(int var0, int var1);

   static native void nClose(int var0);

   private static native void nInitVideo();

   private static native boolean nOpenDataSession(int var0, byte[] var1, String var2, int var3, int var4, boolean var5);

   private static native int nGetProperty(int var0, byte var1) throws MediaException;

   private static native int nSetProperty(int var0, byte var1, int var2) throws MediaException;

   private native String[] nGetMetaDataKeys(int var1);

   private native String nGetMetaDataKey(int var1, String var2) throws IllegalArgumentException;

   static native boolean getRateControlRange(int var0, int[] var1);

   private static native int nSetLevel(int var0, int var1) throws MediaException;

   private static native void nSetMute(int var0, boolean var1) throws MediaException;

   private static native boolean nIsMuted(int var0) throws MediaException;

   private static native int nSeek(int var0, int var1);

   private static native int nSkip(int var0, int var1);

   private static native long nMapFrameToTime(int var0, int var1);

   private static native int nMapTimeToFrame(int var0, long var1);

   static {
      nInitVideo();
   }
}
