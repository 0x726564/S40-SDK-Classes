package com.nokia.mid.impl.isa.wireless.messaging;

import java.util.Date;
import java.util.TimeZone;
import javax.wireless.messaging.Message;

public class SMSMessage implements Message {
   private String address = null;
   private int timestamp = 0;
   private int numSegments = 0;

   public String getAddress() {
      return this.address;
   }

   public Date getTimestamp() {
      Date var1;
      if (this.timestamp == 0) {
         var1 = null;
      } else {
         TimeZone var2 = TimeZone.getDefault();
         var1 = new Date((long)this.timestamp * 1000L - (long)var2.getRawOffset());
      }

      return var1;
   }

   public void setAddress(String var1) {
      this.address = var1;
   }
}
