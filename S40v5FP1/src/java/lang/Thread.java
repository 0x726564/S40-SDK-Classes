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

   private void init(Runnable target, String name) {
      Thread parent = currentThread();
      this.target = target;
      this.name = name.toCharArray();
      this.priority = parent.getPriority();
      this.setPriority0(this.priority);
   }

   public Thread() {
      this.init((Runnable)null, "Thread-" + nextThreadNum());
   }

   public Thread(String name) {
      this.init((Runnable)null, name);
   }

   public Thread(Runnable target) {
      this.init(target, "Thread-" + nextThreadNum());
   }

   public Thread(Runnable target, String name) {
      this.init(target, name);
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

   public final void setPriority(int newPriority) {
      if (newPriority <= 10 && newPriority >= 1) {
         this.setPriority0(this.priority = newPriority);
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
