package javax.obex;

import java.io.IOException;
import javax.microedition.io.Connection;

public interface ClientSession extends Connection {
   void setAuthenticator(Authenticator var1);

   HeaderSet createHeaderSet();

   void setConnectionID(long var1);

   long getConnectionID();

   HeaderSet connect(HeaderSet var1) throws IOException;

   HeaderSet disconnect(HeaderSet var1) throws IOException;

   HeaderSet setPath(HeaderSet var1, boolean var2, boolean var3) throws IOException;

   HeaderSet delete(HeaderSet var1) throws IOException;

   Operation get(HeaderSet var1) throws IOException;

   Operation put(HeaderSet var1) throws IOException;
}
