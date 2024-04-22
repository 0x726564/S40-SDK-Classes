package javax.microedition.pim;

public class FieldFullException extends RuntimeException {
   private int j;

   public FieldFullException() {
      this.j = -1;
   }

   public FieldFullException(String var1) {
      this(var1, -1);
   }

   public FieldFullException(String var1, int var2) {
      super(var1);
      this.j = -1;
      this.j = var2;
   }

   public int getField() {
      return this.j;
   }
}
