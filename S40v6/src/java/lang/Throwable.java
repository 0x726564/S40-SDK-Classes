package java.lang;

import java.io.PrintStream;

public class Throwable {
   private String detailMessage;
   private transient Object backtrace;

   public Throwable() {
   }

   public Throwable(String message) {
      this.detailMessage = message;
   }

   public String getMessage() {
      return this.detailMessage;
   }

   public String toString() {
      String s = this.getClass().getName();
      String message = this.getMessage();
      return message != null ? s + ": " + message : s;
   }

   public void printStackTrace() {
      PrintStream err = System.err;
      String message = this.getMessage();
      err.print(this.getClass().getName());
      if (message != null) {
         err.print(": ");
         err.println(message);
      } else {
         err.println();
      }

      if (this.backtrace != null) {
         this.printStackTrace0(System.err);
      }

   }

   private native void printStackTrace0(Object var1);
}
