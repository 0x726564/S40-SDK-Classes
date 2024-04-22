package com.nokia.mid.impl.isa.mmedia.video;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import com.nokia.mid.impl.isa.mmedia.MediaOut;
import com.nokia.mid.impl.isa.mmedia.control.RateOut;
import com.nokia.mid.impl.isa.mmedia.control.StopTimeOut;
import javax.microedition.media.MediaException;

public class VideoOutImpl extends MediaOut implements RateOut, StopTimeOut {
   static final int DEFAULT_IMG_MAX_RATE = 300000;
   static final int DEFAULT_IMG_MIN_RATE = 33333;
   static final int STOP_TIME_CTRL_RESET = -2;
   private static final long MICROSECS_PER_MILLISEC = 1000L;

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
      switch(var1) {
      case 3:
         return (long)nSetProperty(this.player.getPlayerId(), var1, (int)(var2 > 0L ? var2 / 1000L : var2));
      case 4:
      case 11:
         return (long)nSetProperty(this.player.getPlayerId(), var1, (int)var2);
      default:
         throw new MediaException("Not supported.");
      }
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
      return this.player.locator.contentType.toLowerCase().compareTo("image/gif") == 0 ? 300000 : 100000;
   }

   public int getDefaultMinRate() {
      return this.player.locator.contentType.toLowerCase().compareTo("image/gif") == 0 ? '舵' : 100000;
   }

   public boolean pause() {
      return nPause(this.player.getPlayerId());
   }

   public boolean resume() {
      return nResume(this.player.getPlayerId());
   }

   public boolean openSession(byte[] var1, String var2, int var3, int var4) {
      return nOpenDataSession(this.player.getPlayerId(), var1, var2, var3, var4);
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

   static native boolean nResume(int var0);

   static native void nClose(int var0);

   private static native void nInitVideo();

   private static native boolean nOpenDataSession(int var0, byte[] var1, String var2, int var3, int var4);

   private static native int nGetProperty(int var0, byte var1) throws MediaException;

   private static native int nSetProperty(int var0, byte var1, int var2) throws MediaException;

   private native String[] nGetMetaDataKeys(int var1);

   private native String nGetMetaDataKey(int var1, String var2) throws IllegalArgumentException;

   static native boolean getRateControlRange(int var0, int[] var1);

   private static native int nSetLevel(int var0, int var1) throws MediaException;

   private static native void nSetMute(int var0, boolean var1) throws MediaException;

   private static native boolean nIsMuted(int var0) throws MediaException;

   static {
      nInitVideo();
   }
}
