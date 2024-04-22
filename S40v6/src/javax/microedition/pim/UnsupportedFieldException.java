package javax.microedition.pim;

public class UnsupportedFieldException extends RuntimeException {
   int iProblemField;

   public UnsupportedFieldException() {
      this.iProblemField = -1;
   }

   public UnsupportedFieldException(String detailMessage) {
      this(detailMessage, -1);
   }

   public UnsupportedFieldException(String detailMessage, int field) {
      super(detailMessage);
      this.iProblemField = -1;
      this.iProblemField = field;
   }

   public int getField() {
      return this.iProblemField;
   }
}
