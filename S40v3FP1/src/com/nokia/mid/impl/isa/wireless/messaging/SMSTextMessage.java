package com.nokia.mid.impl.isa.wireless.messaging;

import javax.wireless.messaging.TextMessage;

public class SMSTextMessage extends SMSMessage implements TextMessage {
   private String payload = null;

   public String getPayloadText() {
      return this.payload;
   }

   public void setPayloadText(String var1) {
      this.payload = var1;
   }
}
