package java.lang.ref;

public abstract class Reference {
   private Object referent;
   private int gcReserved;

   public Object get() {
      return this.referent;
   }

   public void clear() {
      this.referent = null;
   }

   Reference(Object var1) {
      this.referent = var1;
   }
}