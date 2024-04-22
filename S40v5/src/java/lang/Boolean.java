package java.lang;

public final class Boolean {
   public static final Boolean TRUE = new Boolean(true);
   public static final Boolean FALSE = new Boolean(false);
   private boolean value;

   public Boolean(boolean var1) {
      this.value = var1;
   }

   public final boolean booleanValue() {
      return this.value;
   }

   public final String toString() {
      return this.value ? "true" : "false";
   }

   public final int hashCode() {
      return this.value ? 1231 : 1237;
   }

   public final boolean equals(Object var1) {
      if (var1 instanceof Boolean) {
         return this.value == (Boolean)var1;
      } else {
         return false;
      }
   }
}
