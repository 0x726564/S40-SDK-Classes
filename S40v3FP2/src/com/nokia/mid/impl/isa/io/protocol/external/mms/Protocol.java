package com.nokia.mid.impl.isa.io.protocol.external.mms;

import com.nokia.mid.impl.isa.io.protocol.external.MessageEventConsumer;
import com.nokia.mid.impl.isa.wireless.messaging.MMSMultipartMessage;
import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.IOException;
import java.io.InterruptedIOException;
import javax.microedition.io.Connection;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;

public class Protocol implements MessageConnection, ConnectionBaseInterface {
   private static final String PROTOCOL = "mms:";
   private static final String MMSC_URL = System.getProperty("wireless.messaging.mms.mmsc");
   private static final int RETRY_DELAY = 3000;
   private static final int MAX_FETCH_ATTEMPTS = 3;
   public static final int MAX_MULTIPART_MESSAGE_SIZE;
   private static int MAX_MULTIPART_MESSAGE_SIZE_TEMP;
   private static MessageEventConsumer _event_cons;
   private static final Object sendPermissionLock;
   private static final Object MESLock;
   private int ucid = 0;
   private String connectionUrl;
   private boolean open = false;
   private boolean isServer;

   public Connection openPrim(String var1, int var2, boolean var3) throws IOException {
      if (!MMSCheckOpenPermissions()) {
         throw new SecurityException("Not allowed to open the connection");
      } else if (MMSC_URL == null) {
         throw new IOException("MMSC address not configured");
      } else if (var1 != null && MMSAddress.validateUrl(var1, false)) {
         if (var2 != 1 && var2 != 2 && var2 != 3) {
            throw new IllegalArgumentException("Invalid mode");
         } else {
            if (MMSAddress.getConnectionMode(var1)) {
               this.isServer = true;
               this.connectionUrl = MMSAddress.getAppIdFromUrl(var1);
               this.ucid = this.MMSOpen(this.connectionUrl);
            } else {
               if (var2 == 1) {
                  throw new IllegalArgumentException("Illegal mode");
               }

               this.isServer = false;
               this.connectionUrl = MMSAddress.getAddressFromUrl(var1);
               this.ucid = this.MMSOpen((String)null);
            }

            this.open = true;
            return this;
         }
      } else {
         throw new IllegalArgumentException("Invalid url");
      }
   }

   public void close() throws IOException {
      if (this.open) {
         if (this.isServer) {
            _event_cons.setConnectionListener(this.ucid, (MessageListener)null, this);
         }

         this.MMSClose(this.ucid);
         this.open = false;
      }

   }

   public Message newMessage(String var1) {
      return this.newMessage(var1, "mms://" + this.connectionUrl);
   }

   public Message newMessage(String var1, String var2) {
      if (var1.equals("multipart")) {
         MMSMultipartMessage var3 = new MMSMultipartMessage();
         if (!this.isServer) {
            var3.setAddress(var2);
         }

         return var3;
      } else {
         throw new IllegalArgumentException("Invalid message type");
      }
   }

   public int numberOfSegments(Message var1) {
      return 1;
   }

   public Message receive() throws IOException, InterruptedIOException {
      if (!this.open) {
         throw new IOException("The connection has been closed");
      } else if (!this.isServer) {
         throw new IOException("Cannot receive in client mode");
      } else if (!MMSCheckReceivePermissions()) {
         throw new SecurityException("Not allowed to receive");
      } else {
         String var1 = MMSReceive(this.ucid, this.connectionUrl);
         if (var1 != null && var1.length() != 0) {
            MMSMultipartMessage var2 = null;
            synchronized(MESLock) {
               int var4 = 3;

               while(true) {
                  try {
                     var2 = fetchPDU(this.ucid, var1);
                     if (var2 != null) {
                        var2.postReceiving();
                     }
                     break;
                  } catch (InterruptedIOException var9) {
                     break;
                  } catch (IOException var10) {
                     if (this.open) {
                        try {
                           Thread.sleep(3000L);
                        } catch (InterruptedException var8) {
                        }

                        --var4;
                     }

                     if (!this.open || var4 <= 0) {
                        break;
                     }
                  }
               }
            }

            if (!this.open) {
               throw new InterruptedIOException("Connection closed while retrieving");
            } else if (var2 == null) {
               throw new IOException("Error while retrieving message");
            } else {
               return var2;
            }
         } else {
            throw new IOException("The URL in notification is not valid");
         }
      }
   }

   public void send(Message var1) throws IOException, InterruptedIOException {
      if (!this.open) {
         throw new IOException("The connection has been closed");
      } else if (var1 == null) {
         throw new NullPointerException("null parameter");
      } else if (!(var1 instanceof MMSMultipartMessage)) {
         throw new IllegalArgumentException("Invalid message");
      } else {
         MMSMultipartMessage var2 = (MMSMultipartMessage)var1;
         int var3 = var2.getSize();
         if (var3 > MAX_MULTIPART_MESSAGE_SIZE) {
            throw new IllegalArgumentException("Maximum length exceeded");
         } else {
            int var4;
            if (var2.getSubject() == null) {
               var4 = 0;
            } else {
               var4 = var2.getSubject().length();
            }

            synchronized(sendPermissionLock) {
               String[][] var6 = var2.getFormattedAddresses();
               if (var6 == null) {
                  throw new IllegalArgumentException("No recipient address has been set");
               }

               if (!MMSCheckSendPermissions(var3 + var4, var6)) {
                  throw new SecurityException("Not allowed to send a MMS");
               }
            }

            if (this.open) {
               synchronized(MESLock) {
                  var2.encodeAndSend(this.isServer ? this.connectionUrl : null, this.ucid);
               }
            } else {
               throw new IOException("The connection has been closed");
            }
         }
      }
   }

   public void setMessageListener(MessageListener var1) throws IOException {
      if (!this.open) {
         throw new IOException("The connection has been closed");
      } else if (!this.isServer) {
         throw new IOException("Cannot listen in client mode");
      } else if (!MMSCheckReceivePermissions()) {
         throw new SecurityException("Not allowed to receive messages");
      } else {
         _event_cons.setConnectionListener(this.ucid, var1, this);
      }
   }

   private native int MMSOpen(String var1);

   private static native boolean MMSCheckOpenPermissions();

   private native void MMSClose(int var1);

   private static native String MMSReceive(int var0, String var1);

   private static native boolean MMSCheckSendPermissions(int var0, String[][] var1);

   private static native boolean MMSCheckReceivePermissions();

   private static native void nativeInit();

   private static native MMSMultipartMessage fetchPDU(int var0, String var1) throws IOException;

   static {
      try {
         Class.forName("com.nokia.mid.impl.isa.io.protocol.external.sms.Protocol");
      } catch (ClassNotFoundException var1) {
      }

      nativeInit();
      MAX_MULTIPART_MESSAGE_SIZE = MAX_MULTIPART_MESSAGE_SIZE_TEMP;
      _event_cons = MessageEventConsumer.instance();
      sendPermissionLock = com.nokia.mid.impl.isa.io.protocol.external.sms.Protocol.permissionLock;
      MESLock = new Object();
   }
}
