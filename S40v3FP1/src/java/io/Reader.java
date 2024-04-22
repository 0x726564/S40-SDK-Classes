package java.io;

public abstract class Reader {
   protected Object lock;
   private static final int maxSkipBufferSize = 8192;
   private char[] skipBuffer = null;

   protected Reader() {
      this.lock = this;
   }

   protected Reader(Object var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.lock = var1;
      }
   }

   public int read() throws IOException {
      char[] var1 = new char[1];
      return this.read(var1, 0, 1) == -1 ? -1 : var1[0];
   }

   public int read(char[] var1) throws IOException {
      return this.read(var1, 0, var1.length);
   }

   public abstract int read(char[] var1, int var2, int var3) throws IOException;

   public long skip(long var1) throws IOException {
      if (var1 < 0L) {
         throw new IllegalArgumentException("skip value is negative");
      } else {
         int var3 = (int)Math.min(var1, 8192L);
         synchronized(this.lock) {
            if (this.skipBuffer == null || this.skipBuffer.length < var3) {
               this.skipBuffer = new char[var3];
            }

            long var5;
            int var7;
            for(var5 = var1; var5 > 0L; var5 -= (long)var7) {
               var7 = this.read(this.skipBuffer, 0, (int)Math.min(var5, (long)var3));
               if (var7 == -1) {
                  break;
               }
            }

            return var1 - var5;
         }
      }
   }

   public boolean ready() throws IOException {
      return false;
   }

   public boolean markSupported() {
      return false;
   }

   public void mark(int var1) throws IOException {
      throw new IOException("mark() not supported");
   }

   public void reset() throws IOException {
      throw new IOException("reset() not supported");
   }

   public abstract void close() throws IOException;
}
