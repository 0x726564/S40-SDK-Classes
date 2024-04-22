package java.lang;

public class StringIndexOutOfBoundsException extends IndexOutOfBoundsException {
   public StringIndexOutOfBoundsException() {
   }

   public StringIndexOutOfBoundsException(String var1) {
      super(var1);
   }

   public StringIndexOutOfBoundsException(int var1) {
      super("String index out of range: " + var1);
   }
}
