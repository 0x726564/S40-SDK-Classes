package java.util;

public class Timer {
   private TaskQueue cO = new TaskQueue();
   private TimerThread hn;

   public Timer() {
      this.hn = new TimerThread(this.cO);
      this.hn.start();
   }

   public void schedule(TimerTask var1, long var2) {
      if (var2 < 0L) {
         throw new IllegalArgumentException("Negative delay.");
      } else {
         this.a(var1, System.currentTimeMillis() + var2, 0L);
      }
   }

   public void schedule(TimerTask var1, Date var2) {
      this.a(var1, var2.getTime(), 0L);
   }

   public void schedule(TimerTask var1, long var2, long var4) {
      if (var2 < 0L) {
         throw new IllegalArgumentException("Negative delay.");
      } else if (var4 <= 0L) {
         throw new IllegalArgumentException("Non-positive period.");
      } else {
         this.a(var1, System.currentTimeMillis() + var2, -var4);
      }
   }

   public void schedule(TimerTask var1, Date var2, long var3) {
      if (var3 <= 0L) {
         throw new IllegalArgumentException("Non-positive period.");
      } else {
         this.a(var1, var2.getTime(), -var3);
      }
   }

   public void scheduleAtFixedRate(TimerTask var1, long var2, long var4) {
      if (var2 < 0L) {
         throw new IllegalArgumentException("Negative delay.");
      } else if (var4 <= 0L) {
         throw new IllegalArgumentException("Non-positive period.");
      } else {
         this.a(var1, System.currentTimeMillis() + var2, var4);
      }
   }

   public void scheduleAtFixedRate(TimerTask var1, Date var2, long var3) {
      if (var3 <= 0L) {
         throw new IllegalArgumentException("Non-positive period.");
      } else {
         this.a(var1, var2.getTime(), var3);
      }
   }

   private void a(TimerTask var1, long var2, long var4) {
      if (var2 < 0L) {
         throw new IllegalArgumentException("Illegal execution time.");
      } else {
         synchronized(this.cO) {
            if (!this.hn.cN) {
               throw new IllegalStateException("Timer already cancelled.");
            } else {
               if (!this.hn.isAlive()) {
                  this.hn = new TimerThread(this.cO);
                  this.hn.start();
               }

               synchronized(var1.lock) {
                  if (var1.state != 0) {
                     throw new IllegalStateException("Task already scheduled or cancelled");
                  }

                  var1.hl = var2;
                  var1.hm = var4;
                  var1.state = 1;
               }

               this.cO.a(var1);
               this.cO.getMin();
            }
         }
      }
   }

   public void cancel() {
      synchronized(this.cO) {
         this.hn.cN = false;
         this.cO.clear();
      }
   }
}
