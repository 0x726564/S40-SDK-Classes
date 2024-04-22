package java.io;

public abstract class Reader {
   protected Object lock;
   private static final int maxSkipBufferSize = 8192;
   private char[] skipBuffer = null;

   protected Reader() {
      this.lock = this;
   }

   protected Reader(Object lock) {
      if (lock == null) {
         throw new NullPointerException();
      } else {
         this.lock = lock;
      }
   }

   public int read() throws IOException {
      char[] cb = new char[1];
      return this.read(cb, 0, 1) == -1 ? -1 : cb[0];
   }

   public int read(char[] cbuf) throws IOException {
      return this.read(cbuf, 0, cbuf.length);
   }

   public abstract int read(char[] var1, int var2, int var3) throws IOException;

   public long skip(long n) throws IOException {
      if (n < 0L) {
         throw new IllegalArgumentException("skip value is negative");
      } else {
         int nn = (int)Math.min(n, 8192L);
         synchronized(this.lock) {
            if (this.skipBuffer == null || this.skipBuffer.length < nn) {
               this.skipBuffer = new char[nn];
            }

            long r;
            int nc;
            for(r = n; r > 0L; r -= (long)nc) {
               nc = this.read(this.skipBuffer, 0, (int)Math.min(r, (long)nn));
               if (nc == -1) {
                  break;
               }
            }

            return n - r;
         }
      }
   }

   public boolean ready() throws IOException {
      return false;
   }

   public boolean markSupported() {
      return false;
   }

   public void mark(int readAheadLimit) throws IOException {
      throw new IOException("mark() not supported");
   }

   public void reset() throws IOException {
      throw new IOException("reset() not supported");
   }

   public abstract void close() throws IOException;
}
