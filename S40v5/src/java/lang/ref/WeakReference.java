package java.lang.ref;

public class WeakReference extends Reference {
   public WeakReference(Object var1) {
      super(var1);
      this.initializeWeakReference();
   }

   private native void initializeWeakReference();
}
