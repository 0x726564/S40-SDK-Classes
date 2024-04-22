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
   private RemoteDevice kd;

   RemoteServiceRecord(RemoteDevice var1, byte[] var2) throws NullPointerException, IllegalArgumentException {
      if (var1 == null) {
         throw new NullPointerException("device is null");
      } else {
         this.kd = var1;
         this.parserSetAttributes(var2);
      }
   }

   public RemoteDevice getHostDevice() {
      return this.kd;
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
         } catch (BluetoothStateException var11) {
            throw new IOException("Unable to contact remote device");
         }

         UUID[] var3 = null;
         UUID[] var6 = this.getUUIDsFromServiceRecord();
         int var7 = 0;
         int var2;
         int var13;
         if (var6 != null) {
            for(var2 = 0; var2 < var6.length; ++var2) {
               if (var6[var2] != null) {
                  ++var7;

                  for(var13 = var2 + 1; var13 < var6.length; ++var13) {
                     if (var6[var2].equals(var6[var13])) {
                        var6[var13] = null;
                     }
                  }
               }
            }

            var3 = new UUID[var7];
            var7 = 0;

            for(var2 = 0; var2 < var6.length; ++var2) {
               if (var6[var2] != null) {
                  var3[var7++] = var6[var2];
               }
            }
         }

         Object var16 = new Object();
         RemoteServiceRecord.PopulateRecordListener var12 = new RemoteServiceRecord.PopulateRecordListener(this, var16);
         synchronized(var16) {
            try {
               var5.searchServices(var1, var3, this.kd, var12);
            } catch (BluetoothStateException var9) {
               throw new IOException("Unable to contact remote device(service search not started)");
            }

            try {
               var16.wait();
            } catch (InterruptedException var8) {
            }
         }

         ServiceRecord var15;
         if ((var15 = var12.getFoundRecord()) == null) {
            throw new IOException("Remote device(or record) could not be found");
         } else {
            int[] var14;
            if ((var14 = var15.getAttributeIDs()).length != 0) {
               for(var2 = 0; var2 < var14.length; ++var2) {
                  if (var14[var2] != 0) {
                     this.updateAttributeValue(var14[var2], var15.getAttributeValue(var14[var2]));
                  }

                  if (!var4) {
                     for(var13 = 0; var13 < var1.length; ++var13) {
                        if (var14[var2] == var1[var13]) {
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
         long var4 = RemoteServiceRecord.ProtocolDescriptionParameters.a(var3);
         String var6 = RemoteServiceRecord.ProtocolDescriptionParameters.b(var3);
         return this.createConnectionURL(this.kd.getBluetoothAddress(), var1, var2, var6, var4);
      }
   }

   public void setDeviceServiceClasses(int var1) {
      throw new RuntimeException("ServiceRecord was obtained from a remote device");
   }

   private RemoteServiceRecord.ProtocolDescriptionParameters getProtocolParameters() {
      UUID var3 = new UUID(3L);
      UUID var4 = new UUID(256L);
      UUID var5 = new UUID(8L);
      String var6 = null;
      long var9 = 0L;
      DataElement var1;
      if ((var1 = this.getAttributeValue(4)) != null && var1.getDataType() == 48) {
         Enumeration var7 = (Enumeration)var1.getValue();

         while(var7.hasMoreElements() && !"btl2cap".equals(var6)) {
            if ((var1 = (DataElement)var7.nextElement()) != null && var1.getDataType() == 48) {
               Enumeration var2 = (Enumeration)var1.getValue();
               if (var1.getSize() > 1) {
                  var1 = (DataElement)var2.nextElement();
                  DataElement var19 = (DataElement)var2.nextElement();
                  if (var1 != null && var19 != null && var1.getDataType() == 24 && (var19.getDataType() == 8 || var19.getDataType() == 9)) {
                     UUID var17 = (UUID)var1.getValue();
                     long var15 = var19.getLong();
                     if (var3.equals(var17) && var19.getDataType() == 8 && validRFCOMMChannelValue(var15)) {
                        var6 = "btspp";
                        var9 = var15;
                     } else if (var4.equals(var17) && var19.getDataType() == 9 && validPSMValue(var15)) {
                        var6 = "btl2cap";
                        var9 = var15;
                     }
                  }
               } else {
                  UUID var8 = (UUID)((DataElement)var2.nextElement()).getValue();
                  if (var5.equals(var8)) {
                     var6 = "btgoep";
                  }
               }
            }
         }

         RemoteServiceRecord.ProtocolDescriptionParameters var18 = null;
         if (var6 != null) {
            var18 = new RemoteServiceRecord.ProtocolDescriptionParameters(this, var6, var9);
         }

         return var18;
      } else {
         return null;
      }
   }

   private final byte[] getBluetoothAddressAsByteArray() {
      String var3 = this.kd.getBluetoothAddress();
      byte[] var1 = new byte[6];

      for(int var2 = 0; var2 < 12; var2 += 2) {
         var1[var2 / 2] |= Integer.parseInt(var3.substring(var2, var2 + 2), 16);
      }

      return var1;
   }

   private final UUID[] getUUIDsFromServiceRecord() {
      UUID[] var4 = null;
      UUID[] var5 = null;
      int[] var3;
      if ((var3 = this.getAttributeIDs()).length != 0) {
         for(int var1 = 0; var1 < var3.length; ++var1) {
            DataElement var2;
            switch((var2 = this.getAttributeValue(var3[var1])).getDataType()) {
            case 24:
               (var5 = new UUID[1])[0] = (UUID)var2.getValue();
               break;
            case 48:
            case 56:
               var5 = this.a(var2);
            }

            if (var5 != null) {
               if (var4 == null) {
                  var4 = var5;
               } else {
                  UUID[] var6 = new UUID[var4.length + var5.length];

                  int var7;
                  for(var7 = 0; var7 < var4.length; ++var7) {
                     var6[var7] = var4[var7];
                  }

                  for(var7 = 0; var7 < var5.length; ++var7) {
                     var6[var4.length + var7] = var5[var7];
                  }

                  var4 = var6;
               }
            }
         }
      }

      return var4;
   }

   private final UUID[] a(DataElement var1) {
      UUID[] var2 = null;
      UUID[] var3 = null;
      if (var1.getDataType() != 48 && var1.getDataType() != 56) {
         throw new IllegalArgumentException("Illegal data element type");
      } else {
         Enumeration var4 = (Enumeration)var1.getValue();

         while(true) {
            while(true) {
               do {
                  if (!var4.hasMoreElements()) {
                     return var2;
                  }

                  switch((var1 = (DataElement)var4.nextElement()).getDataType()) {
                  case 24:
                     (var3 = new UUID[1])[0] = (UUID)var1.getValue();
                     break;
                  case 48:
                  case 56:
                     var3 = this.a(var1);
                  }
               } while(var3 == null);

               if (var2 == null) {
                  var2 = var3;
               } else {
                  UUID[] var5 = new UUID[var2.length + var3.length];

                  int var6;
                  for(var6 = 0; var6 < var2.length; ++var6) {
                     var5[var6] = var2[var6];
                  }

                  for(var6 = 0; var6 < var3.length; ++var6) {
                     var5[var2.length + var6] = var3[var6];
                  }

                  var2 = var5;
               }
            }
         }
      }
   }

   private class PopulateRecordListener implements DiscoveryListener {
      private Object ks;
      private ServiceRecord kt;
      private byte[] ku;

      public ServiceRecord getFoundRecord() {
         return this.kt;
      }

      public void deviceDiscovered(RemoteDevice var1, DeviceClass var2) {
      }

      public void inquiryCompleted(int var1) {
      }

      public void serviceSearchCompleted(int var1, int var2) {
         synchronized(this.ks) {
            this.ks.notify();
         }
      }

      public void servicesDiscovered(int var1, ServiceRecord[] var2) {
         for(int var4 = 0; var4 < var2.length && this.kt == null; ++var4) {
            byte[] var3;
            if ((var3 = b(var2[var4].getAttributeValue(0))) != null && var3.length == 8 && this.ku != null && this.ku.length == 8) {
               boolean var6 = true;

               for(int var5 = 0; var5 < 8; ++var5) {
                  if (var3[var5] != this.ku[var5]) {
                     var6 = false;
                     break;
                  }
               }

               if (var6) {
                  this.kt = var2[var4];
                  return;
               }
            }
         }

      }

      private static byte[] b(DataElement var0) {
         byte[] var1 = null;
         if (var0 != null) {
            switch(var0.getDataType()) {
            case 8:
            case 9:
            case 10:
            case 16:
            case 17:
            case 18:
            case 19:
               var1 = new byte[8];
               long var2 = var0.getLong();

               for(int var4 = 0; var4 < 8; ++var4) {
                  var1[var4] = (byte)((int)((var2 & 255L << (var4 << 3)) >> (var4 << 3)));
               }

               return var1;
            case 11:
               var1 = (byte[])var0.getValue();
            case 12:
            case 13:
            case 14:
            case 15:
            }
         }

         return var1;
      }

      PopulateRecordListener(RemoteServiceRecord var1, Object var2, Object var3) {
         this.ks = var2;
         this.kt = null;
         this.ku = b(var1.getAttributeValue(0));
      }
   }

   private class ProtocolDescriptionParameters {
      private long kT;
      private String protocol;

      ProtocolDescriptionParameters(RemoteServiceRecord var1, String var2, long var3) {
         this.kT = var3;
         this.protocol = new String(var2);
      }

      private long getChannelID() {
         return this.kT;
      }

      private String getProtocol() {
         return this.protocol;
      }

      static long a(RemoteServiceRecord.ProtocolDescriptionParameters var0) {
         return var0.getChannelID();
      }

      static String b(RemoteServiceRecord.ProtocolDescriptionParameters var0) {
         return var0.getProtocol();
      }
   }
}
