package javax.bluetooth;

import java.io.IOException;

public class BluetoothStateException extends IOException {
   public BluetoothStateException() {
   }

   public BluetoothStateException(String msg) {
      super(msg);
   }
}
