package com.nokia.mid.impl.isa.ui;

final class EventDispatcher extends Thread implements EventProducer {
   private static EventDispatcher a = null;
   private Object b;
   private ExitManager c;
   private EventConsumer[] d;

   static EventDispatcher a(Object var0, ExitManager var1) {
      if (a == null) {
         a = new EventDispatcher(var0, var1);
      }

      return a;
   }

   static EventProducer s_getEventProducer() {
      return a;
   }

   static void a() {
      a = null;
   }

   private EventDispatcher(Object var1, ExitManager var2) {
      this.b = var1;
      this.c = var2;
      this.d = new EventConsumer[11];
   }

   public final void run() {
      while(true) {
         checkEvent();
         getEvent();
         synchronized(this.b) {
            try {
               EventConsumer var2;
               if ((var2 = this.d[0]) != null) {
                  var2.consumeEvent(0, 0, 0);
               }
            } catch (Throwable var3) {
               this.c.exitOnError(var3);
            }
         }
      }
   }

   public final void attachEventConsumer(int var1, EventConsumer var2) {
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         synchronized(this.b) {
            try {
               this.d[var1] = var2;
            } catch (ArrayIndexOutOfBoundsException var4) {
               throw new IllegalArgumentException();
            }

         }
      }
   }

   public final void detachEventConsumer(int var1, EventConsumer var2) {
      synchronized(this.b) {
         try {
            this.d[var1] = null;
         } catch (ArrayIndexOutOfBoundsException var3) {
            throw new IllegalArgumentException();
         }

      }
   }

   private static final native void getEvent();

   private static final native void checkEvent();
}
