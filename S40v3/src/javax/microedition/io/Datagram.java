package javax.microedition.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface Datagram extends DataInput, DataOutput {
   String getAddress();

   byte[] getData();

   int getLength();

   int getOffset();

   void setAddress(String var1) throws IOException;

   void setAddress(Datagram var1);

   void setLength(int var1);

   void setData(byte[] var1, int var2, int var3);

   void reset();
}
