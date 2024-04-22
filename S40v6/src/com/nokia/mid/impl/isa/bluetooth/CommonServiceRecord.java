package com.nokia.mid.impl.isa.bluetooth;

import java.util.Enumeration;
import java.util.Vector;
import javax.bluetooth.DataElement;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

public abstract class CommonServiceRecord implements ServiceRecord {
   public static final int RFCOMM_UUID = 3;
   public static final int OBEX_UUID = 8;
   public static final int L2CAP_UUID = 256;
   public static final int SERIAL_PORT_UUID = 4353;
   public static final int SERVICE_RECORD_HANDLE_ID = 0;
   public static final int SERVICE_CLASS_IDLIST_ID = 1;
   public static final int SERVICE_RECORD_STATE_ID = 2;
   public static final int SERVICE_ID = 3;
   public static final int PROTOCOL_DESCRIPTOR_LIST_ID = 4;
   public static final int SERVICE_NAME_ID = 256;
   private static final long MIN_RFCOMM_CHANNEL_ID = 1L;
   private static final long MAX_RFCOMM_CHANNEL_ID = 30L;
   private static final long MIN_PSM_VALUE = 4097L;
   private static final long MAX_PSM_VALUE = 65535L;
   protected Vector attrCollection = new Vector();
   protected final Object serializeLock = new Object();

   public static boolean validRFCOMMChannelValue(long value) {
      return value >= 1L && value <= 30L;
   }

   public static boolean validPSMValue(long value) {
      return value >= 4097L && value <= 65535L && (value & 72340172838076673L) == 1L;
   }

   public int[] getAttributeIDs() {
      synchronized(this.attrCollection) {
         int i = 0;
         int[] attrIDs = new int[this.attrCollection.size()];

         for(Enumeration e = this.attrCollection.elements(); e.hasMoreElements(); attrIDs[i++] = ((CommonServiceRecord.AttributePair)e.nextElement()).getAttrID()) {
         }

         return attrIDs;
      }
   }

   public DataElement getAttributeValue(int attrID) {
      if (attrID >= 0 && attrID <= 65535) {
         synchronized(this.attrCollection) {
            Enumeration e = this.attrCollection.elements();

            CommonServiceRecord.AttributePair attrPair;
            do {
               if (!e.hasMoreElements()) {
                  return null;
               }

               attrPair = (CommonServiceRecord.AttributePair)e.nextElement();
            } while(attrPair.getAttrID() != attrID);

            return attrPair.getAttrValue();
         }
      } else {
         throw new IllegalArgumentException("attribute ID out of range: " + attrID);
      }
   }

   boolean updateAttributeValue(int attrID, DataElement attrValue) {
      if (attrID >= 0 && attrID <= 65535) {
         synchronized(this.attrCollection) {
            int length = this.attrCollection.size();

            for(int i = 0; i < length; ++i) {
               CommonServiceRecord.AttributePair attrPair = (CommonServiceRecord.AttributePair)this.attrCollection.elementAt(i);
               if (attrPair.getAttrID() == attrID) {
                  if (attrValue == null) {
                     this.attrCollection.removeElementAt(i);
                     return true;
                  }

                  attrPair.replaceAttrValue(attrValue);
                  return true;
               }
            }

            if (attrValue != null) {
               this.attrCollection.addElement(new CommonServiceRecord.AttributePair(attrID, attrValue));
               return true;
            } else {
               return false;
            }
         }
      } else {
         throw new IllegalArgumentException("invalid attribute ID");
      }
   }

   public boolean setAttributeValue(int attrID, DataElement attrValue) {
      if (this instanceof RemoteServiceRecord) {
         throw new RuntimeException("object does not represents a service from remote");
      } else if (attrID == 0) {
         throw new IllegalArgumentException("invalid attribute ID");
      } else {
         return this.updateAttributeValue(attrID, attrValue);
      }
   }

   protected String createConnectionURL(String address, int requiredSecurity, boolean mustBeMaster, String protocol, long channelID) throws IllegalArgumentException {
      if (requiredSecurity != 0 && requiredSecurity != 1 && requiredSecurity != 2) {
         throw new IllegalArgumentException("Security is not one of the allowed values(NOAUTHENTICATE_NOENCRYPT,AUTHENTICATE_NOENCRYPT,AUTHENTICATE_ENCRYPT)");
      } else if (null == protocol || !protocol.equals("btspp") && !protocol.equals("btl2cap") && !protocol.equals("btgoep")) {
         throw new Error("createConnectionURL, protocol param must be btspp, btl2cap or btgoep");
      } else if (address == null) {
         return null;
      } else {
         String connectionString = new String(protocol);
         connectionString = connectionString.concat("://");
         connectionString = connectionString.concat(address);
         connectionString = connectionString.concat(":");
         if (protocol.equals("btl2cap")) {
            connectionString = connectionString.concat(Integer.toHexString((int)(channelID & 61440L) >> 12));
            connectionString = connectionString.concat(Integer.toHexString((int)(channelID & 3840L) >> 8));
            connectionString = connectionString.concat(Integer.toHexString((int)(channelID & 240L) >> 4));
            connectionString = connectionString.concat(Integer.toHexString((int)(channelID & 15L)));
         } else {
            connectionString = connectionString.concat(Long.toString(channelID));
         }

         switch(requiredSecurity) {
         case 0:
            connectionString = connectionString.concat(";authenticate=false;encrypt=false");
            break;
         case 1:
            connectionString = connectionString.concat(";authenticate=true;encrypt=false");
            break;
         case 2:
            connectionString = connectionString.concat(";authenticate=true;encrypt=true");
         }

         if (mustBeMaster) {
            connectionString = connectionString.concat(";master=true");
         } else {
            connectionString = connectionString.concat(";master=false");
         }

         return connectionString;
      }
   }

   public DataElement checkProtocolDescriptorListForProtocolUuid(UUID protocolUuid) throws NullPointerException {
      DataElement protocolDescriptorListItem = this.getAttributeValue(4);
      if (protocolDescriptorListItem != null && protocolDescriptorListItem.getDataType() == 48) {
         Enumeration protocolDescriptorListValue = (Enumeration)protocolDescriptorListItem.getValue();
         Enumeration e = protocolDescriptorListValue;

         while(true) {
            DataElement subitemListItem;
            do {
               do {
                  if (!e.hasMoreElements()) {
                     throw new NullPointerException();
                  }

                  subitemListItem = (DataElement)e.nextElement();
               } while(subitemListItem == null);
            } while(subitemListItem.getDataType() != 48);

            Enumeration subitemListValue = (Enumeration)subitemListItem.getValue();
            Enumeration i = subitemListValue;

            while(i.hasMoreElements()) {
               DataElement el1 = (DataElement)i.nextElement();
               if (el1 != null && el1.getDataType() == 24 && protocolUuid.equals((UUID)el1.getValue())) {
                  if (i.hasMoreElements()) {
                     return (DataElement)i.nextElement();
                  }

                  return null;
               }
            }
         }
      } else {
         throw new NullPointerException();
      }
   }

   private class AttributePair {
      int attrID;
      DataElement attrValue;

      AttributePair(int attrID, DataElement attrValue) {
         this.attrID = attrID;
         this.attrValue = attrValue;
      }

      private int getAttrID() {
         return this.attrID;
      }

      private DataElement getAttrValue() {
         return this.attrValue;
      }

      private void replaceAttrValue(DataElement attriValue) {
         this.attrValue = attriValue;
      }
   }
}
