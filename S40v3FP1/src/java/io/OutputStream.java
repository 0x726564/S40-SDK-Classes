package java.io;

public abstract class OutputStream {
   public abstract void write(int var1) throws IOException;

   public void write(byte[] var1) throws IOException {
      this.write(var1, 0, var1.length);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         if (var3 != 0) {
            for(int var4 = 0; var4 < var3; ++var4) {
               this.write(var1[var2 + var4]);
            }

         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void flush() throws IOException {
   }

   public void close() throws IOException {
   }
}
