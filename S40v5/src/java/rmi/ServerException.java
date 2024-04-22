package java.rmi;

public class ServerException extends RemoteException {
   public ServerException(String var1) {
      super(var1);
   }

   public ServerException(String var1, Exception var2) {
      super(var1, var2);
   }
}
