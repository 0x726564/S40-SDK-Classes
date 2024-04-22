package com.nokia.mid.impl.isa.wireless.messaging;

import com.nokia.mid.impl.isa.io.protocol.external.mms.MMSAddress;
import com.nokia.mid.impl.isa.io.protocol.external.mms.Protocol;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.wireless.messaging.MessagePart;
import javax.wireless.messaging.MultipartMessage;
import javax.wireless.messaging.SizeExceededException;

public class MMSMultipartMessage implements MultipartMessage {
   public static final String ADDRESS_TYPE_TO = "to";
   public static final String ADDRESS_TYPE_CC = "cc";
   public static final String ADDRESS_TYPE_BCC = "bcc";
   public static final String HEADER_DELIVERYTIME_STRING = "x-mms-delivery-time";
   public static final String HEADER_PRIORITY_STRING = "x-mms-priority";
   public static final String PRIORITY_LOW_STRING = "low";
   public static final String PRIORITY_NORMAL_STRING = "normal";
   public static final String PRIORITY_HIGH_STRING = "high";
   private Vector fc = null;
   private MessagePart[] fd = null;
   private Hashtable fe = null;
   private String[] ff;
   private String[] fg;
   private int fh = 0;
   private String fi;
   private String fj;
   private String[] fk = new String[2];
   private String fl;
   private String fm;
   private long timestamp = 0L;

   public boolean addAddress(String var1, String var2) {
      if (!G(var1) || !MMSAddress.validateUrl(var2, true)) {
         throw new IllegalArgumentException("Invalid Address");
      } else {
         this.setApplicationId(MMSAddress.getAppIdFromUrl(var2));
         if (this.fe == null) {
            this.fe = new Hashtable(3);
         }

         Vector var3;
         if ((var3 = (Vector)this.fe.get(var1.toLowerCase())) == null) {
            var3 = new Vector();
            this.fe.put(var1.toLowerCase(), var3);
         }

         var3.addElement(var2);
         return true;
      }
   }

   public void addMessagePart(MessagePart var1) throws SizeExceededException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         String var2;
         if ((var2 = var1.getContentID()) != null && this.getMessagePart(var2) != null) {
            throw new IllegalArgumentException();
         } else if (this.fh + var1.getLength() > Protocol.MAX_MULTIPART_MESSAGE_SIZE) {
            throw new SizeExceededException("Multipart Message content exceeds the maximum size");
         } else {
            this.fh += var1.getLength();
            if (this.fc == null) {
               this.fc = new Vector();
            }

            this.fc.addElement(var1);
         }
      }
   }

   public String getAddress() {
      if (this.fj == null) {
         if (this.fe == null) {
            return null;
         } else {
            Vector var1;
            if ((var1 = (Vector)this.fe.get("to")) == null) {
               return null;
            } else {
               System.out.println("toAddresses: " + var1);
               return (String)var1.elementAt(0);
            }
         }
      } else {
         System.out.println("fromAddress: " + this.fj);
         return this.fj;
      }
   }

   public String[] getAddresses(String var1) {
      String[] var2 = null;
      if (var1 != null) {
         if (var1.toLowerCase().equals("from")) {
            if (this.fj != null) {
               var2 = new String[]{this.fj};
            }
         } else {
            Vector var3;
            int var4;
            if (this.fe != null && (var3 = (Vector)this.fe.get(var1.toLowerCase())) != null && (var4 = var3.size()) > 0) {
               var2 = new String[var4];
               var3.copyInto(var2);
            }
         }
      }

      return var2;
   }

   public String[][] getFormattedAddresses() {
      String[][] var1 = null;
      String[][] var6;
      if ((var6 = new String[][]{this.J("to"), this.J("cc"), this.J("bcc")})[0] != null || var6[1] != null || var6[2] != null) {
         var1 = var6;

         for(int var2 = 0; var2 <= 2; ++var2) {
            if (var6[var2] != null) {
               for(int var3 = 0; var3 < var6[var2].length; ++var3) {
                  String var4;
                  String var5;
                  if ((var5 = com.nokia.mid.impl.isa.io.protocol.external.sms.Protocol.phonebookLookUp(var4 = var6[var2][var3], MMSAddress.isPhoneNumber(var4))) != null) {
                     var1[var2][var3] = var5 + "<" + var4 + ">";
                  }
               }
            }
         }
      }

      return var1;
   }

   public String getHeader(String var1) {
      int var2;
      if ((var2 = H(var1)) == -2) {
         throw new SecurityException();
      } else if (var2 == -1) {
         throw new IllegalArgumentException();
      } else {
         return this.fk[var2];
      }
   }

   public MessagePart getMessagePart(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         if (this.fc != null) {
            Enumeration var3 = this.fc.elements();

            while(var3.hasMoreElements()) {
               MessagePart var2;
               if ((var2 = (MessagePart)var3.nextElement()).getContentID().equals(var1)) {
                  return var2;
               }
            }
         }

         return null;
      }
   }

   public MessagePart[] getMessageParts() {
      if (this.fc == null) {
         return null;
      } else {
         int var1 = this.fc.size();
         MessagePart[] var2 = null;
         if (var1 > 0) {
            var2 = new MessagePart[var1];
            var1 = 0;

            for(Enumeration var3 = this.fc.elements(); var3.hasMoreElements(); ++var1) {
               var2[var1] = (MessagePart)var3.nextElement();
            }
         }

         return var2;
      }
   }

   public String getStartContentId() {
      return this.fl;
   }

   public String getSubject() {
      return this.fi;
   }

   public boolean removeAddress(String var1, String var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (!G(var1 = var1.toLowerCase())) {
         throw new IllegalArgumentException();
      } else if (this.fe == null) {
         return false;
      } else {
         Vector var3;
         if ((var3 = (Vector)this.fe.get(var1)) == null) {
            return false;
         } else if (var2 != null && var3.contains(var2)) {
            var3.removeElement(var2);
            if (var3.isEmpty()) {
               this.I(var1);
            }

            this.N();
            if (var2.equals(this.fj)) {
               this.fj = null;
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public void removeAddresses() {
      this.fe = null;
      this.fj = null;
      this.fm = null;
   }

   public void removeAddresses(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (!G(var1 = var1.toLowerCase())) {
         throw new IllegalArgumentException();
      } else {
         if (var1.equals("to")) {
            this.fj = null;
         }

         if (this.fe != null && this.getAddresses(var1) != null) {
            this.I(var1);
            this.N();
         }

      }
   }

   public boolean removeMessagePart(MessagePart var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         boolean var10000;
         label31: {
            MessagePart var3 = var1;
            if (this.fc != null) {
               Enumeration var2 = this.fc.elements();

               while(var2.hasMoreElements()) {
                  if ((MessagePart)var2.nextElement() == var3) {
                     var10000 = true;
                     break label31;
                  }
               }
            }

            var10000 = false;
         }

         if (var10000) {
            this.fc.removeElement(var1);
            this.fh -= var1.getLength();
            if (var1.getContentID().equals(this.fl)) {
               this.fl = null;
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public boolean removeMessagePartId(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (this.fc == null) {
         return false;
      } else {
         MessagePart var2;
         if ((var2 = this.getMessagePart(var1)) != null) {
            this.fc.removeElement(var2);
            this.fh -= var2.getLength();
            if (var1.equals(this.fl)) {
               this.fl = null;
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public boolean removeMessagePartLocation(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (this.fc == null) {
         return false;
      } else {
         boolean var2 = false;
         int var3 = this.fc.size();
         int var4 = 0;

         for(int var5 = 0; var5 < var3; ++var5) {
            MessagePart var6;
            if ((var6 = (MessagePart)this.fc.elementAt(var4)).getContentLocation().equals(var1)) {
               String var7 = var6.getContentID();
               this.fh -= var6.getLength();
               this.removeMessagePart(var6);
               if (var7 != null && var7.equals(this.fl)) {
                  this.fl = null;
               }

               var2 = true;
            } else {
               ++var4;
            }
         }

         return var2;
      }
   }

   public void setAddress(String var1) {
      if (var1 != null) {
         this.addAddress("to", var1);
      }

   }

   public void setHeader(String var1, String var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         int var3;
         if ((var3 = H(var1)) == -2) {
            throw new SecurityException();
         } else if (!MMSAddress.validateHeader(var1, var2)) {
            throw new IllegalArgumentException();
         } else if (var3 == -1) {
            throw new IllegalArgumentException();
         } else {
            this.fk[var3] = var2;
         }
      }
   }

   public void setStartContentId(String var1) {
      if (var1 != null && this.getMessagePart(var1) == null) {
         throw new IllegalArgumentException();
      } else {
         this.fl = var1;
      }
   }

   public void setSubject(String var1) {
      this.fi = var1;
   }

   public Date getTimestamp() {
      return null;
   }

   public int getSize() {
      return this.fh;
   }

   public void encodeAndSend(String var1, int var2) throws IllegalArgumentException {
      if (this.fe != null) {
         this.ff = a((Vector)this.fe.get("to"));
         this.fg = a((Vector)this.fe.get("cc"));
         a((Vector)this.fe.get("bcc"));
      }

      if (this.fc != null) {
         this.fd = new MessagePart[this.fc.size()];
         int var3 = 0;

         for(Enumeration var4 = this.fc.elements(); var4.hasMoreElements(); ++var3) {
            this.fd[var3] = (MessagePart)var4.nextElement();
         }
      }

      this.encodeAndSendMessage(var1, var2);
   }

   public void postReceiving() throws SizeExceededException {
      this.fk = new String[2];
      if (!MMSAddress.validateAppId(this.fm)) {
         this.fm = null;
      }

      if (this.fj != null) {
         if (this.fj.endsWith("/TYPE=PLMN")) {
            this.fj = this.fj.substring(0, this.fj.length() - "/TYPE=PLMN".length());
         }

         this.fj = "mms://" + this.fj;
         if (this.fm != null) {
            this.fj = this.fj + ':' + this.fm;
         }
      }

      if (this.ff != null) {
         if (this.fe == null) {
            this.fe = new Hashtable(3);
         }

         this.fe.put("to", a(this.ff));
      }

      if (this.fg != null) {
         if (this.fe == null) {
            this.fe = new Hashtable(3);
         }

         this.fe.put("cc", a(this.fg));
      }

      String var1;
      if ((var1 = this.getHeader("x-mms-delivery-time")) != null) {
         try {
            long var3 = Long.parseLong(var1);
            this.setHeader("x-mms-delivery-time", String.valueOf(var3 * 1000L));
         } catch (NumberFormatException var5) {
         }
      }

      if (this.fd != null) {
         for(int var6 = 0; var6 < this.fd.length; ++var6) {
            this.addMessagePart(this.fd[var6]);
         }
      }

   }

   private static boolean G(String var0) {
      if (var0 == null) {
         return false;
      } else {
         String var1;
         return (var1 = var0.toLowerCase()).equals("to") || var1.equals("cc") || var1.equals("bcc");
      }
   }

   private static int H(String var0) {
      if (var0 != null) {
         if ((var0 = var0.toLowerCase()).equals("x-mms-delivery-time")) {
            return 0;
         }

         if (var0.equals("x-mms-priority")) {
            return 1;
         }

         if (var0.equals("x-mms-subject") || var0.equals("x-mms-from") || var0.equals("x-mms-to") || var0.equals("x-mms-cc") || var0.equals("x-mms-bcc")) {
            return -2;
         }
      }

      return -1;
   }

   private static boolean a(String[] var0, String var1) {
      if (var0 != null && var1 != null) {
         for(int var2 = 0; var2 < var0.length; ++var2) {
            if (MMSAddress.getAppIdFromUrl(var0[var2]).equals(var1)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private void setApplicationId(String var1) throws IllegalArgumentException {
      boolean var4 = true;
      if (var1 != null && this.fm != null && !this.fm.equals(var1)) {
         var4 = false;
      }

      if (!var4) {
         throw new IllegalArgumentException("Invalid Application Id");
      } else {
         if (this.fm == null && var1 != null) {
            this.fm = var1;
         }

      }
   }

   private void I(String var1) {
      this.fe.remove(var1);
      if (this.fe.isEmpty()) {
         this.fe = null;
      }

   }

   private void N() {
      if (this.fm != null) {
         String var2 = this.fm;
         if (var2 == null || !a(this.getAddresses("to"), var2) && !a(this.getAddresses("cc"), var2) && !a(this.getAddresses("bcc"), var2)) {
            this.fm = null;
         }
      }

   }

   private static String[] a(Vector var0) {
      String[] var1 = null;
      if (var0 != null) {
         var1 = new String[var0.size()];

         for(int var2 = 0; var2 < var1.length; ++var2) {
            String var3;
            if (MMSAddress.isPhoneNumber(var3 = MMSAddress.getDeviceAddress((String)var0.elementAt(var2)))) {
               var3 = var3 + "/TYPE=PLMN";
            }

            var1[var2] = var3;
         }
      }

      return var1;
   }

   private static Vector a(String[] var0) {
      Vector var1 = new Vector();
      if (var0 != null) {
         for(int var2 = 0; var2 < var0.length; ++var2) {
            String var3;
            if ((var3 = var0[var2]).endsWith("/TYPE=PLMN")) {
               var3 = var3.substring(0, var3.length() - "/TYPE=PLMN".length());
            }

            var3 = "mms://" + var3;
            var1.addElement(var3);
         }
      }

      return var1;
   }

   private String[] J(String var1) {
      String[] var3 = this.getAddresses(var1);
      String[] var4 = null;
      if (var3 != null) {
         var4 = new String[var3.length];

         for(int var2 = 0; var2 < var3.length; ++var2) {
            var4[var2] = MMSAddress.getDeviceAddress(var3[var2]);
         }
      }

      return var4;
   }

   private native void encodeAndSendMessage(String var1, int var2);
}
