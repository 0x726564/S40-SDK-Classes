package javax.xml.rpc;

public class JAXRPCException extends RuntimeException {
   private Throwable cause;

   public JAXRPCException() {
   }

   public JAXRPCException(String message) {
      super(message);
   }

   public JAXRPCException(String message, Throwable cause) {
      super(message);
      this.cause = cause;
   }

   public JAXRPCException(Throwable cause) {
      super(cause == null ? null : cause.toString());
      this.cause = cause;
   }

   public Throwable getLinkedCause() {
      return this.cause;
   }
}
