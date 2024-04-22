package java.security;

public class InvalidKeyException extends KeyException {
   public InvalidKeyException() {
   }

   public InvalidKeyException(String msg) {
      super(msg);
   }
}
