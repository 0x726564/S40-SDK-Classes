package javax.microedition.pim;

public class FieldEmptyException extends RuntimeException {
   int iProblemField;

   public FieldEmptyException() {
      this.iProblemField = -1;
   }

   public FieldEmptyException(String var1) {
      this(var1, -1);
   }

   public FieldEmptyException(String var1, int var2) {
      super(var1);
      this.iProblemField = -1;
      this.iProblemField = var2;
   }

   public int getField() {
      return this.iProblemField;
   }
}
