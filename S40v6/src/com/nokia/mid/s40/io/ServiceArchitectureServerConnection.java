package com.nokia.mid.s40.io;

import java.io.IOException;

public interface ServiceArchitectureServerConnection extends LocalProtocolServerConnection {
   void registerWithServiceRegistry(String var1, String var2, String var3, byte[] var4) throws IOException;
}
