package javax.microedition.apdu;

import java.io.IOException;
import javax.microedition.io.Connection;

public interface APDUConnection extends Connection {
   byte[] exchangeAPDU(byte[] var1) throws IOException;

   byte[] getATR();

   byte[] enterPin(int var1) throws IOException;

   byte[] changePin(int var1) throws IOException;

   byte[] disablePin(int var1) throws IOException;

   byte[] enablePin(int var1) throws IOException;

   byte[] unblockPin(int var1, int var2) throws IOException;
}
