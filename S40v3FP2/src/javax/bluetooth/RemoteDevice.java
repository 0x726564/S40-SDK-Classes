package javax.bluetooth;

import com.nokia.mid.impl.isa.io.protocol.external.btl2cap.ProtocolConnection;
import com.nokia.mid.impl.isa.io.protocol.external.btl2cap.ProtocolNotifier;
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
   private String address = null;

   protected RemoteDevice(String var1) throws IllegalArgumentException, NullPointerException {
      if (var1 == null) {
         throw new NullPointerException("null is not valid BD address");
      } else if (var1.length() != 12) {
         throw new IllegalArgumentException("Invalid BD address");
      } else {
         String var2 = new String("0123456789ABCDEF");
         var1 = var1.toUpperCase();

         for(int var3 = 0; var3 < 12; ++var3) {
            if (var2.indexOf(var1.charAt(var3)) == -1) {
               throw new IllegalArgumentException("Invalid BD address");
            }
         }

         try {
            LocalDevice var5 = LocalDevice.getLocalDevice();
            if (var1.equals(var5.getBluetoothAddress().toUpperCase())) {
               throw new IllegalArgumentException("Can not create remote device with local BD address");
            }
         } catch (BluetoothStateException var4) {
         }

         this.address = new String(var1);
      }
   }

   public boolean isTrustedDevice() {
      return false;
   }

   public String getFriendlyName(boolean var1) throws IOException {
      String var2 = null;
      boolean var3 = false;

      do {
         try {
            var2 = this.getFriendlyName0(this.getBluetoothAddressAsByteArray());
            var3 = true;
         } catch (BluetoothStateException var7) {
            if (!var7.getMessage().equals("busy")) {
               throw var7;
            }

            try {
               Thread.sleep(1000L);
            } catch (InterruptedException var6) {
            }
         }
      } while(!var3);

      return var2;
   }

   public final String getBluetoothAddress() {
      return new String(this.address);
   }

   public boolean equals(Object var1) {
      return var1 != null && var1 instanceof RemoteDevice && ((RemoteDevice)var1).getBluetoothAddress().equals(this.getBluetoothAddress());
   }

   public int hashCode() {
      return this.getBluetoothAddress().hashCode();
   }

   public static RemoteDevice getRemoteDevice(Connection var0) throws IOException, IllegalArgumentException, NullPointerException {
      if (var0 == null) {
         throw new NullPointerException("Connection does not exits");
      } else if (!(var0 instanceof ProtocolConnection) && !(var0 instanceof com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolConnection)) {
         throw new IllegalArgumentException("Invalid connection parameter");
      } else if (!(var0 instanceof ProtocolNotifier) && !(var0 instanceof com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolNotifier)) {
         if (var0 instanceof ProtocolConnection) {
            ProtocolConnection var2 = (ProtocolConnection)var0;
            if (var2.isClosed() && !var2.isPushConnection()) {
               throw new IOException("Connection is closed");
            } else {
               return new RemoteDevice(var2.getRemoteDeviceAddress());
            }
         } else {
            com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolConnection var1 = (com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolConnection)var0;
            if (var1.isClosed() && !var1.isPushConnection()) {
               throw new IOException("Connection is closed");
            } else {
               return new RemoteDevice(var1.getRemoteDeviceAddress());
            }
         }
      } else {
         throw new IllegalArgumentException("Invalid connection parameter");
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

   public boolean authorize(Connection var1) throws IOException, IllegalArgumentException {
      if (var1 == null) {
         throw new IllegalArgumentException("Connection argument must not be null");
      } else if (!(var1 instanceof ProtocolConnection) && !(var1 instanceof com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolConnection)) {
         throw new IllegalArgumentException("Invalid connection parameter");
      } else if (!(var1 instanceof ProtocolNotifier) && !(var1 instanceof com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolNotifier)) {
         if (var1 instanceof ProtocolConnection) {
            ProtocolConnection var2 = (ProtocolConnection)var1;
            if (var2.isClosed()) {
               throw new IOException("Connection is closed");
            }

            if (!this.getBluetoothAddress().equals(var2.getRemoteDeviceAddress().toUpperCase())) {
               throw new IllegalArgumentException("Not a connection to this remote device");
            }

            if (var2.isClient()) {
               throw new IllegalArgumentException("Connection is a client side connection");
            }
         } else {
            com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolConnection var3 = (com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolConnection)var1;
            if (var3.isClosed()) {
               throw new IOException("Connection is closed");
            }

            if (!this.getBluetoothAddress().equals(var3.getRemoteDeviceAddress().toUpperCase())) {
               throw new IllegalArgumentException("Not a connection to this remote device");
            }

            if (var3.isClient()) {
               throw new IllegalArgumentException("Connection is a client side connection");
            }
         }

         return this.isAuthenticated();
      } else {
         throw new IllegalArgumentException("Invalid connection parameter");
      }
   }

   public boolean encrypt(Connection var1, boolean var2) throws IOException, IllegalArgumentException {
      if (var1 == null) {
         throw new IllegalArgumentException("Connection argument must not be null");
      } else if (!(var1 instanceof ProtocolConnection) && !(var1 instanceof com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolConnection)) {
         throw new IllegalArgumentException("Invalid connection parameter");
      } else if (!(var1 instanceof ProtocolNotifier) && !(var1 instanceof com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolNotifier)) {
         if (var1 instanceof ProtocolConnection) {
            ProtocolConnection var3 = (ProtocolConnection)var1;
            if (!this.getBluetoothAddress().equals(var3.getRemoteDeviceAddress().toUpperCase())) {
               throw new IllegalArgumentException("Not a connection to this remote device");
            }

            if (var3.isClosed()) {
               throw new IOException("Connection is closed");
            }
         } else {
            com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolConnection var4 = (com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolConnection)var1;
            if (!this.getBluetoothAddress().equals(var4.getRemoteDeviceAddress().toUpperCase())) {
               throw new IllegalArgumentException("Not a connection to this remote device");
            }

            if (var4.isClosed()) {
               throw new IOException("Connection is closed");
            }
         }

         return var2 == this.isEncrypted();
      } else {
         throw new IllegalArgumentException("Invalid connection parameter");
      }
   }

   public boolean isAuthenticated() {
      return this.getAuthenticationStatus() == 0;
   }

   public boolean isAuthorized(Connection var1) throws IOException, IllegalArgumentException {
      if (var1 == null) {
         throw new IllegalArgumentException("Connection argument must not be null");
      } else if (!(var1 instanceof ProtocolConnection) && !(var1 instanceof com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolConnection)) {
         throw new IllegalArgumentException("Invalid connection parameter");
      } else if (!(var1 instanceof ProtocolNotifier) && !(var1 instanceof com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolNotifier)) {
         if (var1 instanceof ProtocolConnection) {
            ProtocolConnection var2 = (ProtocolConnection)var1;
            if (var2.isClosed()) {
               throw new IOException("Connection is closed");
            }

            if (!this.getBluetoothAddress().equals(var2.getRemoteDeviceAddress().toUpperCase())) {
               throw new IllegalArgumentException("Not a connection to this remote device");
            }

            if (var2.isClient()) {
               return false;
            }
         } else {
            com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolConnection var3 = (com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolConnection)var1;
            if (var3.isClosed()) {
               throw new IOException("Connection is closed");
            }

            if (!this.getBluetoothAddress().equals(var3.getRemoteDeviceAddress().toUpperCase())) {
               throw new IllegalArgumentException("Not a connection to this remote device");
            }

            if (var3.isClient()) {
               return false;
            }
         }

         return true;
      } else {
         throw new IllegalArgumentException("Invalid connection parameter");
      }
   }

   public boolean isEncrypted() {
      return this.getEncryptionStatus() == 0;
   }

   private final byte[] getBluetoothAddressAsByteArray() {
      byte[] var1 = new byte[6];
      String var2 = this.getBluetoothAddress();

      for(int var3 = 0; var3 < 12; var3 += 2) {
         var1[var3 / 2] = (byte)Integer.parseInt(var2.substring(var3, var3 + 2), 16);
      }

      return var1;
   }

   private final int getAuthenticationStatus() {
      int var1 = 2;

      try {
         int var2 = this.getPropertiesFromDeviceDB0(this.getBluetoothAddressAsByteArray());
         if ((var2 & 1) != 0 && (var2 & 8) != 0) {
            var1 = (var2 & 4) != 0 ? 0 : 1;
         }
      } catch (IllegalArgumentException var4) {
      }

      return var1;
   }

   private final int getEncryptionStatus() {
      int var1 = 2;

      try {
         int var2 = this.getPropertiesFromDeviceDB0(this.getBluetoothAddressAsByteArray());
         if ((var2 & 1) != 0 && (var2 & 8) != 0) {
            var1 = (var2 & 2) != 0 ? 0 : 1;
         }
      } catch (IllegalArgumentException var4) {
      }

      return var1;
   }

   private static native void initialize0();

   private native String getFriendlyName0(byte[] var1) throws IOException;

   private native int getPropertiesFromDeviceDB0(byte[] var1) throws IllegalArgumentException;

   static {
      initialize0();
   }
}
