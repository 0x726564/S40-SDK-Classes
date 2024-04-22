package java.util;

class TimerThread extends Thread {
   boolean newTasksMayBeScheduled = true;
   private TaskQueue queue;
   private static final long THREAD_TIMEOUT = 3000L;

   TimerThread(TaskQueue var1) {
      this.queue = var1;
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
               TimerTask var1;
               boolean var2;
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

                  var1 = this.queue.getMin();
                  long var4;
                  long var6;
                  synchronized(var1.lock) {
                     if (var1.state != 3) {
                        var4 = System.currentTimeMillis();
                        var6 = var1.nextExecutionTime;
                        if (var2 = var6 <= var4) {
                           if (var1.period == 0L) {
                              this.queue.removeMin();
                              var1.state = 2;
                           } else {
                              this.queue.rescheduleMin(var1.period < 0L ? var4 - var1.period : var6 + var1.period);
                           }
                        }
                     } else {
                        this.queue.removeMin();
                        continue;
                     }
                  }

                  if (!var2) {
                     this.queue.wait(var6 - var4);
                  }
               }

               if (var2) {
                  try {
                     var1.run();
                  } catch (Exception var11) {
                     var1.cancel();
                  }
               }
            } catch (InterruptedException var14) {
            }
         }
      }
   }
}
