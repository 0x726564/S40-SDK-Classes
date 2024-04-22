package java.lang.ref;

public class WeakReference extends Reference {
   public WeakReference(Object ref) {
      super(ref);
      this.initializeWeakReference();
   }

   private native void initializeWeakReference();
}
