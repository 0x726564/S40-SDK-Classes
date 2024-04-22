package javax.microedition.xml.rpc;

public class Type {
   public static final Type BOOLEAN = new Type(0);
   public static final Type BYTE = new Type(1);
   public static final Type SHORT = new Type(2);
   public static final Type INT = new Type(3);
   public static final Type LONG = new Type(4);
   public static final Type FLOAT = new Type(5);
   public static final Type DOUBLE = new Type(6);
   public static final Type STRING = new Type(7);
   public final int value;

   Type(int value) {
      this.value = value;
   }
}
