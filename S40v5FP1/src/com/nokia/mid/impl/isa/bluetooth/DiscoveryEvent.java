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

   public RemoteServiceRecord[] getServiceRecords(RemoteDevice remoteDevice) throws IllegalStateException {
      byte[] currentRecordStream = null;
      RemoteServiceRecord[] res = null;
      int foundServiceRecordsCount = 0;
      if (this.serviceRecordsStream != null && remoteDevice != null) {
         if (this.serviceRecordsStream.length == 0) {
            throw new IllegalStateException("serviceRecordsStream is not correctly initialized");
         } else {
            int current = 0;

            while(current < this.serviceRecordsStream.length) {
               if (current + 4 > 0 && current + 4 <= this.serviceRecordsStream.length) {
                  int currentRecordSize = this.serviceRecordsStream[current + 0] << 24 & -16777216 | this.serviceRecordsStream[current + 1] << 16 & 16711680 | this.serviceRecordsStream[current + 2] << 8 & '\uff00' | this.serviceRecordsStream[current + 3] & 255;
                  if (currentRecordSize > 0 && current + 4 + currentRecordSize > 0 && current + 4 + currentRecordSize <= this.serviceRecordsStream.length) {
                     byte[] currentRecordStream = new byte[currentRecordSize];

                     for(int i = 0; i < currentRecordSize; ++i) {
                        currentRecordStream[i] = this.serviceRecordsStream[current + 4 + i];
                     }

                     try {
                        RemoteServiceRecord currentRecord = new RemoteServiceRecord(remoteDevice, currentRecordStream);
                        ++foundServiceRecordsCount;
                        RemoteServiceRecord[] temp = new RemoteServiceRecord[foundServiceRecordsCount];

                        for(int j = 0; j < foundServiceRecordsCount - 1; ++j) {
                           temp[j] = res[j];
                        }

                        temp[foundServiceRecordsCount - 1] = currentRecord;
                        res = temp;
                     } catch (Exception var10) {
                     }

                     current += 4 + currentRecordSize;
                     continue;
                  }

                  throw new IllegalStateException("serviceRecordsStream is not correctly initialized");
               }

               throw new IllegalStateException("serviceRecordsStream is not correctly initialized");
            }

            if (current != this.serviceRecordsStream.length) {
               throw new IllegalStateException("serviceRecordsStream is not correctly initialized");
            } else {
               return res;
            }
         }
      } else {
         throw new NullPointerException("serviceRecordsStream and/or remoteDevice is/are null");
      }
   }

   public final String getAddress() {
      String res = null;
      if (this.address != null && this.address.length == 6) {
         res = new String("");

         for(int i = 0; i < 6; ++i) {
            res = res.concat(Integer.toHexString((this.address[i] & 240) >> 4));
            res = res.concat(Integer.toHexString(this.address[i] & 15));
         }
      }

      return res;
   }
}
