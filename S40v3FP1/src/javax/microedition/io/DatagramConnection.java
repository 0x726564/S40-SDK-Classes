package javax.microedition.io;

import java.io.IOException;

public interface DatagramConnection extends Connection {
   int getMaximumLength() throws IOException;

   int getNominalLength() throws IOException;

   void send(Datagram var1) throws IOException;

   void receive(Datagram var1) throws IOException;

   Datagram newDatagram(int var1) throws IOException;

   Datagram newDatagram(int var1, String var2) throws IOException;

   Datagram newDatagram(byte[] var1, int var2) throws IOException;

   Datagram newDatagram(byte[] var1, int var2, String var3) throws IOException;
}
