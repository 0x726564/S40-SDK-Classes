package com.nokia.mid.s40.io;

import java.io.IOException;

public interface LocalStreamProtocolServerConnection extends LocalProtocolServerConnection {
   LocalStreamProtocolConnection acceptAndOpen() throws IOException;
}
