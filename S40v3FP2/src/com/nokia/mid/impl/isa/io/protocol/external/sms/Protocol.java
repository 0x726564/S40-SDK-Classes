package com.nokia.mid.impl.isa.io.protocol.external.sms;

import com.nokia.mid.impl.isa.io.protocol.external.MessageEventConsumer;
import com.nokia.mid.impl.isa.util.SharedObjects;
import com.nokia.mid.impl.isa.wireless.messaging.SMSBinaryMessage;
import com.nokia.mid.impl.isa.wireless.messaging.SMSTextMessage;
import com.nokia.mid.pri.PriAccess;
import com.sun.cldc.io.ConnectionBaseInterface;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import javax.microedition.io.Connection;
import javax.wireless.messaging.BinaryMessage;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;
import javax.wireless.messaging.TextMessage;

public class Protocol implements MessageConnection, ConnectionBaseInterface {
   private static final String PROTOCOL = "sms:";
   private static final int MAX_NB_MSG;
   private static final int[] NB_SMS;
   private static final int MAX_PORT_NUMBER = 65535;
   private static MessageEventConsumer _event_cons = MessageEventConsumer.instance();
   private static final Object openLock;
   private static final Object closeLock;
   private static final Object sendLock;
   private static final Object prepaidLock;
   public static final Object permissionLock;
   private String destPhoneNum;
   private int portNum;
   private int conId = 0;
   private boolean open = false;

   public Connection openPrim(String var1, int var2, boolean var3) throws IOException {
      if (SMSCheckOpenPermissions()) {
         if (var1 != null) {
            if (var2 != 1 && var2 != 2 && var2 != 3) {
               throw new IllegalArgumentException("Illegal mode");
            } else {
               this.destPhoneNum = Protocol.SmsUrl.getPhone("sms:" + var1);
               this.portNum = Protocol.SmsUrl.getPort("sms:" + var1);
               if (!Protocol.SmsUrl.isServer(this.destPhoneNum, this.portNum) && var2 == 1) {
                  throw new IllegalArgumentException("Invalid mode");
               } else {
                  synchronized(openLock) {
                     this.conId = this.SMSOpen(this.destPhoneNum, this.portNum);
                  }

                  this.open = true;
                  return this;
               }
            }
         } else {
            throw new IllegalArgumentException("No phone number");
         }
      } else {
         throw new SecurityException("Not allowed to open the connection");
      }
   }

   public void close() throws IOException {
      if (this.open) {
         if (Protocol.SmsUrl.isServer(this.destPhoneNum, this.portNum)) {
            _event_cons.setConnectionListener(this.conId, (MessageListener)null, this);
         }

         synchronized(closeLock) {
            this.SMSClose(this.conId);
         }

         this.open = false;
      }

   }

   public Message newMessage(String var1) {
      String var2 = null;
      if (!Protocol.SmsUrl.isServer(this.destPhoneNum, this.portNum)) {
         if (this.portNum == 0) {
            var2 = "sms://" + this.destPhoneNum;
         } else {
            var2 = "sms://" + this.destPhoneNum + ":" + this.portNum;
         }
      }

      return this.newMessage(var1, var2);
   }

   public Message newMessage(String var1, String var2) {
      Object var3;
      if (var1.equals("binary")) {
         var3 = new SMSBinaryMessage();
      } else {
         if (!var1.equals("text")) {
            throw new IllegalArgumentException("Invalid message type");
         }

         var3 = new SMSTextMessage();
      }

      ((Message)var3).setAddress(var2);
      return (Message)var3;
   }

   public int numberOfSegments(Message var1) {
      int var2 = 0;
      if (var1 != null) {
         boolean var3;
         try {
            var3 = Protocol.SmsUrl.getPort(var1.getAddress()) != 0;
         } catch (IllegalArgumentException var5) {
            var3 = false;
         }

         var2 = this.numberOfSegments(new Protocol.SmsData(var1), var3);
      }

      return var2;
   }

   public Message receive() throws IOException, InterruptedIOException {
      Message var1 = null;
      if (this.open) {
         if (Protocol.SmsUrl.isServer(this.destPhoneNum, this.portNum)) {
            if (SMSCheckReceivePermissions()) {
               var1 = this.SMSReceive(this.conId, this.portNum);
               if (var1 != null) {
                  byte var2 = 1;
                  if (var1 instanceof BinaryMessage) {
                     var2 = 0;
                  }

                  synchronized(prepaidLock) {
                     if (!this.SMSCheckTracFonePermission(this.portNum, var1, var2)) {
                        throw new IOException("TracFone permission failed");
                     }
                  }
               }

               return var1;
            } else {
               throw new SecurityException("not allowed to receive");
            }
         } else {
            throw new IOException("Cannot receive in client mode");
         }
      } else {
         throw new IOException("The connection has been closed");
      }
   }

   public void send(Message var1) throws IOException, InterruptedIOException {
      if (!this.open) {
         throw new IOException("The connection has been closed");
      } else if (var1 != null) {
         if (System.getProperty("wireless.messaging.sms.smsc") == null) {
            throw new IOException("SMSC address not configured");
         } else {
            String var2 = var1.getAddress();
            int var3 = Protocol.SmsUrl.getPort(var2);
            if ((!Protocol.SmsUrl.isServer(this.destPhoneNum, this.portNum) || Protocol.SmsUrl.isRestricted(var3)) && (Protocol.SmsUrl.isServer(this.destPhoneNum, this.portNum) || Protocol.SmsUrl.isRestricted(var3))) {
               throw new SecurityException("Restricted port number");
            } else {
               String var4 = Protocol.SmsUrl.getPhone(var2);
               Protocol.SmsData var5 = new Protocol.SmsData(var1);
               int var6 = this.numberOfSegments(var5, var3 != 0);
               if (var6 != 0) {
                  synchronized(permissionLock) {
                     String var8 = phonebookLookUp(var4, true);
                     if (!SMSCheckSendPermissions(var6, var4, var8)) {
                        throw new SecurityException("not allowed to send");
                     }
                  }

                  if (this.open) {
                     String var7;
                     int var9;
                     int var14;
                     if (Protocol.SmsUrl.isServer(this.destPhoneNum, this.portNum)) {
                        var7 = var4;
                        var14 = var3;
                        var9 = this.portNum;
                     } else {
                        var7 = this.destPhoneNum;
                        var14 = this.portNum;
                        var9 = 0;
                     }

                     synchronized(sendLock) {
                        this.SMSSend(this.conId, var5.getPayload(), var5.getFormat(), var7, var14, var9);
                     }
                  } else {
                     throw new IOException("The connection has been closed");
                  }
               } else {
                  throw new IllegalArgumentException("Too big message");
               }
            }
         }
      } else {
         throw new NullPointerException("No Message");
      }
   }

   public void setMessageListener(MessageListener var1) throws IOException {
      if (SMSCheckReceivePermissions()) {
         if (this.open) {
            if (Protocol.SmsUrl.isServer(this.destPhoneNum, this.portNum)) {
               _event_cons.setConnectionListener(this.conId, var1, this);
            } else {
               throw new IOException("Cannot listen in client mode");
            }
         } else {
            throw new IOException("The connection has been closed");
         }
      } else {
         throw new SecurityException("No permission to send");
      }
   }

   private int numberOfSegments(Protocol.SmsData var1, boolean var2) {
      byte[] var3 = var1.getPayload();
      int var4 = var3 == null ? 0 : var3.length;
      int var5 = var1.getFormat();
      int var6 = var2 ? MAX_NB_MSG * (var5 * 2 + 1) : MAX_NB_MSG * var5 * 2;
      int var7 = PriAccess.getInt(5) == 1 ? 1 : MAX_NB_MSG;

      for(int var8 = 0; var8 < var7; ++var8) {
         if (var4 <= NB_SMS[var6 + var8]) {
            return var8 + 1;
         }
      }

      return 0;
   }

   private static boolean SMSCheckReceivePermissions() {
      boolean var0 = SMSCheckReceivePermissions0();
      return var0;
   }

   private static native void SMSStartUp();

   private native int SMSOpen(String var1, int var2);

   private static native boolean SMSCheckOpenPermissions();

   private native void SMSClose(int var1);

   private native Message SMSReceive(int var1, int var2);

   private native void SMSSend(int var1, byte[] var2, int var3, String var4, int var5, int var6);

   public static native String phonebookLookUp(String var0, boolean var1);

   private static native boolean SMSCheckSendPermissions(int var0, String var1, String var2);

   private static native boolean SMSCheckReceivePermissions0();

   private native boolean SMSCheckTracFonePermission(int var1, Message var2, int var3);

   static {
      SMSStartUp();
      MAX_NB_MSG = 3;
      NB_SMS = new int[]{140, 266, 399, 133, 254, 381, 160, 304, 456, 152, 290, 435, 140, 264, 396, 132, 252, 378};
      openLock = SharedObjects.getLock("javax.wireless.messaging.openLock");
      closeLock = SharedObjects.getLock("javax.wireless.messaging.closeLock");
      sendLock = new Object();
      prepaidLock = new Object();
      permissionLock = new Object();
   }

   private static final class SmsUrl {
      private static final int[] SECURE_PORTS = new int[]{2805, 2923, 2948, 2949, 5502, 5503, 5508, 5511, 5512, 9200, 9201, 9202, 9203, 9207, 49996, 49999};

      static String getPhone(String var0) {
         String var1 = null;
         if (var0 != null) {
            int var2 = indPrefix(var0);
            if (var2 > 0) {
               int var3 = var0.length();
               if (var2 < var3) {
                  int var4 = var0.indexOf(58, var2);
                  if (var4 < 0) {
                     var1 = var0.substring(var2, var3);
                  } else if (var4 > var2) {
                     var1 = var0.substring(var2, var4);
                  }

                  if (var1 != null) {
                     validatePhone(var1);
                  }

                  return var1;
               } else {
                  throw new IllegalArgumentException("no number");
               }
            } else {
               throw new IllegalArgumentException("malformed address");
            }
         } else {
            throw new IllegalArgumentException("null address");
         }
      }

      static int getPort(String var0) {
         int var1 = 0;
         if (var0 != null) {
            int var2 = indPrefix(var0);
            if (var2 > 0) {
               int var3 = var0.indexOf(58, var2);
               if (var3 > 0) {
                  int var4 = var0.length();
                  String var5 = var0.substring(var3 + 1, var4);
                  if (var5 != null) {
                     var1 = validatePort(var5);
                  }
               }

               return var1;
            } else {
               throw new IllegalArgumentException("malformed address");
            }
         } else {
            throw new IllegalArgumentException("null address");
         }
      }

      private static int indPrefix(String var0) {
         int var1 = 0;
         if (var0.startsWith("sms://")) {
            var1 = "sms:".length() + 2;
         }

         return var1;
      }

      static void validatePhone(String var0) {
         long var1;
         try {
            if (var0.startsWith("+")) {
               var1 = Long.parseLong(var0.substring(1));
            } else {
               var1 = Long.parseLong(var0);
            }
         } catch (NumberFormatException var4) {
            throw new IllegalArgumentException("Address format error");
         }

         if (var1 < 0L) {
            throw new IllegalArgumentException("Negative address value");
         }
      }

      static int validatePort(String var0) {
         int var1;
         try {
            var1 = Integer.parseInt(var0);
         } catch (NumberFormatException var3) {
            throw new IllegalArgumentException("Port number format error");
         }

         if (var1 >= 0 && var1 <= 65535) {
            return var1;
         } else {
            throw new IllegalArgumentException("Illegal port value");
         }
      }

      static boolean isRestricted(int var0) {
         int var1 = 0;

         do {
            if (var1 >= SECURE_PORTS.length) {
               return false;
            }
         } while(SECURE_PORTS[var1++] != var0);

         return true;
      }

      static boolean isServer(String var0, int var1) {
         return var0 == null && var1 != 0;
      }
   }

   private class SmsData {
      private static final int BINARY_FORMAT = 0;
      private static final int GSM_FORMAT = 1;
      private static final int UNICODE_FORMAT = 2;
      private byte[] payload = null;
      private int format;

      SmsData(Message var2) {
         if (var2 instanceof BinaryMessage) {
            this.format = 0;
            byte[] var3 = ((BinaryMessage)var2).getPayloadData();
            if (var3 != null) {
               this.payload = new byte[var3.length];
               System.arraycopy(var3, 0, this.payload, 0, var3.length);
            }
         } else {
            if (!(var2 instanceof TextMessage)) {
               throw new IllegalArgumentException("Unknown message type");
            }

            String var7 = ((TextMessage)var2).getPayloadText();
            if (var7 == null) {
               this.format = 1;
            } else {
               Object var4 = null;

               byte[] var8;
               try {
                  var8 = var7.getBytes("UCS-2BE");
               } catch (UnsupportedEncodingException var6) {
                  throw new RuntimeException("UCS-2 encoding not supported!");
               }

               byte[] var5 = TextEncoder.encode(var8);
               if (var5 != null) {
                  this.format = 1;
                  this.payload = var5;
               } else {
                  this.format = 2;
                  this.payload = var8;
               }
            }
         }

      }

      private byte[] getPayload() {
         return this.payload;
      }

      private int getFormat() {
         return this.format;
      }
   }
}
