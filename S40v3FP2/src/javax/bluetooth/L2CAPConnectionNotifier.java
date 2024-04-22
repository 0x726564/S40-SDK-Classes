package javax.bluetooth;

import java.io.IOException;
import javax.microedition.io.Connection;

public interface L2CAPConnectionNotifier extends Connection {
   L2CAPConnection acceptAndOpen() throws IOException;
}
