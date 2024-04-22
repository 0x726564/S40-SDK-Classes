package javax.wireless.messaging;

import java.io.IOException;

public class SizeExceededException extends IOException {
   public SizeExceededException(String reason) {
      super(reason);
   }
}
