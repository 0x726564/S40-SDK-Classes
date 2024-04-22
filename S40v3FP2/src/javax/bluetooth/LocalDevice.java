package javax.bluetooth;

import com.nokia.mid.impl.isa.bluetooth.CommonBluetooth;
import com.nokia.mid.impl.isa.bluetooth.CommonNotifier;
import com.nokia.mid.impl.isa.bluetooth.LocalDevicePropertiesEvent;
import com.nokia.mid.impl.isa.bluetooth.LocalServiceRecord;
import com.nokia.mid.impl.isa.bluetooth.Tracer;
import com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolNotifier;
import javax.microedition.io.Connection;

public class LocalDevice {
   private static final String jsr82ReleaseID = "F_JSR82_06w15_00\n05-APR-2006";
   private static LocalDevice theLocalDevice = null;
   private static DiscoveryAgent theDiscoveryAgent = null;
   private static String bd_addr = null;
   private static NativeSettings nativeSettings = null;

   private LocalDevice() throws BluetoothStateException, IllegalArgumentException {
      if (1 == CommonBluetooth.activateMedia()) {
         throw new BluetoothStateException("BT System is not active");
      } else {
         LocalDevicePropertiesEvent var1 = this.getProperties0();
         if (var1 == null) {
            throw new Error("LocalDevicePropertiesEvent is null!");
         } else {
            bd_addr = var1.getAddress();
            if (bd_addr != null && bd_addr.length() == 12) {
               try {
                  theDiscoveryAgent = new DiscoveryAgent();
               } catch (Exception var3) {
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

   public static LocalDevice getLocalDevice() throws BluetoothStateException {
      if (theLocalDevice == null) {
         theLocalDevice = new LocalDevice();
      }

      return theLocalDevice;
   }

   public DiscoveryAgent getDiscoveryAgent() {
      return theDiscoveryAgent;
   }

   public String getFriendlyName() {
      Tracer.println("getFriendlyName");

      LocalDevicePropertiesEvent var1;
      try {
         var1 = this.getProperties0();
      } catch (Exception var3) {
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
      } catch (Exception var3) {
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
      String var1 = null;
      if (null == nativeSettings) {
         nativeSettings = new NativeSettings();
         getNativeSettings0(nativeSettings);
      }

      if (null != var0) {
         if (var0.equals("bluetooth.api.version")) {
            var1 = "1.0";
         } else if (var0.equals("bluetooth.sd.attr.retrievable.max")) {
            var1 = "13";
         } else if (var0.equals("bluetooth.sd.trans.max")) {
            var1 = "1";
         } else if (var0.equals("bluetooth.l2cap.receiveMTU.max")) {
            var1 = "672";
         } else if (var0.equals("bluetooth.master.switch")) {
            var1 = "false";
         } else if (var0.equals("bluetooth.connected.devices.max")) {
            var1 = nativeSettings.JSR82_P2MP_SUPPORT ? "7" : "1";
         } else if (var0.equals("bluetooth.connected.inquiry.scan")) {
            var1 = nativeSettings.JSR82_P2MP_SUPPORT ? "true" : "false";
         } else if (var0.equals("bluetooth.connected.page.scan")) {
            var1 = nativeSettings.JSR82_P2MP_SUPPORT ? "true" : "false";
         } else if (var0.equals("bluetooth.connected.inquiry")) {
            var1 = nativeSettings.JSR82_P2MP_SUPPORT ? "true" : "false";
         } else if (var0.equals("bluetooth.connected.page")) {
            var1 = nativeSettings.JSR82_P2MP_SUPPORT ? "true" : "false";
         } else if (var0.equals("nokia.jsr82.releaseID")) {
            var1 = "F_JSR82_06w15_00\n05-APR-2006";
         }
      }

      Tracer.println("getProperty(" + var0 + ")=" + var1);
      return var1;
   }

   public int getDiscoverable() {
      LocalDevicePropertiesEvent var1;
      try {
         var1 = this.getProperties0();
      } catch (BluetoothStateException var3) {
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
      return new String(bd_addr);
   }

   public ServiceRecord getRecord(Connection var1) {
      Tracer.println("getRecord()");
      if (var1 == null) {
         throw new NullPointerException("parameter is null");
      } else if (var1 instanceof ProtocolNotifier) {
         if (((ProtocolNotifier)var1).isClosed()) {
            throw new IllegalArgumentException("notifier closed already");
         } else {
            return ((ProtocolNotifier)var1).getLocalServiceRecord();
         }
      } else if (var1 instanceof com.nokia.mid.impl.isa.io.protocol.external.btl2cap.ProtocolNotifier) {
         if (((com.nokia.mid.impl.isa.io.protocol.external.btl2cap.ProtocolNotifier)var1).isClosed()) {
            throw new IllegalArgumentException("notifier closed already");
         } else {
            return ((com.nokia.mid.impl.isa.io.protocol.external.btl2cap.ProtocolNotifier)var1).getLocalServiceRecord();
         }
      } else {
         throw new IllegalArgumentException("illegal parameter");
      }
   }

   public void updateRecord(ServiceRecord var1) throws NullPointerException, IllegalArgumentException, ServiceRegistrationException {
      Tracer.println("updateRecord()");
      if (var1 == null) {
         throw new NullPointerException("got no serviceRecord object");
      } else if (!(var1 instanceof LocalServiceRecord)) {
         throw new IllegalArgumentException("not of type LocalServiceRecord, use LocalDevice.getRecord to obtain");
      } else {
         CommonNotifier var2 = ((LocalServiceRecord)var1).getConnectionNotifier();
         if (var2 == null) {
            throw new IllegalArgumentException("serviceRecord invalid; use LocalDevice.getRecord to obtain");
         } else {
            LocalServiceRecord var3 = var2.getLocalServiceRecord();
            if (var3 != null) {
               synchronized(var3.getLockObject()) {
                  if (!var2.checkServiceOkToUpdate((LocalServiceRecord)var1)) {
                     throw new IllegalArgumentException("mandatory protocol specific serviceRecord attributes invalid");
                  } else {
                     var2.updateService((LocalServiceRecord)var1);
                  }
               }
            }
         }
      }
   }

   private static native void initialize0();

   private native LocalDevicePropertiesEvent getProperties0() throws BluetoothStateException, IllegalArgumentException;

   private native boolean setDiscoverable0(int var1) throws BluetoothStateException;

   private static native void getNativeSettings0(NativeSettings var0);

   static {
      initialize0();
   }
}
