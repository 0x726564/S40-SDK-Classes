package java.util;

public class Random {
   private long dN;

   public Random() {
      this(System.currentTimeMillis());
   }

   public Random(long var1) {
      this.setSeed(var1);
   }

   public synchronized void setSeed(long var1) {
      this.dN = (var1 ^ 25214903917L) & 281474976710655L;
   }

   protected synchronized int next(int var1) {
      long var2 = this.dN * 25214903917L + 11L & 281474976710655L;
      this.dN = var2;
      return (int)(var2 >>> 48 - var1);
   }

   public int nextInt() {
      return this.next(32);
   }

   public int nextInt(int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException("n must be positive");
      } else if ((var1 & -var1) == var1) {
         return (int)((long)var1 * (long)this.next(31) >> 31);
      } else {
         int var2;
         int var3;
         do {
            var3 = (var2 = this.next(31)) % var1;
         } while(var2 - var3 + (var1 - 1) < 0);

         return var3;
      }
   }

   public long nextLong() {
      return ((long)this.next(32) << 32) + (long)this.next(32);
   }

   public float nextFloat() {
      return (float)this.next(24) / 1.6777216E7F;
   }

   public double nextDouble() {
      return (double)(((long)this.next(26) << 27) + (long)this.next(27)) / 9.007199254740992E15D;
   }
}
