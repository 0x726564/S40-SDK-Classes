package com.nokia.mid.impl.isa.amms;

import com.nokia.mid.impl.isa.mmedia.MediaEventConsumer;

public class AdvancedMediaEventConsumer extends MediaEventConsumer {
   public static final int FIRST_JSR234_EVENT = 101;
   public static final int EVENT_PROCESSING_ABORTED = 101;
   public static final int EVENT_PROCESSING_COMPLETED = 102;
   public static final int EVENT_PROCESSING_ERROR = 103;
   public static final int EVENT_PROCESSING_STARTED = 104;
   public static final int EVENT_PROCESSING_STOPPED = 105;
   public static final int EVENT_PROCESSING_REALIZED = 106;
   public static final int LAST_JSR234_EVENT = 106;

   protected void processEvent(int category, int sessionId, int eventId, long eventTime) {
      if (eventId < 101) {
         super.processEvent(category, sessionId, eventId, eventTime);
      } else {
         switch(eventId) {
         case 101:
         case 102:
         case 103:
         case 104:
         case 105:
         case 106:
         }
      }

   }
}
