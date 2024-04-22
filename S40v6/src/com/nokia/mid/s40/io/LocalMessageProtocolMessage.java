package com.nokia.mid.s40.io;

public interface LocalMessageProtocolMessage {
   byte[] getData();

   void setData(byte[] var1);

   int getLength();

   boolean isReallocatable();

   void setReallocatable(boolean var1);
}
