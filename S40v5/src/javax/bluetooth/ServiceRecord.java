package javax.bluetooth;

import java.io.IOException;

public interface ServiceRecord {
   int NOAUTHENTICATE_NOENCRYPT = 0;
   int AUTHENTICATE_NOENCRYPT = 1;
   int AUTHENTICATE_ENCRYPT = 2;

   DataElement getAttributeValue(int var1);

   RemoteDevice getHostDevice();

   int[] getAttributeIDs();

   boolean populateRecord(int[] var1) throws IOException;

   String getConnectionURL(int var1, boolean var2);

   void setDeviceServiceClasses(int var1);

   boolean setAttributeValue(int var1, DataElement var2);
}
