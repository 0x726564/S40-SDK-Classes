package javax.microedition.pim;

public class UnsupportedFieldException extends RuntimeException {
   int iProblemField;

   public UnsupportedFieldException() {
      this.iProblemField = -1;
   }

   public UnsupportedFieldException(String var1) {
      this(var1, -1);
   }

   public UnsupportedFieldException(String var1, int var2) {
      super(var1);
      this.iProblemField = -1;
      this.iProblemField = var2;
   }

   public int getField() {
      return this.iProblemField;
   }
}
