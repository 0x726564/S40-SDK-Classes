package java.lang;

import java.io.PrintStream;

public class Throwable {
   private String detailMessage;
   private transient Object backtrace;

   public Throwable() {
   }

   public Throwable(String var1) {
      this.detailMessage = var1;
   }

   public String getMessage() {
      return this.detailMessage;
   }

   public String toString() {
      String var1 = this.getClass().getName();
      String var2 = this.getMessage();
      return var2 != null ? var1 + ": " + var2 : var1;
   }

   public void printStackTrace() {
      PrintStream var1 = System.err;
      String var2 = this.getMessage();
      var1.print(this.getClass().getName());
      if (var2 != null) {
         var1.print(": ");
         var1.println(var2);
      } else {
         var1.println();
      }

      if (this.backtrace != null) {
         this.printStackTrace0(System.err);
      }

   }

   private native void printStackTrace0(Object var1);
}
