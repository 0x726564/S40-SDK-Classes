package javax.microedition.io.file;

public class ConnectionClosedException extends RuntimeException {
   public ConnectionClosedException() {
   }

   public ConnectionClosedException(String s) {
      super(s);
   }
}
