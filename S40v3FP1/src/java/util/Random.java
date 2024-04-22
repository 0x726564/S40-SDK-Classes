package java.util;

public class Random {
   private long seed;
   private static final long multiplier = 25214903917L;
   private static final long addend = 11L;
   private static final long mask = 281474976710655L;
   private static final int BITS_PER_BYTE = 8;
   private static final int BYTES_PER_INT = 4;

   public Random() {
      this(System.currentTimeMillis());
   }

   public Random(long var1) {
      this.setSeed(var1);
   }

   public synchronized void setSeed(long var1) {
      this.seed = (var1 ^ 25214903917L) & 281474976710655L;
   }

   protected synchronized int next(int var1) {
      long var2 = this.seed * 25214903917L + 11L & 281474976710655L;
      this.seed = var2;
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
            var2 = this.next(31);
            var3 = var2 % var1;
         } while(var2 - var3 + (var1 - 1) < 0);

         return var3;
      }
   }

   public long nextLong() {
      return ((long)this.next(32) << 32) + (long)this.next(32);
   }

   public float nextFloat() {
      int var1 = this.next(24);
      return (float)var1 / 1.6777216E7F;
   }

   public double nextDouble() {
      long var1 = ((long)this.next(26) << 27) + (long)this.next(27);
      return (double)var1 / 9.007199254740992E15D;
   }
}
