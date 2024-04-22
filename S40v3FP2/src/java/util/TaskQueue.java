package java.util;

class TaskQueue {
   private TimerTask[] queue = new TimerTask[4];
   private int size = 0;

   void add(TimerTask var1) {
      if (++this.size == this.queue.length) {
         TimerTask[] var2 = new TimerTask[2 * this.queue.length];
         System.arraycopy(this.queue, 0, var2, 0, this.size);
         this.queue = var2;
      }

      this.queue[this.size] = var1;
      this.fixUp(this.size);
   }

   TimerTask getMin() {
      return this.queue[1];
   }

   void removeMin() {
      this.queue[1] = this.queue[this.size];
      this.queue[this.size--] = null;
      this.fixDown(1);
   }

   void rescheduleMin(long var1) {
      this.queue[1].nextExecutionTime = var1;
      this.fixDown(1);
   }

   boolean isEmpty() {
      return this.size == 0;
   }

   void clear() {
      for(int var1 = 1; var1 <= this.size; ++var1) {
         this.queue[var1] = null;
      }

      this.size = 0;
   }

   private void fixUp(int var1) {
      while(true) {
         if (var1 > 1) {
            int var2 = var1 >> 1;
            if (this.queue[var2].nextExecutionTime > this.queue[var1].nextExecutionTime) {
               TimerTask var3 = this.queue[var2];
               this.queue[var2] = this.queue[var1];
               this.queue[var1] = var3;
               var1 = var2;
               continue;
            }
         }

         return;
      }
   }

   private void fixDown(int var1) {
      while(true) {
         int var2;
         if ((var2 = var1 << 1) <= this.size) {
            if (var2 < this.size && this.queue[var2].nextExecutionTime > this.queue[var2 + 1].nextExecutionTime) {
               ++var2;
            }

            if (this.queue[var1].nextExecutionTime > this.queue[var2].nextExecutionTime) {
               TimerTask var3 = this.queue[var2];
               this.queue[var2] = this.queue[var1];
               this.queue[var1] = var3;
               var1 = var2;
               continue;
            }
         }

         return;
      }
   }
}
