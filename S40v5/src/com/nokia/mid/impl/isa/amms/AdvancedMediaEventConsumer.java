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

   protected void processEvent(int var1, int var2, int var3, long var4) {
      if (var3 < 101) {
         super.processEvent(var1, var2, var3, var4);
      }

   }
}
