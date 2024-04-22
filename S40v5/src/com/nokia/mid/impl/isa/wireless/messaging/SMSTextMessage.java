package com.nokia.mid.impl.isa.wireless.messaging;

import javax.wireless.messaging.TextMessage;

public class SMSTextMessage extends SMSMessage implements TextMessage {
   private String iB = null;

   public String getPayloadText() {
      return this.iB;
   }

   public void setPayloadText(String var1) {
      this.iB = var1;
   }
}
