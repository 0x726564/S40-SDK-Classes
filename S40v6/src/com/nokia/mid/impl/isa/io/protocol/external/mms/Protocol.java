package com.nokia.mid.impl.isa.io.protocol.external.mms;

import com.nokia.mid.impl.isa.io.protocol.external.MessageEventConsumer;
import com.nokia.mid.impl.isa.util.SharedObjects;
import com.nokia.mid.impl.isa.wireless.messaging.MMSMultipartMessage;
import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.IOException;
import java.io.InterruptedIOException;
import javax.microedition.io.Connection;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;

public class Protocol implements MessageConnection, ConnectionBaseInterface {
   private static final String MMSC_URL = System.getProperty("wireless.messaging.mms.mmsc");
   private static final int RETRY_DELAY = 3000;
   private static final int MAX_FETCH_ATTEMPTS = 3;
   private static final int MAX_RETRIEVALS_ATTEMPTS = 3;
   public static final int MAX_MULTIPART_MESSAGE_SIZE;
   private static int MAX_MULTIPART_MESSAGE_SIZE_TEMP;
   private static MessageEventConsumer _event_cons;
   private static final Object sendPermissionLock;
   private static final Object MESLock;
   private int ucid = 0;
   private String connectionUrl;
   private boolean open = false;
   private boolean isServer;

   public Connection openPrim(String url, int mode, boolean timeouts) throws IOException {
      if (!MMSCheckOpenPermissions()) {
         throw new SecurityException("Not allowed to open the connection");
      } else if (MMSC_URL == null) {
         throw new IOException("MMSC address not configured");
      } else if (url != null && MMSAddress.validateUrl(url, false)) {
         if (mode != 1 && mode != 2 && mode != 3) {
            throw new IllegalArgumentException("Invalid mode");
         } else {
            if (MMSAddress.getConnectionMode(url)) {
               this.isServer = true;
               this.connectionUrl = MMSAddress.getAppIdFromUrl(url);
               this.ucid = this.MMSOpen(this.connectionUrl);
            } else {
               if (mode == 1) {
                  throw new IllegalArgumentException("Illegal mode");
               }

               this.isServer = false;
               this.connectionUrl = MMSAddress.getAddressFromUrl(url);
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

   public Message newMessage(String type) {
      return this.newMessage(type, "mms://" + this.connectionUrl);
   }

   public Message newMessage(String type, String addr) {
      if (type.equals("multipart")) {
         Message msg = new MMSMultipartMessage();
         if (!this.isServer) {
            msg.setAddress(addr);
         }

         return msg;
      } else {
         throw new IllegalArgumentException("Invalid message type");
      }
   }

   public int numberOfSegments(Message msg) {
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
         String messageUrl = MMSReceive(this.ucid, this.connectionUrl);
         if (messageUrl != null && messageUrl.length() != 0) {
            MMSMultipartMessage msg = null;
            synchronized(MESLock) {
               int nbAttempts = 3;

               while(true) {
                  try {
                     msg = fetchPDU(this.ucid, messageUrl);
                     if (msg != null) {
                        msg.postReceiving();
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

                        --nbAttempts;
                     }

                     if (!this.open || nbAttempts <= 0) {
                        break;
                     }
                  }
               }
            }

            if (!this.open) {
               throw new InterruptedIOException("Connection closed while retrieving");
            } else if (msg == null) {
               throw new IOException("Error while retrieving message");
            } else {
               return msg;
            }
         } else {
            throw new IOException("The URL in notification is not valid");
         }
      }
   }

   public void send(Message msg) throws IOException, InterruptedIOException {
      if (!this.open) {
         throw new IOException("The connection has been closed");
      } else if (msg == null) {
         throw new NullPointerException("null parameter");
      } else if (!(msg instanceof MMSMultipartMessage)) {
         throw new IllegalArgumentException("Invalid message");
      } else {
         MMSMultipartMessage mms = (MMSMultipartMessage)msg;
         int mmsSize = mms.getSize();
         if (mmsSize > MAX_MULTIPART_MESSAGE_SIZE) {
            throw new IllegalArgumentException("Maximum length exceeded");
         } else {
            int subjLength;
            if (mms.getSubject() == null) {
               subjLength = 0;
            } else {
               subjLength = mms.getSubject().length();
            }

            synchronized(sendPermissionLock) {
               String[][] recipientsList = mms.getFormattedAddresses();
               if (recipientsList == null) {
                  throw new IllegalArgumentException("No recipient address has been set");
               }

               if (!MMSCheckSendPermissions(mmsSize + subjLength, recipientsList)) {
                  throw new SecurityException("Not allowed to send a MMS");
               }
            }

            if (this.open) {
               synchronized(MESLock) {
                  int nbAttempts = 3;

                  while(true) {
                     try {
                        mms.encodeAndSend(this.isServer ? this.connectionUrl : null, this.ucid);
                        break;
                     } catch (IOException var11) {
                        try {
                           Thread.sleep(3000L);
                        } catch (InterruptedException var10) {
                        }

                        --nbAttempts;
                        if (nbAttempts <= 0) {
                           break;
                        }
                     }
                  }

               }
            } else {
               throw new IOException("The connection has been closed");
            }
         }
      }
   }

   public void setMessageListener(MessageListener l) throws IOException {
      if (!this.open) {
         throw new IOException("The connection has been closed");
      } else if (!this.isServer) {
         throw new IOException("Cannot listen in client mode");
      } else if (!MMSCheckReceivePermissions()) {
         throw new SecurityException("Not allowed to receive messages");
      } else {
         _event_cons.setConnectionListener(this.ucid, l, this);
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
      sendPermissionLock = SharedObjects.getLock("javax.wireless.messaging.permissionLock");
      MESLock = SharedObjects.getLock("javax.wireless.messaging.mesLock");
   }
}
