package java.lang;

public class Object {
   public final native Class getClass();

   public native int hashCode();

   public boolean equals(Object obj) {
      return this == obj;
   }

   public String toString() {
      return this.getClass().getName() + "@" + Integer.toHexString(this.hashCode());
   }

   public final native void notify();

   public final native void notifyAll();

   public final native void wait(long var1) throws InterruptedException;

   public final void wait(long timeout, int nanos) throws InterruptedException {
      if (timeout < 0L) {
         throw new IllegalArgumentException("timeout value is negative");
      } else if (nanos >= 0 && nanos <= 999999) {
         if (nanos >= 500000 || nanos != 0 && timeout == 0L) {
            ++timeout;
         }

         this.wait(timeout);
      } else {
         throw new IllegalArgumentException("nanosecond timeout value out of range");
      }
   }

   public final void wait() throws InterruptedException {
      this.wait(0L);
   }
}
