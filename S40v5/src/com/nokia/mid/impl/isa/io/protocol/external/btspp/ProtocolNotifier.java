package com.nokia.mid.impl.isa.io.protocol.external.btspp;

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

public class ProtocolNotifier extends Protocol implements CommonNotifier, ServiceRecordAccessor, StreamConnectionNotifier {
   private byte hq = 0;
   private byte hr;
   private int hs = 2;
   private boolean hu = false;
   private int hv = 0;
   private boolean hw = false;
   private LocalServiceRecord hy;
   private final Object hz = new Object();
   private boolean hA = false;
   private byte[] hB = null;
   private int hC = 0;
   private byte[] hD = null;

   ProtocolNotifier(String var1) throws IOException {
      Tracer.println("ProtocolNotifier(url=" + var1 + ")");
      this.hy = new LocalServiceRecord(this);
      this.hD = var1.getBytes();
   }

   public StreamConnection acceptAndOpen() throws IOException {
      synchronized(this.hz) {
         synchronized(this.hy.getLockObject()) {
            Tracer.println("acceptAndOpen");
            if (this.isClosed) {
               Tracer.println("IOException Notifier closed");
               throw new IOException("closed was called before");
            }

            if (this.hv == 0) {
               Tracer.println("Error reg.");
               throw new Error("service should be registered at that point");
            }

            try {
               if (!this.checkServiceOkToUpdate(this.hy)) {
                  Tracer.println("Exception serv.");
                  throw new Exception("Service not OK");
               }
            } catch (Exception var5) {
               Tracer.println("ServiceRegistrationException invalid");
               throw new ServiceRegistrationException("Invalid service record");
            }

            this.hv = 2;
            this.updateService(this.hy);
            this.hw = true;
         }

         ProtocolConnection var2 = new ProtocolConnection(false, this.mode);

         try {
            Tracer.println("NON-PUSH acceptAndOpen0");
            this.acceptAndOpen0(var2, this.hq);
         } catch (IOException var4) {
            this.hw = false;
            Tracer.println("IOException");
            throw var4;
         }

         this.hw = false;
         Tracer.println("return protocolConnection");
         return var2;
      }
   }

   public void close() throws IOException {
      synchronized(this.hy.getLockObject()) {
         if (!this.isClosed) {
            Tracer.println("Notifier.close");
            this.isClosed = true;
            this.unregisterService();
            if (0 != this.hq) {
               while(this.hw) {
                  Tracer.println("attemptClose0");
                  if (this.attemptClose0(this.hq)) {
                     return;
                  }

                  try {
                     Tracer.println("sleep(500)");
                     Thread.sleep(500L);
                  } catch (InterruptedException var2) {
                  }
               }
            }
         }

      }
   }

   public void checkAndRegisterService() throws ServiceRegistrationException, NullPointerException, IOException {
      synchronized(this.hy.getLockObject()) {
         Tracer.println("checkAndRegisterService");
         if (this.hv != 0) {
            Tracer.println("Error reg.");
            throw new Error("Service should not be registered at that point!");
         } else {
            try {
               if (!this.checkServiceOkToUpdate(this.hy)) {
                  Tracer.println("Exception serv.");
                  throw new Exception("Service not OK");
               }
            } catch (Exception var2) {
               Tracer.println("ServiceRegistrationException invalid");
               throw new ServiceRegistrationException("Invalid service record");
            }

            this.registerService(this.hy);
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
         int var3 = this.addService0(var2, var2.length, var1.getDeviceServiceClasses(), this.hr);
         this.hs = var3 & 255;
         this.hq = (byte)(var3 >> 8);
         b(this.hy, this.hs);
         this.hv = 1;
      }
   }

   public void unregisterService() throws IOException {
      Tracer.println("unregisterService");
      if (this.hv != 0 && 0 != this.hq) {
         Tracer.println("removeService0");
         if (!this.removeService0(this.hq)) {
            throw new IOException("SDDB service unregistration failed");
         } else {
            this.hv = 0;
            this.hs = 2;
         }
      }
   }

   public void updateService(LocalServiceRecord var1) throws ServiceRegistrationException, NullPointerException {
      Tracer.println("updateService");
      if (var1 == null) {
         Tracer.println("NullPointerException");
         throw new NullPointerException("passed record is null");
      } else {
         if (2 != this.hv) {
            if (!this.isClosed) {
               return;
            }
         } else {
            byte[] var2;
            if (0 != this.hq) {
               var2 = var1.parserSerialize();

               try {
                  Tracer.println("updateService0");
                  if (this.updateService0(var2, var2.length, var1.getDeviceServiceClasses(), this.hr, this.hq) != this.hq) {
                     Tracer.println("Error han");
                     throw new Error("updateService0 returned wrong serviceHandle");
                  }

                  var1.setLockObject(this.hy.getLockObject());
                  this.hy = var1;
               } catch (ServiceRegistrationException var4) {
                  throw var4;
               } catch (IOException var5) {
                  Tracer.println("caught IOException");
                  return;
               }
            } else {
               if (null == this.hB) {
                  Tracer.println("Error cont");
                  throw new Error("(JSR82 PUSH) Service record content is unknown");
               }

               var2 = var1.parserSerialize();
               boolean var3 = false;
               if (0 == var1.getDeviceServiceClasses() && var2.length == this.hB.length) {
                  var3 = true;
               }

               if (!var3) {
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
            } catch (NullPointerException var3) {
               Tracer.println("UUID not found");
               return false;
            }

            var2 = new UUID(3L);
            DataElement var5;
            if ((var5 = var1.checkProtocolDescriptorListForProtocolUuid(var2)) == null) {
               Tracer.println("elem=null!");
               return false;
            } else if (var5.getDataType() != 8) {
               Tracer.println("elem not found!");
               return false;
            } else if (var5.getLong() != ((long)this.hs & 255L)) {
               Tracer.println("elem_L not found!");
               return false;
            } else {
               return true;
            }
         } catch (Exception var4) {
            Tracer.println("anything wrong!");
            return false;
         }
      } else {
         Tracer.println("IllegalArgumentException");
         throw new IllegalArgumentException("mandatory serviceRecord attributes missing");
      }
   }

   public LocalServiceRecord getLocalServiceRecord() {
      return this.hy;
   }

   public void setOptions(byte var1) {
      this.hr = var1;
      Tracer.println("setOptions()=" + String.valueOf((int)var1));
   }

   public long getChannelID() {
      return (long)this.hs;
   }

   public String getProtocol() {
      UUID var1 = new UUID(8L);

      DataElement var2;
      try {
         var2 = LocalDevice.getLocalDevice().getRecord(this).getAttributeValue(4);
      } catch (BluetoothStateException var3) {
         throw new NullPointerException("Local Service URL cannot be retrieved presently");
      }

      if (var2 != null && var2.getDataType() == 48) {
         Enumeration var6 = (Enumeration)var2.getValue();

         while(var6.hasMoreElements()) {
            DataElement var4;
            if ((var4 = (DataElement)var6.nextElement()) != null && var4.getDataType() == 48) {
               UUID var5 = (UUID)((DataElement)((Enumeration)var4.getValue()).nextElement()).getValue();
               if (var1.equals(var5)) {
                  return new String("btgoep");
               }
            }
         }

         return new String("btspp");
      } else {
         return new String("btspp");
      }
   }

   private static void b(ServiceRecord var0, int var1) {
      Tracer.println("setServerChannel()=" + String.valueOf(var1));
      DataElement var2;
      if ((var2 = var0.getAttributeValue(4)) != null && var2.getDataType() == 48) {
         UUID var5 = new UUID(3L);
         Enumeration var6 = (Enumeration)var2.getValue();

         label47:
         while(true) {
            DataElement var3;
            do {
               if (!var6.hasMoreElements()) {
                  Tracer.println("RuntimeException not found");
                  throw new RuntimeException("Error in setServerChannel:service record item not found");
               }
            } while((var3 = (DataElement)var6.nextElement()).getDataType() != 48);

            Enumeration var7 = (Enumeration)var3.getValue();

            while(true) {
               DataElement var4;
               do {
                  do {
                     if (!var7.hasMoreElements()) {
                        continue label47;
                     }
                  } while((var4 = (DataElement)var7.nextElement()).getDataType() != 24);
               } while(!var4.getValue().equals(var5));

               try {
                  if ((var4 = (DataElement)var7.nextElement()).getDataType() == 8) {
                     var3.removeElement(var4);
                     var3.addElement(new DataElement(8, (long)var1));
                     var0.setAttributeValue(4, var2);
                     return;
                  }

                  Tracer.println("RuntimeException chan");
                  throw new RuntimeException("Error in setServerChannel:protocolSpecificParameter");
               } catch (NoSuchElementException var8) {
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
      Tracer.println("isNotifierForPushService()=" + String.valueOf(false));
      return false;
   }

   protected byte[] getServiceRecordOfThePushNotifier() {
      return this.hB;
   }

   protected void createLocalPushServiceRecord() {
      Tracer.println("createLocalPushServiceRecord");
      this.hy.initRecord(this.hB);
      b(this.hy, this.hs);
   }

   private native int addService0(byte[] var1, int var2, int var3, byte var4) throws ServiceRegistrationException;

   private native byte updateService0(byte[] var1, int var2, int var3, byte var4, byte var5) throws ServiceRegistrationException, IOException;

   private native void acceptAndOpen0(ProtocolConnection var1, byte var2) throws IOException;

   private native boolean removeService0(byte var1);

   private native boolean attemptClose0(byte var1);
}
