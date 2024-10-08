package java.lang;

public class ArrayIndexOutOfBoundsException extends IndexOutOfBoundsException {
   public ArrayIndexOutOfBoundsException() {
   }

   public ArrayIndexOutOfBoundsException(int index) {
      super("Array index out of range: " + index);
   }

   public ArrayIndexOutOfBoundsException(String s) {
      super(s);
   }
}
