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
   private static final String ADDRESS_TYPE_FROM = "from";
   private static final int ADDRESSES_HASHTABLE_INIT = 3;
   private static final int RESTRICTED_HEADER_INDEX = -2;
   private static final int UNIDENTIFIED_HEADER_INDEX = -1;
   private static final int HEADER_DELIVERYTIME_INDEX = 0;
   private static final int HEADER_PRIORITY_INDEX = 1;
   private static final int ACCESSIBLE_HEADER_MAX = 2;
   public static final String HEADER_DELIVERYTIME_STRING = "x-mms-delivery-time";
   public static final String HEADER_PRIORITY_STRING = "x-mms-priority";
   private static final String HEADER_SUBJECT_STRING = "x-mms-subject";
   private static final String HEADER_FROM_STRING = "x-mms-from";
   private static final String HEADER_TO_STRING = "x-mms-to";
   private static final String HEADER_CC_STRING = "x-mms-cc";
   private static final String HEADER_BCC_STRING = "x-mms-bcc";
   public static final String PRIORITY_LOW_STRING = "low";
   public static final String PRIORITY_NORMAL_STRING = "normal";
   public static final String PRIORITY_HIGH_STRING = "high";
   private static final String PLMN_TYPE_STRING = "/TYPE=PLMN";
   private Vector messagepartVector = null;
   private MessagePart[] _messagepartArray = null;
   private Hashtable addressTable = null;
   private String[] _toAddresses;
   private String[] _ccAddresses;
   private String[] _bccAddresses;
   private int contentLength = 0;
   private String subject;
   private String fromAddress;
   private String[] accessibleHeadersArray = new String[2];
   private String startContentId;
   private String applicationID;
   private long timestamp = 0L;

   public boolean addAddress(String var1, String var2) {
      if (!this.isAddressValid(var1, var2)) {
         throw new IllegalArgumentException("Invalid Address");
      } else {
         this.setApplicationId(MMSAddress.getAppIdFromUrl(var2));
         if (this.addressTable == null) {
            this.addressTable = new Hashtable(3);
         }

         Vector var3 = (Vector)this.addressTable.get(var1.toLowerCase());
         if (var3 == null) {
            var3 = new Vector();
            this.addressTable.put(var1.toLowerCase(), var3);
         }

         var3.addElement(var2);
         return true;
      }
   }

   public void addMessagePart(MessagePart var1) throws SizeExceededException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         String var2 = var1.getContentID();
         if (var2 != null && this.getMessagePart(var2) != null) {
            throw new IllegalArgumentException();
         } else if (this.contentLength + var1.getLength() > Protocol.MAX_MULTIPART_MESSAGE_SIZE) {
            throw new SizeExceededException("Multipart Message content exceeds the maximum size");
         } else {
            this.contentLength += var1.getLength();
            if (this.messagepartVector == null) {
               this.messagepartVector = new Vector();
            }

            this.messagepartVector.addElement(var1);
         }
      }
   }

   public String getAddress() {
      if (this.fromAddress == null) {
         if (this.addressTable == null) {
            return null;
         } else {
            Vector var1 = (Vector)this.addressTable.get("to");
            return var1 == null ? null : (String)var1.elementAt(0);
         }
      } else {
         return this.fromAddress;
      }
   }

   public String[] getAddresses(String var1) {
      String[] var2 = null;
      if (var1 != null) {
         if (var1.toLowerCase().equals("from")) {
            if (this.fromAddress != null) {
               var2 = new String[]{this.fromAddress};
            }
         } else if (this.addressTable != null) {
            Vector var3 = (Vector)this.addressTable.get(var1.toLowerCase());
            if (var3 != null) {
               int var4 = var3.size();
               if (var4 > 0) {
                  var2 = new String[var4];
                  var3.copyInto(var2);
               }
            }
         }
      }

      return var2;
   }

   public String[][] getFormattedAddresses() {
      byte var1 = 0;
      byte var2 = 1;
      byte var3 = 2;
      String[][] var4 = (String[][])null;
      String[][] var5 = new String[][]{this.getStrippedAddresses("to"), this.getStrippedAddresses("cc"), this.getStrippedAddresses("bcc")};
      if (var5[var1] != null || var5[var2] != null || var5[var3] != null) {
         var4 = var5;

         for(int var6 = var1; var6 <= var3; ++var6) {
            if (var5[var6] != null) {
               for(int var7 = 0; var7 < var5[var6].length; ++var7) {
                  String var8 = var5[var6][var7];
                  String var9 = com.nokia.mid.impl.isa.io.protocol.external.sms.Protocol.phonebookLookUp(var8, MMSAddress.isPhoneNumber(var8));
                  if (var9 != null) {
                     var4[var6][var7] = var9 + "<" + var8 + ">";
                  }
               }
            }
         }
      }

      return var4;
   }

   public String getHeader(String var1) {
      int var2 = this.getHeaderIndex(var1);
      if (var2 == -2) {
         throw new SecurityException();
      } else if (var2 == -1) {
         throw new IllegalArgumentException();
      } else {
         return this.accessibleHeadersArray[var2];
      }
   }

   public MessagePart getMessagePart(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         if (this.messagepartVector != null) {
            Enumeration var2 = this.messagepartVector.elements();

            while(var2.hasMoreElements()) {
               MessagePart var3 = (MessagePart)var2.nextElement();
               if (var3.getContentID().equals(var1)) {
                  return var3;
               }
            }
         }

         return null;
      }
   }

   public MessagePart[] getMessageParts() {
      if (this.messagepartVector == null) {
         return null;
      } else {
         int var1 = this.messagepartVector.size();
         MessagePart[] var2 = null;
         if (var1 > 0) {
            var2 = new MessagePart[var1];
            int var3 = 0;

            for(Enumeration var4 = this.messagepartVector.elements(); var4.hasMoreElements(); ++var3) {
               var2[var3] = (MessagePart)var4.nextElement();
            }
         }

         return var2;
      }
   }

   public String getStartContentId() {
      return this.startContentId;
   }

   public String getSubject() {
      return this.subject;
   }

   public boolean removeAddress(String var1, String var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         var1 = var1.toLowerCase();
         if (!this.isTypeValid(var1)) {
            throw new IllegalArgumentException();
         } else if (this.addressTable == null) {
            return false;
         } else {
            Vector var3 = (Vector)this.addressTable.get(var1);
            if (var3 == null) {
               return false;
            } else if (var2 != null && var3.contains(var2)) {
               var3.removeElement(var2);
               if (var3.isEmpty()) {
                  this.cleanupAddress(var1);
               }

               this.cleanupAppID();
               if (var2.equals(this.fromAddress)) {
                  this.fromAddress = null;
               }

               return true;
            } else {
               return false;
            }
         }
      }
   }

   public void removeAddresses() {
      this.addressTable = null;
      this.fromAddress = null;
      this.applicationID = null;
   }

   public void removeAddresses(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         var1 = var1.toLowerCase();
         if (!this.isTypeValid(var1)) {
            throw new IllegalArgumentException();
         } else {
            if (var1.equals("to")) {
               this.fromAddress = null;
            }

            if (this.addressTable != null) {
               String[] var2 = this.getAddresses(var1);
               if (var2 != null) {
                  this.cleanupAddress(var1);
                  this.cleanupAppID();
               }
            }

         }
      }
   }

   public boolean removeMessagePart(MessagePart var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return this.removeMessagePartId(var1.getContentID());
      }
   }

   public boolean removeMessagePartId(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (this.messagepartVector == null) {
         return false;
      } else {
         MessagePart var2 = this.getMessagePart(var1);
         if (var2 != null) {
            this.messagepartVector.removeElement(var2);
            this.contentLength -= var2.getLength();
            if (var1.equals(this.startContentId)) {
               this.startContentId = null;
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
      } else if (this.messagepartVector == null) {
         return false;
      } else {
         boolean var2 = false;
         int var3 = this.messagepartVector.size();
         int var4 = 0;

         for(int var5 = 0; var5 < var3; ++var5) {
            MessagePart var6 = (MessagePart)this.messagepartVector.elementAt(var4);
            if (var6.getContentLocation().equals(var1)) {
               String var7 = var6.getContentID();
               this.contentLength -= var6.getLength();
               this.removeMessagePart(var6);
               if (var7 != null && var7.equals(this.startContentId)) {
                  this.startContentId = null;
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
         int var3 = this.getHeaderIndex(var1);
         if (var3 == -2) {
            throw new SecurityException();
         } else if (!MMSAddress.validateHeader(var1, var2)) {
            throw new IllegalArgumentException();
         } else if (var3 == -1) {
            throw new IllegalArgumentException();
         } else {
            this.accessibleHeadersArray[var3] = var2;
         }
      }
   }

   public void setStartContentId(String var1) {
      if (var1 != null && this.getMessagePart(var1) == null) {
         throw new IllegalArgumentException();
      } else {
         this.startContentId = var1;
      }
   }

   public void setSubject(String var1) {
      this.subject = var1;
   }

   public Date getTimestamp() {
      Date var1 = null;
      if (this.timestamp != 0L) {
         var1 = new Date(this.timestamp);
      }

      return var1;
   }

   public int getSize() {
      return this.contentLength;
   }

   public void encodeAndSend(String var1, int var2) throws IllegalArgumentException {
      if (this.addressTable != null) {
         this._toAddresses = addressesToStringArray((Vector)this.addressTable.get("to"));
         this._ccAddresses = addressesToStringArray((Vector)this.addressTable.get("cc"));
         this._bccAddresses = addressesToStringArray((Vector)this.addressTable.get("bcc"));
      }

      if (this.messagepartVector != null) {
         this._messagepartArray = new MessagePart[this.messagepartVector.size()];
         int var3 = 0;

         for(Enumeration var4 = this.messagepartVector.elements(); var4.hasMoreElements(); ++var3) {
            this._messagepartArray[var3] = (MessagePart)var4.nextElement();
         }
      }

      this.encodeAndSendMessage(var1, var2);
   }

   public void postReceiving() throws SizeExceededException {
      this.accessibleHeadersArray = new String[2];
      if (!MMSAddress.validateAppId(this.applicationID)) {
         this.applicationID = null;
      }

      if (this.fromAddress != null) {
         if (this.fromAddress.endsWith("/TYPE=PLMN")) {
            this.fromAddress = this.fromAddress.substring(0, this.fromAddress.length() - "/TYPE=PLMN".length());
         }

         this.fromAddress = "mms://" + this.fromAddress;
         if (this.applicationID != null) {
            this.fromAddress = this.fromAddress + ':' + this.applicationID;
         }
      }

      if (this._toAddresses != null) {
         if (this.addressTable == null) {
            this.addressTable = new Hashtable(3);
         }

         this.addressTable.put("to", stringArrayToAddresses(this._toAddresses));
      }

      if (this._ccAddresses != null) {
         if (this.addressTable == null) {
            this.addressTable = new Hashtable(3);
         }

         this.addressTable.put("cc", stringArrayToAddresses(this._ccAddresses));
      }

      String var2 = this.getHeader("x-mms-delivery-time");
      if (var2 != null) {
         try {
            long var3 = Long.parseLong(var2);
            this.setHeader("x-mms-delivery-time", String.valueOf(var3 * 1000L));
         } catch (NumberFormatException var5) {
         }
      }

      if (this._messagepartArray != null) {
         for(int var1 = 0; var1 < this._messagepartArray.length; ++var1) {
            this.addMessagePart(this._messagepartArray[var1]);
         }
      }

   }

   private boolean isAppIdValid(String var1) {
      boolean var2 = true;
      if (var1 != null && this.applicationID != null && !this.applicationID.equals(var1)) {
         var2 = false;
      }

      return var2;
   }

   private boolean isAddressValid(String var1, String var2) {
      return this.isTypeValid(var1) && MMSAddress.validateUrl(var2, true);
   }

   private boolean isTypeValid(String var1) {
      if (var1 == null) {
         return false;
      } else {
         String var2 = var1.toLowerCase();
         return var2.equals("to") || var2.equals("cc") || var2.equals("bcc");
      }
   }

   private int getHeaderIndex(String var1) {
      if (var1 != null) {
         var1 = var1.toLowerCase();
         if (var1.equals("x-mms-delivery-time")) {
            return 0;
         }

         if (var1.equals("x-mms-priority")) {
            return 1;
         }

         if (var1.equals("x-mms-subject") || var1.equals("x-mms-from") || var1.equals("x-mms-to") || var1.equals("x-mms-cc") || var1.equals("x-mms-bcc")) {
            return -2;
         }
      }

      return -1;
   }

   private static boolean addressesContainAppId(String[] var0, String var1) {
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
      if (!this.isAppIdValid(var1)) {
         throw new IllegalArgumentException("Invalid Application Id");
      } else {
         if (this.applicationID == null && var1 != null) {
            this.applicationID = var1;
         }

      }
   }

   private boolean containsAppId(String var1) {
      if (var1 == null) {
         return false;
      } else {
         return addressesContainAppId(this.getAddresses("to"), var1) || addressesContainAppId(this.getAddresses("cc"), var1) || addressesContainAppId(this.getAddresses("bcc"), var1);
      }
   }

   private void cleanupAddress(String var1) {
      this.addressTable.remove(var1);
      if (this.addressTable.isEmpty()) {
         this.addressTable = null;
      }

   }

   private void cleanupAppID() {
      if (this.applicationID != null && !this.containsAppId(this.applicationID)) {
         this.applicationID = null;
      }

   }

   private static String[] addressesToStringArray(Vector var0) {
      String[] var1 = null;
      if (var0 != null) {
         var1 = new String[var0.size()];

         for(int var2 = 0; var2 < var1.length; ++var2) {
            String var3 = (String)var0.elementAt(var2);
            var3 = MMSAddress.getDeviceAddress(var3);
            if (MMSAddress.isPhoneNumber(var3)) {
               var3 = var3 + "/TYPE=PLMN";
            }

            var1[var2] = var3;
         }
      }

      return var1;
   }

   private static Vector stringArrayToAddresses(String[] var0) {
      Vector var1 = new Vector();
      if (var0 != null) {
         for(int var2 = 0; var2 < var0.length; ++var2) {
            String var3 = var0[var2];
            if (var3.endsWith("/TYPE=PLMN")) {
               var3 = var3.substring(0, var3.length() - "/TYPE=PLMN".length());
            }

            var3 = "mms://" + var3;
            var1.addElement(var3);
         }
      }

      return var1;
   }

   private String[] getStrippedAddresses(String var1) {
      String[] var2 = this.getAddresses(var1);
      String[] var3 = null;
      if (var2 != null) {
         var3 = new String[var2.length];

         for(int var4 = 0; var4 < var2.length; ++var4) {
            var3[var4] = MMSAddress.getDeviceAddress(var2[var4]);
         }
      }

      return var3;
   }

   private native void encodeAndSendMessage(String var1, int var2);
}
