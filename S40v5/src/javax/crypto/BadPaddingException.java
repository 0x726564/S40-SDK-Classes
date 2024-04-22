package javax.crypto;

import java.security.GeneralSecurityException;

public class BadPaddingException extends GeneralSecurityException {
   public BadPaddingException() {
   }

   public BadPaddingException(String var1) {
      super(var1);
   }
}
