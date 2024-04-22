package javax.bluetooth;

import java.io.IOException;

public class BluetoothConnectionException extends IOException {
   public static final int UNKNOWN_PSM = 1;
   public static final int SECURITY_BLOCK = 2;
   public static final int NO_RESOURCES = 3;
   public static final int FAILED_NOINFO = 4;
   public static final int TIMEOUT = 5;
   public static final int UNACCEPTABLE_PARAMS = 6;
   private int error;

   public BluetoothConnectionException(int error) {
      if (!this.validExceptionValue(error)) {
         throw new IllegalArgumentException("Illegal error code");
      } else {
         this.error = error;
      }
   }

   public BluetoothConnectionException(int error, String msg) {
      super(msg);
      if (!this.validExceptionValue(error)) {
         throw new IllegalArgumentException("Illegal error code");
      } else {
         this.error = error;
      }
   }

   public int getStatus() {
      return this.error;
   }

   private final boolean validExceptionValue(int error) {
      return error >= 1 && error <= 6;
   }
}
