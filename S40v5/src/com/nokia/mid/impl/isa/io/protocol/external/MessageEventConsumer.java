package com.nokia.mid.impl.isa.io.protocol.external;

import com.nokia.mid.impl.isa.ui.EventConsumer;
import com.nokia.mid.impl.isa.ui.EventProducer;
import com.nokia.mid.impl.isa.ui.InitJALM;
import java.util.Hashtable;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;

public final class MessageEventConsumer implements EventConsumer {
   private static final EventProducer ap = InitJALM.s_getEventProducer();
   private static MessageEventConsumer aq = null;
   private static Hashtable ar = new Hashtable();
   private static Hashtable as = new Hashtable();

   protected MessageEventConsumer() {
      ap.attachEventConsumer(5, this);
   }

   public static MessageEventConsumer instance() {
      if (aq == null) {
         aq = new MessageEventConsumer();
      }

      return aq;
   }

   public final void consumeEvent(int var1, int var2, int var3) {
      if (var1 == 5) {
         Integer var4 = new Integer(var3);
         MessageListener var6;
         if (ar.containsKey(var4) && (var6 = (MessageListener)ar.get(var4)) != null) {
            MessageConnection var5 = (MessageConnection)as.get(var4);
            var6.notifyIncomingMessage(var5);
         }
      }

   }

   public final synchronized void setConnectionListener(int var1, MessageListener var2, MessageConnection var3) {
      Integer var4 = new Integer(var1);
      if (var2 == null) {
         if (ar.remove(var4) != null) {
            this.getNotified(var1, false);
         }

         as.remove(var4);
      } else {
         if (ar.put(var4, var2) == null) {
            this.getNotified(var1, true);
         }

         as.put(var4, var3);
      }
   }

   private native void getNotified(int var1, boolean var2);
}
