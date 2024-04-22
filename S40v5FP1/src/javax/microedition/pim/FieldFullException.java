package javax.microedition.pim;

public class FieldFullException extends RuntimeException {
   int iProblemField;

   public FieldFullException() {
      this.iProblemField = -1;
   }

   public FieldFullException(String detailMessage) {
      this(detailMessage, -1);
   }

   public FieldFullException(String detailMessage, int field) {
      super(detailMessage);
      this.iProblemField = -1;
      this.iProblemField = field;
   }

   public int getField() {
      return this.iProblemField;
   }
}
