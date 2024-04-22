package java.rmi;

import java.io.IOException;

public class RemoteException extends IOException {
   public Throwable detail;

   public RemoteException() {
   }

   public RemoteException(String var1) {
      super(var1);
   }

   public RemoteException(String var1, Throwable var2) {
      super(var1);
      this.detail = var2;
   }

   public String getMessage() {
      return this.detail == null ? super.getMessage() : super.getMessage() + "; nested exception is: \n\t" + this.detail.toString();
   }
}
