package java.lang;

public class ArrayIndexOutOfBoundsException extends IndexOutOfBoundsException {
   public ArrayIndexOutOfBoundsException() {
   }

   public ArrayIndexOutOfBoundsException(int var1) {
      super("Array index out of range: " + var1);
   }

   public ArrayIndexOutOfBoundsException(String var1) {
      super(var1);
   }
}
