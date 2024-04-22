package com.nokia.mid.s40.io;

import java.io.IOException;
import javax.microedition.io.Connection;

public interface LocalProtocolServerConnection extends Connection {
   String getLocalName() throws IOException;

   String getClientDomain();

   String getClientSecurityPolicy();
}
