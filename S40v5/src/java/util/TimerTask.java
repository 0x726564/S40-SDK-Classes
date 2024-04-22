package java.util;

public abstract class TimerTask implements Runnable {
   final Object lock = new Object();
   int state = 0;
   long hl;
   long hm = 0L;

   protected TimerTask() {
   }

   public abstract void run();

   public boolean cancel() {
      synchronized(this.lock) {
         boolean var2 = this.state == 1;
         this.state = 3;
         return var2;
      }
   }

   public long scheduledExecutionTime() {
      synchronized(this.lock) {
         return this.hm < 0L ? this.hl + this.hm : this.hl - this.hm;
      }
   }
}
