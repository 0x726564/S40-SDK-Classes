package com.nokia.mid.impl.isa.wireless.messaging;

import javax.wireless.messaging.BinaryMessage;

public class SMSBinaryMessage extends SMSMessage implements BinaryMessage {
   private byte[] payload = null;

   public byte[] getPayloadData() {
      return this.payload;
   }

   public void setPayloadData(byte[] data) {
      this.payload = data;
   }
}
