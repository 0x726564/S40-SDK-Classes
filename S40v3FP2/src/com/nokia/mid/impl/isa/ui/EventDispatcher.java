package com.nokia.mid.impl.isa.ui;

final class EventDispatcher extends Thread implements EventProducer {
   private static EventDispatcher s_eventDispatcher = null;
   private Object mStateMutex;
   private ExitManager exitManager;
   private EventConsumer[] eventConsumerTable;
   private static int category;
   private static int type;
   private static int param;

   static EventDispatcher s_createEventDispatcher(Object var0, ExitManager var1) {
      if (s_eventDispatcher == null) {
         s_eventDispatcher = new EventDispatcher(var0, var1);
      }

      return s_eventDispatcher;
   }

   static EventProducer s_getEventProducer() {
      return s_eventDispatcher;
   }

   static void s_destroyEventDispatcher() {
      s_eventDispatcher = null;
   }

   private EventDispatcher(Object var1, ExitManager var2) {
      this.mStateMutex = var1;
      this.exitManager = var2;
      this.eventConsumerTable = new EventConsumer[11];
   }

   public final void run() {
      while(true) {
         checkEvent();
         getEvent();
         synchronized(this.mStateMutex) {
            try {
               EventConsumer var2 = this.eventConsumerTable[category];
               if (var2 != null) {
                  var2.consumeEvent(category, type, param);
               }
            } catch (Throwable var4) {
               this.exitManager.exitOnError(var4);
            }
         }
      }
   }

   public final void attachEventConsumer(int var1, EventConsumer var2) {
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         synchronized(this.mStateMutex) {
            try {
               this.eventConsumerTable[var1] = var2;
            } catch (ArrayIndexOutOfBoundsException var6) {
               throw new IllegalArgumentException();
            }

         }
      }
   }

   public final void detachEventConsumer(int var1, EventConsumer var2) {
      synchronized(this.mStateMutex) {
         try {
            this.eventConsumerTable[var1] = null;
         } catch (ArrayIndexOutOfBoundsException var6) {
            throw new IllegalArgumentException();
         }

      }
   }

   private static final native void getEvent();

   private static final native void checkEvent();
}
