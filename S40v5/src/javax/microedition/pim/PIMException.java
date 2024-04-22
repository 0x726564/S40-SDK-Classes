package javax.microedition.pim;

public class PIMException extends Exception {
   public static final int FEATURE_NOT_SUPPORTED = 0;
   public static final int GENERAL_ERROR = 1;
   public static final int LIST_CLOSED = 2;
   public static final int LIST_NOT_ACCESSIBLE = 3;
   public static final int MAX_CATEGORIES_EXCEEDED = 4;
   public static final int UNSUPPORTED_VERSION = 5;
   public static final int UPDATE_ERROR = 6;
   private int q;

   public PIMException() {
      this.q = 1;
   }

   public PIMException(String var1) {
      this(var1, 1);
   }

   public PIMException(String var1, int var2) {
      super(var1);
      this.q = 1;
      this.q = var2;
   }

   public int getReason() {
      return this.q;
   }
}
