package com.nokia.mid.impl.isa.mmedia;

import javax.microedition.media.MediaException;

public abstract class MediaOut {
   public static final byte MUTE = 0;
   public static final byte MEDIA_TIME = 1;
   public static final byte DURATION = 2;
   public static final byte STOP_TIME = 3;
   public static final byte LOOP_COUNT = 4;
   public static final byte SMF_TEMPO = 5;
   public static final byte TEMPO_RATE = 6;
   public static final byte PITCH_RAISE = 7;
   public static final byte CHANNEL_VOL = 8;
   public static final byte RATE_CONTROL_MIN = 9;
   public static final byte RATE_CONTROL_MAX = 10;
   public static final byte RATE_CONTROL_CURRENT = 11;
   public static final byte MAX_PROPERTY = 11;
   protected BasicPlayer player;
   protected byte mediaType;

   protected MediaOut(BasicPlayer player, byte mediaType) {
      this.player = player;
      this.mediaType = mediaType;
   }

   public abstract long getProperty(byte var1) throws MediaException;

   public abstract long setProperty(byte var1, long var2) throws MediaException;

   protected abstract boolean pause();

   protected abstract boolean resume();

   protected abstract boolean openSession(byte[] var1, String var2, int var3, int var4, boolean var5);

   protected abstract void close();

   public long getDuration() {
      if (this.player.isActive()) {
         try {
            return this.getProperty((byte)2);
         } catch (MediaException var2) {
         }
      }

      return -1L;
   }

   protected String[] getMetaDataKeys() {
      throw new RuntimeException("Not Implemented");
   }

   protected String getMetaDataKeyValue(String key) throws IllegalArgumentException {
      throw new RuntimeException("Not Implemented");
   }

   public long getMediaTime() {
      if (this.player.isActive()) {
         try {
            return this.getProperty((byte)1);
         } catch (MediaException var2) {
         }
      }

      return 0L;
   }

   public boolean openDataSession(byte[] sourceId, String contentType, int loopCount, int vol) {
      return !this.player.isActive() && this.openSession(sourceId, contentType, loopCount, vol, this.player.locator.previewMode);
   }

   public void setLoopCount(int count) {
      if (this.player.isActive()) {
         try {
            this.setProperty((byte)4, (long)count);
         } catch (MediaException var3) {
         }
      }

   }

   long setMediaTime(long time) throws MediaException {
      if (this.player.isActive()) {
         return this.setProperty((byte)1, time);
      } else {
         throw new MediaException("Player not active");
      }
   }

   protected abstract boolean isMuted() throws MediaException;

   protected abstract int setLevel(int var1) throws MediaException;

   protected abstract void setMute(boolean var1) throws MediaException;
}
