package com.nokia.mid.impl.isa.bluetooth;

import javax.bluetooth.RemoteDevice;

public class DiscoveryEvent {
   public static final int DEVICE_DISCOVERED = 1;
   public static final int INQUIRY_COMPLETED = 2;
   public static final int SERVICE_DISCOVERED = 3;
   public static final int SERVICE_SEARCH_COMPLETED = 4;
   public int messageType;
   public int cod;
   public int discType;
   public byte[] address;
   public int transID;
   public int respCode;
   public byte[] serviceRecordsStream = null;

   public RemoteServiceRecord[] getServiceRecords(RemoteDevice var1) throws IllegalStateException {
      Object var4 = null;
      RemoteServiceRecord[] var6 = null;
      int var7 = 0;
      if (this.serviceRecordsStream != null && var1 != null) {
         if (this.serviceRecordsStream.length == 0) {
            throw new IllegalStateException("serviceRecordsStream is not correctly initialized");
         } else {
            int var2 = 0;

            while(var2 < this.serviceRecordsStream.length) {
               if (var2 + 4 > 0 && var2 + 4 <= this.serviceRecordsStream.length) {
                  int var3 = this.serviceRecordsStream[var2 + 0] << 24 & -16777216 | this.serviceRecordsStream[var2 + 1] << 16 & 16711680 | this.serviceRecordsStream[var2 + 2] << 8 & '\uff00' | this.serviceRecordsStream[var2 + 3] & 255;
                  if (var3 > 0 && var2 + 4 + var3 > 0 && var2 + 4 + var3 <= this.serviceRecordsStream.length) {
                     byte[] var11 = new byte[var3];

                     for(int var8 = 0; var8 < var3; ++var8) {
                        var11[var8] = this.serviceRecordsStream[var2 + 4 + var8];
                     }

                     try {
                        RemoteServiceRecord var5 = new RemoteServiceRecord(var1, var11);
                        ++var7;
                        RemoteServiceRecord[] var12 = new RemoteServiceRecord[var7];

                        for(int var9 = 0; var9 < var7 - 1; ++var9) {
                           var12[var9] = var6[var9];
                        }

                        var12[var7 - 1] = var5;
                        var6 = var12;
                     } catch (Exception var10) {
                     }

                     var2 += 4 + var3;
                     continue;
                  }

                  throw new IllegalStateException("serviceRecordsStream is not correctly initialized");
               }

               throw new IllegalStateException("serviceRecordsStream is not correctly initialized");
            }

            if (var2 != this.serviceRecordsStream.length) {
               throw new IllegalStateException("serviceRecordsStream is not correctly initialized");
            } else {
               return var6;
            }
         }
      } else {
         throw new NullPointerException("serviceRecordsStream and/or remoteDevice is/are null");
      }
   }

   public final String getAddress() {
      String var1 = null;
      if (this.address != null && this.address.length == 6) {
         var1 = new String("");

         for(int var2 = 0; var2 < 6; ++var2) {
            var1 = var1.concat(Integer.toHexString((this.address[var2] & 240) >> 4));
            var1 = var1.concat(Integer.toHexString(this.address[var2] & 15));
         }
      }

      return var1;
   }
}
