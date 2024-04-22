package com.nokia.mid.impl.isa.io.protocol.external.localmsg;

import com.nokia.mid.s40.io.LocalMessageProtocolMessage;

class Message implements LocalMessageProtocolMessage {
   byte[] data;
   boolean reallocatable;
   int length;

   Message(byte[] data) {
      if (data == null) {
         this.reallocatable = true;
      } else {
         this.data = data;
      }

   }

   public byte[] getData() {
      return this.data;
   }

   public void setData(byte[] data) {
      this.data = data;
   }

   public int getLength() {
      return this.length;
   }

   public boolean isReallocatable() {
      return this.reallocatable;
   }

   public void setReallocatable(boolean reallocatable) {
      this.reallocatable = reallocatable;
   }
}
