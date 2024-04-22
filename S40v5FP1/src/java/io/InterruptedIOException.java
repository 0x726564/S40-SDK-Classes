package java.io;

public class InterruptedIOException extends IOException {
   public int bytesTransferred = 0;

   public InterruptedIOException() {
   }

   public InterruptedIOException(String s) {
      super(s);
   }
}
