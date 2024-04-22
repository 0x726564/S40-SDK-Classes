package javax.crypto;

import java.security.GeneralSecurityException;

public class IllegalBlockSizeException extends GeneralSecurityException {
   public IllegalBlockSizeException() {
   }

   public IllegalBlockSizeException(String msg) {
      super(msg);
   }
}
