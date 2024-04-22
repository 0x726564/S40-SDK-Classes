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

   public Random(long seed) {
      this.setSeed(seed);
   }

   public synchronized void setSeed(long seed) {
      this.seed = (seed ^ 25214903917L) & 281474976710655L;
   }

   protected synchronized int next(int bits) {
      long nextseed = this.seed * 25214903917L + 11L & 281474976710655L;
      this.seed = nextseed;
      return (int)(nextseed >>> 48 - bits);
   }

   public int nextInt() {
      return this.next(32);
   }

   public int nextInt(int n) {
      if (n <= 0) {
         throw new IllegalArgumentException("n must be positive");
      } else if ((n & -n) == n) {
         return (int)((long)n * (long)this.next(31) >> 31);
      } else {
         int bits;
         int val;
         do {
            bits = this.next(31);
            val = bits % n;
         } while(bits - val + (n - 1) < 0);

         return val;
      }
   }

   public long nextLong() {
      return ((long)this.next(32) << 32) + (long)this.next(32);
   }

   public float nextFloat() {
      int i = this.next(24);
      return (float)i / 1.6777216E7F;
   }

   public double nextDouble() {
      long l = ((long)this.next(26) << 27) + (long)this.next(27);
      return (double)l / 9.007199254740992E15D;
   }
}
