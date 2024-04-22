package java.io;

public abstract class OutputStream {
   public abstract void write(int var1) throws IOException;

   public void write(byte[] b) throws IOException {
      this.write(b, 0, b.length);
   }

   public void write(byte[] b, int off, int len) throws IOException {
      if (b == null) {
         throw new NullPointerException();
      } else if (off >= 0 && off <= b.length && len >= 0 && off + len <= b.length && off + len >= 0) {
         if (len != 0) {
            for(int i = 0; i < len; ++i) {
               this.write(b[off + i]);
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
