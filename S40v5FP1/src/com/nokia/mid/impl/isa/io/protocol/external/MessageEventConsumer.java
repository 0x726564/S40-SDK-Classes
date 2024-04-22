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

   public void consumeEvent(int category, int type, int param) {
      if (category == 5) {
         Integer connIdT = new Integer(param);
         if (listenerByUcid.containsKey(connIdT)) {
            MessageListener listener = (MessageListener)listenerByUcid.get(connIdT);
            if (listener != null) {
               MessageConnection connection = (MessageConnection)connectionByUcid.get(connIdT);
               listener.notifyIncomingMessage(connection);
            }
         }
      }

   }

   public synchronized void setConnectionListener(int connId, MessageListener listener, MessageConnection connection) {
      Integer connIdT = new Integer(connId);
      if (listener == null) {
         if (listenerByUcid.remove(connIdT) != null) {
            this.getNotified(connId, false);
         }

         connectionByUcid.remove(connIdT);
      } else {
         if (listenerByUcid.put(connIdT, listener) == null) {
            this.getNotified(connId, true);
         }

         connectionByUcid.put(connIdT, connection);
      }

   }

   private native void getNotified(int var1, boolean var2);
}
