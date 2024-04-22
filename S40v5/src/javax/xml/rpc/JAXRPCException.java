package javax.xml.rpc;

public class JAXRPCException extends RuntimeException {
   private Throwable a;

   public JAXRPCException() {
   }

   public JAXRPCException(String var1) {
      super(var1);
   }

   public JAXRPCException(String var1, Throwable var2) {
      super(var1);
      this.a = var2;
   }

   public JAXRPCException(Throwable var1) {
      super(var1 == null ? null : var1.toString());
      this.a = var1;
   }

   public Throwable getLinkedCause() {
      return this.a;
   }
}
