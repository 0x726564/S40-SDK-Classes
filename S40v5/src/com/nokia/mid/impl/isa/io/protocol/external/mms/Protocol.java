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

public class Protocol implements ConnectionBaseInterface, MessageConnection {
   private static final String mz = System.getProperty("wireless.messaging.mms.mmsc");
   public static final int MAX_MULTIPART_MESSAGE_SIZE;
   private static MessageEventConsumer kK;
   private static final Object mA;
   private static final Object mB;
   private int mC = 0;
   private String mD;
   private boolean open = false;
   private boolean isServer;

   public Connection openPrim(String var1, int var2, boolean var3) throws IOException {
      if (!MMSCheckOpenPermissions()) {
         throw new SecurityException("Not allowed to open the connection");
      } else if (mz == null) {
         throw new IOException("MMSC address not configured");
      } else if (var1 != null && MMSAddress.validateUrl(var1, false)) {
         if (var2 != 1 && var2 != 2 && var2 != 3) {
            throw new IllegalArgumentException("Invalid mode");
         } else {
            if (MMSAddress.getConnectionMode(var1)) {
               this.isServer = true;
               this.mD = MMSAddress.getAppIdFromUrl(var1);
               this.mC = this.MMSOpen(this.mD);
            } else {
               if (var2 == 1) {
                  throw new IllegalArgumentException("Illegal mode");
               }

               this.isServer = false;
               this.mD = MMSAddress.getAddressFromUrl(var1);
               this.mC = this.MMSOpen((String)null);
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
            kK.setConnectionListener(this.mC, (MessageListener)null, this);
         }

         this.MMSClose(this.mC);
         this.open = false;
      }

   }

   public Message newMessage(String var1) {
      return this.newMessage(var1, "mms://" + this.mD);
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
         String var1;
         if ((var1 = MMSReceive(this.mC, this.mD)) != null && var1.length() != 0) {
            MMSMultipartMessage var2 = null;
            synchronized(mB) {
               int var4 = 3;

               while(true) {
                  try {
                     if ((var2 = fetchPDU(this.mC, var1)) != null) {
                        var2.postReceiving();
                     }
                     break;
                  } catch (InterruptedIOException var6) {
                     break;
                  } catch (IOException var7) {
                     if (this.open) {
                        try {
                           Thread.sleep(3000L);
                        } catch (InterruptedException var5) {
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
         int var2;
         MMSMultipartMessage var8;
         if ((var2 = (var8 = (MMSMultipartMessage)var1).getSize()) > MAX_MULTIPART_MESSAGE_SIZE) {
            throw new IllegalArgumentException("Maximum length exceeded");
         } else {
            int var3;
            if (var8.getSubject() == null) {
               var3 = 0;
            } else {
               var3 = var8.getSubject().length();
            }

            synchronized(mA) {
               String[][] var5;
               if ((var5 = var8.getFormattedAddresses()) == null) {
                  throw new IllegalArgumentException("No recipient address has been set");
               }

               if (!MMSCheckSendPermissions(var2 + var3, var5)) {
                  throw new SecurityException("Not allowed to send a MMS");
               }
            }

            if (this.open) {
               synchronized(mB) {
                  var8.encodeAndSend(this.isServer ? this.mD : null, this.mC);
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
         kK.setConnectionListener(this.mC, var1, this);
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
      } catch (ClassNotFoundException var0) {
      }

      nativeInit();
      MAX_MULTIPART_MESSAGE_SIZE = 0;
      kK = MessageEventConsumer.instance();
      mA = SharedObjects.getLock("javax.wireless.messaging.permissionLock");
      mB = SharedObjects.getLock("javax.wireless.messaging.mesLock");
   }
}
