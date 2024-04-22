package java.util;

public class Timer {
   private TaskQueue queue = new TaskQueue();
   private TimerThread thread;

   public Timer() {
      this.thread = new TimerThread(this.queue);
      this.thread.start();
   }

   public void schedule(TimerTask var1, long var2) {
      if (var2 < 0L) {
         throw new IllegalArgumentException("Negative delay.");
      } else {
         this.sched(var1, System.currentTimeMillis() + var2, 0L);
      }
   }

   public void schedule(TimerTask var1, Date var2) {
      this.sched(var1, var2.getTime(), 0L);
   }

   public void schedule(TimerTask var1, long var2, long var4) {
      if (var2 < 0L) {
         throw new IllegalArgumentException("Negative delay.");
      } else if (var4 <= 0L) {
         throw new IllegalArgumentException("Non-positive period.");
      } else {
         this.sched(var1, System.currentTimeMillis() + var2, -var4);
      }
   }

   public void schedule(TimerTask var1, Date var2, long var3) {
      if (var3 <= 0L) {
         throw new IllegalArgumentException("Non-positive period.");
      } else {
         this.sched(var1, var2.getTime(), -var3);
      }
   }

   public void scheduleAtFixedRate(TimerTask var1, long var2, long var4) {
      if (var2 < 0L) {
         throw new IllegalArgumentException("Negative delay.");
      } else if (var4 <= 0L) {
         throw new IllegalArgumentException("Non-positive period.");
      } else {
         this.sched(var1, System.currentTimeMillis() + var2, var4);
      }
   }

   public void scheduleAtFixedRate(TimerTask var1, Date var2, long var3) {
      if (var3 <= 0L) {
         throw new IllegalArgumentException("Non-positive period.");
      } else {
         this.sched(var1, var2.getTime(), var3);
      }
   }

   private void sched(TimerTask var1, long var2, long var4) {
      if (var2 < 0L) {
         throw new IllegalArgumentException("Illegal execution time.");
      } else {
         synchronized(this.queue) {
            if (!this.thread.newTasksMayBeScheduled) {
               throw new IllegalStateException("Timer already cancelled.");
            } else {
               if (!this.thread.isAlive()) {
                  this.thread = new TimerThread(this.queue);
                  this.thread.start();
               }

               synchronized(var1.lock) {
                  if (var1.state != 0) {
                     throw new IllegalStateException("Task already scheduled or cancelled");
                  }

                  var1.nextExecutionTime = var2;
                  var1.period = var4;
                  var1.state = 1;
               }

               this.queue.add(var1);
               if (this.queue.getMin() == var1) {
                  this.queue.notify();
               }

            }
         }
      }
   }

   public void cancel() {
      synchronized(this.queue) {
         this.thread.newTasksMayBeScheduled = false;
         this.queue.clear();
         this.queue.notify();
      }
   }
}
