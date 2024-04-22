package javax.bluetooth;

import java.io.IOException;

public class ServiceRegistrationException extends IOException {
   public ServiceRegistrationException() {
   }

   public ServiceRegistrationException(String msg) {
      super(msg);
   }
}
