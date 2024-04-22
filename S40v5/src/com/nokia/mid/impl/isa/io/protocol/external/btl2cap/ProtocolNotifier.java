package com.nokia.mid.impl.isa.io.protocol.external.btl2cap;

import com.nokia.mid.impl.isa.bluetooth.CommonNotifier;
import com.nokia.mid.impl.isa.bluetooth.LocalServiceRecord;
import com.nokia.mid.impl.isa.bluetooth.ServiceRecordAccessor;
import java.io.IOException;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import javax.bluetooth.DataElement;
import javax.bluetooth.L2CAPConnection;
import javax.bluetooth.L2CAPConnectionNotifier;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.ServiceRegistrationException;
import javax.bluetooth.UUID;

public class ProtocolNotifier extends Protocol implements CommonNotifier, ServiceRecordAccessor, L2CAPConnectionNotifier {
   private byte hq = 0;
   private byte hr;
   private int hs = 24577;
   private int ht = 0;
   private boolean hu = false;
   private int hv = 0;
   private boolean hw = false;
   private boolean hx = false;
   private LocalServiceRecord hy = new LocalServiceRecord(this);
   private final Object hz = new Object();
   private boolean hA = false;
   private byte[] hB = null;
   private int hC = 0;
   private byte[] hD = null;

   ProtocolNotifier(String var1) throws IOException {
      this.hD = var1.getBytes();
   }

   public L2CAPConnection acceptAndOpen() throws IOException {
      synchronized(this.hz) {
         ProtocolConnection var2 = null;

         while(var2 == null) {
            synchronized(this.hy.getLockObject()) {
               if (this.isClosed) {
                  throw new IOException("closed was called before");
               }

               if (this.hv == 0) {
                  throw new Error("service should be registered at that point");
               }

               try {
                  if (!this.checkServiceOkToUpdate(this.hy)) {
                     throw new Exception("Service not OK");
                  }
               } catch (Exception var6) {
                  throw new ServiceRegistrationException("Invalid service record");
               }

               this.hv = 2;
               this.updateService(this.hy);
               this.hw = true;
            }

            var2 = new ProtocolConnection(false, this.mode);

            try {
               this.acceptAndOpen0(var2, this.hq);
            } catch (IOException var5) {
               this.hw = false;
               throw var5;
            }

            this.hw = false;
            if (this.hx) {
               if (this.transmitMTU <= var2.getTransmitMTU()) {
                  var2.setTransmitMTU(this.transmitMTU);
               } else {
                  try {
                     var2.close();
                  } catch (Exception var4) {
                  }

                  var2 = null;
               }
            }
         }

         return var2;
      }
   }

   public void close() throws IOException {
      synchronized(this.hy.getLockObject()) {
         if (!this.isClosed) {
            this.isClosed = true;
            this.unregisterService();
            if (0 != this.hq) {
               while(this.hw) {
                  if (this.attemptClose0(this.hq)) {
                     return;
                  }

                  try {
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
         if (this.hv != 0) {
            throw new Error("Service should not be registered at that point!");
         } else {
            try {
               if (!this.checkServiceOkToUpdate(this.hy)) {
                  throw new Exception("Service not OK");
               }
            } catch (Exception var2) {
               throw new ServiceRegistrationException("Invalid service record");
            }

            this.registerService(this.hy);
         }
      }
   }

   public void registerService(LocalServiceRecord var1) throws ServiceRegistrationException, NullPointerException, IOException {
      if (var1 == null) {
         throw new NullPointerException("passed record is null");
      } else {
         byte[] var2 = var1.parserSerialize();
         int var3 = this.addService0(var2, var2.length, var1.getDeviceServiceClasses(), this.hr, this.receiveMTU, this.transmitMTU);
         this.hs = var3 & '\uffff';
         this.hq = (byte)(var3 >> 16);
         a(this.hy, this.hs);
         this.hv = 1;
      }
   }

   public void unregisterService() throws IOException {
      if (this.hv != 0 && 0 != this.hq) {
         if (!this.removeService0(this.hq)) {
            throw new IOException("SDDB service unregistration failed");
         } else {
            this.hv = 0;
            this.hs = 24577;
         }
      }
   }

   public void updateService(LocalServiceRecord var1) throws ServiceRegistrationException, NullPointerException {
      if (var1 == null) {
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
                  if (this.updateService0(var2, var2.length, var1.getDeviceServiceClasses(), this.hr, this.hq) != this.hq) {
                     throw new Error("updateService0 returned wrong serviceHandle");
                  }

                  var1.setLockObject(this.hy.getLockObject());
                  this.hy = var1;
               } catch (ServiceRegistrationException var4) {
                  throw var4;
               } catch (IOException var5) {
                  return;
               }
            } else {
               if (null == this.hB) {
                  throw new Error("(JSR82 PUSH) Service record content is unknown");
               }

               var2 = var1.parserSerialize();
               boolean var3 = false;
               if (0 == var1.getDeviceServiceClasses() && var2.length == this.hB.length) {
                  var3 = true;
               }

               if (!var3) {
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
            DataElement var4;
            if ((var4 = var1.checkProtocolDescriptorListForProtocolUuid(var2)) == null) {
               return false;
            } else if (var4.getDataType() != 9) {
               return false;
            } else {
               return var4.getLong() == ((long)this.hs & 65535L);
            }
         } catch (Exception var3) {
            return false;
         }
      } else {
         throw new IllegalArgumentException("mandatory serviceRecord attributes missing");
      }
   }

   public LocalServiceRecord getLocalServiceRecord() {
      return this.hy;
   }

   public void setOptions(byte var1) {
      this.hr = var1;
   }

   public void setTransmitMTUSpecifiedInConnURL() {
      this.hx = true;
   }

   public long getChannelID() {
      return (long)this.hs;
   }

   public String getProtocol() {
      return new String("btl2cap");
   }

   private static void a(ServiceRecord var0, int var1) {
      DataElement var2;
      if ((var2 = var0.getAttributeValue(4)) != null && var2.getDataType() == 48) {
         UUID var5 = new UUID(256L);
         Enumeration var6 = (Enumeration)var2.getValue();

         label47:
         while(true) {
            DataElement var3;
            do {
               if (!var6.hasMoreElements()) {
                  throw new RuntimeException("Error in ProtocolNotifier.setPSM(): service record item not found");
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
                  if ((var4 = (DataElement)var7.nextElement()).getDataType() == 9) {
                     var3.removeElement(var4);
                     var3.addElement(new DataElement(9, (long)var1));
                     var0.setAttributeValue(4, var2);
                     return;
                  }

                  throw new RuntimeException("Error in ProtocolNotifier.setPSM(): protocolSpecificParameter");
               } catch (NoSuchElementException var8) {
               }
            }
         }
      } else {
         throw new RuntimeException("Error in ProtocolNotifier.setPSM(): protocolDescriptorListItem");
      }
   }

   protected boolean isNotifierForPushService() {
      return false;
   }

   protected byte[] getServiceRecordOfThePushNotifier() {
      return this.hB;
   }

   protected void createLocalPushServiceRecord() {
      this.hy.initRecord(this.hB);
      a(this.hy, this.hs);
   }

   private native int addService0(byte[] var1, int var2, int var3, byte var4, int var5, int var6) throws ServiceRegistrationException;

   private native byte updateService0(byte[] var1, int var2, int var3, byte var4, byte var5) throws ServiceRegistrationException, IOException;

   private native void acceptAndOpen0(ProtocolConnection var1, byte var2) throws IOException;

   private native boolean removeService0(byte var1);

   private native boolean attemptClose0(byte var1);
}
