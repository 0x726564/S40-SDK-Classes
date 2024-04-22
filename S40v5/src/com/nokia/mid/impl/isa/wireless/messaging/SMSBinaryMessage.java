package com.nokia.mid.impl.isa.wireless.messaging;

import javax.wireless.messaging.BinaryMessage;

public class SMSBinaryMessage extends SMSMessage implements BinaryMessage {
   private byte[] mj = null;

   public byte[] getPayloadData() {
      return this.mj;
   }

   public void setPayloadData(byte[] var1) {
      this.mj = var1;
   }
}
