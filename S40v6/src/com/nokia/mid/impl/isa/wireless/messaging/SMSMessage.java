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
      Date time;
      if (this.timestamp == 0) {
         time = null;
      } else {
         TimeZone tz_default = TimeZone.getDefault();
         time = new Date((long)this.timestamp * 1000L - (long)tz_default.getRawOffset());
      }

      return time;
   }

   public void setAddress(String addr) {
      this.address = addr;
   }
}
