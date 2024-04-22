package com.nokia.mid.impl.isa.io.protocol.external.btl2cap;

import com.nokia.mid.impl.isa.bluetooth.CommonBluetooth;
import com.nokia.mid.impl.isa.bluetooth.CommonNotifier;
import com.nokia.mid.impl.isa.bluetooth.LocalServiceRecord;
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

public class ProtocolNotifier extends Protocol implements CommonNotifier, L2CAPConnectionNotifier {
   private final int SERVICE_HANDLE_NOT_INITIALIZED = 0;
   private static final int DEFAULT_PSM_VALUE = 24577;
   private static final int PSM_VALUE_NOT_SET = 0;
   private static final int SERVICE_NOT_REGISTERED = 0;
   private static final int SERVICE_REGISTERED = 1;
   private static final int SERVICE_ENABLED = 2;
   private byte serviceHandle = 0;
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

   ProtocolNotifier(String var1) throws IOException {
      this.url_as_byte_stream = var1.getBytes();
   }

   public L2CAPConnection acceptAndOpen() throws IOException {
      synchronized(this.acceptAndOpenLock) {
         ProtocolConnection var2 = null;

         while(var2 == null) {
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

            var2 = new ProtocolConnection(false, this.mode);

            try {
               if (!this.isPushNotifier) {
                  this.acceptAndOpen0(var2, this.serviceHandle);
               } else {
                  try {
                     boolean var3 = false;

                     while(!var3) {
                        try {
                           this.acceptAndOpenPush(var2, this.url_as_byte_stream);
                           var3 = true;
                        } catch (BluetoothStateException var8) {
                           byte var5 = CommonBluetooth.activateMedia();
                           if (1 == var5) {
                              throw new IOException("BT System is not active");
                           }
                        }
                     }
                  } catch (ServiceRegistrationException var9) {
                     byte var4 = CommonBluetooth.activateMedia();
                     if (1 == var4) {
                        throw new IOException("BT System is not active");
                     }

                     if (0 == this.serviceHandle) {
                        this.checkAndRegisterService();
                     }

                     this.acceptAndOpen0(var2, this.serviceHandle);
                  }
               }
            } catch (IOException var10) {
               this.activeAccept = false;
               var2 = null;
               throw var10;
            }

            this.activeAccept = false;
            if (this.transmitMTUSpecifiedInConnURL) {
               if (this.transmitMTU <= var2.getTransmitMTU()) {
                  var2.setTransmitMTU(this.transmitMTU);
               } else {
                  try {
                     var2.close();
                  } catch (Exception var7) {
                  }

                  var2 = null;
               }
            }
         }

         return var2;
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

   public void registerService(LocalServiceRecord var1) throws ServiceRegistrationException, NullPointerException, IOException {
      if (var1 == null) {
         throw new NullPointerException("passed record is null");
      } else {
         byte[] var2 = var1.parserSerialize();
         int var3 = this.addService0(var2, var2.length, var1.getDeviceServiceClasses(), this.options, this.receiveMTU, this.transmitMTU);
         this.channelID = var3 & '\uffff';
         this.serviceHandle = (byte)(var3 >> 16);
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

   public void updateService(LocalServiceRecord var1) throws ServiceRegistrationException, NullPointerException {
      if (var1 == null) {
         throw new NullPointerException("passed record is null");
      } else {
         if (2 == this.serviceStatus || !this.isClosed && this.isPushNotifier) {
            byte[] var2;
            if (0 != this.serviceHandle) {
               if (this.isChannelIDUpdated && this.channelID != this.channelIDUpdatedValue) {
                  this.setPSM(var1, this.channelIDUpdatedValue);
                  this.channelID = this.channelIDUpdatedValue;
               }

               var2 = var1.parserSerialize();

               try {
                  byte var3 = this.updateService0(var2, var2.length, var1.getDeviceServiceClasses(), this.options, this.serviceHandle);
                  if (var3 != this.serviceHandle) {
                     throw new Error("updateService0 returned wrong serviceHandle");
                  }

                  if (this.isPushNotifier) {
                     this.serviceRecordOfThePushNotifier = var2;
                  }

                  var1.setLockObject(this.localService.getLockObject());
                  this.localService = var1;
               } catch (ServiceRegistrationException var5) {
                  throw var5;
               } catch (IOException var6) {
               }
            } else {
               if (null == this.serviceRecordOfThePushNotifier) {
                  throw new Error("(JSR82 PUSH) Service record content is unknown");
               }

               var2 = var1.parserSerialize();
               boolean var7 = false;
               if (this.deviceServiceClassesOfThePushNotifier == var1.getDeviceServiceClasses() && var2.length == this.serviceRecordOfThePushNotifier.length) {
                  for(int var4 = 0; var4 < var2.length && var2[var4] == this.serviceRecordOfThePushNotifier[var4]; ++var4) {
                  }

                  var7 = true;
               }

               if (!var7) {
                  throw new ServiceRegistrationException("Service registration/updating failed");
               }
            }
         }

      }
   }

   public boolean checkServiceOkToUpdate(LocalServiceRecord var1) {
      if (null != var1.getAttributeValue(1) && null != var1.getAttributeValue(4)) {
         try {
            UUID var2 = new UUID(256L);
            DataElement var3 = var1.checkProtocolDescriptorListForProtocolUuid(var2);
            if (var3 == null) {
               return false;
            } else if (var3.getDataType() != 9) {
               return false;
            } else {
               return var3.getLong() == ((long)this.channelID & 65535L);
            }
         } catch (Exception var4) {
            return false;
         }
      } else {
         throw new IllegalArgumentException("mandatory serviceRecord attributes missing");
      }
   }

   public LocalServiceRecord getLocalServiceRecord() {
      return this.localService;
   }

   public void setOptions(byte var1) {
      this.options = var1;
   }

   public void setTransmitMTUSpecifiedInConnURL() {
      this.transmitMTUSpecifiedInConnURL = true;
   }

   public long getChannelID() {
      long var1 = (long)this.channelID;
      if (this.isChannelIDUpdated) {
         var1 = (long)this.channelIDUpdatedValue;
      }

      return var1;
   }

   public String getProtocol() {
      return new String("btl2cap");
   }

   private void acceptAndOpenPush(ProtocolConnection var1, byte[] var2) throws IOException {
      this.acceptAndOpenPush0(var1, var2);
      var1.setPushConnection();
   }

   private void setPSM(ServiceRecord var1, int var2) {
      DataElement var3 = var1.getAttributeValue(4);
      if (var3 != null && var3.getDataType() == 48) {
         UUID var7 = new UUID(256L);
         Enumeration var8 = (Enumeration)var3.getValue();

         label46:
         while(true) {
            DataElement var4;
            do {
               if (!var8.hasMoreElements()) {
                  throw new RuntimeException("Error in ProtocolNotifier.setPSM(): service record item not found");
               }

               var4 = (DataElement)var8.nextElement();
            } while(var4.getDataType() != 48);

            Enumeration var9 = (Enumeration)var4.getValue();

            while(true) {
               DataElement var5;
               do {
                  do {
                     if (!var9.hasMoreElements()) {
                        continue label46;
                     }

                     var5 = (DataElement)var9.nextElement();
                  } while(var5.getDataType() != 24);
               } while(!var5.getValue().equals(var7));

               try {
                  DataElement var6 = (DataElement)var9.nextElement();
                  if (var6.getDataType() == 9) {
                     var4.removeElement(var6);
                     var4.addElement(new DataElement(9, (long)var2));
                     var1.setAttributeValue(4, var3);
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
      this.setPSM(this.localService, this.channelID);
   }

   private native int addService0(byte[] var1, int var2, int var3, byte var4, int var5, int var6) throws ServiceRegistrationException;

   private native byte updateService0(byte[] var1, int var2, int var3, byte var4, byte var5) throws ServiceRegistrationException, IOException;

   private native void acceptAndOpen0(ProtocolConnection var1, byte var2) throws IOException;

   private native void acceptAndOpenPush0(ProtocolConnection var1, byte[] var2) throws IOException;

   private native boolean removeService0(byte var1);

   private native boolean attemptClose0(byte var1);

   private native void pushNotifierClosed0(byte[] var1);
}
