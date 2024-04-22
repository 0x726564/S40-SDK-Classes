package java.lang;

import java.io.PrintStream;

public class Throwable {
   private String bn;

   public Throwable() {
   }

   public Throwable(String var1) {
      this.bn = var1;
   }

   public String getMessage() {
      return this.bn;
   }

   public String toString() {
      String var1 = this.getClass().getName();
      String var2;
      return (var2 = this.getMessage()) != null ? var1 + ": " + var2 : var1;
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
   }
}
