package java.util;

class TaskQueue {
   private TimerTask[] queue = new TimerTask[4];
   private int size = 0;

   void add(TimerTask task) {
      if (++this.size == this.queue.length) {
         TimerTask[] newQueue = new TimerTask[2 * this.queue.length];
         System.arraycopy(this.queue, 0, newQueue, 0, this.size);
         this.queue = newQueue;
      }

      this.queue[this.size] = task;
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

   void rescheduleMin(long newTime) {
      this.queue[1].nextExecutionTime = newTime;
      this.fixDown(1);
   }

   boolean isEmpty() {
      return this.size == 0;
   }

   void clear() {
      for(int i = 1; i <= this.size; ++i) {
         this.queue[i] = null;
      }

      this.size = 0;
   }

   private void fixUp(int k) {
      while(true) {
         if (k > 1) {
            int j = k >> 1;
            if (this.queue[j].nextExecutionTime > this.queue[k].nextExecutionTime) {
               TimerTask tmp = this.queue[j];
               this.queue[j] = this.queue[k];
               this.queue[k] = tmp;
               k = j;
               continue;
            }
         }

         return;
      }
   }

   private void fixDown(int k) {
      while(true) {
         int j;
         if ((j = k << 1) <= this.size) {
            if (j < this.size && this.queue[j].nextExecutionTime > this.queue[j + 1].nextExecutionTime) {
               ++j;
            }

            if (this.queue[k].nextExecutionTime > this.queue[j].nextExecutionTime) {
               TimerTask tmp = this.queue[j];
               this.queue[j] = this.queue[k];
               this.queue[k] = tmp;
               k = j;
               continue;
            }
         }

         return;
      }
   }
}
