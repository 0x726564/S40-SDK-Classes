package javax.bluetooth;

import com.nokia.mid.impl.isa.bluetooth.CommonBluetooth;
import com.nokia.mid.impl.isa.bluetooth.CommonNotifier;
import com.nokia.mid.impl.isa.bluetooth.LocalDevicePropertiesEvent;
import com.nokia.mid.impl.isa.bluetooth.LocalServiceRecord;
import com.nokia.mid.impl.isa.bluetooth.ServiceRecordAccessor;
import com.nokia.mid.impl.isa.bluetooth.Tracer;
import com.nokia.mid.impl.isa.util.SharedObjects;
import javax.microedition.io.Connection;

public class LocalDevice {
   private static LocalDevice theLocalDevice = null;
   private static DiscoveryAgent theDiscoveryAgent = null;
   private static String bd_addr = null;
   private static final Object sharedLock = SharedObjects.getLock("javax.bluetooth.LocalDevice.sharedLock");
   private static boolean doneInit = false;

   private static void performInit() {
      synchronized(sharedLock) {
         if (!doneInit) {
            initialize0();
            doneInit = true;
         }
      }
   }

   private LocalDevice() throws BluetoothStateException, IllegalArgumentException {
      performInit();
      if (1 == CommonBluetooth.activateMedia()) {
         throw new BluetoothStateException("BT System is not active");
      } else {
         LocalDevicePropertiesEvent devProperties;
         synchronized(sharedLock) {
            devProperties = this.getProperties0();
         }

         if (devProperties == null) {
            throw new Error("LocalDevicePropertiesEvent is null!");
         } else {
            bd_addr = devProperties.getAddress();
            if (bd_addr != null && bd_addr.length() == 12) {
               try {
                  theDiscoveryAgent = new DiscoveryAgent();
               } catch (Exception var4) {
                  bd_addr = null;
                  throw new BluetoothStateException("Internal DiscoveryAgent could not be created!");
               }
            } else {
               bd_addr = null;
               throw new Error("Got invalid bd_addr in LocalDevicePropertiesEvent!");
            }
         }
      }
   }

   public static synchronized LocalDevice getLocalDevice() throws BluetoothStateException {
      if (theLocalDevice == null) {
         theLocalDevice = new LocalDevice();
      }

      return theLocalDevice;
   }

   public static boolean isPowerOn() {
      synchronized(sharedLock) {
         boolean result = isPowerOn0();
         return result;
      }
   }

   public DiscoveryAgent getDiscoveryAgent() {
      return theDiscoveryAgent;
   }

   public String getFriendlyName() {
      Tracer.println("getFriendlyName");

      LocalDevicePropertiesEvent devProperties;
      try {
         synchronized(sharedLock) {
            devProperties = this.getProperties0();
         }
      } catch (Exception var5) {
         return null;
      }

      if (devProperties == null) {
         throw new Error("LocalDevicePropertiesEvent is null!");
      } else {
         return devProperties.name.length() > 0 ? new String(devProperties.name) : null;
      }
   }

   public DeviceClass getDeviceClass() {
      LocalDevicePropertiesEvent devProperties;
      try {
         synchronized(sharedLock) {
            devProperties = this.getProperties0();
         }
      } catch (Exception var5) {
         return null;
      }

      if (devProperties == null) {
         throw new Error("LocalDevicePropertiesEvent is null!");
      } else {
         Tracer.println("getDeviceClass()->cod=" + String.valueOf(devProperties.cod));
         return new DeviceClass(devProperties.cod);
      }
   }

   public boolean setDiscoverable(int mode) throws BluetoothStateException {
      Tracer.println("setDiscoverable(IAC=" + String.valueOf(mode) + ")");
      if ((mode < 10390272 || 10390335 < mode) && mode != 0) {
         throw new IllegalArgumentException("got invalid mode value!");
      } else {
         synchronized(sharedLock) {
            boolean result = this.setDiscoverable0(mode);
            return result;
         }
      }
   }

   public static String getProperty(String property) {
      return property == null || !property.startsWith("bluetooth.") && !property.startsWith("obex.") ? null : System.getProperty(property);
   }

   public int getDiscoverable() {
      LocalDevicePropertiesEvent devProperties;
      try {
         synchronized(sharedLock) {
            devProperties = this.getProperties0();
         }
      } catch (BluetoothStateException var5) {
         throw new NullPointerException("LocalDevice properties cannot be retrieved presently");
      }

      if (devProperties == null) {
         throw new Error("LocalDevicePropertiesEvent is null!");
      } else {
         Tracer.println("getDiscoverable()=" + String.valueOf(devProperties.discoverability));
         return devProperties.discoverability;
      }
   }

   public String getBluetoothAddress() {
      return new String(bd_addr);
   }

   public ServiceRecord getRecord(Connection notifier) {
      Tracer.println("getRecord()");
      if (notifier == null) {
         throw new NullPointerException("parameter is null");
      } else if (notifier instanceof ServiceRecordAccessor) {
         if (((ServiceRecordAccessor)notifier).isClosed()) {
            throw new IllegalArgumentException("notifier closed already");
         } else {
            return ((ServiceRecordAccessor)notifier).getLocalServiceRecord();
         }
      } else {
         throw new IllegalArgumentException("illegal parameter");
      }
   }

   public void updateRecord(ServiceRecord srvRecord) throws ServiceRegistrationException {
      Tracer.println("updateRecord()");
      if (srvRecord == null) {
         throw new NullPointerException("got no serviceRecord object");
      } else if (!(srvRecord instanceof LocalServiceRecord)) {
         throw new IllegalArgumentException("not of type LocalServiceRecord, use LocalDevice.getRecord to obtain");
      } else {
         CommonNotifier recordNotifier = ((LocalServiceRecord)srvRecord).getConnectionNotifier();
         if (recordNotifier == null) {
            throw new IllegalArgumentException("serviceRecord invalid; use LocalDevice.getRecord to obtain");
         } else {
            LocalServiceRecord localRecord = recordNotifier.getLocalServiceRecord();
            if (localRecord != null) {
               synchronized(localRecord.getLockObject()) {
                  if (!recordNotifier.checkServiceOkToUpdate((LocalServiceRecord)srvRecord)) {
                     throw new IllegalArgumentException("mandatory protocol specific serviceRecord attributes invalid");
                  } else {
                     recordNotifier.updateService((LocalServiceRecord)srvRecord);
                  }
               }
            }
         }
      }
   }

   private static native void initialize0();

   private native LocalDevicePropertiesEvent getProperties0() throws BluetoothStateException, IllegalArgumentException;

   private native boolean setDiscoverable0(int var1) throws BluetoothStateException;

   private static native boolean isPowerOn0();
}
