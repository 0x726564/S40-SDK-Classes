package com.nokia.mid.impl.isa.mmedia;

import com.nokia.mid.impl.isa.ui.EventConsumer;
import com.nokia.mid.impl.isa.ui.EventProducer;
import com.nokia.mid.impl.isa.ui.InitJALM;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.media.control.RecordControl;

public class MediaEventConsumer implements EventConsumer {
   public static final int BROADCAST_TO_ALL_RADIO_SESSIONS = -1;
   public static final int BROADCAST_TO_ALL_AUDIO_SESSIONS = -2;
   public static final int BROADCAST_TO_ALL_VIDEO_SESSIONS = -3;
   private final Hashtable registeredPlayers = new Hashtable();
   private static final long MICROSECS_PER_MILLISEC = 1000L;
   private static MediaEventConsumer instance;
   private static Object nativeLock = new Object();

   private MediaEventConsumer() {
   }

   public static synchronized MediaEventConsumer getInstance() {
      if (instance == null) {
         instance = new MediaEventConsumer();
         EventProducer var0 = InitJALM.s_getEventProducer();
         var0.attachEventConsumer(6, instance);
      }

      return instance;
   }

   public synchronized int register(byte var1, BasicPlayer var2) {
      int var3;
      synchronized(nativeLock) {
         var3 = nGenerateSessionID();
         nCreateSession(var3);
      }

      this.registeredPlayers.put(new Integer(var3), new MediaEventConsumer.PlayerInfo(var2, var1));
      return var3;
   }

   public synchronized void unRegister(int var1) {
      synchronized(nativeLock) {
         nDestroySession(var1);
      }

      this.registeredPlayers.remove(new Integer(var1));
   }

   public synchronized void consumeEvent(int var1, int var2, int var3) {
      long var4;
      synchronized(nativeLock) {
         var4 = (long)nReadEventInfo(var2, var3);
      }

      if (var4 != -1L) {
         var4 *= 1000L;
      }

      Long var6 = new Long(var4);
      if (var2 < 0) {
         byte var14;
         switch(var2) {
         case -3:
            var14 = 2;
            break;
         case -2:
            var14 = 0;
            break;
         case -1:
            var14 = 1;
            break;
         default:
            throw new RuntimeException("Bad broadcast session id.");
         }

         String var15;
         switch(var3) {
         case 2:
            var15 = "deviceAvailable";
            break;
         case 3:
            var15 = "deviceUnavailable";
            break;
         case 10:
            var15 = "volumeChanged";
            break;
         default:
            throw new RuntimeException("Bad broadcast event id.");
         }

         this.broadcastEvent(var14, var15);
      } else {
         MediaEventConsumer.PlayerInfo var7 = (MediaEventConsumer.PlayerInfo)this.registeredPlayers.get(new Integer(var2));
         if (var7 != null) {
            BasicPlayer var8 = var7.getPlayer();
            byte var9 = var7.getMediaType();
            String var10 = var8.getDeviceName();

            try {
               switch(var3) {
               case 1:
                  this.unRegister(var8.playerId);
                  var8.dispatchEvent("closed", (Object)null);
                  break;
               case 2:
                  var8.dispatchEvent("deviceAvailable", var10);
                  break;
               case 3:
                  var8.dispatchEvent("deviceUnavailable", var10);
                  break;
               case 4:
                  var8.dispatchEvent("endOfMedia", var6);
                  break;
               case 5:
                  var8.dispatchEvent("deviceUnavailable", var10);
                  var8.dispatchEvent("error", "Player Error");
                  break;
               case 6:
                  var8.dispatchEvent("sizeChanged", var8.getControl("VideoControl"));
                  break;
               case 7:
                  var8.dispatchEvent("started", var6);
                  break;
               case 8:
                  var8.dispatchEvent("stopped", var6);
                  break;
               case 9:
                  var8.dispatchEvent("stoppedAtTime", var6);
                  break;
               case 10:
                  var8.dispatchEvent("volumeChanged", var8.getControl("VolumeControl"));
                  break;
               case 11:
                  var8.dispatchEvent("recordStarted", var6);
                  break;
               case 12:
                  var8.dispatchEvent("recordStopped", var6);
                  break;
               case 13:
                  ((RecordControl)var8.getControl("RecordControl")).commit();
                  break;
               case 14:
                  var8.dispatchEvent("recordError", "Record Error");
               }
            } catch (Throwable var12) {
            }
         }

      }
   }

   public int getNumPlayers(byte var1) {
      int var2 = 0;
      synchronized(this.registeredPlayers) {
         Enumeration var4 = this.registeredPlayers.elements();

         while(var4.hasMoreElements()) {
            MediaEventConsumer.PlayerInfo var5 = (MediaEventConsumer.PlayerInfo)var4.nextElement();
            if (var5.getMediaType() == var1) {
               ++var2;
            }
         }

         return var2;
      }
   }

   public boolean isRegistered(BasicPlayer var1) {
      synchronized(this.registeredPlayers) {
         Enumeration var3 = this.registeredPlayers.elements();

         MediaEventConsumer.PlayerInfo var4;
         do {
            if (!var3.hasMoreElements()) {
               return false;
            }

            var4 = (MediaEventConsumer.PlayerInfo)var3.nextElement();
         } while(var4.getPlayer() != var1);

         return true;
      }
   }

   public void serializeEvent(int var1, int var2, long var3) {
      int var5 = -1;
      if (var3 != -1L) {
         var5 = (int)(var3 / 1000L);
      }

      synchronized(nativeLock) {
         nSendEvent(var1, var2, var5);
      }
   }

   private void broadcastEvent(byte var1, String var2) {
      Enumeration var3 = this.registeredPlayers.elements();

      while(var3.hasMoreElements()) {
         MediaEventConsumer.PlayerInfo var4 = (MediaEventConsumer.PlayerInfo)var3.nextElement();
         if (var4.getMediaType() == var1) {
            BasicPlayer var5 = var4.getPlayer();

            try {
               Object var6 = var2 == "volumeChanged" ? var5.getControl("VolumeControl") : var5.getDeviceName();
               if (var6 != null) {
                  var5.dispatchEvent(var2, var6);
               }
            } catch (IllegalStateException var7) {
            }
         }
      }

   }

   private static native int nReadEventInfo(int var0, int var1);

   private static native void nInitSessionManager();

   private static native int nGenerateSessionID();

   private static native void nCreateSession(int var0);

   private static native void nDestroySession(int var0);

   private static native void nSendEvent(int var0, int var1, int var2);

   static {
      synchronized(nativeLock) {
         nInitSessionManager();
      }
   }

   class PlayerInfo {
      private BasicPlayer p;
      private byte mediaType;

      PlayerInfo(BasicPlayer var2, byte var3) {
         this.p = var2;
         this.mediaType = var3;
      }

      BasicPlayer getPlayer() {
         return this.p;
      }

      byte getMediaType() {
         return this.mediaType;
      }
   }
}
