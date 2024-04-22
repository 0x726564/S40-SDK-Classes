package com.nokia.mid.s40.io;

import java.io.IOException;

public interface LocalMessageProtocolConnection extends LocalProtocolConnection {
   LocalMessageProtocolMessage newMessage(byte[] var1);

   int receive(byte[] var1) throws IOException;

   void receive(LocalMessageProtocolMessage var1) throws IOException;

   void send(byte[] var1, int var2, int var3) throws IOException;
}
