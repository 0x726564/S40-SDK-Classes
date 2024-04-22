package com.nokia.mid.impl.isa.bluetooth;

import java.io.IOException;
import java.util.Enumeration;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

public class RemoteServiceRecord extends SerializedCommonServiceRecord {
   private static final int SERVICE_RECORD_NOT_FOUND = 0;
   private static final int SERVICE_RECORD_FOUND = 1;
   private static final int DEVICE_NOT_FOUND = 2;
   private static final int ATTRIBUTE_MAX_VALUE = 65535;
   private RemoteDevice remoteDevice;

   RemoteServiceRecord(RemoteDevice device, byte[] serviceRecordStream) throws NullPointerException, IllegalArgumentException {
      if (serviceRecordStream == null) {
         throw new NullPointerException("serviceRecordStream is null");
      } else if (device == null) {
         throw new NullPointerException("device is null");
      } else {
         this.remoteDevice = device;
         this.parserSetAttributes(serviceRecordStream);
      }
   }

   public RemoteDevice getHostDevice() {
      return this.remoteDevice;
   }

   public boolean populateRecord(int[] attrIDs) throws IOException {
      boolean ret = false;
      if (attrIDs == null) {
         throw new NullPointerException("attr. set is null");
      } else if (attrIDs.length == 0) {
         throw new IllegalArgumentException("Attribute set length is 0");
      } else {
         DiscoveryAgent discoveryAgent;
         try {
            discoveryAgent = LocalDevice.getLocalDevice().getDiscoveryAgent();
         } catch (BluetoothStateException var17) {
            throw new IOException("Unable to contact remote device");
         }

         UUID[] uuidSet = null;
         UUID[] uuidsAll = this.getUUIDsFromServiceRecord();
         int uniqueUUIDsNum = 0;
         int i;
         int j;
         if (uuidsAll != null) {
            for(i = 0; i < uuidsAll.length; ++i) {
               if (uuidsAll[i] != null) {
                  ++uniqueUUIDsNum;

                  for(j = i + 1; j < uuidsAll.length; ++j) {
                     if (uuidsAll[i].equals(uuidsAll[j])) {
                        uuidsAll[j] = null;
                     }
                  }
               }
            }

            uuidSet = new UUID[uniqueUUIDsNum];
            int pos = 0;

            for(i = 0; i < uuidsAll.length; ++i) {
               if (uuidsAll[i] != null) {
                  uuidSet[pos++] = uuidsAll[i];
               }
            }
         }

         Object threadLockObject = new Object();
         RemoteServiceRecord.PopulateRecordListener populateRecordListener = new RemoteServiceRecord.PopulateRecordListener(threadLockObject);
         synchronized(threadLockObject) {
            try {
               discoveryAgent.searchServices(attrIDs, uuidSet, this.remoteDevice, populateRecordListener);
            } catch (BluetoothStateException var15) {
               throw new IOException("Unable to contact remote device(service search not started)");
            }

            try {
               threadLockObject.wait();
            } catch (InterruptedException var14) {
            }
         }

         ServiceRecord foundRecord = populateRecordListener.getFoundRecord();
         if (foundRecord == null) {
            throw new IOException("Remote device(or record) could not be found");
         } else {
            int[] foundAttrIDs = foundRecord.getAttributeIDs();
            if (foundAttrIDs.length != 0) {
               for(i = 0; i < foundAttrIDs.length; ++i) {
                  if (foundAttrIDs[i] != 0) {
                     this.updateAttributeValue(foundAttrIDs[i], foundRecord.getAttributeValue(foundAttrIDs[i]));
                  }

                  if (!ret) {
                     for(j = 0; j < attrIDs.length; ++j) {
                        if (foundAttrIDs[i] == attrIDs[j]) {
                           ret = true;
                        }
                     }
                  }
               }
            }

            return ret;
         }
      }
   }

   public String getConnectionURL(int requiredSecurity, boolean mustBeMaster) {
      RemoteServiceRecord.ProtocolDescriptionParameters protocolParameters = this.getProtocolParameters();
      if (null == protocolParameters) {
         return null;
      } else {
         long channelID = protocolParameters.getChannelID();
         String protocol = protocolParameters.getProtocol();
         return this.createConnectionURL(this.remoteDevice.getBluetoothAddress(), requiredSecurity, mustBeMaster, protocol, channelID);
      }
   }

   public void setDeviceServiceClasses(int classes) {
      throw new RuntimeException("ServiceRecord was obtained from a remote device");
   }

   private RemoteServiceRecord.ProtocolDescriptionParameters getProtocolParameters() {
      UUID btsppUUID = new UUID(3L);
      UUID btl2capUUID = new UUID(256L);
      UUID btgoepUUID = new UUID(8L);
      String protocol = null;
      long channelId = 0L;
      DataElement protocolDescriptorListItem = this.getAttributeValue(4);
      if (protocolDescriptorListItem != null && protocolDescriptorListItem.getDataType() == 48) {
         Enumeration protocolDescriptorListValue = (Enumeration)protocolDescriptorListItem.getValue();
         Enumeration e = protocolDescriptorListValue;

         while(e.hasMoreElements() && !"btl2cap".equals(protocol)) {
            DataElement subitemListItem = (DataElement)e.nextElement();
            if (subitemListItem != null && subitemListItem.getDataType() == 48) {
               Enumeration subitemListValue = (Enumeration)subitemListItem.getValue();
               DataElement el1;
               if (subitemListItem.getSize() > 1) {
                  el1 = (DataElement)subitemListValue.nextElement();
                  DataElement el2 = (DataElement)subitemListValue.nextElement();
                  if (el1 != null && el2 != null && el1.getDataType() == 24 && (el2.getDataType() == 8 || el2.getDataType() == 9)) {
                     UUID el1value = (UUID)el1.getValue();
                     long el2value = el2.getLong();
                     if (btsppUUID.equals(el1value) && el2.getDataType() == 8 && validRFCOMMChannelValue(el2value)) {
                        protocol = "btspp";
                        channelId = el2value;
                     } else if (btl2capUUID.equals(el1value) && el2.getDataType() == 9 && validPSMValue(el2value)) {
                        protocol = "btl2cap";
                        channelId = el2value;
                     }
                  }
               } else {
                  el1 = (DataElement)subitemListValue.nextElement();
                  UUID el1value = (UUID)el1.getValue();
                  if (btgoepUUID.equals(el1value)) {
                     protocol = "btgoep";
                  }
               }
            }
         }

         RemoteServiceRecord.ProtocolDescriptionParameters res = null;
         if (protocol != null) {
            res = new RemoteServiceRecord.ProtocolDescriptionParameters(protocol, channelId);
         }

         return res;
      } else {
         return null;
      }
   }

   private final byte[] getBluetoothAddressAsByteArray() {
      String address = this.remoteDevice.getBluetoothAddress();
      byte[] res = new byte[6];

      for(int i = 0; i < 12; i += 2) {
         res[i / 2] = (byte)(res[i / 2] | Integer.parseInt(address.substring(i, i + 2), 16));
      }

      return res;
   }

   private final UUID[] getUUIDsFromServiceRecord() {
      UUID[] res = null;
      UUID[] temp = null;
      int[] attrIDs = this.getAttributeIDs();
      if (attrIDs.length != 0) {
         for(int i = 0; i < attrIDs.length; ++i) {
            DataElement attrValue = this.getAttributeValue(attrIDs[i]);
            switch(attrValue.getDataType()) {
            case 24:
               temp = new UUID[]{(UUID)attrValue.getValue()};
               break;
            case 48:
            case 56:
               temp = this.getUUIDsFromDSEQorDALT(attrValue);
            }

            if (temp != null) {
               if (res == null) {
                  res = temp;
               } else {
                  UUID[] new_res = new UUID[res.length + temp.length];

                  int j;
                  for(j = 0; j < res.length; ++j) {
                     new_res[j] = res[j];
                  }

                  for(j = 0; j < temp.length; ++j) {
                     new_res[res.length + j] = temp[j];
                  }

                  res = new_res;
               }
            }
         }
      }

      return res;
   }

   private final UUID[] getUUIDsFromDSEQorDALT(DataElement element) {
      UUID[] res = null;
      UUID[] temp = null;
      if (element.getDataType() != 48 && element.getDataType() != 56) {
         throw new IllegalArgumentException("Illegal data element type");
      } else {
         Enumeration e = (Enumeration)element.getValue();

         while(true) {
            while(true) {
               do {
                  if (!e.hasMoreElements()) {
                     return res;
                  }

                  DataElement currentDataElement = (DataElement)e.nextElement();
                  switch(currentDataElement.getDataType()) {
                  case 24:
                     temp = new UUID[]{(UUID)currentDataElement.getValue()};
                     break;
                  case 48:
                  case 56:
                     temp = this.getUUIDsFromDSEQorDALT(currentDataElement);
                  }
               } while(temp == null);

               if (res == null) {
                  res = temp;
               } else {
                  UUID[] new_res = new UUID[res.length + temp.length];

                  int j;
                  for(j = 0; j < res.length; ++j) {
                     new_res[j] = res[j];
                  }

                  for(j = 0; j < temp.length; ++j) {
                     new_res[res.length + j] = temp[j];
                  }

                  res = new_res;
               }
            }
         }
      }
   }

   private class PopulateRecordListener implements DiscoveryListener {
      private Object callingThreadLock;
      private ServiceRecord foundRecord;
      private byte[] currentRecordHandleValue;

      private PopulateRecordListener(Object threadLockObject) {
         this.callingThreadLock = threadLockObject;
         this.foundRecord = null;
         this.currentRecordHandleValue = this.getHandleValue(RemoteServiceRecord.this.getAttributeValue(0));
      }

      public ServiceRecord getFoundRecord() {
         return this.foundRecord;
      }

      public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
      }

      public void inquiryCompleted(int discType) {
      }

      public void serviceSearchCompleted(int transID, int respCode) {
         synchronized(this.callingThreadLock) {
            this.callingThreadLock.notify();
         }
      }

      public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
         for(int i = 0; i < servRecord.length && this.foundRecord == null; ++i) {
            byte[] tempHandleValue = this.getHandleValue(servRecord[i].getAttributeValue(0));
            if (tempHandleValue != null && tempHandleValue.length == 8 && this.currentRecordHandleValue != null && this.currentRecordHandleValue.length == 8) {
               boolean matchingRecord = true;

               for(int j = 0; j < 8; ++j) {
                  if (tempHandleValue[j] != this.currentRecordHandleValue[j]) {
                     matchingRecord = false;
                     break;
                  }
               }

               if (matchingRecord) {
                  this.foundRecord = servRecord[i];
                  break;
               }
            }
         }

      }

      private byte[] getHandleValue(DataElement handle) {
         byte[] res = null;
         if (handle != null) {
            switch(handle.getDataType()) {
            case 8:
            case 9:
            case 10:
            case 16:
            case 17:
            case 18:
            case 19:
               res = new byte[8];
               long longValue = handle.getLong();

               for(int i = 0; i < 8; ++i) {
                  res[i] = (byte)((int)((longValue & 255L << 8 * i) >> 8 * i));
               }

               return res;
            case 11:
               res = (byte[])handle.getValue();
            case 12:
            case 13:
            case 14:
            case 15:
            }
         }

         return res;
      }

      // $FF: synthetic method
      PopulateRecordListener(Object x1, Object x2) {
         this(x1);
      }
   }

   private class ProtocolDescriptionParameters {
      private long channelID;
      private String protocol;

      ProtocolDescriptionParameters(String protocol, long channelID) {
         this.channelID = channelID;
         this.protocol = new String(protocol);
      }

      private long getChannelID() {
         return this.channelID;
      }

      private String getProtocol() {
         return this.protocol;
      }
   }
}
