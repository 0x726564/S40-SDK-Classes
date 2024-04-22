package com.nokia.mid.impl.isa.io.protocol.external.btspp;

import com.nokia.mid.impl.isa.bluetooth.CommonBluetooth;
import com.nokia.mid.impl.isa.bluetooth.CommonNotifier;
import com.nokia.mid.impl.isa.bluetooth.LocalServiceRecord;
import com.nokia.mid.impl.isa.bluetooth.ServiceRecordAccessor;
import com.nokia.mid.impl.isa.bluetooth.Tracer;
import java.io.IOException;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DataElement;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.ServiceRegistrationException;
import javax.bluetooth.UUID;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class ProtocolNotifier extends Protocol implements CommonNotifier, StreamConnectionNotifier, ServiceRecordAccessor {
   private final int SERVICE_HANDLE_NOT_INITIALIZED = 0;
   private static final int SERVICE_NOT_REGISTERED = 0;
   private static final int SERVICE_REGISTERED = 1;
   private static final int SERVICE_ENABLED = 2;
   private static final int DEFAULT_CHANNEL_ID = 2;
   private byte serviceHandle = 0;
   private long sdpHandle = 0L;
   private byte options;
   private int channelID = 2;
   private int channelIDUpdatedValue;
   private boolean isChannelIDUpdated = false;
   private int serviceStatus = 0;
   private boolean activeAccept = false;
   private LocalServiceRecord localService;
   private final Object acceptAndOpenLock = new Object();
   private boolean isPushNotifier = false;
   private byte[] serviceRecordOfThePushNotifier = null;
   private int deviceServiceClassesOfThePushNotifier = 0;
   private byte[] url_as_byte_stream = null;

   ProtocolNotifier(String url) throws IOException {
      Tracer.println("ProtocolNotifier(url=" + url + ")");
      this.localService = new LocalServiceRecord(this);
      this.url_as_byte_stream = url.getBytes();
   }

   public StreamConnection acceptAndOpen() throws IOException {
      synchronized(this.acceptAndOpenLock) {
         synchronized(this.localService.getLockObject()) {
            Tracer.println("acceptAndOpen");
            if (this.isClosed) {
               Tracer.println("IOException Notifier closed");
               throw new IOException("closed was called before");
            }

            if (!this.isPushNotifier && this.serviceStatus == 0) {
               Tracer.println("Error reg.");
               throw new Error("service should be registered at that point");
            }

            try {
               if (!this.checkServiceOkToUpdate(this.localService)) {
                  Tracer.println("Exception serv.");
                  throw new Exception("Service not OK");
               }
            } catch (Exception var10) {
               Tracer.println("ServiceRegistrationException invalid");
               throw new ServiceRegistrationException("Invalid service record");
            }

            this.serviceStatus = 2;
            this.updateService(this.localService);
            this.activeAccept = true;
         }

         ProtocolConnection protocolConnection = new ProtocolConnection(false, this.mode);

         try {
            if (!this.isPushNotifier) {
               Tracer.println("NON-PUSH acceptAndOpen0");
               this.acceptAndOpen0(protocolConnection, this.serviceHandle);
            } else {
               try {
                  boolean acceptAndOpenFinished = false;

                  while(!acceptAndOpenFinished) {
                     try {
                        this.acceptAndOpenPush(protocolConnection, this.url_as_byte_stream);
                        acceptAndOpenFinished = true;
                     } catch (BluetoothStateException var7) {
                        Tracer.println("caught BluetoothStateException activating Media");
                        byte bluetoothState = CommonBluetooth.activateMedia();
                        if (1 == bluetoothState) {
                           Tracer.println("IOException BT OFF");
                           throw new IOException("BT System is not active");
                        }
                     }
                  }
               } catch (ServiceRegistrationException var8) {
                  Tracer.println("caught ServiceRegistrationException activating media");
                  byte bluetoothState = CommonBluetooth.activateMedia();
                  if (1 == bluetoothState) {
                     Tracer.println("IOException BT OFF");
                     throw new IOException("BT System is not active");
                  }

                  if (0 == this.serviceHandle) {
                     this.checkAndRegisterService();
                  }

                  Tracer.println("acceptAndOpen0");
                  this.acceptAndOpen0(protocolConnection, this.serviceHandle);
               }
            }
         } catch (IOException var9) {
            this.activeAccept = false;
            protocolConnection = null;
            Tracer.println("IOException");
            throw var9;
         }

         this.activeAccept = false;
         Tracer.println("return protocolConnection");
         return protocolConnection;
      }
   }

   public void close() throws IOException {
      synchronized(this.localService.getLockObject()) {
         if (!this.isClosed) {
            Tracer.println("Notifier.close");
            this.isClosed = true;
            this.unregisterService();
            if (this.isPushNotifier) {
               Tracer.println("pushNotifierClosed0");
               this.pushNotifierClosed0(this.url_as_byte_stream);
               this.serviceRecordOfThePushNotifier = null;
            }

            if (0 != this.serviceHandle) {
               while(this.activeAccept) {
                  Tracer.println("attemptClose0");
                  if (this.attemptClose0(this.serviceHandle)) {
                     return;
                  }

                  try {
                     Tracer.println("sleep(500)");
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
         Tracer.println("checkAndRegisterService");
         if (this.serviceStatus != 0) {
            Tracer.println("Error reg.");
            throw new Error("Service should not be registered at that point!");
         } else {
            try {
               if (!this.checkServiceOkToUpdate(this.localService)) {
                  Tracer.println("Exception serv.");
                  throw new Exception("Service not OK");
               }
            } catch (Exception var4) {
               Tracer.println("ServiceRegistrationException invalid");
               throw new ServiceRegistrationException("Invalid service record");
            }

            this.registerService(this.localService);
         }
      }
   }

   public void registerService(LocalServiceRecord record) throws ServiceRegistrationException, NullPointerException, IOException {
      Tracer.println("registerService");
      if (record == null) {
         Tracer.println("NullPointerException");
         throw new NullPointerException("passed record is null");
      } else {
         byte[] serviceRecord = record.parserSerialize();
         Tracer.println("addService0");
         long retval = this.addService0(serviceRecord, serviceRecord.length, record.getDeviceServiceClasses(), this.options);
         this.channelID = (int)(retval & 255L);
         this.serviceHandle = (byte)((int)((retval & 65280L) >> 8));
         this.sdpHandle = (retval & 281474976645120L) >> 16;
         this.localService.initServiceRecordHandle(this.sdpHandle);
         this.setServerChannel(this.localService, this.channelID);
         this.serviceStatus = 1;
      }
   }

   public void unregisterService() throws IOException {
      Tracer.println("unregisterService");
      if (this.serviceStatus != 0 && 0 != this.serviceHandle) {
         Tracer.println("removeService0");
         if (!this.removeService0(this.serviceHandle)) {
            throw new IOException("SDDB service unregistration failed");
         } else {
            this.serviceStatus = 0;
            this.channelID = 2;
         }
      }
   }

   public void updateService(LocalServiceRecord record) throws ServiceRegistrationException, NullPointerException {
      Tracer.println("updateService");
      if (record == null) {
         Tracer.println("NullPointerException");
         throw new NullPointerException("passed record is null");
      } else {
         if (2 == this.serviceStatus || !this.isClosed && this.isPushNotifier) {
            byte[] serviceRecord;
            if (0 != this.serviceHandle) {
               if (this.isChannelIDUpdated && this.channelID != this.channelIDUpdatedValue) {
                  this.setServerChannel(record, this.channelIDUpdatedValue);
                  this.channelID = this.channelIDUpdatedValue;
               }

               serviceRecord = record.parserSerialize();

               try {
                  Tracer.println("updateService0");
                  byte retval = this.updateService0(serviceRecord, serviceRecord.length, record.getDeviceServiceClasses(), this.options, this.serviceHandle);
                  if (retval != this.serviceHandle) {
                     Tracer.println("Error han");
                     throw new Error("updateService0 returned wrong serviceHandle");
                  }

                  if (this.isPushNotifier) {
                     Tracer.println("set serviceRecordOfThePushNotifier");
                     this.serviceRecordOfThePushNotifier = serviceRecord;
                  }

                  record.setLockObject(this.localService.getLockObject());
                  this.localService = record;
               } catch (ServiceRegistrationException var5) {
                  throw var5;
               } catch (IOException var6) {
                  Tracer.println("caught IOException");
               }
            } else {
               if (null == this.serviceRecordOfThePushNotifier) {
                  Tracer.println("Error cont");
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
                  Tracer.println("ServiceRegistrationException uneq");
                  throw new ServiceRegistrationException("Service registration/updating failed");
               }
            }
         }

      }
   }

   public boolean checkServiceOkToUpdate(LocalServiceRecord record) {
      Tracer.println("checkServiceOkToUpdate");
      if (null != record.getAttributeValue(1) && null != record.getAttributeValue(4)) {
         try {
            UUID btl2capUUID = new UUID(256L);

            try {
               if (record.checkProtocolDescriptorListForProtocolUuid(btl2capUUID) != null) {
                  Tracer.println("not allowed");
                  return false;
               }
            } catch (NullPointerException var5) {
               Tracer.println("UUID not found");
               return false;
            }

            UUID btRfcommUUID = new UUID(3L);
            DataElement newScDataElement = record.checkProtocolDescriptorListForProtocolUuid(btRfcommUUID);
            if (newScDataElement == null) {
               Tracer.println("elem=null!");
               return false;
            } else if (newScDataElement.getDataType() != 8) {
               Tracer.println("elem not found!");
               return false;
            } else if (newScDataElement.getLong() != ((long)this.channelID & 255L)) {
               Tracer.println("elem_L not found!");
               return false;
            } else {
               return true;
            }
         } catch (Exception var6) {
            Tracer.println("anything wrong!");
            return false;
         }
      } else {
         Tracer.println("IllegalArgumentException");
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
      Tracer.println("setOptions()=" + String.valueOf((int)options));
   }

   public long getChannelID() {
      long result = (long)this.channelID;
      if (this.isChannelIDUpdated) {
         result = (long)this.channelIDUpdatedValue;
      }

      return result;
   }

   public String getProtocol() {
      UUID btgoepUUID = new UUID(8L);

      DataElement protocolDescriptorList;
      try {
         protocolDescriptorList = LocalDevice.getLocalDevice().getRecord(this).getAttributeValue(4);
      } catch (BluetoothStateException var9) {
         throw new NullPointerException("Local Service URL cannot be retrieved presently");
      }

      if (protocolDescriptorList != null && protocolDescriptorList.getDataType() == 48) {
         Enumeration protocolDescriptorListValue = (Enumeration)protocolDescriptorList.getValue();
         Enumeration e = protocolDescriptorListValue;

         while(e.hasMoreElements()) {
            DataElement subitemListItem = (DataElement)e.nextElement();
            if (subitemListItem != null && subitemListItem.getDataType() == 48) {
               Enumeration subitemListValue = (Enumeration)subitemListItem.getValue();
               DataElement uuidEl = (DataElement)subitemListValue.nextElement();
               UUID theUUID = (UUID)uuidEl.getValue();
               if (btgoepUUID.equals(theUUID)) {
                  return new String("btgoep");
               }
            }
         }

         return new String("btspp");
      } else {
         return new String("btspp");
      }
   }

   private void acceptAndOpenPush(ProtocolConnection protocolConnection, byte[] url) throws IOException {
      Tracer.println("acceptAndOpenPush calling acceptAndOpenPush0");
      this.acceptAndOpenPush0(protocolConnection, url);
      protocolConnection.setPushConnection();
   }

   private void setServerChannel(ServiceRecord record, int channel) {
      Tracer.println("setServerChannel()=" + String.valueOf(channel));
      DataElement protocolDescriptorListItem = record.getAttributeValue(4);
      if (protocolDescriptorListItem != null && protocolDescriptorListItem.getDataType() == 48) {
         UUID rfcommUUID = new UUID(3L);
         Enumeration protocolDescriptorListValue = (Enumeration)protocolDescriptorListItem.getValue();

         label47:
         while(true) {
            DataElement subItem;
            do {
               if (!protocolDescriptorListValue.hasMoreElements()) {
                  Tracer.println("RuntimeException not found");
                  throw new RuntimeException("Error in setServerChannel:service record item not found");
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
               } while(!protocol.getValue().equals(rfcommUUID));

               try {
                  DataElement protocolSpecificParameter = (DataElement)subItemValue.nextElement();
                  if (protocolSpecificParameter.getDataType() == 8) {
                     subItem.removeElement(protocolSpecificParameter);
                     subItem.addElement(new DataElement(8, (long)channel));
                     record.setAttributeValue(4, protocolDescriptorListItem);
                     return;
                  }

                  Tracer.println("RuntimeException chan");
                  throw new RuntimeException("Error in setServerChannel:protocolSpecificParameter");
               } catch (NoSuchElementException var11) {
                  Tracer.println("caught NoSuchElementException");
               }
            }
         }
      } else {
         Tracer.println("RuntimeException");
         throw new RuntimeException("Error in setServerChannel:protocolDescriptorListItem");
      }
   }

   protected boolean isNotifierForPushService() {
      Tracer.println("isNotifierForPushService()=" + String.valueOf(this.isPushNotifier));
      return this.isPushNotifier;
   }

   protected byte[] getServiceRecordOfThePushNotifier() {
      return this.serviceRecordOfThePushNotifier;
   }

   protected void createLocalPushServiceRecord() {
      Tracer.println("createLocalPushServiceRecord");
      this.localService.initRecord(this.serviceRecordOfThePushNotifier);
      this.setServerChannel(this.localService, this.channelID);
   }

   private native long addService0(byte[] var1, int var2, int var3, byte var4) throws ServiceRegistrationException;

   private native byte updateService0(byte[] var1, int var2, int var3, byte var4, byte var5) throws ServiceRegistrationException, IOException;

   private native void acceptAndOpen0(ProtocolConnection var1, byte var2) throws IOException;

   private native void acceptAndOpenPush0(ProtocolConnection var1, byte[] var2) throws IOException;

   private native boolean removeService0(byte var1);

   private native boolean attemptClose0(byte var1);

   private native void pushNotifierClosed0(byte[] var1);
}
