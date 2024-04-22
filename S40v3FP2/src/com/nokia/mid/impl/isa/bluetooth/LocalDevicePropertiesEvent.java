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
      String var1 = null;
      if (this.address != null && this.address.length == 6) {
         var1 = new String("");

         for(int var2 = 0; var2 < 6; ++var2) {
            var1 = var1.concat(Integer.toHexString((this.address[var2] & 240) >> 4));
            var1 = var1.concat(Integer.toHexString(this.address[var2] & 15));
         }
      }

      return var1.toUpperCase();
   }
}
