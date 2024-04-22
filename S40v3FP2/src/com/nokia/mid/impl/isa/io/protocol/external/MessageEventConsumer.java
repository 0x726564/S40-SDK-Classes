package com.nokia.mid.impl.isa.io.protocol.external;

import com.nokia.mid.impl.isa.ui.EventConsumer;
import com.nokia.mid.impl.isa.ui.EventProducer;
import com.nokia.mid.impl.isa.ui.InitJALM;
import java.util.Hashtable;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;

public final class MessageEventConsumer implements EventConsumer {
   private static final EventProducer _eventDispatcher = InitJALM.s_getEventProducer();
   private static MessageEventConsumer _instance = null;
   private static Hashtable listenerByUcid = new Hashtable();
   private static Hashtable connectionByUcid = new Hashtable();

   protected MessageEventConsumer() {
      _eventDispatcher.attachEventConsumer(5, this);
   }

   public static MessageEventConsumer instance() {
      if (_instance == null) {
         _instance = new MessageEventConsumer();
      }

      return _instance;
   }

   public void consumeEvent(int var1, int var2, int var3) {
      if (var1 == 5) {
         Integer var4 = new Integer(var3);
         if (listenerByUcid.containsKey(var4)) {
            MessageListener var5 = (MessageListener)listenerByUcid.get(var4);
            if (var5 != null) {
               MessageConnection var6 = (MessageConnection)connectionByUcid.get(var4);
               var5.notifyIncomingMessage(var6);
            }
         }
      }

   }

   public synchronized void setConnectionListener(int var1, MessageListener var2, MessageConnection var3) {
      Integer var4 = new Integer(var1);
      if (var2 == null) {
         if (listenerByUcid.remove(var4) != null) {
            this.getNotified(var1, false);
         }

         connectionByUcid.remove(var4);
      } else {
         if (listenerByUcid.put(var4, var2) == null) {
            this.getNotified(var1, true);
         }

         connectionByUcid.put(var4, var3);
      }

   }

   private native void getNotified(int var1, boolean var2);
}
