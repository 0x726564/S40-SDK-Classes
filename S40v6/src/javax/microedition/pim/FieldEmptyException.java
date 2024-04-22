package javax.microedition.pim;

public class FieldEmptyException extends RuntimeException {
   int iProblemField;

   public FieldEmptyException() {
      this.iProblemField = -1;
   }

   public FieldEmptyException(String detailMessage) {
      this(detailMessage, -1);
   }

   public FieldEmptyException(String detailMessage, int field) {
      super(detailMessage);
      this.iProblemField = -1;
      this.iProblemField = field;
   }

   public int getField() {
      return this.iProblemField;
   }
}
