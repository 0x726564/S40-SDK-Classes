package javax.bluetooth;

import com.nokia.mid.impl.isa.bluetooth.ObexConnection;
import com.nokia.mid.impl.isa.bluetooth.RemoteDeviceAccessor;
import com.nokia.mid.impl.isa.io.protocol.external.btl2cap.ProtocolConnection;
import com.nokia.mid.impl.isa.io.protocol.external.btl2cap.ProtocolNotifier;
import com.nokia.mid.impl.isa.util.SharedObjects;
import java.io.IOException;
import javax.microedition.io.Connection;

public class RemoteDevice {
   private static final int LINK_IS_ENCRYPTED = 0;
   private static final int LINK_NOT_ENCRYPTED = 1;
   private static final int DEVICE_IS_AUTHENTICATED = 0;
   private static final int DEVICE_NOT_AUTHENTICATED = 1;
   private static final int DEVICE_NOT_CONNECTED = 2;
   private static final int RESPONSE_STATUS_BIT_POS = 0;
   private static final int ENCRYPTION_STATUS_BIT_POS = 1;
   private static final int AUTHENTICATION_STATUS_BIT_POS = 2;
   private static final int CONNECTION_STATUS_BIT_POS = 3;
   private static final int TRUSTED_STATUS_BIT_POS = 4;
   private static final int BLACKLISTED_STATUS_BIT_POS = 5;
   private static final int PREKNOWN_STATUS_BIT_POS = 6;
   private static final int RESPONSE_STATUS_BIT_MASK = 1;
   private static final int ENCRYPTION_STATUS_BIT_MASK = 2;
   private static final int AUTHENTICATION_STATUS_BIT_MASK = 4;
   private static final int CONNECTION_STATUS_BIT_MASK = 8;
   private static final int TRUSTED_STATUS_BIT_MASK = 16;
   private static final int BLACKLISTED_STATUS_BIT_MASK = 32;
   private static final int PREKNOWN_STATUS_BIT_MASK = 64;
   private static final int WAIT = 0;
   private static final int SUCCEEDED = 1;
   private static final int BD_ADDRESS_LENGTH = 12;
   private static final Object sharedLock = SharedObjects.getLock("javax.bluetooth.RemoteDevice.sharedLock");
   private static boolean doneInit = false;
   private String address;

   private static void performInit() {
      synchronized(sharedLock) {
         if (!doneInit) {
            initialize0();
            doneInit = true;
         }
      }
   }

   protected RemoteDevice(String address) {
      performInit();
      this.address = null;
      if (address == null) {
         throw new NullPointerException("null is not valid BD address");
      } else if (address.length() != 12) {
         throw new IllegalArgumentException("Invalid BD address");
      } else {
         String validHexValues = new String("0123456789ABCDEF");
         address = address.toUpperCase();

         for(int i = 0; i < 12; ++i) {
            if (validHexValues.indexOf(address.charAt(i)) == -1) {
               throw new IllegalArgumentException("Invalid BD address");
            }
         }

         try {
            LocalDevice localDevice = LocalDevice.getLocalDevice();
            if (address.equals(localDevice.getBluetoothAddress().toUpperCase())) {
               throw new IllegalArgumentException("Can not create remote device with local BD address");
            }
         } catch (BluetoothStateException var4) {
         }

         this.address = new String(address);
      }
   }

   public boolean isTrustedDevice() {
      return false;
   }

   public String getFriendlyName(boolean alwaysAsk) throws IOException {
      String result = null;
      boolean discoveryFinished = false;

      do {
         try {
            result = this.getFriendlyName0(this.getBluetoothAddressAsByteArray());
            discoveryFinished = true;
         } catch (BluetoothStateException var7) {
            if (!var7.getMessage().equals("busy")) {
               throw var7;
            }

            try {
               Thread.sleep(1000L);
            } catch (InterruptedException var6) {
            }
         }
      } while(!discoveryFinished);

      return result;
   }

   public final String getBluetoothAddress() {
      return new String(this.address);
   }

   public boolean equals(Object obj) {
      return obj != null && obj instanceof RemoteDevice && ((RemoteDevice)obj).getBluetoothAddress().equals(this.getBluetoothAddress());
   }

   public int hashCode() {
      return this.getBluetoothAddress().hashCode();
   }

   public static RemoteDevice getRemoteDevice(Connection conn) throws IOException {
      performInit();
      if (conn == null) {
         throw new NullPointerException("Connection does not exist");
      } else {
         RemoteDeviceAccessor accessor;
         try {
            accessor = (RemoteDeviceAccessor)conn;
         } catch (ClassCastException var3) {
            throw new IllegalArgumentException("Invalid connection parameter");
         }

         return new RemoteDevice(accessor.getRemoteDeviceAddress());
      }
   }

   public boolean authenticate() throws IOException {
      switch(this.getAuthenticationStatus()) {
      case 0:
         return true;
      case 1:
         return false;
      default:
         throw new IOException("Remote device is not connected");
      }
   }

   public boolean authorize(Connection conn) throws IOException {
      if (conn == null) {
         throw new IllegalArgumentException("Connection argument must not be null");
      } else {
         conn = convertBtGoep(conn);
         if (!(conn instanceof ProtocolConnection) && !(conn instanceof com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolConnection)) {
            throw new IllegalArgumentException("Invalid connection parameter");
         } else if (!(conn instanceof ProtocolNotifier) && !(conn instanceof com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolNotifier)) {
            if (conn instanceof ProtocolConnection) {
               ProtocolConnection l2capConnection = (ProtocolConnection)conn;
               if (l2capConnection.isClosed()) {
                  throw new IOException("Connection is closed");
               }

               if (!this.getBluetoothAddress().equals(l2capConnection.getRemoteDeviceAddress().toUpperCase())) {
                  throw new IllegalArgumentException("Not a connection to this remote device");
               }

               if (l2capConnection.isClient()) {
                  throw new IllegalArgumentException("Connection is a client side connection");
               }
            } else {
               com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolConnection btsppConnection = (com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolConnection)conn;
               if (btsppConnection.isClosed()) {
                  throw new IOException("Connection is closed");
               }

               if (!this.getBluetoothAddress().equals(btsppConnection.getRemoteDeviceAddress().toUpperCase())) {
                  throw new IllegalArgumentException("Not a connection to this remote device");
               }

               if (btsppConnection.isClient()) {
                  throw new IllegalArgumentException("Connection is a client side connection");
               }
            }

            return this.isAuthenticated();
         } else {
            throw new IllegalArgumentException("Invalid connection parameter");
         }
      }
   }

   public boolean encrypt(Connection conn, boolean on) throws IOException {
      if (conn == null) {
         throw new IllegalArgumentException("Connection argument must not be null");
      } else {
         conn = convertBtGoep(conn);
         if (!(conn instanceof ProtocolConnection) && !(conn instanceof com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolConnection)) {
            throw new IllegalArgumentException("Invalid connection parameter");
         } else if (!(conn instanceof ProtocolNotifier) && !(conn instanceof com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolNotifier)) {
            if (conn instanceof ProtocolConnection) {
               ProtocolConnection l2capConnection = (ProtocolConnection)conn;
               if (!this.getBluetoothAddress().equals(l2capConnection.getRemoteDeviceAddress().toUpperCase())) {
                  throw new IllegalArgumentException("Not a connection to this remote device");
               }

               if (l2capConnection.isClosed()) {
                  throw new IOException("Connection is closed");
               }
            } else {
               com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolConnection btsppConnection = (com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolConnection)conn;
               if (!this.getBluetoothAddress().equals(btsppConnection.getRemoteDeviceAddress().toUpperCase())) {
                  throw new IllegalArgumentException("Not a connection to this remote device");
               }

               if (btsppConnection.isClosed()) {
                  throw new IOException("Connection is closed");
               }
            }

            return on == this.isEncrypted();
         } else {
            throw new IllegalArgumentException("Invalid connection parameter");
         }
      }
   }

   public boolean isAuthenticated() {
      return this.getAuthenticationStatus() == 0;
   }

   public boolean isAuthorized(Connection conn) throws IOException {
      if (conn == null) {
         throw new IllegalArgumentException("Connection argument must not be null");
      } else {
         conn = convertBtGoep(conn);
         if (!(conn instanceof ProtocolConnection) && !(conn instanceof com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolConnection)) {
            throw new IllegalArgumentException("Invalid connection parameter");
         } else if (!(conn instanceof ProtocolNotifier) && !(conn instanceof com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolNotifier)) {
            if (conn instanceof ProtocolConnection) {
               ProtocolConnection l2capConnection = (ProtocolConnection)conn;
               if (l2capConnection.isClosed()) {
                  throw new IOException("Connection is closed");
               }

               if (!this.getBluetoothAddress().equals(l2capConnection.getRemoteDeviceAddress().toUpperCase())) {
                  throw new IllegalArgumentException("Not a connection to this remote device");
               }

               if (l2capConnection.isClient()) {
                  return false;
               }
            } else {
               com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolConnection btsppConnection = (com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolConnection)conn;
               if (btsppConnection.isClosed()) {
                  throw new IOException("Connection is closed");
               }

               if (!this.getBluetoothAddress().equals(btsppConnection.getRemoteDeviceAddress().toUpperCase())) {
                  throw new IllegalArgumentException("Not a connection to this remote device");
               }

               if (btsppConnection.isClient()) {
                  return false;
               }
            }

            return true;
         } else {
            throw new IllegalArgumentException("Invalid connection parameter");
         }
      }
   }

   public boolean isEncrypted() {
      return this.getEncryptionStatus() == 0;
   }

   private final byte[] getBluetoothAddressAsByteArray() {
      byte[] res = new byte[6];
      String address = this.getBluetoothAddress();

      for(int i = 0; i < 12; i += 2) {
         res[i / 2] = (byte)Integer.parseInt(address.substring(i, i + 2), 16);
      }

      return res;
   }

   private final int getAuthenticationStatus() {
      int ret = 2;

      try {
         int properties = this.getPropertiesFromDeviceDB0(this.getBluetoothAddressAsByteArray());
         if ((properties & 1) != 0 && (properties & 8) != 0) {
            ret = (properties & 4) != 0 ? 0 : 1;
         }
      } catch (IllegalArgumentException var4) {
      }

      return ret;
   }

   private final int getEncryptionStatus() {
      int ret = 2;

      try {
         int properties = this.getPropertiesFromDeviceDB0(this.getBluetoothAddressAsByteArray());
         if ((properties & 1) != 0 && (properties & 8) != 0) {
            ret = (properties & 2) != 0 ? 0 : 1;
         }
      } catch (IllegalArgumentException var4) {
      }

      return ret;
   }

   private static Connection convertBtGoep(Connection conn) {
      if (conn instanceof ObexConnection) {
         ObexConnection obex = (ObexConnection)conn;
         return obex.getBluetoothConnection();
      } else {
         return conn;
      }
   }

   private static native void initialize0();

   private native String getFriendlyName0(byte[] var1) throws IOException;

   private native int getPropertiesFromDeviceDB0(byte[] var1) throws IllegalArgumentException;
}
