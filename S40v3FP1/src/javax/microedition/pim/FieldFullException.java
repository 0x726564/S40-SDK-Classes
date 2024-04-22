package javax.microedition.pim;

public class FieldFullException extends RuntimeException {
   int iProblemField;

   public FieldFullException() {
      this.iProblemField = -1;
   }

   public FieldFullException(String var1) {
      this(var1, -1);
   }

   public FieldFullException(String var1, int var2) {
      super(var1);
      this.iProblemField = -1;
      this.iProblemField = var2;
   }

   public int getField() {
      return this.iProblemField;
   }
}
