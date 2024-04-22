package java.util;

class TimerThread extends Thread {
   boolean cN = true;
   private TaskQueue cO;

   TimerThread(TaskQueue var1) {
      this.cO = var1;
   }

   public void run() {
      try {
         TimerThread var1 = this;

         while(true) {
            while(true) {
               try {
                  while(true) {
                     TimerTask var2;
                     boolean var3;
                     synchronized(var1.cO) {
                        if (var1.cO.isEmpty()) {
                           return;
                        }

                        synchronized((var2 = var1.cO.getMin()).lock) {
                           if (var2.state != 3) {
                              long var8 = System.currentTimeMillis();
                              long var10;
                              if (var3 = (var10 = var2.hl) <= var8) {
                                 if (var2.hm == 0L) {
                                    var1.cO.aB();
                                    var2.state = 2;
                                 } else {
                                    var1.cO.a(var2.hm < 0L ? var8 - var2.hm : var10 + var2.hm);
                                 }
                              }
                           } else {
                              var1.cO.aB();
                              continue;
                           }
                        }
                     }

                     if (!var3) {
                        continue;
                     }

                     try {
                        var2.run();
                     } catch (Exception var13) {
                        var2.cancel();
                     }
                  }
               } catch (InterruptedException var16) {
               }
            }
         }
      } catch (Throwable var17) {
         synchronized(this.cO) {
            this.cN = false;
            this.cO.clear();
         }

         if (var17 instanceof Error) {
            throw (Error)var17;
         } else if (var17 instanceof RuntimeException) {
            throw (RuntimeException)var17;
         }
      }
   }
}
