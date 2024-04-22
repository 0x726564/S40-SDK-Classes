package com.nokia.mid.s40;

import com.nokia.mid.impl.isa.ui.EventConsumer;
import com.nokia.mid.impl.isa.ui.EventProducer;
import com.nokia.mid.impl.isa.ui.InitJALM;
import java.io.IOException;
import java.io.InputStream;

public final class MusicClientUtils {
   private static MusicClientUtils.ActiveIdleKeyListener listener;
   public static final int LIGHT_MODE_VISIBLE_NOT_PLAYING = 100;
   public static final int LIGHT_MODE_VISIBLE_PLAYING = 101;
   public static final int LIGHT_MODE_NOT_VISIBLE = 200;

   private MusicClientUtils() {
      throw new IllegalStateException();
   }

   public static void register(MusicClientUtils.ActiveIdleKeyListener activeIdleKeyListener, String label, InputStream is) throws IOException {
      if (activeIdleKeyListener == null) {
         throw new IllegalArgumentException();
      } else {
         if (listener != null) {
            if (listener == activeIdleKeyListener) {
               return;
            }

            MusicClientUtils.MusicClientEventConsumer.unregister();
         }

         listener = activeIdleKeyListener;
         MusicClientUtils.MusicClientEventConsumer.register(label, inputStreamToByteArray(is));
      }
   }

   public static void unregister() {
      listener = null;
      MusicClientUtils.MusicClientEventConsumer.unregister();
   }

   public static void setMetaData(String text1, String text2, InputStream is1, InputStream is2, boolean status) throws IOException {
      nativeSetMetaData(text1, text2, inputStreamToByteArray(is1), inputStreamToByteArray(is2), status);
   }

   public static void setLightMode(int mode) {
      if (mode != 100 && mode != 101 && mode != 200) {
         throw new IllegalArgumentException();
      } else {
         nativeSetLightMode(mode);
      }
   }

   public static boolean isDefaultMusicPlayer() {
      return nativeIsDefaultMusicPlayer();
   }

   public static void setDefaultMusicPlayer(boolean defaultMusicPlayer) {
      nativeSetDefaultMusicPlayer(defaultMusicPlayer);
   }

   private static void processMusicClientEvent(int type) {
      if (listener != null) {
         listener.activeIdleKeyEvent(type);
      }
   }

   private static byte[] inputStreamToByteArray(InputStream is) throws IOException {
      if (is == null) {
         return null;
      } else {
         int MAX_BUF_SIZE = 1024;
         byte[] buffer = new byte[MAX_BUF_SIZE];

         int readbytes;
         byte[] newbuffer;
         for(readbytes = is.read(buffer); readbytes == MAX_BUF_SIZE; readbytes = is.read(newbuffer, newbuffer.length - MAX_BUF_SIZE, MAX_BUF_SIZE)) {
            newbuffer = new byte[buffer.length + MAX_BUF_SIZE];
            System.arraycopy(buffer, 0, newbuffer, 0, buffer.length);
            buffer = newbuffer;
         }

         int remainder;
         if (readbytes > 0) {
            remainder = MAX_BUF_SIZE - readbytes;
         } else {
            remainder = MAX_BUF_SIZE;
         }

         byte[] newbuffer = new byte[buffer.length - remainder];
         System.arraycopy(buffer, 0, newbuffer, 0, newbuffer.length);
         return newbuffer;
      }
   }

   private static native void nativeStaticInitialize();

   private static native void nativeRegisterMusicClient(String var0, byte[] var1);

   private static native void nativeUnregisterMusicClient();

   private static native void nativeSetMetaData(String var0, String var1, byte[] var2, byte[] var3, boolean var4);

   private static native void nativeSetLightMode(int var0);

   private static native boolean nativeIsDefaultMusicPlayer();

   private static native void nativeSetDefaultMusicPlayer(boolean var0);

   static {
      nativeStaticInitialize();
   }

   public interface ActiveIdleKeyListener {
      int SELECT = 0;
      int RIGHT = 1;
      int LEFT = 2;
      int RIGHT_LONG = 3;
      int LEFT_LONG = 4;
      int LONG_RELEASED = 5;

      void activeIdleKeyEvent(int var1);
   }

   private static class MusicClientEventConsumer implements EventConsumer {
      private static void register(String label, byte[] iconLabel) {
         MusicClientUtils.nativeRegisterMusicClient(label, iconLabel);
      }

      private static void unregister() {
         MusicClientUtils.nativeUnregisterMusicClient();
      }

      public void consumeEvent(int category, int type, int param) {
         if (category == 11) {
            MusicClientUtils.processMusicClientEvent(type);
         }
      }

      static {
         MusicClientUtils.MusicClientEventConsumer singletonInstance = new MusicClientUtils.MusicClientEventConsumer();
         EventProducer eventProducer = InitJALM.s_getEventProducer();
         eventProducer.attachEventConsumer(11, singletonInstance);
      }
   }
}
