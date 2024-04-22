package java.io;

public abstract class InputStream {
   public abstract int read() throws IOException;

   public int read(byte[] var1) throws IOException {
      return this.read(var1, 0, var1.length);
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         if (var3 == 0) {
            return 0;
         } else {
            int var4 = this.read();
            if (var4 == -1) {
               return -1;
            } else {
               var1[var2] = (byte)var4;
               int var5 = 1;

               try {
                  for(; var5 < var3; ++var5) {
                     var4 = this.read();
                     if (var4 == -1) {
                        break;
                     }

                     if (var1 != null) {
                        var1[var2 + var5] = (byte)var4;
                     }
                  }
               } catch (IOException var7) {
               }

               return var5;
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public long skip(long var1) throws IOException {
      long var3;
      for(var3 = var1; var3 > 0L && this.read() >= 0; --var3) {
      }

      return var1 - var3;
   }

   public int available() throws IOException {
      return 0;
   }

   public void close() throws IOException {
   }

   public synchronized void mark(int var1) {
   }

   public synchronized void reset() throws IOException {
      throw new IOException("mark/reset not supported");
   }

   public boolean markSupported() {
      return false;
   }
}
