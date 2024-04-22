package javax.microedition.content;

import java.io.IOException;

public class ContentHandlerException extends IOException {
   public static final int AMBIGUOUS = 3;
   public static final int NO_REGISTERED_HANDLER = 1;
   public static final int TYPE_UNKNOWN = 2;
   private int errcode;

   public ContentHandlerException(String reason, int errcode) {
      super(reason);
      if (errcode != 1 && errcode != 3 && errcode != 2) {
         throw new IllegalArgumentException();
      } else {
         this.errcode = errcode;
      }
   }

   public int getErrorCode() {
      return this.errcode;
   }
}
