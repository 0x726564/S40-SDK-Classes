package java.lang;

public class Thread implements Runnable {
   private int priority = 5;
   private Thread threadQ;
   private Runnable target;
   private char[] name;
   public static final int MIN_PRIORITY = 1;
   public static final int NORM_PRIORITY = 5;
   public static final int MAX_PRIORITY = 10;
   private static int threadInitNumber;

   public static native Thread currentThread();

   private static synchronized int nextThreadNum() {
      return ++threadInitNumber;
   }

   public static native void yield();

   public static native void sleep(long var0) throws InterruptedException;

   private void init(Runnable var1, String var2) {
      Thread var3 = currentThread();
      this.target = var1;
      this.name = var2.toCharArray();
      this.priority = var3.getPriority();
      this.setPriority0(this.priority);
   }

   public Thread() {
      this.init((Runnable)null, "Thread-" + nextThreadNum());
   }

   public Thread(String var1) {
      this.init((Runnable)null, var1);
   }

   public Thread(Runnable var1) {
      this.init(var1, "Thread-" + nextThreadNum());
   }

   public Thread(Runnable var1, String var2) {
      this.init(var1, var2);
   }

   public synchronized native void start();

   public void run() {
      if (this.target != null) {
         this.target.run();
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
      return String.valueOf(this.name);
   }

   public static native int activeCount();

   public final synchronized void join() throws InterruptedException {
      while(this.isAlive()) {
         this.wait(1000L);
      }

   }

   public String toString() {
      return "Thread[" + this.getName() + "," + this.getPriority() + "]";
   }

   private native void setPriority0(int var1);

   private native void interrupt0();
}
