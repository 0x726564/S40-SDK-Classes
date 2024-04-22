package java.rmi;

public class ServerException extends RemoteException {
   private static final long serialVersionUID = -4775845313121906682L;

   public ServerException(String s) {
      super(s);
   }

   public ServerException(String s, Exception ex) {
      super(s, ex);
   }
}
