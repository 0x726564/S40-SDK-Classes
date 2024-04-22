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

public class Protocol implements ConnectionBaseInterface, MessageConnection {
   private static final int kI;
   private static final int[] kJ;
   private static MessageEventConsumer kK = MessageEventConsumer.instance();
   private static final Object kL;
   private static final Object ag;
   private static final Object kM;
   private static final Object kN;
   private static final Object kO;
   private String kP;
   private int kQ;
   private int kR = 0;
   private boolean open = false;

   public Connection openPrim(String var1, int var2, boolean var3) throws IOException {
      if (SMSCheckOpenPermissions()) {
         if (var1 != null) {
            if (var2 != 1 && var2 != 2 && var2 != 3) {
               throw new IllegalArgumentException("Illegal mode");
            } else {
               this.kP = Protocol.SmsUrl.y("sms:" + var1);
               this.kQ = Protocol.SmsUrl.z("sms:" + var1);
               if (!Protocol.SmsUrl.a(this.kP, this.kQ) && var2 == 1) {
                  throw new IllegalArgumentException("Invalid mode");
               } else {
                  synchronized(kL) {
                     this.kR = this.SMSOpen(this.kP, this.kQ);
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
         if (Protocol.SmsUrl.a(this.kP, this.kQ)) {
            kK.setConnectionListener(this.kR, (MessageListener)null, this);
         }

         synchronized(ag) {
            this.SMSClose(this.kR);
         }

         this.open = false;
      }

   }

   public Message newMessage(String var1) {
      String var2 = null;
      if (!Protocol.SmsUrl.a(this.kP, this.kQ)) {
         if (this.kQ < 0) {
            var2 = "sms://" + this.kP;
         } else {
            var2 = "sms://" + this.kP + ":" + this.kQ;
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
         boolean var4;
         try {
            var4 = Protocol.SmsUrl.z(var1.getAddress()) != 0;
         } catch (IllegalArgumentException var3) {
            var4 = false;
         }

         var2 = a(new Protocol.SmsData(this, var1), var4);
      }

      return var2;
   }

   public Message receive() throws IOException, InterruptedIOException {
      Message var1 = null;
      if (this.open) {
         if (Protocol.SmsUrl.a(this.kP, this.kQ)) {
            if (SMSCheckReceivePermissions0()) {
               if ((var1 = this.SMSReceive(this.kR, this.kQ)) != null) {
                  byte var2 = 1;
                  if (var1 instanceof BinaryMessage) {
                     var2 = 0;
                  }

                  synchronized(kN) {
                     if (!this.SMSCheckTracFonePermission(this.kQ, var1, var2)) {
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
            String var2;
            int var3 = Protocol.SmsUrl.z(var2 = var1.getAddress());
            if ((!Protocol.SmsUrl.a(this.kP, this.kQ) || Protocol.SmsUrl.i(var3)) && (Protocol.SmsUrl.a(this.kP, this.kQ) || Protocol.SmsUrl.i(var3))) {
               throw new SecurityException("Restricted port number");
            } else {
               var2 = Protocol.SmsUrl.y(var2);
               int var4;
               Protocol.SmsData var9;
               if ((var4 = a(var9 = new Protocol.SmsData(this, var1), var3 != 0)) != 0) {
                  synchronized(kO) {
                     String var6 = phonebookLookUp(var2, true);
                     if (!SMSCheckSendPermissions(var4, var2, var6)) {
                        throw new SecurityException("not allowed to send");
                     }
                  }

                  if (this.open) {
                     String var5;
                     int var10;
                     if (Protocol.SmsUrl.a(this.kP, this.kQ)) {
                        var5 = var2;
                        var10 = var3;
                        var4 = this.kQ;
                     } else {
                        var5 = this.kP;
                        var10 = this.kQ;
                        var4 = 0;
                     }

                     synchronized(kM) {
                        this.SMSSend(this.kR, Protocol.SmsData.a(var9), Protocol.SmsData.b(var9), var5, var10, var4);
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
      if (SMSCheckReceivePermissions0()) {
         if (this.open) {
            if (Protocol.SmsUrl.a(this.kP, this.kQ)) {
               kK.setConnectionListener(this.kR, var1, this);
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

   private static int a(Protocol.SmsData var0, boolean var1) {
      byte[] var2;
      int var6 = (var2 = Protocol.SmsData.a(var0)) == null ? 0 : var2.length;
      int var4 = Protocol.SmsData.b(var0);
      var4 = var1 ? kI * ((var4 << 1) + 1) : kI * var4 << 1;
      int var5 = PriAccess.getInt(5) == 1 ? 1 : kI;

      for(int var3 = 0; var3 < var5; ++var3) {
         if (var6 <= kJ[var4 + var3]) {
            return var3 + 1;
         }
      }

      return 0;
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
      kI = 3;
      kJ = new int[]{140, 266, 399, 133, 254, 381, 160, 304, 456, 152, 290, 435, 140, 264, 396, 132, 252, 378};
      kL = SharedObjects.getLock("javax.wireless.messaging.openLock");
      ag = SharedObjects.getLock("javax.wireless.messaging.closeLock");
      kM = SharedObjects.getLock("javax.wireless.messaging.sendLock");
      kN = SharedObjects.getLock("javax.wireless.messaging.prepaidLock");
      kO = SharedObjects.getLock("javax.wireless.messaging.permissionLock");
   }

   private static final class SmsUrl {
      private static final int[] dQ = new int[]{2805, 2923, 2948, 2949, 5502, 5503, 5508, 5511, 5512, 9200, 9201, 9202, 9203, 9207, 49996, 49999};

      static String y(String var0) {
         String var1 = null;
         if (var0 != null) {
            int var2;
            if ((var2 = A(var0)) > 0) {
               int var3 = var0.length();
               if (var2 < var3) {
                  int var4;
                  if ((var4 = var0.indexOf(58, var2)) < 0) {
                     var1 = var0.substring(var2, var3);
                  } else if (var4 > var2) {
                     var1 = var0.substring(var2, var4);
                  }

                  if (var1 != null) {
                     var0 = var1;

                     long var6;
                     try {
                        if (var0.startsWith("+")) {
                           var6 = Long.parseLong(var0.substring(1));
                        } else {
                           var6 = Long.parseLong(var0);
                        }
                     } catch (NumberFormatException var8) {
                        throw new IllegalArgumentException("Address format error");
                     }

                     if (var6 < 0L) {
                        throw new IllegalArgumentException("Negative address value");
                     }
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

      static int z(String var0) {
         int var1 = 0;
         if (var0 != null) {
            int var2;
            if ((var2 = A(var0)) > 0) {
               if ((var2 = var0.indexOf(58, var2)) > 0) {
                  int var3 = var0.length();
                  if ((var0 = var0.substring(var2 + 1, var3)) != null) {
                     var1 = B(var0);
                  }

                  return var1;
               } else {
                  return -1;
               }
            } else {
               throw new IllegalArgumentException("malformed address");
            }
         } else {
            throw new IllegalArgumentException("null address");
         }
      }

      private static int A(String var0) {
         int var1 = 0;
         if (var0.startsWith("sms://")) {
            var1 = "sms:".length() + 2;
         }

         return var1;
      }

      private static int B(String var0) {
         int var1;
         try {
            var1 = Integer.parseInt(var0);
         } catch (NumberFormatException var2) {
            throw new IllegalArgumentException("Port number format error");
         }

         if (var1 >= 0 && var1 <= 65535) {
            return var1;
         } else {
            throw new IllegalArgumentException("Illegal port value");
         }
      }

      static boolean i(int var0) {
         int var1 = 0;

         do {
            if (var1 >= dQ.length) {
               return false;
            }
         } while(dQ[var1++] != var0);

         return true;
      }

      static boolean a(String var0, int var1) {
         return var0 == null && var1 > 0;
      }
   }

   private class SmsData {
      private byte[] mj = null;
      private int mv;

      SmsData(Protocol var1, Message var2) {
         byte[] var5;
         if (var2 instanceof BinaryMessage) {
            this.mv = 0;
            if ((var5 = ((BinaryMessage)var2).getPayloadData()) != null) {
               this.mj = new byte[var5.length];
               System.arraycopy(var5, 0, this.mj, 0, var5.length);
            }

         } else if (var2 instanceof TextMessage) {
            String var4;
            if ((var4 = ((TextMessage)var2).getPayloadText()) == null) {
               this.mv = 1;
            } else {
               var2 = null;

               byte[] var6;
               try {
                  var6 = var4.getBytes("UCS-2BE");
               } catch (UnsupportedEncodingException var3) {
                  throw new RuntimeException("UCS-2 encoding not supported!");
               }

               if ((var5 = TextEncoder.encode(var6)) == null) {
                  this.mv = 2;
                  this.mj = var6;
                  return;
               }

               this.mv = 1;
               this.mj = var5;
            }

         } else {
            throw new IllegalArgumentException("Unknown message type");
         }
      }

      private byte[] getPayload() {
         return this.mj;
      }

      private int getFormat() {
         return this.mv;
      }

      static byte[] a(Protocol.SmsData var0) {
         return var0.getPayload();
      }

      static int b(Protocol.SmsData var0) {
         return var0.getFormat();
      }
   }
}
