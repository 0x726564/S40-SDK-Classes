package com.nokia.mid.s40.io;

import java.io.IOException;

public interface LocalMessageProtocolServerConnection extends LocalProtocolServerConnection {
   LocalMessageProtocolConnection acceptAndOpen() throws IOException;
}
