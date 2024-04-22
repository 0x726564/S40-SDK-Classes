package com.nokia.mid.impl.isa.io.protocol.external.cbs;

import com.nokia.mid.impl.isa.io.protocol.external.MessageEventConsumer;
import com.nokia.mid.impl.isa.util.SharedObjects;
import com.nokia.mid.impl.isa.wireless.messaging.SMSBinaryMessage;
import com.nokia.mid.impl.isa.wireless.messaging.SMSTextMessage;
import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.IOException;
import java.io.InterruptedIOException;
import javax.microedition.io.Connection;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;

public class Protocol implements MessageConnection, ConnectionBaseInterface {
   private static final String PROTOCOL = "cbs:";
   private static final String CBS_PREFIX = "cbs://:";
   private static final int MAX_PORT_NUMBER = 65535;
   private static MessageEventConsumer _event_cons = MessageEventConsumer.instance();
   private static final Object openLock;
   private static final Object closeLock;
   private int portNum;
   private int conId = 0;
   private boolean open = false;

   public Connection openPrim(String name, int mode, boolean timeouts) throws IOException {
      if (CBSCheckOpenPermissions() && this.IsCBSAllowed()) {
         if (mode != 1 && mode != 3) {
            throw new IllegalArgumentException("Illegal mode");
         } else if (name == null) {
            throw new IllegalArgumentException("Invalid address");
         } else {
            this.portNum = getPort("cbs:" + name);
            synchronized(openLock) {
               this.conId = this.CBSOpen(this.portNum);
            }

            this.open = true;
            return this;
         }
      } else {
         throw new SecurityException("Not allowed to open the connection");
      }
   }

   public void close() throws IOException {
      if (this.open) {
         _event_cons.setConnectionListener(this.conId, (MessageListener)null, this);
         synchronized(closeLock) {
            this.CBSClose(this.conId);
         }

         this.open = false;
      }

   }

   public Message newMessage(String type) {
      return this.newMessage(type, (String)null);
   }

   public Message newMessage(String type, String address) {
      Object var3;
      if (type.equals("binary")) {
         var3 = new SMSBinaryMessage();
      } else {
         if (!type.equals("text")) {
            throw new IllegalArgumentException("Invalid message type");
         }

         var3 = new SMSTextMessage();
      }

      return (Message)var3;
   }

   public int numberOfSegments(Message msg) {
      return 0;
   }

   public Message receive() throws IOException, InterruptedIOException {
      Message msg = null;
      if (this.open) {
         if (CBSCheckReceivePermissions()) {
            msg = this.CBSReceive(this.conId, this.portNum);
            return msg;
         } else {
            throw new SecurityException("not allowed to receive");
         }
      } else {
         throw new IOException("The connection has been closed");
      }
   }

   public void send(Message msg) throws IOException, InterruptedIOException {
      throw new IOException("Sending messages not supported by CBS");
   }

   public void setMessageListener(MessageListener l) throws IOException {
      if (CBSCheckReceivePermissions()) {
         if (this.open) {
            _event_cons.setConnectionListener(this.conId, l, this);
         } else {
            throw new IOException("The connection has been closed");
         }
      } else {
         throw new SecurityException("No permissions to receive");
      }
   }

   private static int getPort(String url) {
      int port = 0;
      if (url != null && url.startsWith("cbs://:")) {
         String s_port = "";

         try {
            s_port = url.substring("cbs://:".length());
         } catch (IndexOutOfBoundsException var4) {
            throw new IllegalArgumentException("malformed address");
         }

         if (s_port != null) {
            port = validatePort(s_port);
         }

         return port;
      } else {
         throw new IllegalArgumentException("malformed address");
      }
   }

   private static int validatePort(String s_port) {
      int port;
      try {
         port = Integer.parseInt(s_port);
      } catch (NumberFormatException var3) {
         throw new IllegalArgumentException("Port number format error");
      }

      if (port >= 0 && port <= 65535) {
         return port;
      } else {
         throw new IllegalArgumentException("Illegal port value");
      }
   }

   private static native void CBSStartUp();

   private native int CBSOpen(int var1);

   private static native boolean CBSCheckOpenPermissions();

   private native boolean IsCBSAllowed();

   private static native boolean CBSCheckReceivePermissions();

   private native void CBSClose(int var1);

   private native Message CBSReceive(int var1, int var2);

   static {
      CBSStartUp();
      openLock = SharedObjects.getLock("javax.wireless.messaging.openLock");
      closeLock = SharedObjects.getLock("javax.wireless.messaging.closeLock");
   }
}
