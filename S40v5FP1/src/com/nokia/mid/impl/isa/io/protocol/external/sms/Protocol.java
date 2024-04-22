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
   private static final Object permissionLock;
   private String destPhoneNum;
   private int portNum;
   private int conId = 0;
   private boolean open = false;
   private boolean isPortPresent = false;

   public Connection openPrim(String name, int mode, boolean timeouts) throws IOException {
      if (SMSCheckOpenPermissions()) {
         if (name != null) {
            if (mode != 1 && mode != 2 && mode != 3) {
               throw new IllegalArgumentException("Illegal mode");
            } else {
               this.destPhoneNum = Protocol.SmsUrl.getPhone("sms:" + name);
               this.portNum = Protocol.SmsUrl.getPort("sms:" + name);
               this.isPortPresent = name.indexOf(58) > 0;
               if (!Protocol.SmsUrl.isServer(this.destPhoneNum, this.portNum) && mode == 1) {
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

   public Message newMessage(String type) {
      String addr = null;
      if (!Protocol.SmsUrl.isServer(this.destPhoneNum, this.portNum)) {
         if (this.portNum == 0 && !this.isPortPresent) {
            addr = "sms://" + this.destPhoneNum;
         } else {
            addr = "sms://" + this.destPhoneNum + ":" + this.portNum;
         }
      }

      return this.newMessage(type, addr);
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

      ((Message)var3).setAddress(address);
      return (Message)var3;
   }

   public int numberOfSegments(Message msg) {
      int res = 0;
      if (msg != null) {
         boolean portPresent;
         try {
            portPresent = Protocol.SmsUrl.getPort(msg.getAddress()) != 0;
         } catch (IllegalArgumentException var5) {
            portPresent = false;
         }

         res = this.numberOfSegments(new Protocol.SmsData(msg), portPresent);
      }

      return res;
   }

   public Message receive() throws IOException, InterruptedIOException {
      Message msg = null;
      if (this.open) {
         if (Protocol.SmsUrl.isServer(this.destPhoneNum, this.portNum)) {
            if (SMSCheckReceivePermissions()) {
               msg = this.SMSReceive(this.conId, this.portNum);
               if (msg != null) {
                  int format = 1;
                  if (msg instanceof BinaryMessage) {
                     format = 0;
                  }

                  synchronized(prepaidLock) {
                     if (!this.SMSCheckTracFonePermission(this.portNum, msg, format)) {
                        throw new IOException("TracFone permission failed");
                     }
                  }
               }

               return msg;
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

   public void send(Message msg) throws IOException, InterruptedIOException {
      if (!this.open) {
         throw new IOException("The connection has been closed");
      } else if (msg != null) {
         if (System.getProperty("wireless.messaging.sms.smsc") == null) {
            throw new IOException("SMSC address not configured");
         } else {
            String msgAddr = msg.getAddress();
            int msgPort = Protocol.SmsUrl.getPort(msgAddr);
            if (Protocol.SmsUrl.isServer(this.destPhoneNum, this.portNum) && !Protocol.SmsUrl.isRestricted(msgPort) || !Protocol.SmsUrl.isServer(this.destPhoneNum, this.portNum) && !Protocol.SmsUrl.isRestricted(msgPort)) {
               String msgPhone = Protocol.SmsUrl.getPhone(msgAddr);
               Protocol.SmsData data = new Protocol.SmsData(msg);
               int numSegments = this.numberOfSegments(data, msgPort != 0);
               if (numSegments != 0) {
                  synchronized(permissionLock) {
                     String destName = phonebookLookUp(msgPhone, true);
                     if (!SMSCheckSendPermissions(numSegments, msgPhone, destName)) {
                        throw new SecurityException("not allowed to send");
                     }
                  }

                  if (this.open) {
                     String destPhone;
                     int origPort;
                     int destPort;
                     if (Protocol.SmsUrl.isServer(this.destPhoneNum, this.portNum)) {
                        destPhone = msgPhone;
                        destPort = msgPort;
                        origPort = this.portNum;
                     } else {
                        destPhone = this.destPhoneNum;
                        destPort = this.portNum;
                        origPort = 0;
                     }

                     synchronized(sendLock) {
                        this.SMSSend(this.conId, data.getPayload(), data.getFormat(), destPhone, destPort, origPort);
                     }
                  } else {
                     throw new IOException("The connection has been closed");
                  }
               } else {
                  throw new IllegalArgumentException("Too big message");
               }
            } else {
               throw new SecurityException("Restricted port number");
            }
         }
      } else {
         throw new NullPointerException("No Message");
      }
   }

   public void setMessageListener(MessageListener l) throws IOException {
      if (SMSCheckReceivePermissions()) {
         if (this.open) {
            if (Protocol.SmsUrl.isServer(this.destPhoneNum, this.portNum)) {
               _event_cons.setConnectionListener(this.conId, l, this);
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

   private int numberOfSegments(Protocol.SmsData data, boolean portPresent) {
      byte[] payload = data.getPayload();
      int length = payload == null ? 0 : payload.length;
      int format = data.getFormat();
      int ind = portPresent ? MAX_NB_MSG * (format * 2 + 1) : MAX_NB_MSG * format * 2;
      int max_msgs = PriAccess.getInt(5) == 1 ? 1 : MAX_NB_MSG;

      for(int i = 0; i < max_msgs; ++i) {
         if (length <= NB_SMS[ind + i]) {
            return i + 1;
         }
      }

      return 0;
   }

   private static boolean SMSCheckReceivePermissions() {
      boolean allowed = SMSCheckReceivePermissions0();
      return allowed;
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
      sendLock = SharedObjects.getLock("javax.wireless.messaging.sendLock");
      prepaidLock = SharedObjects.getLock("javax.wireless.messaging.prepaidLock");
      permissionLock = SharedObjects.getLock("javax.wireless.messaging.permissionLock");
   }

   private static final class SmsUrl {
      private static final int[] SECURE_PORTS = new int[]{2805, 2923, 2948, 2949, 5502, 5503, 5508, 5511, 5512, 9200, 9201, 9202, 9203, 9207, 49996, 49999};

      static String getPhone(String url) {
         String phone = null;
         if (url != null) {
            int i = indPrefix(url);
            if (i > 0) {
               int length = url.length();
               if (i < length) {
                  int colon = url.indexOf(58, i);
                  if (colon < 0) {
                     phone = url.substring(i, length);
                  } else if (colon > i) {
                     phone = url.substring(i, colon);
                  }

                  if (phone != null) {
                     validatePhone(phone);
                  }

                  return phone;
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

      static int getPort(String url) {
         int port = 0;
         if (url != null) {
            int i = indPrefix(url);
            if (i > 0) {
               int colon = url.indexOf(58, i);
               if (colon > 0) {
                  int length = url.length();
                  String s_port = url.substring(colon + 1, length);
                  if (s_port != null) {
                     port = validatePort(s_port);
                  }
               }

               return port;
            } else {
               throw new IllegalArgumentException("malformed address");
            }
         } else {
            throw new IllegalArgumentException("null address");
         }
      }

      private static int indPrefix(String url) {
         int i = 0;
         if (url.startsWith("sms://")) {
            i = "sms:".length() + 2;
         }

         return i;
      }

      static void validatePhone(String s_addr) {
         long addr;
         try {
            if (s_addr.startsWith("+")) {
               addr = Long.parseLong(s_addr.substring(1));
            } else {
               addr = Long.parseLong(s_addr);
            }
         } catch (NumberFormatException var4) {
            throw new IllegalArgumentException("Address format error");
         }

         if (addr < 0L) {
            throw new IllegalArgumentException("Negative address value");
         }
      }

      static int validatePort(String s_port) {
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

      static boolean isRestricted(int port) {
         int i = 0;

         do {
            if (i >= SECURE_PORTS.length) {
               return false;
            }
         } while(SECURE_PORTS[i++] != port);

         return true;
      }

      static boolean isServer(String phone, int port) {
         return phone == null && port > 0;
      }
   }

   private class SmsData {
      private static final int BINARY_FORMAT = 0;
      private static final int GSM_FORMAT = 1;
      private static final int UNICODE_FORMAT = 2;
      private byte[] payload = new byte[0];
      private int format;

      SmsData(Message msg) {
         if (msg instanceof BinaryMessage) {
            this.format = 0;
            byte[] origData = ((BinaryMessage)msg).getPayloadData();
            if (origData != null) {
               this.payload = new byte[origData.length];
               System.arraycopy(origData, 0, this.payload, 0, origData.length);
            }
         } else {
            if (!(msg instanceof TextMessage)) {
               throw new IllegalArgumentException("Unknown message type");
            }

            String payloadText = ((TextMessage)msg).getPayloadText();
            if (payloadText == null) {
               this.format = 1;
            } else {
               Object var4 = null;

               byte[] ucs2bytes;
               try {
                  ucs2bytes = payloadText.getBytes("UCS-2BE");
               } catch (UnsupportedEncodingException var6) {
                  throw new RuntimeException("UCS-2 encoding not supported!");
               }

               byte[] gsm7bytes = TextEncoder.encode(ucs2bytes);
               if (gsm7bytes != null) {
                  this.format = 1;
                  this.payload = gsm7bytes;
               } else {
                  this.format = 2;
                  this.payload = ucs2bytes;
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
