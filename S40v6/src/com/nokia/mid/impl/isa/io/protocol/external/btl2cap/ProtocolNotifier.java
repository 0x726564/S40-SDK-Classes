package com.nokia.mid.impl.isa.io.protocol.external.btl2cap;

import com.nokia.mid.impl.isa.bluetooth.CommonBluetooth;
import com.nokia.mid.impl.isa.bluetooth.CommonNotifier;
import com.nokia.mid.impl.isa.bluetooth.LocalServiceRecord;
import com.nokia.mid.impl.isa.bluetooth.ServiceRecordAccessor;
import java.io.IOException;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DataElement;
import javax.bluetooth.L2CAPConnection;
import javax.bluetooth.L2CAPConnectionNotifier;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.ServiceRegistrationException;
import javax.bluetooth.UUID;

public class ProtocolNotifier extends Protocol implements CommonNotifier, L2CAPConnectionNotifier, ServiceRecordAccessor {
   private final int SERVICE_HANDLE_NOT_INITIALIZED = 0;
   private static final int DEFAULT_PSM_VALUE = 24577;
   private static final int PSM_VALUE_NOT_SET = 0;
   private static final int SERVICE_NOT_REGISTERED = 0;
   private static final int SERVICE_REGISTERED = 1;
   private static final int SERVICE_ENABLED = 2;
   private byte serviceHandle = 0;
   private long sdpHandle = 0L;
   private byte options;
   private int channelID = 24577;
   private int channelIDUpdatedValue = 0;
   private boolean isChannelIDUpdated = false;
   private int serviceStatus = 0;
   private boolean activeAccept = false;
   private boolean transmitMTUSpecifiedInConnURL = false;
   private LocalServiceRecord localService = new LocalServiceRecord(this);
   private final Object acceptAndOpenLock = new Object();
   private boolean isPushNotifier = false;
   private byte[] serviceRecordOfThePushNotifier = null;
   private int deviceServiceClassesOfThePushNotifier = 0;
   private byte[] url_as_byte_stream = null;

   ProtocolNotifier(String url) throws IOException {
      this.url_as_byte_stream = url.getBytes();
   }

   public L2CAPConnection acceptAndOpen() throws IOException {
      synchronized(this.acceptAndOpenLock) {
         ProtocolConnection protocolConnection = null;

         while(protocolConnection == null) {
            synchronized(this.localService.getLockObject()) {
               if (this.isClosed) {
                  throw new IOException("closed was called before");
               }

               if (!this.isPushNotifier && this.serviceStatus == 0) {
                  throw new Error("service should be registered at that point");
               }

               try {
                  if (!this.checkServiceOkToUpdate(this.localService)) {
                     throw new Exception("Service not OK");
                  }
               } catch (Exception var11) {
                  throw new ServiceRegistrationException("Invalid service record");
               }

               this.serviceStatus = 2;
               this.updateService(this.localService);
               this.activeAccept = true;
            }

            protocolConnection = new ProtocolConnection(false, this.mode);

            try {
               if (!this.isPushNotifier) {
                  this.acceptAndOpen0(protocolConnection, this.serviceHandle);
               } else {
                  try {
                     boolean acceptAndOpenFinished = false;

                     while(!acceptAndOpenFinished) {
                        try {
                           this.acceptAndOpenPush(protocolConnection, this.url_as_byte_stream);
                           acceptAndOpenFinished = true;
                        } catch (BluetoothStateException var8) {
                           byte bluetoothState = CommonBluetooth.activateMedia();
                           if (1 == bluetoothState) {
                              throw new IOException("BT System is not active");
                           }
                        }
                     }
                  } catch (ServiceRegistrationException var9) {
                     byte bluetoothState = CommonBluetooth.activateMedia();
                     if (1 == bluetoothState) {
                        throw new IOException("BT System is not active");
                     }

                     if (0 == this.serviceHandle) {
                        this.checkAndRegisterService();
                     }

                     this.acceptAndOpen0(protocolConnection, this.serviceHandle);
                  }
               }
            } catch (IOException var10) {
               this.activeAccept = false;
               protocolConnection = null;
               throw var10;
            }

            this.activeAccept = false;
            if (this.transmitMTUSpecifiedInConnURL) {
               if (this.transmitMTU <= protocolConnection.getTransmitMTU()) {
                  protocolConnection.setTransmitMTU(this.transmitMTU);
               } else {
                  try {
                     protocolConnection.close();
                  } catch (Exception var7) {
                  }

                  protocolConnection = null;
               }
            }
         }

         return protocolConnection;
      }
   }

   public void close() throws IOException {
      synchronized(this.localService.getLockObject()) {
         if (!this.isClosed) {
            this.isClosed = true;
            this.unregisterService();
            if (this.isPushNotifier) {
               this.pushNotifierClosed0(this.url_as_byte_stream);
               this.serviceRecordOfThePushNotifier = null;
            }

            if (0 != this.serviceHandle) {
               while(this.activeAccept) {
                  if (this.attemptClose0(this.serviceHandle)) {
                     return;
                  }

                  try {
                     Thread.sleep(500L);
                  } catch (InterruptedException var4) {
                  }
               }
            }
         }

      }
   }

   public void checkAndRegisterService() throws ServiceRegistrationException, NullPointerException, IOException {
      synchronized(this.localService.getLockObject()) {
         if (this.serviceStatus != 0) {
            throw new Error("Service should not be registered at that point!");
         } else {
            try {
               if (!this.checkServiceOkToUpdate(this.localService)) {
                  throw new Exception("Service not OK");
               }
            } catch (Exception var4) {
               throw new ServiceRegistrationException("Invalid service record");
            }

            this.registerService(this.localService);
         }
      }
   }

   public void registerService(LocalServiceRecord record) throws ServiceRegistrationException, NullPointerException, IOException {
      if (record == null) {
         throw new NullPointerException("passed record is null");
      } else {
         byte[] serviceRecord = record.parserSerialize();
         long retval = this.addService0(serviceRecord, serviceRecord.length, record.getDeviceServiceClasses(), this.options, this.receiveMTU, this.transmitMTU);
         this.channelID = (int)(retval & 65535L);
         this.serviceHandle = (byte)((int)((retval & 16711680L) >> 16));
         this.sdpHandle = (retval & 72057594021150720L) >> 24;
         this.localService.initServiceRecordHandle(this.sdpHandle);
         this.setPSM(this.localService, this.channelID);
         this.serviceStatus = 1;
      }
   }

   public void unregisterService() throws IOException {
      if (this.serviceStatus != 0 && 0 != this.serviceHandle) {
         if (!this.removeService0(this.serviceHandle)) {
            throw new IOException("SDDB service unregistration failed");
         } else {
            this.serviceStatus = 0;
            this.channelID = 24577;
         }
      }
   }

   public void updateService(LocalServiceRecord record) throws ServiceRegistrationException, NullPointerException {
      if (record == null) {
         throw new NullPointerException("passed record is null");
      } else {
         if (2 == this.serviceStatus || !this.isClosed && this.isPushNotifier) {
            byte[] serviceRecord;
            if (0 != this.serviceHandle) {
               if (this.isChannelIDUpdated && this.channelID != this.channelIDUpdatedValue) {
                  this.setPSM(record, this.channelIDUpdatedValue);
                  this.channelID = this.channelIDUpdatedValue;
               }

               serviceRecord = record.parserSerialize();

               try {
                  byte retval = this.updateService0(serviceRecord, serviceRecord.length, record.getDeviceServiceClasses(), this.options, this.serviceHandle);
                  if (retval != this.serviceHandle) {
                     throw new Error("updateService0 returned wrong serviceHandle");
                  }

                  if (this.isPushNotifier) {
                     this.serviceRecordOfThePushNotifier = serviceRecord;
                  }

                  record.setLockObject(this.localService.getLockObject());
                  this.localService = record;
               } catch (ServiceRegistrationException var5) {
                  throw var5;
               } catch (IOException var6) {
               }
            } else {
               if (null == this.serviceRecordOfThePushNotifier) {
                  throw new Error("(JSR82 PUSH) Service record content is unknown");
               }

               serviceRecord = record.parserSerialize();
               boolean recordsEqual = false;
               if (this.deviceServiceClassesOfThePushNotifier == record.getDeviceServiceClasses() && serviceRecord.length == this.serviceRecordOfThePushNotifier.length) {
                  for(int i = 0; i < serviceRecord.length && serviceRecord[i] == this.serviceRecordOfThePushNotifier[i]; ++i) {
                  }

                  recordsEqual = true;
               }

               if (!recordsEqual) {
                  throw new ServiceRegistrationException("Service registration/updating failed");
               }
            }
         }

      }
   }

   public boolean checkServiceOkToUpdate(LocalServiceRecord record) {
      if (null != record.getAttributeValue(1) && null != record.getAttributeValue(4)) {
         try {
            UUID btl2capUUID = new UUID(256L);
            DataElement newPsmDataElement = record.checkProtocolDescriptorListForProtocolUuid(btl2capUUID);
            if (newPsmDataElement == null) {
               return false;
            } else if (newPsmDataElement.getDataType() != 9) {
               return false;
            } else {
               return newPsmDataElement.getLong() == ((long)this.channelID & 65535L);
            }
         } catch (Exception var4) {
            return false;
         }
      } else {
         throw new IllegalArgumentException("mandatory serviceRecord attributes missing");
      }
   }

   public LocalServiceRecord getLocalServiceRecord() {
      if (this.localService != null) {
         this.localService.initServiceRecordHandle(this.sdpHandle);
      }

      return this.localService;
   }

   public void setOptions(byte options) {
      this.options = options;
   }

   public void setTransmitMTUSpecifiedInConnURL() {
      this.transmitMTUSpecifiedInConnURL = true;
   }

   public long getChannelID() {
      long result = (long)this.channelID;
      if (this.isChannelIDUpdated) {
         result = (long)this.channelIDUpdatedValue;
      }

      return result;
   }

   public String getProtocol() {
      return new String("btl2cap");
   }

   private void acceptAndOpenPush(ProtocolConnection protocolConnection, byte[] url) throws IOException {
      this.acceptAndOpenPush0(protocolConnection, url);
      protocolConnection.setPushConnection();
   }

   private void setPSM(ServiceRecord record, int psm) {
      DataElement protocolDescriptorListItem = record.getAttributeValue(4);
      if (protocolDescriptorListItem != null && protocolDescriptorListItem.getDataType() == 48) {
         UUID l2capUUID = new UUID(256L);
         Enumeration protocolDescriptorListValue = (Enumeration)protocolDescriptorListItem.getValue();

         label47:
         while(true) {
            DataElement subItem;
            do {
               if (!protocolDescriptorListValue.hasMoreElements()) {
                  throw new RuntimeException("Error in ProtocolNotifier.setPSM(): service record item not found");
               }

               subItem = (DataElement)protocolDescriptorListValue.nextElement();
            } while(subItem.getDataType() != 48);

            Enumeration subItemValue = (Enumeration)subItem.getValue();

            while(true) {
               DataElement protocol;
               do {
                  do {
                     if (!subItemValue.hasMoreElements()) {
                        continue label47;
                     }

                     protocol = (DataElement)subItemValue.nextElement();
                  } while(protocol.getDataType() != 24);
               } while(!protocol.getValue().equals(l2capUUID));

               try {
                  DataElement protocolSpecificParameter = (DataElement)subItemValue.nextElement();
                  if (protocolSpecificParameter.getDataType() == 9) {
                     subItem.removeElement(protocolSpecificParameter);
                     subItem.addElement(new DataElement(9, (long)psm));
                     record.setAttributeValue(4, protocolDescriptorListItem);
                     return;
                  }

                  throw new RuntimeException("Error in ProtocolNotifier.setPSM(): protocolSpecificParameter");
               } catch (NoSuchElementException var11) {
               }
            }
         }
      } else {
         throw new RuntimeException("Error in ProtocolNotifier.setPSM(): protocolDescriptorListItem");
      }
   }

   protected boolean isNotifierForPushService() {
      return this.isPushNotifier;
   }

   protected byte[] getServiceRecordOfThePushNotifier() {
      return this.serviceRecordOfThePushNotifier;
   }

   protected void createLocalPushServiceRecord() {
      this.localService.initRecord(this.serviceRecordOfThePushNotifier);
      this.localService.initServiceRecordHandle(this.sdpHandle);
      this.setPSM(this.localService, this.channelID);
   }

   private native long addService0(byte[] var1, int var2, int var3, byte var4, int var5, int var6) throws ServiceRegistrationException;

   private native byte updateService0(byte[] var1, int var2, int var3, byte var4, byte var5) throws ServiceRegistrationException, IOException;

   private native void acceptAndOpen0(ProtocolConnection var1, byte var2) throws IOException;

   private native void acceptAndOpenPush0(ProtocolConnection var1, byte[] var2) throws IOException;

   private native boolean removeService0(byte var1);

   private native boolean attemptClose0(byte var1);

   private native void pushNotifierClosed0(byte[] var1);
}
