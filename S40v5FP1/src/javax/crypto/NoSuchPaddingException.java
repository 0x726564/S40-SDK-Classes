package javax.crypto;

import java.security.GeneralSecurityException;

public class NoSuchPaddingException extends GeneralSecurityException {
   public NoSuchPaddingException() {
   }

   public NoSuchPaddingException(String var1) {
      super(var1);
   }
}
