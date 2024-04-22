package javax.bluetooth;

import java.io.IOException;

public interface ServiceRecord {
   int NOAUTHENTICATE_NOENCRYPT = 0;
   int AUTHENTICATE_NOENCRYPT = 1;
   int AUTHENTICATE_ENCRYPT = 2;
   int RFCOMM_UUID = 3;
   int L2CAP_UUID = 256;
   int SERIAL_PORT_UUID = 4353;
   int SERVICE_RECORD_HANDLE_ID = 0;
   int SERVICE_CLASS_IDLIST_ID = 1;
   int SERVICE_RECORD_STATE_ID = 2;
   int SERVICE_ID = 3;
   int PROTOCOL_DESCRIPTOR_LIST_ID = 4;
   int SERVICE_NAME_ID = 256;

   DataElement getAttributeValue(int var1);

   RemoteDevice getHostDevice();

   int[] getAttributeIDs();

   boolean populateRecord(int[] var1) throws IOException;

   String getConnectionURL(int var1, boolean var2);

   void setDeviceServiceClasses(int var1);

   boolean setAttributeValue(int var1, DataElement var2);
}
