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

   protected MediaOut(BasicPlayer var1, byte var2) {
      this.player = var1;
      this.mediaType = var2;
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
         } catch (MediaException var1) {
         }
      }

      return -1L;
   }

   protected String[] getMetaDataKeys() {
      throw new RuntimeException("Not Implemented");
   }

   protected String getMetaDataKeyValue(String var1) throws IllegalArgumentException {
      throw new RuntimeException("Not Implemented");
   }

   public long getMediaTime() {
      if (this.player.isActive()) {
         try {
            return this.getProperty((byte)1);
         } catch (MediaException var1) {
         }
      }

      return 0L;
   }

   public boolean openDataSession(byte[] var1, String var2, int var3, int var4) {
      return !this.player.isActive() && this.openSession(var1, var2, var3, var4, this.player.locator.previewMode);
   }

   public void setLoopCount(int var1) {
      if (this.player.isActive()) {
         try {
            this.setProperty((byte)4, (long)var1);
            return;
         } catch (MediaException var2) {
         }
      }

   }

   final long setMediaTime(long var1) throws MediaException {
      if (this.player.isActive()) {
         return this.setProperty((byte)1, var1);
      } else {
         throw new MediaException("Player not active");
      }
   }

   protected abstract boolean isMuted() throws MediaException;

   protected abstract int setLevel(int var1) throws MediaException;

   protected abstract void setMute(boolean var1) throws MediaException;
}
