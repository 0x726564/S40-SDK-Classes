package com.nokia.mid.impl.isa.wireless.messaging;

import com.nokia.mid.impl.isa.io.protocol.external.mms.MMSAddress;
import com.nokia.mid.impl.isa.io.protocol.external.mms.Protocol;
import java.io.IOException;
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

   public boolean addAddress(String type, String address) {
      if (!this.isAddressValid(type, address)) {
         throw new IllegalArgumentException("Invalid Address");
      } else {
         this.setApplicationId(MMSAddress.getAppIdFromUrl(address));
         if (this.addressTable == null) {
            this.addressTable = new Hashtable(3);
         }

         Vector vect = (Vector)this.addressTable.get(type.toLowerCase());
         if (vect == null) {
            vect = new Vector();
            this.addressTable.put(type.toLowerCase(), vect);
         }

         vect.addElement(address);
         return true;
      }
   }

   public void addMessagePart(MessagePart part) throws SizeExceededException {
      if (part == null) {
         throw new NullPointerException();
      } else {
         String contentId = part.getContentID();
         if (contentId != null && this.getMessagePart(contentId) != null) {
            throw new IllegalArgumentException();
         } else if (this.contentLength + part.getLength() > Protocol.MAX_MULTIPART_MESSAGE_SIZE) {
            throw new SizeExceededException("Multipart Message content exceeds the maximum size");
         } else {
            this.contentLength += part.getLength();
            if (this.messagepartVector == null) {
               this.messagepartVector = new Vector();
            }

            this.messagepartVector.addElement(part);
         }
      }
   }

   public String getAddress() {
      if (this.fromAddress == null) {
         if (this.addressTable == null) {
            return null;
         } else {
            Vector toAddresses = (Vector)this.addressTable.get("to");
            return toAddresses == null ? null : (String)toAddresses.elementAt(0);
         }
      } else {
         return this.fromAddress;
      }
   }

   public String[] getAddresses(String type) {
      String[] addressesArray = null;
      if (type != null) {
         if (type.toLowerCase().equals("from")) {
            if (this.fromAddress != null) {
               addressesArray = new String[]{this.fromAddress};
            }
         } else if (this.addressTable != null) {
            Vector addresses = (Vector)this.addressTable.get(type.toLowerCase());
            if (addresses != null) {
               int nbAddresses = addresses.size();
               if (nbAddresses > 0) {
                  addressesArray = new String[nbAddresses];
                  addresses.copyInto(addressesArray);
               }
            }
         }
      }

      return addressesArray;
   }

   public String[][] getFormattedAddresses() {
      int to = 0;
      int cc = 1;
      int bcc = 2;
      String[][] formattedContactList = (String[][])null;
      String[][] addressesList = new String[][]{this.getStrippedAddresses("to"), this.getStrippedAddresses("cc"), this.getStrippedAddresses("bcc")};
      if (addressesList[to] != null || addressesList[cc] != null || addressesList[bcc] != null) {
         formattedContactList = addressesList;

         for(int type = to; type <= bcc; ++type) {
            if (addressesList[type] != null) {
               for(int i = 0; i < addressesList[type].length; ++i) {
                  String addr = addressesList[type][i];
                  String name = com.nokia.mid.impl.isa.io.protocol.external.sms.Protocol.phonebookLookUp(addr, MMSAddress.isPhoneNumber(addr));
                  if (name != null) {
                     formattedContactList[type][i] = name + "<" + addr + ">";
                  }
               }
            }
         }
      }

      return formattedContactList;
   }

   public String getHeader(String headerField) {
      int headerIndex = this.getHeaderIndex(headerField);
      if (headerIndex == -2) {
         throw new SecurityException();
      } else if (headerIndex == -1) {
         throw new IllegalArgumentException();
      } else {
         return this.accessibleHeadersArray[headerIndex];
      }
   }

   public MessagePart getMessagePart(String contentID) {
      if (contentID == null) {
         throw new NullPointerException();
      } else {
         if (this.messagepartVector != null) {
            Enumeration e = this.messagepartVector.elements();

            while(e.hasMoreElements()) {
               MessagePart mp = (MessagePart)e.nextElement();
               if (mp.getContentID().equals(contentID)) {
                  return mp;
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
         int size = this.messagepartVector.size();
         MessagePart[] mp = null;
         if (size > 0) {
            mp = new MessagePart[size];
            int index = 0;

            for(Enumeration e = this.messagepartVector.elements(); e.hasMoreElements(); ++index) {
               mp[index] = (MessagePart)e.nextElement();
            }
         }

         return mp;
      }
   }

   public String getStartContentId() {
      return this.startContentId;
   }

   public String getSubject() {
      return this.subject;
   }

   public boolean removeAddress(String type, String address) {
      if (type == null) {
         throw new NullPointerException();
      } else {
         type = type.toLowerCase();
         if (!this.isTypeValid(type)) {
            throw new IllegalArgumentException();
         } else if (this.addressTable == null) {
            return false;
         } else {
            Vector vect = (Vector)this.addressTable.get(type);
            if (vect == null) {
               return false;
            } else if (address != null && vect.contains(address)) {
               vect.removeElement(address);
               if (vect.isEmpty()) {
                  this.cleanupAddress(type);
               }

               if (MMSAddress.getAppIdFromAddress(address) != null) {
                  this.cleanupAppID();
               }

               if (address.equals(this.fromAddress)) {
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

   public void removeAddresses(String type) {
      if (type == null) {
         throw new NullPointerException();
      } else {
         type = type.toLowerCase();
         if (!this.isTypeValid(type)) {
            throw new IllegalArgumentException();
         } else {
            if (type.equals("to")) {
               this.fromAddress = null;
            }

            if (this.addressTable != null) {
               String[] addresses = this.getAddresses(type);
               if (addresses != null) {
                  this.cleanupAddress(type);
                  this.cleanupAppID();
               }
            }

         }
      }
   }

   public boolean removeMessagePart(MessagePart part) {
      if (part == null) {
         throw new NullPointerException();
      } else if (this.containsMessagePart(part)) {
         this.messagepartVector.removeElement(part);
         this.contentLength -= part.getLength();
         if (part.getContentID().equals(this.startContentId)) {
            this.startContentId = null;
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean removeMessagePartId(String contentId) {
      if (contentId == null) {
         throw new NullPointerException();
      } else if (this.messagepartVector == null) {
         return false;
      } else {
         MessagePart mpRemoved = this.getMessagePart(contentId);
         if (mpRemoved != null) {
            this.messagepartVector.removeElement(mpRemoved);
            this.contentLength -= mpRemoved.getLength();
            if (contentId.equals(this.startContentId)) {
               this.startContentId = null;
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public boolean removeMessagePartLocation(String contentLocation) {
      if (contentLocation == null) {
         throw new NullPointerException();
      } else if (this.messagepartVector == null) {
         return false;
      } else {
         boolean res = false;
         int originalSize = this.messagepartVector.size();
         int index = 0;

         for(int a = 0; a < originalSize; ++a) {
            MessagePart mp = (MessagePart)this.messagepartVector.elementAt(index);
            if (mp.getContentLocation().equals(contentLocation)) {
               String contentID = mp.getContentID();
               this.contentLength -= mp.getLength();
               this.removeMessagePart(mp);
               if (contentID != null && contentID.equals(this.startContentId)) {
                  this.startContentId = null;
               }

               res = true;
            } else {
               ++index;
            }
         }

         return res;
      }
   }

   public void setAddress(String address) {
      if (address != null) {
         this.addAddress("to", address);
      }

   }

   public void setHeader(String headerField, String headerValue) {
      if (headerField == null) {
         throw new NullPointerException();
      } else {
         int headerIndex = this.getHeaderIndex(headerField);
         if (headerIndex == -2) {
            throw new SecurityException();
         } else if (!MMSAddress.validateHeader(headerField, headerValue)) {
            throw new IllegalArgumentException();
         } else if (headerIndex == -1) {
            throw new IllegalArgumentException();
         } else {
            this.accessibleHeadersArray[headerIndex] = headerValue;
         }
      }
   }

   public void setStartContentId(String contentId) {
      if (contentId != null && this.getMessagePart(contentId) == null) {
         throw new IllegalArgumentException();
      } else {
         this.startContentId = contentId;
      }
   }

   public void setSubject(String subject) {
      this.subject = subject;
   }

   public Date getTimestamp() {
      Date time = null;
      if (this.timestamp != 0L) {
         time = new Date(this.timestamp);
      }

      return time;
   }

   public int getSize() {
      return this.contentLength;
   }

   public void encodeAndSend(String appId, int ucid) throws IllegalArgumentException, IOException {
      if (this.addressTable != null) {
         this._toAddresses = addressesToStringArray((Vector)this.addressTable.get("to"));
         this._ccAddresses = addressesToStringArray((Vector)this.addressTable.get("cc"));
         this._bccAddresses = addressesToStringArray((Vector)this.addressTable.get("bcc"));
      }

      if (this.messagepartVector != null) {
         this._messagepartArray = new MessagePart[this.messagepartVector.size()];
         int n = 0;

         for(Enumeration elem = this.messagepartVector.elements(); elem.hasMoreElements(); ++n) {
            this._messagepartArray[n] = (MessagePart)elem.nextElement();
         }
      }

      this.encodeAndSendMessage(appId, ucid);
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

      String time = this.getHeader("x-mms-delivery-time");
      if (time != null) {
         try {
            long l = Long.parseLong(time);
            this.setHeader("x-mms-delivery-time", String.valueOf(l * 1000L));
         } catch (NumberFormatException var5) {
         }
      }

      if (this._messagepartArray != null) {
         for(int n = 0; n < this._messagepartArray.length; ++n) {
            this.addMessagePart(this._messagepartArray[n]);
         }
      }

   }

   private boolean isAppIdValid(String newAppId) {
      boolean valid = true;
      if (newAppId != null && this.applicationID != null && !this.applicationID.equals(newAppId)) {
         valid = false;
      }

      return valid;
   }

   private boolean isAddressValid(String type, String address) {
      return this.isTypeValid(type) && MMSAddress.validateUrl(address, true);
   }

   private boolean isTypeValid(String type) {
      if (type == null) {
         return false;
      } else {
         String lcType = type.toLowerCase();
         return lcType.equals("to") || lcType.equals("cc") || lcType.equals("bcc");
      }
   }

   private int getHeaderIndex(String header) {
      if (header != null) {
         header = header.toLowerCase();
         if (header.equals("x-mms-delivery-time")) {
            return 0;
         }

         if (header.equals("x-mms-priority")) {
            return 1;
         }

         if (header.equals("x-mms-subject") || header.equals("x-mms-from") || header.equals("x-mms-to") || header.equals("x-mms-cc") || header.equals("x-mms-bcc")) {
            return -2;
         }
      }

      return -1;
   }

   private static boolean addressesContainAppId(String[] addrs, String appId) {
      if (addrs != null && appId != null) {
         for(int a = 0; a < addrs.length; ++a) {
            String appIdFromUrl = MMSAddress.getAppIdFromUrl(addrs[a]);
            if (appIdFromUrl != null && appIdFromUrl.equals(appId)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private void setApplicationId(String appId) throws IllegalArgumentException {
      if (!this.isAppIdValid(appId)) {
         throw new IllegalArgumentException("Invalid Application Id");
      } else {
         if (this.applicationID == null && appId != null) {
            this.applicationID = appId;
         }

      }
   }

   private boolean containsAppId(String appId) {
      if (appId == null) {
         return false;
      } else {
         return addressesContainAppId(this.getAddresses("to"), appId) || addressesContainAppId(this.getAddresses("cc"), appId) || addressesContainAppId(this.getAddresses("bcc"), appId);
      }
   }

   private void cleanupAddress(String type) {
      this.addressTable.remove(type);
      if (this.addressTable.isEmpty()) {
         this.addressTable = null;
      }

   }

   private void cleanupAppID() {
      if (this.applicationID != null && !this.containsAppId(this.applicationID)) {
         this.applicationID = null;
      }

   }

   private static String[] addressesToStringArray(Vector vect) {
      String[] strs = null;
      if (vect != null) {
         strs = new String[vect.size()];

         for(int a = 0; a < strs.length; ++a) {
            String str = (String)vect.elementAt(a);
            str = MMSAddress.getDeviceAddress(str);
            if (MMSAddress.isPhoneNumber(str)) {
               str = str + "/TYPE=PLMN";
            }

            strs[a] = str;
         }
      }

      return strs;
   }

   private static Vector stringArrayToAddresses(String[] strs) {
      Vector vect = new Vector();
      if (strs != null) {
         for(int n = 0; n < strs.length; ++n) {
            String str = strs[n];
            if (str.endsWith("/TYPE=PLMN")) {
               str = str.substring(0, str.length() - "/TYPE=PLMN".length());
            }

            str = "mms://" + str;
            vect.addElement(str);
         }
      }

      return vect;
   }

   private String[] getStrippedAddresses(String type) {
      String[] addrs = this.getAddresses(type);
      String[] stripped = null;
      if (addrs != null) {
         stripped = new String[addrs.length];

         for(int a = 0; a < addrs.length; ++a) {
            stripped[a] = MMSAddress.getDeviceAddress(addrs[a]);
         }
      }

      return stripped;
   }

   private boolean containsMessagePart(MessagePart mp) {
      if (this.messagepartVector != null) {
         Enumeration e = this.messagepartVector.elements();

         while(e.hasMoreElements()) {
            MessagePart mpart = (MessagePart)e.nextElement();
            if (mpart == mp) {
               return true;
            }
         }
      }

      return false;
   }

   private native void encodeAndSendMessage(String var1, int var2) throws IOException;
}
