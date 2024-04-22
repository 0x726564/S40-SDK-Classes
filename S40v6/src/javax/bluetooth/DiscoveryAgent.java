package javax.bluetooth;

import com.nokia.mid.impl.isa.bluetooth.DiscoveryEvent;
import com.nokia.mid.impl.isa.bluetooth.Tracer;
import com.nokia.mid.impl.isa.util.SharedObjects;
import java.util.Vector;

public class DiscoveryAgent {
   private static final boolean DEBUG = false;
   public static final int NOT_DISCOVERABLE = 0;
   public static final int GIAC = 10390323;
   public static final int LIAC = 10390272;
   public static final int CACHED = 0;
   public static final int PREKNOWN = 1;
   private static final int INQUIRY_NOT_ACTIVE = 0;
   private static final int INQUIRY_ACTIVE = 1;
   private static final int INQUIRY_TERMINATED = 2;
   private static final int SERVICE_SEARCH_NOT_ACTIVE = 0;
   private static final int SERVICE_SEARCH_ACTIVE = 1;
   private static final int SERVICE_SEARCH_TERMINATED = 2;
   private static final int ATTRIBUTE_MAX_VALUE = 65535;
   private static boolean initialized;
   private static int curTransID;
   private static final Object inquiryStatusLock = SharedObjects.getLock("javax.bluetooth.DiscoveryAgent.inquiryStatus");
   private Object serviceSearchStatusLock = SharedObjects.getLock("javax.bluetooth.DiscoveryAgent.serviceSearchStatus");
   private int inquiryHandle;
   private DiscoveryAgent.InquiryListenerThread inquiryListenerThread;
   private int inquiryStatus;
   private int serviceSearchHandle;
   private DiscoveryAgent.ServiceSearchListenerThread serviceSearchListenerThread;
   private int serviceSearchStatus;
   private Vector cachedDevices;
   private static final Object sharedLock = SharedObjects.getLock("javax.bluetooth.DiscoveryAgent.sharedLock");
   private static boolean doneInit = false;

   private static void performInit() {
      synchronized(sharedLock) {
         if (!doneInit) {
            initialize0();
            doneInit = true;
         }
      }
   }

   DiscoveryAgent() {
      performInit();
      synchronized(this) {
         if (initialized) {
            Tracer.println("DiscoveryAgent() throws RuntimeException");
            throw new RuntimeException("Discovery agent is already initialized(there can be only one discovery agent)");
         }

         initialized = true;
      }

      this.cachedDevices = new Vector();
      this.inquiryListenerThread = null;
      this.inquiryStatus = 0;
      this.serviceSearchListenerThread = null;
      this.serviceSearchStatus = 0;
      curTransID = 0;
   }

   private void addDeviceToCache(int discHandle, String address) {
      synchronized(this.cachedDevices) {
         if (discHandle == this.inquiryHandle && !this.cachedDevices.contains(address)) {
            this.cachedDevices.insertElementAt(address, this.cachedDevices.size());
         }

      }
   }

   public RemoteDevice[] retrieveDevices(int option) {
      switch(option) {
      case 0:
         synchronized(this.cachedDevices) {
            RemoteDevice[] result = null;
            int listLength = this.cachedDevices.size();
            if (listLength != 0) {
               result = new RemoteDevice[listLength];

               for(int i = 0; i < listLength; ++i) {
                  result[i] = new RemoteDevice((String)this.cachedDevices.elementAt(i));
               }
            }

            Tracer.println("retrieveDevices CACHED ret n=" + String.valueOf(listLength));
            return result;
         }
      case 1:
         Tracer.println("retrieveDevices PREKNOWN ret=NULL");
         return null;
      default:
         Tracer.println("IllegalArgumentException");
         throw new IllegalArgumentException("Unknown option(valid values: CACHED and PREKNOWN)");
      }
   }

   public boolean startInquiry(int accessCode, DiscoveryListener listener) throws BluetoothStateException {
      Tracer.println("startInquiry(IAC=" + String.valueOf(accessCode) + ")");
      if (accessCode == 10390272 || accessCode == 10390323 || accessCode >= 10390272 && accessCode <= 10390335) {
         if (listener == null) {
            Tracer.println("NullPointerException");
            throw new NullPointerException("Discovery listener can not be null");
         } else {
            synchronized(inquiryStatusLock) {
               if (this.inquiryStatus != 0) {
                  Tracer.println("BluetoothStateException");
                  throw new BluetoothStateException("One inquiry is still active and only one can be active");
               } else {
                  synchronized(this.cachedDevices) {
                     int inquiryHandle = 0;
                     boolean inquiryStarted = false;

                     do {
                        try {
                           Tracer.println("startInquiry0");
                           inquiryHandle = this.startInquiry0(accessCode);
                           inquiryStarted = true;
                        } catch (BluetoothStateException var12) {
                           Tracer.println("caught BluetoothStateException");
                           if (!var12.getMessage().equals("busy")) {
                              Tracer.println("BluetoothStateException");
                              throw var12;
                           }

                           Tracer.println("sleep(1000)");

                           try {
                              Thread.sleep(1000L);
                           } catch (InterruptedException var11) {
                           }
                        }
                     } while(!inquiryStarted);

                     this.cachedDevices.removeAllElements();
                     this.inquiryHandle = inquiryHandle;
                     this.inquiryStatus = 1;
                     this.inquiryListenerThread = new DiscoveryAgent.InquiryListenerThread(this.inquiryHandle, listener);
                     return true;
                  }
               }
            }
         }
      } else {
         Tracer.println("IllegalArgumentException");
         throw new IllegalArgumentException("Illegal parameter value(access code)");
      }
   }

   public boolean cancelInquiry(DiscoveryListener listener) {
      Tracer.println("cancelInquiry");
      if (listener == null) {
         Tracer.println("NullPointerException");
         throw new NullPointerException("Discovery listener can not be null");
      } else {
         synchronized(inquiryStatusLock) {
            if (this.inquiryListenerThread != null && this.inquiryListenerThread.getListener() == listener && this.inquiryStatus == 1) {
               Tracer.println("stopDiscovery0");
               this.stopDiscovery0(this.inquiryHandle);
               this.inquiryStatus = 2;
               return true;
            } else {
               return false;
            }
         }
      }
   }

   public int searchServices(int[] attrSet, UUID[] uuidSet, RemoteDevice btDev, DiscoveryListener discListener) throws BluetoothStateException {
      int[] defaultAttributes = new int[]{0, 1, 2, 3, 4};
      Tracer.println("searchServices");
      if (uuidSet != null && btDev != null && discListener != null) {
         if (uuidSet.length == 0) {
            Tracer.println("IllegalArgumentException");
            throw new IllegalArgumentException("Illegal length of the UUID set");
         } else {
            int i;
            int j;
            for(i = 0; i < uuidSet.length; ++i) {
               if (uuidSet[i] == null) {
                  Tracer.println("NullPointerException");
                  throw new NullPointerException("UUID can not be null");
               }

               for(j = i + 1; j < uuidSet.length; ++j) {
                  if (uuidSet[i].equals(uuidSet[j])) {
                     Tracer.println("IllegalArgumentException");
                     throw new IllegalArgumentException("UUID set contains duplicates(not allowed)");
                  }
               }
            }

            int[] attributesToBeRetreived;
            if (attrSet == null) {
               attributesToBeRetreived = defaultAttributes;
            } else {
               if (attrSet.length == 0) {
                  Tracer.println("IllegalArgumentException");
                  throw new IllegalArgumentException("Illegal length of the attribute set");
               }

               Vector defaultAttributesNotInAttrSet = new Vector(defaultAttributes.length);

               for(i = 0; i < defaultAttributes.length; ++i) {
                  defaultAttributesNotInAttrSet.addElement(new Integer(defaultAttributes[i]));
               }

               i = 0;

               label184:
               while(true) {
                  if (i >= attrSet.length) {
                     attributesToBeRetreived = new int[attrSet.length + defaultAttributesNotInAttrSet.size()];
                     System.arraycopy(attrSet, 0, attributesToBeRetreived, 0, attrSet.length);
                     i = 0;

                     while(true) {
                        if (i >= defaultAttributesNotInAttrSet.size()) {
                           break label184;
                        }

                        attributesToBeRetreived[attrSet.length + i] = (Integer)defaultAttributesNotInAttrSet.elementAt(i);
                        ++i;
                     }
                  }

                  if (attrSet[i] < 0 || attrSet[i] > 65535) {
                     Tracer.println("IllegalArgumentException");
                     throw new IllegalArgumentException("Attribute has illegal value");
                  }

                  for(j = i + 1; j < attrSet.length; ++j) {
                     if (attrSet[i] == attrSet[j]) {
                        Tracer.println("IllegalArgumentException");
                        throw new IllegalArgumentException("Attribut set contains duplicates(not allowed)");
                     }
                  }

                  for(j = 0; j < defaultAttributesNotInAttrSet.size(); ++j) {
                     if ((Integer)defaultAttributesNotInAttrSet.elementAt(j) == attrSet[i]) {
                        defaultAttributesNotInAttrSet.removeElementAt(j);
                        break;
                     }
                  }

                  ++i;
               }
            }

            if (attributesToBeRetreived.length > Integer.parseInt(LocalDevice.getProperty("bluetooth.sd.attr.retrievable.max"))) {
               Tracer.println("IllegalArgumentException");
               throw new IllegalArgumentException("Number of attributs to search is to large");
            } else {
               for(i = 0; i < attributesToBeRetreived.length; ++i) {
                  for(j = i + 1; j < attributesToBeRetreived.length; ++j) {
                     if (attributesToBeRetreived[i] > attributesToBeRetreived[j]) {
                        int tmp = attributesToBeRetreived[i];
                        attributesToBeRetreived[i] = attributesToBeRetreived[j];
                        attributesToBeRetreived[j] = tmp;
                     }
                  }
               }

               String[] uuidCopy = new String[uuidSet.length];
               String zeroesString = new String("00000000000000000000000000000000");

               for(i = 0; i < uuidSet.length; ++i) {
                  String temp = uuidSet[i].toString();
                  uuidCopy[i] = new String(zeroesString.substring(0, 32 - temp.length()).concat(temp));
               }

               byte[] uuidsAttributesStream = new byte[4 + 16 * uuidCopy.length + 2 * attributesToBeRetreived.length];
               int current = 0;
               int var25 = current + 1;
               uuidsAttributesStream[current] = (byte)((uuidCopy.length & '\uff00') >> 8);
               uuidsAttributesStream[var25++] = (byte)(uuidCopy.length & 255);
               uuidsAttributesStream[var25++] = (byte)((attributesToBeRetreived.length & '\uff00') >> 8);
               uuidsAttributesStream[var25++] = (byte)(attributesToBeRetreived.length & 255);

               for(i = 0; i < uuidCopy.length; ++i) {
                  for(int k = 0; k < 32; k += 2) {
                     uuidsAttributesStream[var25++] = (byte)Integer.parseInt(uuidCopy[i].substring(k, k + 2), 16);
                  }
               }

               for(i = 0; i < attributesToBeRetreived.length; ++i) {
                  uuidsAttributesStream[var25++] = (byte)((attributesToBeRetreived[i] & '\uff00') >> 8);
                  uuidsAttributesStream[var25++] = (byte)(attributesToBeRetreived[i] & 255);
               }

               synchronized(this.serviceSearchStatusLock) {
                  if (this.serviceSearchStatus != 0) {
                     Tracer.println("BluetoothStateException");
                     throw new BluetoothStateException("The maximum number of concurrent service search is exceeded");
                  } else {
                     int serviceSearchHandle = 0;
                     boolean serviceDiscoveryStarted = false;

                     do {
                        try {
                           Tracer.println("startServiceSearch0");
                           serviceSearchHandle = this.startServiceSearch0(uuidsAttributesStream, this.getBluetoothAddressAsByteArray(btDev));
                           serviceDiscoveryStarted = true;
                        } catch (BluetoothStateException var21) {
                           Tracer.println("caught BluetoothStateException");
                           if (!var21.getMessage().equals("busy")) {
                              Tracer.println("BluetoothStateException");
                              throw var21;
                           }

                           Tracer.println("sleep(1000)");

                           try {
                              Thread.sleep(1000L);
                           } catch (InterruptedException var20) {
                           }
                        }
                     } while(!serviceDiscoveryStarted);

                     this.serviceSearchHandle = serviceSearchHandle;
                     this.serviceSearchStatus = 1;
                     ++curTransID;
                     if (curTransID < 0) {
                        curTransID = 1;
                     }

                     this.serviceSearchListenerThread = new DiscoveryAgent.ServiceSearchListenerThread(btDev, curTransID, this.serviceSearchHandle, discListener);
                     return curTransID;
                  }
               }
            }
         }
      } else {
         Tracer.println("NullPointerException");
         throw new NullPointerException("Discovery listener or UUID set can not be null");
      }
   }

   public boolean cancelServiceSearch(int transID) {
      synchronized(this.serviceSearchStatusLock) {
         Tracer.println("cancelServiceSearch");
         if (this.serviceSearchListenerThread != null && this.serviceSearchListenerThread.getTransID() == transID && this.serviceSearchStatus == 1) {
            Tracer.println("stopDiscovery0");
            this.stopDiscovery0(this.serviceSearchHandle);
            this.serviceSearchStatus = 2;
            return true;
         } else {
            return false;
         }
      }
   }

   public String selectService(UUID uuid, int security, boolean master) throws BluetoothStateException {
      Tracer.println("selectService");
      if (uuid == null) {
         throw new NullPointerException("UUID can not be null");
      } else if (security != 0 && security != 1 && security != 2) {
         throw new IllegalArgumentException("Security is not one of the allowed values");
      } else {
         Object threadLockObject = new Object();
         DiscoveryAgent.SelectServiceListener selectServiceListener = new DiscoveryAgent.SelectServiceListener(security, master, threadLockObject);
         synchronized(threadLockObject) {
            try {
               this.startInquiry(10390323, selectServiceListener);
            } catch (BluetoothStateException var16) {
               throw new BluetoothStateException("Select service failed(problems to start inquiry)");
            }

            try {
               threadLockObject.wait();
            } catch (InterruptedException var15) {
            }
         }

         RemoteDevice[] foundDevices = selectServiceListener.getDiscoveredDevices();
         if (foundDevices == null) {
            return null;
         } else {
            UUID[] uuidArray = new UUID[]{new UUID(uuid.toString(), false)};

            for(int i = 0; i < foundDevices.length; ++i) {
               synchronized(threadLockObject) {
                  try {
                     this.searchServices((int[])null, uuidArray, foundDevices[i], selectServiceListener);
                  } catch (BluetoothStateException var13) {
                     throw new BluetoothStateException("Select service failed due to internal reasons");
                  }

                  try {
                     threadLockObject.wait();
                  } catch (InterruptedException var12) {
                  }
               }

               String connectionString = selectServiceListener.getURL();
               if (connectionString != null) {
                  return new String(connectionString);
               }
            }

            return null;
         }
      }
   }

   private final byte[] getBluetoothAddressAsByteArray(RemoteDevice btDev) {
      if (btDev == null) {
         throw new NullPointerException("btDev is null");
      } else {
         byte[] res = new byte[6];
         String address = btDev.getBluetoothAddress();

         for(int i = 0; i < 12; i += 2) {
            res[i / 2] = (byte)Integer.parseInt(address.substring(i, i + 2), 16);
         }

         return res;
      }
   }

   private static native void initialize0();

   private native int startInquiry0(int var1) throws BluetoothStateException;

   private native void stopDiscovery0(int var1);

   private native int startServiceSearch0(byte[] var1, byte[] var2) throws BluetoothStateException;

   private native DiscoveryEvent getInquiryEventForHandle0(int var1);

   private native DiscoveryEvent getServiceSearchEventForHandle0(int var1);

   private class ServiceSearchListenerThread extends Thread {
      private RemoteDevice remoteDevice;
      private int transID;
      private int serviceSearchHandle;
      private DiscoveryListener discListener;

      public ServiceSearchListenerThread(RemoteDevice remoteDevice, int transID, int serviceSearchHandle, DiscoveryListener discListener) {
         this.remoteDevice = remoteDevice;
         this.transID = transID;
         this.serviceSearchHandle = serviceSearchHandle;
         this.discListener = discListener;
         this.start();
      }

      public int getTransID() {
         return this.transID;
      }

      public void run() {
         boolean recordsFound = false;
         DiscoveryEvent discEvent = null;

         while(true) {
            while(true) {
               discEvent = DiscoveryAgent.this.getServiceSearchEventForHandle0(this.serviceSearchHandle);
               if (discEvent == null) {
                  try {
                     sleep(50L);
                  } catch (InterruptedException var11) {
                  }
               } else if (discEvent.messageType == 3) {
                  try {
                     ServiceRecord[] serviceRecords = discEvent.getServiceRecords(this.remoteDevice);
                     if (serviceRecords != null) {
                        int i = 0;

                        for(int j = serviceRecords.length - 1; i < j; --j) {
                           ServiceRecord tmp = serviceRecords[i];
                           serviceRecords[i] = serviceRecords[j];
                           serviceRecords[j] = tmp;
                           ++i;
                        }

                        synchronized(DiscoveryAgent.this.serviceSearchStatusLock) {
                           if (DiscoveryAgent.this.serviceSearchStatus == 1) {
                              this.discListener.servicesDiscovered(this.transID, serviceRecords);
                              recordsFound = true;
                           }
                        }
                     }
                  } catch (Exception var13) {
                  }
               } else if (discEvent.messageType == 4) {
                  Tracer.println("calling list.serviceSearchCompleted");
                  synchronized(DiscoveryAgent.this.serviceSearchStatusLock) {
                     DiscoveryAgent.this.serviceSearchListenerThread = null;
                     if (DiscoveryAgent.this.serviceSearchStatus != 2 && discEvent.respCode != 2) {
                        if (discEvent.respCode == 3) {
                           Tracer.println("SERVICE_SEARCH_ERROR");
                           DiscoveryAgent.this.serviceSearchStatus = 0;
                           this.discListener.serviceSearchCompleted(this.transID, discEvent.respCode);
                        } else if (discEvent.respCode == 6) {
                           Tracer.println("SERVICE_SEARCH_DEVICE_NOT_REACHABLE");
                           DiscoveryAgent.this.serviceSearchStatus = 0;
                           this.discListener.serviceSearchCompleted(this.transID, discEvent.respCode);
                        } else if (recordsFound) {
                           Tracer.println("SERVICE_SEARCH_COMPLETED");
                           DiscoveryAgent.this.serviceSearchStatus = 0;
                           this.discListener.serviceSearchCompleted(this.transID, 1);
                        } else {
                           Tracer.println("SERVICE_SEARCH_NO_RECORDS");
                           DiscoveryAgent.this.serviceSearchStatus = 0;
                           this.discListener.serviceSearchCompleted(this.transID, 4);
                        }
                     } else {
                        Tracer.println("SERVICE_SEARCH_TERMINATED");
                        DiscoveryAgent.this.serviceSearchStatus = 0;
                        this.discListener.serviceSearchCompleted(this.transID, 2);
                     }

                     return;
                  }
               }
            }
         }
      }
   }

   private class InquiryListenerThread extends Thread {
      private int inquiryHandle;
      private DiscoveryListener discListener;

      public InquiryListenerThread(int inquiryHandle, DiscoveryListener discListener) {
         this.inquiryHandle = inquiryHandle;
         this.discListener = discListener;
         this.start();
      }

      public DiscoveryListener getListener() {
         return this.discListener;
      }

      public void run() {
         DiscoveryEvent discEvent = null;

         while(true) {
            while(true) {
               discEvent = DiscoveryAgent.this.getInquiryEventForHandle0(this.inquiryHandle);
               if (discEvent == null) {
                  try {
                     sleep(500L);
                  } catch (InterruptedException var7) {
                  }
               } else if (discEvent.messageType == 1) {
                  String address = discEvent.getAddress();
                  if (address != null) {
                     synchronized(DiscoveryAgent.inquiryStatusLock) {
                        if (DiscoveryAgent.this.inquiryStatus == 1) {
                           DiscoveryAgent.this.addDeviceToCache(this.inquiryHandle, address);
                           this.discListener.deviceDiscovered(new RemoteDevice(address), new DeviceClass(discEvent.cod));
                        }
                     }
                  }
               } else if (discEvent.messageType == 2) {
                  synchronized(DiscoveryAgent.inquiryStatusLock) {
                     DiscoveryAgent.this.inquiryListenerThread = null;
                     if (DiscoveryAgent.this.inquiryStatus != 2 && discEvent.discType != 5) {
                        if (discEvent.discType == 7) {
                           DiscoveryAgent.this.inquiryStatus = 0;
                           Tracer.println("INQUIRY_ERROR");
                           this.discListener.inquiryCompleted(7);
                        } else {
                           DiscoveryAgent.this.inquiryStatus = 0;
                           Tracer.println("INQUIRY_COMPLETED");
                           this.discListener.inquiryCompleted(0);
                        }
                     } else {
                        DiscoveryAgent.this.inquiryStatus = 0;
                        Tracer.println("INQUIRY_TERMINATED");
                        this.discListener.inquiryCompleted(5);
                     }

                     return;
                  }
               }
            }
         }
      }
   }

   private class SelectServiceListener implements DiscoveryListener {
      private static final int MAJOR_DEV_CLASS_PHONE_BIT = 512;
      private int security;
      private boolean master;
      private String connectionURL;
      private Object callingThreadLock;
      private Vector foundPhones;
      private Vector foundOtherDevices;

      private SelectServiceListener(int security, boolean master, Object threadLockObject) {
         this.security = security;
         this.master = master;
         this.callingThreadLock = threadLockObject;
         this.connectionURL = null;
         this.foundPhones = new Vector();
         this.foundOtherDevices = new Vector();
      }

      private String getURL() {
         return this.connectionURL;
      }

      public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
         if ((cod.getMajorDeviceClass() & 512) != 0) {
            this.foundPhones.addElement(btDevice);
         } else {
            this.foundOtherDevices.addElement(btDevice);
         }

      }

      public void inquiryCompleted(int discType) {
         synchronized(this.callingThreadLock) {
            Tracer.println("inquiryCompleted()=" + String.valueOf(discType));
            this.callingThreadLock.notify();
         }
      }

      private RemoteDevice[] getDiscoveredDevices() {
         RemoteDevice[] result = null;
         int listLength = this.foundPhones.size() + this.foundOtherDevices.size();
         if (listLength != 0) {
            result = new RemoteDevice[listLength];

            int i;
            for(i = 0; i < this.foundPhones.size(); ++i) {
               result[i] = (RemoteDevice)this.foundPhones.elementAt(i);
            }

            for(i = 0; i < this.foundOtherDevices.size(); ++i) {
               result[this.foundPhones.size() + i] = (RemoteDevice)this.foundOtherDevices.elementAt(i);
            }
         }

         return result;
      }

      public void serviceSearchCompleted(int transID, int respCode) {
         synchronized(this.callingThreadLock) {
            Tracer.println("serviceSearchCompleted()=" + String.valueOf(respCode));
            this.callingThreadLock.notify();
         }
      }

      public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
         this.connectionURL = servRecord[0].getConnectionURL(this.security, this.master);
      }

      // $FF: synthetic method
      SelectServiceListener(int x1, boolean x2, Object x3, Object x4) {
         this(x1, x2, x3);
      }
   }
}
