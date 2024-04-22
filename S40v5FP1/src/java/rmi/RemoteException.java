package java.rmi;

import java.io.IOException;

public class RemoteException extends IOException {
   private static final long serialVersionUID = -5148567311918794206L;
   public Throwable detail;

   public RemoteException() {
   }

   public RemoteException(String s) {
      super(s);
   }

   public RemoteException(String s, Throwable ex) {
      super(s);
      this.detail = ex;
   }

   public String getMessage() {
      return this.detail == null ? super.getMessage() : super.getMessage() + "; nested exception is: \n\t" + this.detail.toString();
   }
}
