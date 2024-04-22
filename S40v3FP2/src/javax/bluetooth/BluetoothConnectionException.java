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

   public BluetoothConnectionException(int var1) {
      if (!this.validExceptionValue(var1)) {
         throw new IllegalArgumentException("Illegal error code");
      } else {
         this.error = var1;
      }
   }

   public BluetoothConnectionException(int var1, String var2) {
      super(var2);
      if (!this.validExceptionValue(var1)) {
         throw new IllegalArgumentException("Illegal error code");
      } else {
         this.error = var1;
      }
   }

   public int getStatus() {
      return this.error;
   }

   private final boolean validExceptionValue(int var1) {
      return var1 >= 1 && var1 <= 6;
   }
}
