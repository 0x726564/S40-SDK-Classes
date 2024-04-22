package java.util;

class TaskQueue {
   private TimerTask[] ho = new TimerTask[4];
   private int size = 0;

   final void a(TimerTask var1) {
      if (++this.size == this.ho.length) {
         TimerTask[] var2 = new TimerTask[2 * this.ho.length];
         System.arraycopy(this.ho, 0, var2, 0, this.size);
         this.ho = var2;
      }

      this.ho[this.size] = var1;
      int var5 = this.size;

      int var6;
      for(TaskQueue var4 = this; var5 > 1; var5 = var6) {
         var6 = var5 >> 1;
         if (var4.ho[var6].hl <= var4.ho[var5].hl) {
            break;
         }

         TimerTask var3 = var4.ho[var6];
         var4.ho[var6] = var4.ho[var5];
         var4.ho[var5] = var3;
      }

   }

   TimerTask getMin() {
      return this.ho[1];
   }

   final void aB() {
      this.ho[1] = this.ho[this.size];
      this.ho[this.size--] = null;
      this.H(1);
   }

   final void a(long var1) {
      this.ho[1].hl = var1;
      this.H(1);
   }

   final boolean isEmpty() {
      return this.size == 0;
   }

   final void clear() {
      for(int var1 = 1; var1 <= this.size; ++var1) {
         this.ho[var1] = null;
      }

      this.size = 0;
   }

   private void H(int var1) {
      while(true) {
         int var2;
         if ((var2 = var1 << 1) <= this.size) {
            if (var2 < this.size && this.ho[var2].hl > this.ho[var2 + 1].hl) {
               ++var2;
            }

            if (this.ho[var1].hl > this.ho[var2].hl) {
               TimerTask var3 = this.ho[var2];
               this.ho[var2] = this.ho[var1];
               this.ho[var1] = var3;
               var1 = var2;
               continue;
            }
         }

         return;
      }
   }
}
