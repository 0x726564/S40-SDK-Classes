package com.nokia.mid.impl.isa.wireless.messaging;

import java.util.Date;
import javax.wireless.messaging.Message;

public class SMSMessage implements Message {
   private String dV = null;
   private int kS = 0;

   public String getAddress() {
      return this.dV;
   }

   public Date getTimestamp() {
      return null;
   }

   public void setAddress(String var1) {
      this.dV = var1;
   }
}
