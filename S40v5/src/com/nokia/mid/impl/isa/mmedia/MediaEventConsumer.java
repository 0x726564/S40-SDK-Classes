package com.nokia.mid.impl.isa.mmedia;

import com.nokia.mid.impl.isa.ui.EventConsumer;
import com.nokia.mid.impl.isa.ui.InitJALM;
import com.nokia.mid.impl.isa.util.SharedObjects;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.media.control.RecordControl;

public class MediaEventConsumer implements EventConsumer {
   public static final int BROADCAST_TO_ALL_RADIO_SESSIONS = -1;
   public static final int BROADCAST_TO_ALL_AUDIO_SESSIONS = -2;
   public static final int BROADCAST_TO_ALL_WRAPPER_SESSIONS = -3;
   public static final byte EVENT_CLOSED = 1;
   public static final byte EVENT_DEVICE_AVAILABLE = 2;
   public static final byte EVENT_DEVICE_UNAVAILABLE = 3;
   public static final byte EVENT_END_OF_MEDIA = 4;
   public static final byte EVENT_ERROR = 5;
   public static final byte EVENT_SIZE_CHANGED = 6;
   public static final byte EVENT_STARTED = 7;
   public static final byte EVENT_STOPPED = 8;
   public static final byte EVENT_STOPPED_AT_TIME = 9;
   public static final byte EVENT_VOLUME_CHANGED = 10;
   public static final byte EVENT_RECORD_STARTED = 11;
   public static final byte EVENT_RECORD_STOPPED = 12;
   public static final byte EVENT_RECORD_COMMITED = 13;
   public static final byte EVENT_RECORD_ERROR = 14;
   public static final byte EVENT_BUFFERING_STARTED = 15;
   public static final byte EVENT_BUFFERING_STOPPED = 16;
   public static final byte EVENT_USER_REQUESTED_STOP = 101;
   protected final Hashtable registeredPlayers = new Hashtable();
   private static MediaEventConsumer kc;
   private static final Object aG;

   protected MediaEventConsumer() {
   }

   public static synchronized MediaEventConsumer getInstance() {
      if (kc == null) {
         try {
            kc = (MediaEventConsumer)Class.forName(nGetMediaConsumer()).newInstance();
         } catch (Exception var1) {
            throw new RuntimeException("Consumer can't load.");
         }

         InitJALM.s_getEventProducer().attachEventConsumer(6, kc);
      }

      return kc;
   }

   public synchronized int register(byte var1, BasicPlayer var2) {
      int var3;
      synchronized(aG) {
         nCreateSession(var3 = nGenerateSessionID());
      }

      this.registeredPlayers.put(new Integer(var3), new MediaEventConsumer.PlayerInfo(this, var2, var1));
      return var3;
   }

   public synchronized void unRegister(int var1) {
      synchronized(aG) {
         nDestroySession(var1);
      }

      this.registeredPlayers.remove(new Integer(var1));
   }

   public synchronized void consumeEvent(int var1, int var2, int var3) {
      long var4;
      synchronized(aG) {
         var4 = (long)nReadEventInfo(var2, var3);
      }

      if (var4 != -1L) {
         var4 *= 1000L;
      }

      this.processEvent(var1, var2, var3, var4);
   }

   protected void processEvent(int var1, int var2, int var3, long var4) {
      Long var11 = new Long(var4);
      BasicPlayer var7;
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

         byte var10001 = var14;
         String var16 = var15;
         byte var5 = var10001;
         Enumeration var13 = this.registeredPlayers.elements();

         while(var13.hasMoreElements()) {
            MediaEventConsumer.PlayerInfo var17;
            if ((var17 = (MediaEventConsumer.PlayerInfo)var13.nextElement()).getMediaType() == var5) {
               var7 = var17.getPlayer();

               try {
                  Object var10000 = var16 == "volumeChanged" ? var7.getControl("VolumeControl") : var7.getDeviceName();
                  Object var8 = var10000;
                  if (var10000 != null) {
                     var7.dispatchEvent(var16, var8);
                  }
               } catch (IllegalStateException var9) {
               }
            }
         }

      } else {
         MediaEventConsumer.PlayerInfo var6;
         if ((var6 = (MediaEventConsumer.PlayerInfo)this.registeredPlayers.get(new Integer(var2))) != null) {
            String var12 = (var7 = var6.getPlayer()).getDeviceName();

            try {
               switch(var3) {
               case 1:
                  this.unRegister(var7.playerId);
                  var7.dispatchEvent("closed", (Object)null);
                  break;
               case 2:
                  var7.dispatchEvent("deviceAvailable", var12);
                  break;
               case 3:
                  var7.dispatchEvent("deviceUnavailable", var12);
                  break;
               case 4:
                  var7.dispatchEvent("endOfMedia", var11);
                  break;
               case 5:
                  var7.dispatchEvent("deviceUnavailable", var12);
                  var7.dispatchEvent("error", "Player Error");
                  break;
               case 6:
                  var7.dispatchEvent("sizeChanged", var7.getControl("VideoControl"));
                  break;
               case 7:
                  var7.dispatchEvent("started", var11);
                  break;
               case 8:
                  var7.dispatchEvent("stopped", var11);
                  break;
               case 9:
                  var7.dispatchEvent("stoppedAtTime", var11);
                  break;
               case 10:
                  var7.dispatchEvent("volumeChanged", var7.getControl("VolumeControl"));
                  break;
               case 11:
                  var7.dispatchEvent("recordStarted", var11);
                  break;
               case 12:
                  var7.dispatchEvent("recordStopped", var11);
                  break;
               case 13:
                  ((RecordControl)var7.getControl("RecordControl")).commit();
                  break;
               case 14:
                  var7.dispatchEvent("recordError", "Record Error");
                  break;
               case 15:
                  var7.dispatchEvent("bufferingStarted", var11);
                  break;
               case 16:
                  var7.dispatchEvent("bufferingStopped", var11);
                  break;
               case 101:
                  var7.dispatchEvent("com.nokia.mid.player.StopRequest", var11);
               default:
                  return;
               }
            } catch (Throwable var10) {
            }
         }

      }
   }

   public int getNumPlayers(byte var1) {
      int var2 = 0;
      synchronized(this.registeredPlayers) {
         Enumeration var6 = this.registeredPlayers.elements();

         while(var6.hasMoreElements()) {
            if (((MediaEventConsumer.PlayerInfo)var6.nextElement()).getMediaType() == var1) {
               ++var2;
            }
         }

         return var2;
      }
   }

   public boolean isRegistered(BasicPlayer var1) {
      synchronized(this.registeredPlayers) {
         Enumeration var5 = this.registeredPlayers.elements();

         do {
            if (!var5.hasMoreElements()) {
               return false;
            }
         } while(((MediaEventConsumer.PlayerInfo)var5.nextElement()).getPlayer() != var1);

         return true;
      }
   }

   public void serializeEvent(int var1, int var2, long var3) {
      int var6 = -1;
      if (var3 != -1L) {
         var6 = (int)(var3 / 1000L);
      }

      synchronized(aG) {
         nSendEvent(var1, var2, var6);
      }
   }

   private static native int nReadEventInfo(int var0, int var1);

   private static native void nInitSessionManager();

   private static native int nGenerateSessionID();

   private static native void nCreateSession(int var0);

   private static native void nDestroySession(int var0);

   private static native void nSendEvent(int var0, int var1, int var2);

   private static native String nGetMediaConsumer();

   static {
      synchronized(aG = SharedObjects.getLock("com.nokia.mid.impl.isa.mmedia.MediaEventConsumer")) {
         nInitSessionManager();
      }
   }

   class PlayerInfo {
      private BasicPlayer ak;
      private byte mediaType;

      PlayerInfo(MediaEventConsumer var1, BasicPlayer var2, byte var3) {
         this.ak = var2;
         this.mediaType = var3;
      }

      BasicPlayer getPlayer() {
         return this.ak;
      }

      byte getMediaType() {
         return this.mediaType;
      }
   }
}
