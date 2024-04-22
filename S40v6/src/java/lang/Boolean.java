package java.lang;

public final class Boolean {
   public static final Boolean TRUE = new Boolean(true);
   public static final Boolean FALSE = new Boolean(false);
   private boolean value;

   public Boolean(boolean value) {
      this.value = value;
   }

   public boolean booleanValue() {
      return this.value;
   }

   public String toString() {
      return this.value ? "true" : "false";
   }

   public int hashCode() {
      return this.value ? 1231 : 1237;
   }

   public boolean equals(Object obj) {
      if (obj instanceof Boolean) {
         return this.value == (Boolean)obj;
      } else {
         return false;
      }
   }
}
