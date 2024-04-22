package java.io;

public abstract class InputStream {
   public abstract int read() throws IOException;

   public int read(byte[] b) throws IOException {
      return this.read(b, 0, b.length);
   }

   public int read(byte[] b, int off, int len) throws IOException {
      if (b == null) {
         throw new NullPointerException();
      } else if (off >= 0 && off <= b.length && len >= 0 && off + len <= b.length && off + len >= 0) {
         if (len == 0) {
            return 0;
         } else {
            int c = this.read();
            if (c == -1) {
               return -1;
            } else {
               b[off] = (byte)c;
               int i = 1;

               try {
                  for(; i < len; ++i) {
                     c = this.read();
                     if (c == -1) {
                        break;
                     }

                     if (b != null) {
                        b[off + i] = (byte)c;
                     }
                  }
               } catch (IOException var7) {
               }

               return i;
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public long skip(long n) throws IOException {
      long m;
      for(m = n; m > 0L && this.read() >= 0; --m) {
      }

      return n - m;
   }

   public int available() throws IOException {
      return 0;
   }

   public void close() throws IOException {
   }

   public synchronized void mark(int readlimit) {
   }

   public synchronized void reset() throws IOException {
      throw new IOException("mark/reset not supported");
   }

   public boolean markSupported() {
      return false;
   }
}
