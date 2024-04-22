package javax.bluetooth;

import com.nokia.mid.impl.isa.bluetooth.RemoteDeviceAccessor;
import com.nokia.mid.impl.isa.io.protocol.external.btl2cap.ProtocolConnection;
import com.nokia.mid.impl.isa.io.protocol.external.btl2cap.ProtocolNotifier;
import java.io.IOException;
import javax.microedition.io.Connection;

public class RemoteDevice {
   private String address = null;

   protected RemoteDevice(String var1) {
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
      String var6 = null;
      boolean var2 = false;

      do {
         try {
            var6 = this.getFriendlyName0(this.getBluetoothAddressAsByteArray());
            var2 = true;
         } catch (BluetoothStateException var5) {
            if (!var5.getMessage().equals("busy")) {
               throw var5;
            }

            try {
               Thread.sleep(1000L);
            } catch (InterruptedException var4) {
            }
         }
      } while(!var2);

      return var6;
   }

   public final String getBluetoothAddress() {
      return new String(this.address);
   }

   public final boolean equals(Object var1) {
      return var1 != null && var1 instanceof RemoteDevice && ((RemoteDevice)var1).getBluetoothAddress().equals(this.getBluetoothAddress());
   }

   public final int hashCode() {
      return this.getBluetoothAddress().hashCode();
   }

   public static RemoteDevice getRemoteDevice(Connection var0) throws IOException {
      if (var0 == null) {
         throw new NullPointerException("Connection does not exist");
      } else {
         RemoteDeviceAccessor var1;
         try {
            var1 = (RemoteDeviceAccessor)var0;
         } catch (ClassCastException var2) {
            throw new IllegalArgumentException("Invalid connection parameter");
         }

         return new RemoteDevice(var1.getRemoteDeviceAddress());
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

   public boolean authorize(Connection var1) throws IOException {
      if (var1 == null) {
         throw new IllegalArgumentException("Connection argument must not be null");
      } else if (!(var1 instanceof ProtocolConnection) && !(var1 instanceof com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolConnection)) {
         throw new IllegalArgumentException("Invalid connection parameter");
      } else if (!(var1 instanceof ProtocolNotifier) && !(var1 instanceof com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolNotifier)) {
         if (var1 instanceof ProtocolConnection) {
            ProtocolConnection var2;
            if ((var2 = (ProtocolConnection)var1).isClosed()) {
               throw new IOException("Connection is closed");
            }

            if (!this.getBluetoothAddress().equals(var2.getRemoteDeviceAddress().toUpperCase())) {
               throw new IllegalArgumentException("Not a connection to this remote device");
            }

            if (var2.isClient()) {
               throw new IllegalArgumentException("Connection is a client side connection");
            }
         } else {
            com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolConnection var3;
            if ((var3 = (com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolConnection)var1).isClosed()) {
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

   public boolean encrypt(Connection var1, boolean var2) throws IOException {
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

   public boolean isAuthorized(Connection var1) throws IOException {
      if (var1 == null) {
         throw new IllegalArgumentException("Connection argument must not be null");
      } else if (!(var1 instanceof ProtocolConnection) && !(var1 instanceof com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolConnection)) {
         throw new IllegalArgumentException("Invalid connection parameter");
      } else if (!(var1 instanceof ProtocolNotifier) && !(var1 instanceof com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolNotifier)) {
         if (var1 instanceof ProtocolConnection) {
            ProtocolConnection var2;
            if ((var2 = (ProtocolConnection)var1).isClosed()) {
               throw new IOException("Connection is closed");
            }

            if (!this.getBluetoothAddress().equals(var2.getRemoteDeviceAddress().toUpperCase())) {
               throw new IllegalArgumentException("Not a connection to this remote device");
            }

            if (var2.isClient()) {
               return false;
            }
         } else {
            com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolConnection var3;
            if ((var3 = (com.nokia.mid.impl.isa.io.protocol.external.btspp.ProtocolConnection)var1).isClosed()) {
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
      String var3 = this.getBluetoothAddress();

      for(int var2 = 0; var2 < 12; var2 += 2) {
         var1[var2 / 2] = (byte)Integer.parseInt(var3.substring(var2, var2 + 2), 16);
      }

      return var1;
   }

   private final int getAuthenticationStatus() {
      int var1 = 2;

      try {
         int var3;
         if (((var3 = this.getPropertiesFromDeviceDB0(this.getBluetoothAddressAsByteArray())) & 1) != 0 && (var3 & 8) != 0) {
            var1 = (var3 & 4) != 0 ? 0 : 1;
         }
      } catch (IllegalArgumentException var2) {
      }

      return var1;
   }

   private final int getEncryptionStatus() {
      int var1 = 2;

      try {
         int var3;
         if (((var3 = this.getPropertiesFromDeviceDB0(this.getBluetoothAddressAsByteArray())) & 1) != 0 && (var3 & 8) != 0) {
            var1 = (var3 & 2) != 0 ? 0 : 1;
         }
      } catch (IllegalArgumentException var2) {
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
