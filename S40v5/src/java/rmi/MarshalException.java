package java.rmi;

public class MarshalException extends RemoteException {
   public MarshalException(String var1) {
      super(var1);
   }

   public MarshalException(String var1, Exception var2) {
      super(var1, var2);
   }
}
