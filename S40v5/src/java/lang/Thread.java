package java.lang;

public class Thread implements Runnable {
   private int priority = 5;
   private Runnable bp;
   private char[] bq;
   public static final int MIN_PRIORITY = 1;
   public static final int NORM_PRIORITY = 5;
   public static final int MAX_PRIORITY = 10;
   private static int br;

   public static native Thread currentThread();

   private static synchronized int q() {
      return ++br;
   }

   public static native void yield();

   public static native void sleep(long var0) throws InterruptedException;

   private void a(Runnable var1, String var2) {
      Thread var3 = currentThread();
      this.bp = var1;
      this.bq = var2.toCharArray();
      this.priority = var3.getPriority();
      this.setPriority0(this.priority);
   }

   public Thread() {
      this.a((Runnable)null, "Thread-" + q());
   }

   public Thread(String var1) {
      this.a((Runnable)null, var1);
   }

   public Thread(Runnable var1) {
      this.a(var1, "Thread-" + q());
   }

   public Thread(Runnable var1, String var2) {
      this.a(var1, var2);
   }

   public synchronized native void start();

   public void run() {
      if (this.bp != null) {
         this.bp.run();
      }

   }

   public void interrupt() {
      this.interrupt0();
   }

   public final native boolean isAlive();

   public final void setPriority(int var1) {
      if (var1 <= 10 && var1 >= 1) {
         this.setPriority0(this.priority = var1);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final int getPriority() {
      return this.priority;
   }

   public final String getName() {
      return String.valueOf(this.bq);
   }

   public static native int activeCount();

   public final synchronized void join() throws InterruptedException {
      while(this.isAlive()) {
         this.wait(1000L);
      }

   }

   public final String toString() {
      return "Thread[" + this.getName() + "," + this.getPriority() + "]";
   }

   private native void setPriority0(int var1);

   private native void interrupt0();
}
