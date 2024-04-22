package com.nokia.mid.impl.isa.mmedia;

import com.nokia.mid.impl.isa.ui.EventConsumer;
import com.nokia.mid.impl.isa.ui.EventProducer;
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
   private static final String PRIVATE_EVENT_USER_REQUESTED_STOP = "com.nokia.mid.player.StopRequest";
   protected final Hashtable registeredPlayers = new Hashtable();
   private static final long MICROSECS_PER_MILLISEC = 1000L;
   private static MediaEventConsumer instance;
   private static final Object nativeLock = SharedObjects.getLock("com.nokia.mid.impl.isa.mmedia.MediaEventConsumer");

   protected MediaEventConsumer() {
   }

   public static synchronized MediaEventConsumer getInstance() {
      if (instance == null) {
         try {
            instance = (MediaEventConsumer)Class.forName(nGetMediaConsumer()).newInstance();
         } catch (Exception var1) {
            throw new RuntimeException("Consumer can't load.");
         }

         EventProducer evtProd = InitJALM.s_getEventProducer();
         evtProd.attachEventConsumer(6, instance);
      }

      return instance;
   }

   public synchronized int register(byte mediaType, BasicPlayer player) {
      int sessionId;
      synchronized(nativeLock) {
         sessionId = nGenerateSessionID();
         nCreateSession(sessionId);
      }

      this.registeredPlayers.put(new Integer(sessionId), new MediaEventConsumer.PlayerInfo(player, mediaType));
      return sessionId;
   }

   public synchronized void unRegister(int sessionId) {
      synchronized(nativeLock) {
         nDestroySession(sessionId);
      }

      this.registeredPlayers.remove(new Integer(sessionId));
   }

   public synchronized void consumeEvent(int category, int sessionId, int eventId) {
      long eventTime;
      synchronized(nativeLock) {
         eventTime = (long)nReadEventInfo(sessionId, eventId);
      }

      if (eventTime != -1L) {
         eventTime *= 1000L;
      }

      this.processEvent(category, sessionId, eventId, eventTime);
   }

   protected void processEvent(int category, int sessionId, int eventId, long eventTime) {
      Long rval = new Long(eventTime);
      if (sessionId < 0) {
         byte var12;
         switch(sessionId) {
         case -3:
            var12 = 2;
            break;
         case -2:
            var12 = 0;
            break;
         case -1:
            var12 = 1;
            break;
         default:
            throw new RuntimeException("Bad broadcast session id.");
         }

         String var13;
         switch(eventId) {
         case 2:
            var13 = "deviceAvailable";
            break;
         case 3:
            var13 = "deviceUnavailable";
            break;
         case 10:
            var13 = "volumeChanged";
            break;
         default:
            throw new RuntimeException("Bad broadcast event id.");
         }

         this.broadcastEvent(var12, var13);
      } else {
         MediaEventConsumer.PlayerInfo pi = (MediaEventConsumer.PlayerInfo)this.registeredPlayers.get(new Integer(sessionId));
         if (pi != null) {
            BasicPlayer player = pi.getPlayer();
            String deviceName = player.getDeviceName();

            try {
               switch(eventId) {
               case 1:
                  this.unRegister(player.playerId);
                  player.dispatchEvent("closed", (Object)null);
                  break;
               case 2:
                  player.dispatchEvent("deviceAvailable", deviceName);
                  break;
               case 3:
                  player.dispatchEvent("deviceUnavailable", deviceName);
                  break;
               case 4:
                  player.dispatchEvent("endOfMedia", rval);
                  break;
               case 5:
                  player.dispatchEvent("deviceUnavailable", deviceName);
                  player.dispatchEvent("error", "Player Error");
                  break;
               case 6:
                  player.dispatchEvent("sizeChanged", player.getControl("VideoControl"));
                  break;
               case 7:
                  player.dispatchEvent("started", rval);
                  break;
               case 8:
                  player.dispatchEvent("stopped", rval);
                  break;
               case 9:
                  player.dispatchEvent("stoppedAtTime", rval);
                  break;
               case 10:
                  player.dispatchEvent("volumeChanged", player.getControl("VolumeControl"));
                  break;
               case 11:
                  player.dispatchEvent("recordStarted", rval);
                  break;
               case 12:
                  player.dispatchEvent("recordStopped", rval);
                  break;
               case 13:
                  ((RecordControl)player.getControl("RecordControl")).commit();
                  break;
               case 14:
                  player.dispatchEvent("recordError", "Record Error");
                  break;
               case 15:
                  player.dispatchEvent("bufferingStarted", rval);
                  break;
               case 16:
                  player.dispatchEvent("bufferingStopped", rval);
                  break;
               case 101:
                  player.dispatchEvent("com.nokia.mid.player.StopRequest", rval);
               }
            } catch (Throwable var11) {
            }
         }

      }
   }

   public int getNumPlayers(byte mediaType) {
      int res = 0;
      synchronized(this.registeredPlayers) {
         Enumeration e = this.registeredPlayers.elements();

         while(e.hasMoreElements()) {
            MediaEventConsumer.PlayerInfo pi = (MediaEventConsumer.PlayerInfo)e.nextElement();
            if (pi.getMediaType() == mediaType) {
               ++res;
            }
         }

         return res;
      }
   }

   public boolean isRegistered(BasicPlayer player) {
      synchronized(this.registeredPlayers) {
         Enumeration e = this.registeredPlayers.elements();

         MediaEventConsumer.PlayerInfo pi;
         do {
            if (!e.hasMoreElements()) {
               return false;
            }

            pi = (MediaEventConsumer.PlayerInfo)e.nextElement();
         } while(pi.getPlayer() != player);

         return true;
      }
   }

   public void serializeEvent(int sessionId, int eventId, long time) {
      int timeInMillis = -1;
      if (time != -1L) {
         timeInMillis = (int)(time / 1000L);
      }

      synchronized(nativeLock) {
         nSendEvent(sessionId, eventId, timeInMillis);
      }
   }

   private void broadcastEvent(byte mediaType, String event) {
      Enumeration e = this.registeredPlayers.elements();

      while(e.hasMoreElements()) {
         MediaEventConsumer.PlayerInfo pi = (MediaEventConsumer.PlayerInfo)e.nextElement();
         if (pi.getMediaType() == mediaType) {
            BasicPlayer player = pi.getPlayer();

            try {
               Object eventData = event == "volumeChanged" ? player.getControl("VolumeControl") : player.getDeviceName();
               if (eventData != null) {
                  player.dispatchEvent(event, eventData);
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

   private static native String nGetMediaConsumer();

   static {
      synchronized(nativeLock) {
         nInitSessionManager();
      }
   }

   class PlayerInfo {
      private BasicPlayer p;
      private byte mediaType;

      PlayerInfo(BasicPlayer p, byte mediaType) {
         this.p = p;
         this.mediaType = mediaType;
      }

      BasicPlayer getPlayer() {
         return this.p;
      }

      byte getMediaType() {
         return this.mediaType;
      }
   }
}
