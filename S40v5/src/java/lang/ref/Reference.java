package java.lang.ref;

public abstract class Reference {
   private Object ef;

   public Object get() {
      return this.ef;
   }

   public void clear() {
      this.ef = null;
   }

   Reference(Object var1) {
      this.ef = var1;
   }
}
