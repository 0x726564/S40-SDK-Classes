package javax.microedition.pim;

public class FieldEmptyException extends RuntimeException {
   private int j;

   public FieldEmptyException() {
      this.j = -1;
   }

   public FieldEmptyException(String var1) {
      this(var1, -1);
   }

   public FieldEmptyException(String var1, int var2) {
      super(var1);
      this.j = -1;
      this.j = var2;
   }

   public int getField() {
      return this.j;
   }
}
