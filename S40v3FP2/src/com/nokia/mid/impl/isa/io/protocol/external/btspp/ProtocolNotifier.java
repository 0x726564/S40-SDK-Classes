package com.nokia.mid.impl.isa.io.protocol.external.btspp;

import com.nokia.mid.impl.isa.bluetooth.CommonBluetooth;
import com.nokia.mid.impl.isa.bluetooth.CommonNotifier;
import com.nokia.mid.impl.isa.bluetooth.LocalServiceRecord;
import com.nokia.mid.impl.isa.bluetooth.Tracer;
import java.io.IOException;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DataElement;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.ServiceRegistrationException;
import javax.bluetooth.UUID;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class ProtocolNotifier extends Protocol implements CommonNotifier, StreamConnectionNotifier {
   private final int SERVICE_HANDLE_NOT_INITIALIZED = 0;
   private static final int SERVICE_NOT_REGISTERED = 0;
   private static final int SERVICE_REGISTERED = 1;
   private static final int SERVICE_ENABLED = 2;
   private static final int DEFAULT_CHANNEL_ID = 2;
   private byte serviceHandle = 0;
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

   ProtocolNotifier(String var1) throws IOException {
      Tracer.println("ProtocolNotifier(url=" + var1 + ")");
      this.localService = new LocalServiceRecord(this);
      this.url_as_byte_stream = var1.getBytes();
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

         ProtocolConnection var2 = new ProtocolConnection(false, this.mode);

         try {
            if (!this.isPushNotifier) {
               Tracer.println("NON-PUSH acceptAndOpen0");
               this.acceptAndOpen0(var2, this.serviceHandle);
            } else {
               try {
                  boolean var3 = false;

                  while(!var3) {
                     try {
                        this.acceptAndOpenPush(var2, this.url_as_byte_stream);
                        var3 = true;
                     } catch (BluetoothStateException var7) {
                        Tracer.println("caught BluetoothStateException activating Media");
                        byte var5 = CommonBluetooth.activateMedia();
                        if (1 == var5) {
                           Tracer.println("IOException BT OFF");
                           throw new IOException("BT System is not active");
                        }
                     }
                  }
               } catch (ServiceRegistrationException var8) {
                  Tracer.println("caught ServiceRegistrationException activating media");
                  byte var4 = CommonBluetooth.activateMedia();
                  if (1 == var4) {
                     Tracer.println("IOException BT OFF");
                     throw new IOException("BT System is not active");
                  }

                  if (0 == this.serviceHandle) {
                     this.checkAndRegisterService();
                  }

                  Tracer.println("acceptAndOpen0");
                  this.acceptAndOpen0(var2, this.serviceHandle);
               }
            }
         } catch (IOException var9) {
            this.activeAccept = false;
            var2 = null;
            Tracer.println("IOException");
            throw var9;
         }

         this.activeAccept = false;
         Tracer.println("return protocolConnection");
         return var2;
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

   public void registerService(LocalServiceRecord var1) throws ServiceRegistrationException, NullPointerException, IOException {
      Tracer.println("registerService");
      if (var1 == null) {
         Tracer.println("NullPointerException");
         throw new NullPointerException("passed record is null");
      } else {
         byte[] var2 = var1.parserSerialize();
         Tracer.println("addService0");
         int var3 = this.addService0(var2, var2.length, var1.getDeviceServiceClasses(), this.options);
         this.channelID = var3 & 255;
         this.serviceHandle = (byte)(var3 >> 8);
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

   public void updateService(LocalServiceRecord var1) throws ServiceRegistrationException, NullPointerException {
      Tracer.println("updateService");
      if (var1 == null) {
         Tracer.println("NullPointerException");
         throw new NullPointerException("passed record is null");
      } else {
         if (2 == this.serviceStatus || !this.isClosed && this.isPushNotifier) {
            byte[] var2;
            if (0 != this.serviceHandle) {
               if (this.isChannelIDUpdated && this.channelID != this.channelIDUpdatedValue) {
                  this.setServerChannel(var1, this.channelIDUpdatedValue);
                  this.channelID = this.channelIDUpdatedValue;
               }

               var2 = var1.parserSerialize();

               try {
                  Tracer.println("updateService0");
                  byte var3 = this.updateService0(var2, var2.length, var1.getDeviceServiceClasses(), this.options, this.serviceHandle);
                  if (var3 != this.serviceHandle) {
                     Tracer.println("Error han");
                     throw new Error("updateService0 returned wrong serviceHandle");
                  }

                  if (this.isPushNotifier) {
                     Tracer.println("set serviceRecordOfThePushNotifier");
                     this.serviceRecordOfThePushNotifier = var2;
                  }

                  var1.setLockObject(this.localService.getLockObject());
                  this.localService = var1;
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

               var2 = var1.parserSerialize();
               boolean var7 = false;
               if (this.deviceServiceClassesOfThePushNotifier == var1.getDeviceServiceClasses() && var2.length == this.serviceRecordOfThePushNotifier.length) {
                  for(int var4 = 0; var4 < var2.length && var2[var4] == this.serviceRecordOfThePushNotifier[var4]; ++var4) {
                  }

                  var7 = true;
               }

               if (!var7) {
                  Tracer.println("ServiceRegistrationException uneq");
                  throw new ServiceRegistrationException("Service registration/updating failed");
               }
            }
         }

      }
   }

   public boolean checkServiceOkToUpdate(LocalServiceRecord var1) {
      Tracer.println("checkServiceOkToUpdate");
      if (null != var1.getAttributeValue(1) && null != var1.getAttributeValue(4)) {
         try {
            UUID var2 = new UUID(256L);

            try {
               if (var1.checkProtocolDescriptorListForProtocolUuid(var2) != null) {
                  Tracer.println("not allowed");
                  return false;
               }
            } catch (NullPointerException var5) {
               Tracer.println("UUID not found");
               return false;
            }

            UUID var3 = new UUID(3L);
            DataElement var4 = var1.checkProtocolDescriptorListForProtocolUuid(var3);
            if (var4 == null) {
               Tracer.println("elem=null!");
               return false;
            } else if (var4.getDataType() != 8) {
               Tracer.println("elem not found!");
               return false;
            } else if (var4.getLong() != ((long)this.channelID & 255L)) {
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
      return this.localService;
   }

   public void setOptions(byte var1) {
      this.options = var1;
      Tracer.println("setOptions()=" + String.valueOf((int)var1));
   }

   public long getChannelID() {
      long var1 = (long)this.channelID;
      if (this.isChannelIDUpdated) {
         var1 = (long)this.channelIDUpdatedValue;
      }

      return var1;
   }

   public String getProtocol() {
      return new String("btspp");
   }

   private void acceptAndOpenPush(ProtocolConnection var1, byte[] var2) throws IOException {
      Tracer.println("acceptAndOpenPush calling acceptAndOpenPush0");
      this.acceptAndOpenPush0(var1, var2);
      var1.setPushConnection();
   }

   private void setServerChannel(ServiceRecord var1, int var2) {
      Tracer.println("setServerChannel()=" + String.valueOf(var2));
      DataElement var3 = var1.getAttributeValue(4);
      if (var3 != null && var3.getDataType() == 48) {
         UUID var7 = new UUID(3L);
         Enumeration var8 = (Enumeration)var3.getValue();

         label46:
         while(true) {
            DataElement var4;
            do {
               if (!var8.hasMoreElements()) {
                  Tracer.println("RuntimeException not found");
                  throw new RuntimeException("Error in setServerChannel:service record item not found");
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
                  if (var6.getDataType() == 8) {
                     var4.removeElement(var6);
                     var4.addElement(new DataElement(8, (long)var2));
                     var1.setAttributeValue(4, var3);
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

   private native int addService0(byte[] var1, int var2, int var3, byte var4) throws ServiceRegistrationException;

   private native byte updateService0(byte[] var1, int var2, int var3, byte var4, byte var5) throws ServiceRegistrationException, IOException;

   private native void acceptAndOpen0(ProtocolConnection var1, byte var2) throws IOException;

   private native void acceptAndOpenPush0(ProtocolConnection var1, byte[] var2) throws IOException;

   private native boolean removeService0(byte var1);

   private native boolean attemptClose0(byte var1);

   private native void pushNotifierClosed0(byte[] var1);
}
