package com.nokia.mid.impl.isa.bluetooth;

public class LocalDevicePropertiesEvent {
   public byte status;
   public byte[] address;
   public int cod;
   public byte mode;
   public int discoverability;
   public byte name_len;
   public String name;

   public final String getAddress() {
      String res = null;
      if (this.address != null && this.address.length == 6) {
         res = new String("");

         for(int i = 0; i < 6; ++i) {
            res = res.concat(Integer.toHexString((this.address[i] & 240) >> 4));
            res = res.concat(Integer.toHexString(this.address[i] & 15));
         }
      }

      return res.toUpperCase();
   }
}
