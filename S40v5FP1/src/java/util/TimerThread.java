package java.util;

class TimerThread extends Thread {
   boolean newTasksMayBeScheduled = true;
   private TaskQueue queue;
   private static final long THREAD_TIMEOUT = 3000L;

   TimerThread(TaskQueue queue) {
      this.queue = queue;
   }

   public void run() {
      try {
         this.mainLoop();
      } catch (Throwable var5) {
         synchronized(this.queue) {
            this.newTasksMayBeScheduled = false;
            this.queue.clear();
         }

         if (var5 instanceof Error) {
            throw (Error)var5;
         }

         if (var5 instanceof RuntimeException) {
            throw (RuntimeException)var5;
         }
      }

   }

   private void mainLoop() {
      while(true) {
         while(true) {
            try {
               TimerTask task;
               boolean taskFired;
               synchronized(this.queue) {
                  while(this.queue.isEmpty() && this.newTasksMayBeScheduled) {
                     this.queue.wait(3000L);
                     if (this.queue.isEmpty()) {
                        break;
                     }
                  }

                  if (this.queue.isEmpty()) {
                     return;
                  }

                  task = this.queue.getMin();
                  long currentTime;
                  long executionTime;
                  synchronized(task.lock) {
                     if (task.state != 3) {
                        currentTime = System.currentTimeMillis();
                        executionTime = task.nextExecutionTime;
                        if (taskFired = executionTime <= currentTime) {
                           if (task.period == 0L) {
                              this.queue.removeMin();
                              task.state = 2;
                           } else {
                              this.queue.rescheduleMin(task.period < 0L ? currentTime - task.period : executionTime + task.period);
                           }
                        }
                     } else {
                        this.queue.removeMin();
                        continue;
                     }
                  }

                  if (!taskFired) {
                     this.queue.wait(executionTime - currentTime);
                  }
               }

               if (taskFired) {
                  try {
                     task.run();
                  } catch (Exception var11) {
                     task.cancel();
                  }
               }
            } catch (InterruptedException var14) {
            }
         }
      }
   }
}
