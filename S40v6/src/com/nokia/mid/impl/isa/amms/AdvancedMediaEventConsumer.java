package com.nokia.mid.impl.isa.amms;

import com.nokia.mid.impl.isa.mmedia.BasicPlayer;
import com.nokia.mid.impl.isa.mmedia.MediaEventConsumer;

public class AdvancedMediaEventConsumer extends MediaEventConsumer {
   public static final int FIRST_JSR234_EVENT = 101;
   public static final int EVENT_PROCESSING_ABORTED = 101;
   public static final int EVENT_PROCESSING_COMPLETED = 102;
   public static final int EVENT_PROCESSING_ERROR = 103;
   public static final int EVENT_PROCESSING_STARTED = 104;
   public static final int EVENT_PROCESSING_STOPPED = 105;
   public static final int EVENT_PROCESSING_REALIZED = 106;
   public static final int EVENT_STORAGE_ERROR = 107;
   public static final int EVENT_SHOOTING_STOPPED = 108;
   public static final int EVENT_WAITING_UNFREEZE = 109;
   public static final int EVENT_FILE_SAVED_OK = 201;
   public static final int EVENT_FILE_SAVED_ERROR = 202;
   public static final int LAST_JSR234_EVENT = 202;
   private static AdvancedMediaEventHelper camHelper;

   protected void processEvent(int category, int sessionId, int eventId, long eventTime) {
      if (eventId < 101) {
         super.processEvent(category, sessionId, eventId, eventTime);
      } else {
         BasicPlayer player = this.getPlayerFromSessionID(sessionId);
         if (player != null) {
            switch(eventId) {
            case 107:
            case 108:
            case 109:
            case 201:
            case 202:
               if (camHelper == null) {
                  throw new RuntimeException("Configuration Error: CAMERA");
               }

               camHelper.processEvent(this, player, eventId, eventTime);
               break;
            default:
               throw new RuntimeException("AMMS Event Error (bad event)");
            }
         }
      }

   }

   static {
      try {
         Class camClazz = Class.forName("com.nokia.mid.impl.isa.amms.video.CameraMediaEventHelper");
         camHelper = (AdvancedMediaEventHelper)camClazz.newInstance();
      } catch (Exception var1) {
      }

   }
}
