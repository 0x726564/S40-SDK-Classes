package java.rmi;

public class MarshalException extends RemoteException {
   private static final long serialVersionUID = 6223554758134037936L;

   public MarshalException(String s) {
      super(s);
   }

   public MarshalException(String s, Exception ex) {
      super(s, ex);
   }
}
