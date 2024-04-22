package com.nokia.mid.impl.isa.ui;

final class EventDispatcher extends Thread implements EventProducer {
   private static EventDispatcher s_eventDispatcher = null;
   private Object mStateMutex;
   private ExitManager exitManager;
   private EventConsumer[] eventConsumerTable;
   private static int category;
   private static int type;
   private static int param;

   static EventDispatcher s_createEventDispatcher(Object mutex, ExitManager exitManager) {
      if (s_eventDispatcher == null) {
         s_eventDispatcher = new EventDispatcher(mutex, exitManager);
      }

      return s_eventDispatcher;
   }

   static EventProducer s_getEventProducer() {
      return s_eventDispatcher;
   }

   static void s_destroyEventDispatcher() {
      s_eventDispatcher = null;
   }

   private EventDispatcher(Object mutex, ExitManager exitManager) {
      this.mStateMutex = mutex;
      this.exitManager = exitManager;
      this.eventConsumerTable = new EventConsumer[12];
   }

   public final void run() {
      while(true) {
         checkEvent();
         getEvent();
         synchronized(this.mStateMutex) {
            try {
               EventConsumer consumer = this.eventConsumerTable[category];
               if (consumer != null) {
                  consumer.consumeEvent(category, type, param);
               }
            } catch (Throwable var4) {
               this.exitManager.exitOnError(var4);
            }
         }
      }
   }

   public final void attachEventConsumer(int category, EventConsumer consumer) {
      if (consumer == null) {
         throw new NullPointerException();
      } else {
         synchronized(this.mStateMutex) {
            try {
               this.eventConsumerTable[category] = consumer;
            } catch (ArrayIndexOutOfBoundsException var6) {
               throw new IllegalArgumentException();
            }

         }
      }
   }

   public final void detachEventConsumer(int category, EventConsumer consumer) {
      synchronized(this.mStateMutex) {
         try {
            this.eventConsumerTable[category] = null;
         } catch (ArrayIndexOutOfBoundsException var6) {
            throw new IllegalArgumentException();
         }

      }
   }

   private static final native void getEvent();

   private static final native void checkEvent();
}
