package javax.obex;

import java.io.IOException;
import javax.microedition.io.Connection;

public interface SessionNotifier extends Connection {
   Connection acceptAndOpen(ServerRequestHandler var1) throws IOException;

   Connection acceptAndOpen(ServerRequestHandler var1, Authenticator var2) throws IOException;
}
