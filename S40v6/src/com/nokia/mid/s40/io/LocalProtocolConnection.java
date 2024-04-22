package com.nokia.mid.s40.io;

import java.io.IOException;
import javax.microedition.io.Connection;

public interface LocalProtocolConnection extends Connection {
   String getLocalName() throws IOException;

   String getRemoteName() throws IOException;
}
