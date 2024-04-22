package java.lang;

public class Object {
   public final native Class getClass();

   public native int hashCode();

   public boolean equals(Object var1) {
      return this == var1;
   }

   public String toString() {
      return this.getClass().getName() + "@" + Integer.toHexString(this.hashCode());
   }

   public final native void notify();

   public final native void notifyAll();

   public final native void wait(long var1) throws InterruptedException;

   public final void wait(long var1, int var3) throws InterruptedException {
      if (var1 < 0L) {
         throw new IllegalArgumentException("timeout value is negative");
      } else if (var3 >= 0 && var3 <= 999999) {
         if (var3 >= 500000 || var3 != 0 && var1 == 0L) {
            ++var1;
         }

         this.wait(var1);
      } else {
         throw new IllegalArgumentException("nanosecond timeout value out of range");
      }
   }

   public final void wait() throws InterruptedException {
      this.wait(0L);
   }
}
