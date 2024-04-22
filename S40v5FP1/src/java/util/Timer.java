package java.util;

public class Timer {
   private TaskQueue queue = new TaskQueue();
   private TimerThread thread;

   public Timer() {
      this.thread = new TimerThread(this.queue);
      this.thread.start();
   }

   public void schedule(TimerTask task, long delay) {
      if (delay < 0L) {
         throw new IllegalArgumentException("Negative delay.");
      } else {
         this.sched(task, System.currentTimeMillis() + delay, 0L);
      }
   }

   public void schedule(TimerTask task, Date time) {
      this.sched(task, time.getTime(), 0L);
   }

   public void schedule(TimerTask task, long delay, long period) {
      if (delay < 0L) {
         throw new IllegalArgumentException("Negative delay.");
      } else if (period <= 0L) {
         throw new IllegalArgumentException("Non-positive period.");
      } else {
         this.sched(task, System.currentTimeMillis() + delay, -period);
      }
   }

   public void schedule(TimerTask task, Date firstTime, long period) {
      if (period <= 0L) {
         throw new IllegalArgumentException("Non-positive period.");
      } else {
         this.sched(task, firstTime.getTime(), -period);
      }
   }

   public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
      if (delay < 0L) {
         throw new IllegalArgumentException("Negative delay.");
      } else if (period <= 0L) {
         throw new IllegalArgumentException("Non-positive period.");
      } else {
         this.sched(task, System.currentTimeMillis() + delay, period);
      }
   }

   public void scheduleAtFixedRate(TimerTask task, Date firstTime, long period) {
      if (period <= 0L) {
         throw new IllegalArgumentException("Non-positive period.");
      } else {
         this.sched(task, firstTime.getTime(), period);
      }
   }

   private void sched(TimerTask task, long time, long period) {
      if (time < 0L) {
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

               synchronized(task.lock) {
                  if (task.state != 0) {
                     throw new IllegalStateException("Task already scheduled or cancelled");
                  }

                  task.nextExecutionTime = time;
                  task.period = period;
                  task.state = 1;
               }

               this.queue.add(task);
               if (this.queue.getMin() == task) {
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
