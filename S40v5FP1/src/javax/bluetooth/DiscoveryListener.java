package javax.bluetooth;

public interface DiscoveryListener {
   int INQUIRY_COMPLETED = 0;
   int INQUIRY_TERMINATED = 5;
   int INQUIRY_ERROR = 7;
   int SERVICE_SEARCH_COMPLETED = 1;
   int SERVICE_SEARCH_TERMINATED = 2;
   int SERVICE_SEARCH_ERROR = 3;
   int SERVICE_SEARCH_NO_RECORDS = 4;
   int SERVICE_SEARCH_DEVICE_NOT_REACHABLE = 6;

   void deviceDiscovered(RemoteDevice var1, DeviceClass var2);

   void servicesDiscovered(int var1, ServiceRecord[] var2);

   void serviceSearchCompleted(int var1, int var2);

   void inquiryCompleted(int var1);
}
