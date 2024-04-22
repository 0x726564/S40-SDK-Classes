package javax.bluetooth;

import com.nokia.mid.impl.isa.bluetooth.CommonBluetooth;
import com.nokia.mid.impl.isa.bluetooth.CommonNotifier;
import com.nokia.mid.impl.isa.bluetooth.LocalDevicePropertiesEvent;
import com.nokia.mid.impl.isa.bluetooth.LocalServiceRecord;
import com.nokia.mid.impl.isa.bluetooth.ServiceRecordAccessor;
import com.nokia.mid.impl.isa.bluetooth.Tracer;
import javax.microedition.io.Connection;

public class LocalDevice {
   private static LocalDevice m = null;
   private static DiscoveryAgent n = null;
   private static String o = null;

   private LocalDevice() throws BluetoothStateException, IllegalArgumentException {
      if (1 == CommonBluetooth.activateMedia()) {
         throw new BluetoothStateException("BT System is not active");
      } else {
         LocalDevicePropertiesEvent var2;
         if ((var2 = this.getProperties0()) == null) {
            throw new Error("LocalDevicePropertiesEvent is null!");
         } else if ((o = var2.getAddress()) != null && o.length() == 12) {
            try {
               n = new DiscoveryAgent();
            } catch (Exception var1) {
               o = null;
               throw new BluetoothStateException("Internal DiscoveryAgent could not be created!");
            }
         } else {
            o = null;
            throw new Error("Got invalid bd_addr in LocalDevicePropertiesEvent!");
         }
      }
   }

   public static synchronized LocalDevice getLocalDevice() throws BluetoothStateException {
      if (m == null) {
         m = new LocalDevice();
      }

      return m;
   }

   public static native boolean isPowerOn();

   public DiscoveryAgent getDiscoveryAgent() {
      return n;
   }

   public String getFriendlyName() {
      Tracer.println("getFriendlyName");

      LocalDevicePropertiesEvent var1;
      try {
         var1 = this.getProperties0();
      } catch (Exception var2) {
         return null;
      }

      if (var1 == null) {
         throw new Error("LocalDevicePropertiesEvent is null!");
      } else {
         return var1.name.length() > 0 ? new String(var1.name) : null;
      }
   }

   public DeviceClass getDeviceClass() {
      LocalDevicePropertiesEvent var1;
      try {
         var1 = this.getProperties0();
      } catch (Exception var2) {
         return null;
      }

      if (var1 == null) {
         throw new Error("LocalDevicePropertiesEvent is null!");
      } else {
         Tracer.println("getDeviceClass()->cod=" + String.valueOf(var1.cod));
         return new DeviceClass(var1.cod);
      }
   }

   public boolean setDiscoverable(int var1) throws BluetoothStateException {
      Tracer.println("setDiscoverable(IAC=" + String.valueOf(var1) + ")");
      if ((var1 < 10390272 || 10390335 < var1) && var1 != 0) {
         throw new IllegalArgumentException("got invalid mode value!");
      } else {
         return this.setDiscoverable0(var1);
      }
   }

   public static String getProperty(String var0) {
      return var0 == null || !var0.startsWith("bluetooth.") && !var0.startsWith("obex.") ? null : System.getProperty(var0);
   }

   public int getDiscoverable() {
      LocalDevicePropertiesEvent var1;
      try {
         var1 = this.getProperties0();
      } catch (BluetoothStateException var2) {
         throw new NullPointerException("LocalDevice properties cannot be retrieved presently");
      }

      if (var1 == null) {
         throw new Error("LocalDevicePropertiesEvent is null!");
      } else {
         Tracer.println("getDiscoverable()=" + String.valueOf(var1.discoverability));
         return var1.discoverability;
      }
   }

   public String getBluetoothAddress() {
      return new String(o);
   }

   public ServiceRecord getRecord(Connection var1) {
      Tracer.println("getRecord()");
      if (var1 == null) {
         throw new NullPointerException("parameter is null");
      } else if (var1 instanceof ServiceRecordAccessor) {
         if (((ServiceRecordAccessor)var1).isClosed()) {
            throw new IllegalArgumentException("notifier closed already");
         } else {
            return ((ServiceRecordAccessor)var1).getLocalServiceRecord();
         }
      } else {
         throw new IllegalArgumentException("illegal parameter");
      }
   }

   public void updateRecord(ServiceRecord var1) throws ServiceRegistrationException {
      Tracer.println("updateRecord()");
      if (var1 == null) {
         throw new NullPointerException("got no serviceRecord object");
      } else if (!(var1 instanceof LocalServiceRecord)) {
         throw new IllegalArgumentException("not of type LocalServiceRecord, use LocalDevice.getRecord to obtain");
      } else {
         CommonNotifier var4;
         if ((var4 = ((LocalServiceRecord)var1).getConnectionNotifier()) == null) {
            throw new IllegalArgumentException("serviceRecord invalid; use LocalDevice.getRecord to obtain");
         } else {
            LocalServiceRecord var2;
            if ((var2 = var4.getLocalServiceRecord()) != null) {
               synchronized(var2.getLockObject()) {
                  if (!var4.checkServiceOkToUpdate((LocalServiceRecord)var1)) {
                     throw new IllegalArgumentException("mandatory protocol specific serviceRecord attributes invalid");
                  } else {
                     var4.updateService((LocalServiceRecord)var1);
                  }
               }
            }
         }
      }
   }

   private static native void initialize0();

   private native LocalDevicePropertiesEvent getProperties0() throws BluetoothStateException, IllegalArgumentException;

   private native boolean setDiscoverable0(int var1) throws BluetoothStateException;

   static {
      initialize0();
   }
}
