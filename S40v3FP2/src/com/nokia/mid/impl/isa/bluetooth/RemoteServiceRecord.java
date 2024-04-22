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

   RemoteServiceRecord(RemoteDevice var1, byte[] var2) throws NullPointerException, IllegalArgumentException {
      if (var2 == null) {
         throw new NullPointerException("serviceRecordStream is null");
      } else if (var1 == null) {
         throw new NullPointerException("device is null");
      } else {
         this.remoteDevice = var1;
         this.parserSetAttributes(var2);
      }
   }

   public RemoteDevice getHostDevice() {
      return this.remoteDevice;
   }

   public boolean populateRecord(int[] var1) throws IOException {
      boolean var4 = false;
      if (var1 == null) {
         throw new NullPointerException("attr. set is null");
      } else if (var1.length == 0) {
         throw new IllegalArgumentException("Attribute set length is 0");
      } else {
         DiscoveryAgent var5;
         try {
            var5 = LocalDevice.getLocalDevice().getDiscoveryAgent();
         } catch (BluetoothStateException var17) {
            throw new IOException("Unable to contact remote device");
         }

         UUID[] var6 = null;
         UUID[] var7 = this.getUUIDsFromServiceRecord();
         int var8 = 0;
         int var2;
         int var3;
         if (var7 != null) {
            for(var2 = 0; var2 < var7.length; ++var2) {
               if (var7[var2] != null) {
                  ++var8;

                  for(var3 = var2 + 1; var3 < var7.length; ++var3) {
                     if (var7[var2].equals(var7[var3])) {
                        var7[var3] = null;
                     }
                  }
               }
            }

            var6 = new UUID[var8];
            int var9 = 0;

            for(var2 = 0; var2 < var7.length; ++var2) {
               if (var7[var2] != null) {
                  var6[var9++] = var7[var2];
               }
            }
         }

         Object var18 = new Object();
         RemoteServiceRecord.PopulateRecordListener var10 = new RemoteServiceRecord.PopulateRecordListener(var18);
         synchronized(var18) {
            try {
               var5.searchServices(var1, var6, this.remoteDevice, var10);
            } catch (BluetoothStateException var15) {
               throw new IOException("Unable to contact remote device(service search not started)");
            }

            try {
               var18.wait();
            } catch (InterruptedException var14) {
            }
         }

         ServiceRecord var11 = var10.getFoundRecord();
         if (var11 == null) {
            throw new IOException("Remote device(or record) could not be found");
         } else {
            int[] var12 = var11.getAttributeIDs();
            if (var12.length != 0) {
               for(var2 = 0; var2 < var12.length; ++var2) {
                  if (var12[var2] != 0) {
                     this.updateAttributeValue(var12[var2], var11.getAttributeValue(var12[var2]));
                  }

                  if (!var4) {
                     for(var3 = 0; var3 < var1.length; ++var3) {
                        if (var12[var2] == var1[var3]) {
                           var4 = true;
                        }
                     }
                  }
               }
            }

            return var4;
         }
      }
   }

   public String getConnectionURL(int var1, boolean var2) {
      RemoteServiceRecord.ProtocolDescriptionParameters var3 = this.getProtocolParameters();
      if (null == var3) {
         return null;
      } else {
         long var4 = var3.getChannelID();
         String var6 = var3.getProtocol();
         return this.createConnectionURL(this.remoteDevice.getBluetoothAddress(), var1, var2, var6, var4);
      }
   }

   public void setDeviceServiceClasses(int var1) {
      throw new RuntimeException("ServiceRecord was obtained from a remote device");
   }

   private RemoteServiceRecord.ProtocolDescriptionParameters getProtocolParameters() {
      UUID var5 = new UUID(3L);
      UUID var6 = new UUID(256L);
      RemoteServiceRecord.ProtocolDescriptionParameters var7 = null;
      DataElement var1 = this.getAttributeValue(4);
      if (var1 != null && var1.getDataType() == 48) {
         Enumeration var2 = (Enumeration)var1.getValue();
         Enumeration var8 = var2;

         while(var8.hasMoreElements() && var7 == null) {
            DataElement var3 = (DataElement)var8.nextElement();
            if (var3 != null && var3.getDataType() == 48) {
               Enumeration var4 = (Enumeration)var3.getValue();
               if (var3.getSize() > 1) {
                  DataElement var9 = (DataElement)var4.nextElement();
                  DataElement var10 = (DataElement)var4.nextElement();
                  if (var9 != null && var10 != null && var9.getDataType() == 24 && (var10.getDataType() == 8 || var10.getDataType() == 9)) {
                     UUID var11 = (UUID)var9.getValue();
                     long var12 = var10.getLong();
                     if (var5.equals(var11) && var10.getDataType() == 8 && validRFCOMMChannelValue(var12)) {
                        var7 = new RemoteServiceRecord.ProtocolDescriptionParameters("btspp", var12);
                     } else if (var6.equals(var11) && var10.getDataType() == 9 && validPSMValue(var12)) {
                        var7 = new RemoteServiceRecord.ProtocolDescriptionParameters("btl2cap", var12);
                     }
                  }
               }
            }
         }

         return var7;
      } else {
         return var7;
      }
   }

   private final byte[] getBluetoothAddressAsByteArray() {
      String var1 = this.remoteDevice.getBluetoothAddress();
      byte[] var2 = new byte[6];

      for(int var3 = 0; var3 < 12; var3 += 2) {
         var2[var3 / 2] = (byte)(var2[var3 / 2] | Integer.parseInt(var1.substring(var3, var3 + 2), 16));
      }

      return var2;
   }

   private final UUID[] getUUIDsFromServiceRecord() {
      UUID[] var4 = null;
      UUID[] var5 = null;
      int[] var3 = this.getAttributeIDs();
      if (var3.length != 0) {
         for(int var1 = 0; var1 < var3.length; ++var1) {
            DataElement var6 = this.getAttributeValue(var3[var1]);
            switch(var6.getDataType()) {
            case 24:
               var5 = new UUID[]{(UUID)var6.getValue()};
               break;
            case 48:
            case 56:
               var5 = this.getUUIDsFromDSEQorDALT(var6);
            }

            if (var5 != null) {
               if (var4 == null) {
                  var4 = var5;
               } else {
                  UUID[] var7 = new UUID[var4.length + var5.length];

                  int var2;
                  for(var2 = 0; var2 < var4.length; ++var2) {
                     var7[var2] = var4[var2];
                  }

                  for(var2 = 0; var2 < var5.length; ++var2) {
                     var7[var4.length + var2] = var5[var2];
                  }

                  var4 = var7;
               }
            }
         }
      }

      return var4;
   }

   private final UUID[] getUUIDsFromDSEQorDALT(DataElement var1) {
      UUID[] var3 = null;
      UUID[] var4 = null;
      if (var1.getDataType() != 48 && var1.getDataType() != 56) {
         throw new IllegalArgumentException("Illegal data element type");
      } else {
         Enumeration var5 = (Enumeration)var1.getValue();

         while(true) {
            while(true) {
               do {
                  if (!var5.hasMoreElements()) {
                     return var3;
                  }

                  DataElement var6 = (DataElement)var5.nextElement();
                  switch(var6.getDataType()) {
                  case 24:
                     var4 = new UUID[]{(UUID)var6.getValue()};
                     break;
                  case 48:
                  case 56:
                     var4 = this.getUUIDsFromDSEQorDALT(var6);
                  }
               } while(var4 == null);

               if (var3 == null) {
                  var3 = var4;
               } else {
                  UUID[] var7 = new UUID[var3.length + var4.length];

                  int var2;
                  for(var2 = 0; var2 < var3.length; ++var2) {
                     var7[var2] = var3[var2];
                  }

                  for(var2 = 0; var2 < var4.length; ++var2) {
                     var7[var3.length + var2] = var4[var2];
                  }

                  var3 = var7;
               }
            }
         }
      }
   }

   private class PopulateRecordListener implements DiscoveryListener {
      private Object callingThreadLock;
      private ServiceRecord foundRecord;
      private byte[] currentRecordHandleValue;

      private PopulateRecordListener(Object var2) {
         this.callingThreadLock = var2;
         this.foundRecord = null;
         this.currentRecordHandleValue = this.getHandleValue(RemoteServiceRecord.this.getAttributeValue(0));
      }

      public ServiceRecord getFoundRecord() {
         return this.foundRecord;
      }

      public void deviceDiscovered(RemoteDevice var1, DeviceClass var2) {
      }

      public void inquiryCompleted(int var1) {
      }

      public void serviceSearchCompleted(int var1, int var2) {
         synchronized(this.callingThreadLock) {
            this.callingThreadLock.notify();
         }
      }

      public void servicesDiscovered(int var1, ServiceRecord[] var2) {
         for(int var5 = 0; var5 < var2.length && this.foundRecord == null; ++var5) {
            byte[] var4 = this.getHandleValue(var2[var5].getAttributeValue(0));
            if (var4 != null && var4.length == 8 && this.currentRecordHandleValue != null && this.currentRecordHandleValue.length == 8) {
               boolean var3 = true;

               for(int var6 = 0; var6 < 8; ++var6) {
                  if (var4[var6] != this.currentRecordHandleValue[var6]) {
                     var3 = false;
                     break;
                  }
               }

               if (var3) {
                  this.foundRecord = var2[var5];
                  break;
               }
            }
         }

      }

      private byte[] getHandleValue(DataElement var1) {
         byte[] var2 = null;
         if (var1 != null) {
            switch(var1.getDataType()) {
            case 8:
            case 9:
            case 10:
            case 16:
            case 17:
            case 18:
            case 19:
               var2 = new byte[8];
               long var3 = var1.getLong();

               for(int var5 = 0; var5 < 8; ++var5) {
                  var2[var5] = (byte)((int)((var3 & 255L << 8 * var5) >> 8 * var5));
               }

               return var2;
            case 11:
               var2 = (byte[])var1.getValue();
            case 12:
            case 13:
            case 14:
            case 15:
            }
         }

         return var2;
      }

      // $FF: synthetic method
      PopulateRecordListener(Object var2, Object var3) {
         this(var2);
      }
   }

   private class ProtocolDescriptionParameters {
      private long channelID;
      private String protocol;

      ProtocolDescriptionParameters(String var2, long var3) {
         this.channelID = var3;
         this.protocol = new String(var2);
      }

      private long getChannelID() {
         return this.channelID;
      }

      private String getProtocol() {
         return this.protocol;
      }
   }
}
