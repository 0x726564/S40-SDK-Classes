package java.io;

public abstract class Reader {
   protected Object lock;
   private char[] al = null;

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
            if (this.al == null || this.al.length < var3) {
               this.al = new char[var3];
            }

            long var5;
            int var7;
            for(var5 = var1; var5 > 0L && (var7 = this.read(this.al, 0, (int)Math.min(var5, (long)var3))) != -1; var5 -= (long)var7) {
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
