package java.io;

public class ByteArrayOutputStream extends OutputStream {
   protected byte[] buf;
   protected int count;
   private boolean isClosed;

   private void ensureOpen() {
      if (this.isClosed) {
         throw new RuntimeException("Writing to closed ByteArrayOutputStream");
      }
   }

   public ByteArrayOutputStream() {
      this(32);
   }

   public ByteArrayOutputStream(int var1) {
      this.isClosed = false;
      if (var1 < 0) {
         throw new IllegalArgumentException("Negative initial size: " + var1);
      } else {
         this.buf = new byte[var1];
      }
   }

   public synchronized void write(int var1) {
      this.ensureOpen();
      int var2 = this.count + 1;
      if (var2 > this.buf.length) {
         byte[] var3;
         if (var2 > 16384) {
            var3 = new byte[Math.max(this.buf.length + 16384, var2)];
         } else {
            var3 = new byte[Math.max(this.buf.length << 1, var2)];
         }

         System.arraycopy(this.buf, 0, var3, 0, this.count);
         this.buf = var3;
      }

      this.buf[this.count] = (byte)var1;
      this.count = var2;
   }

   public synchronized void write(byte[] var1, int var2, int var3) {
      this.ensureOpen();
      if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         if (var3 != 0) {
            int var4 = this.count + var3;
            if (var4 > this.buf.length) {
               byte[] var5;
               if (var4 > 16384) {
                  var5 = new byte[Math.max(this.buf.length + 16384, var4)];
               } else {
                  var5 = new byte[Math.max(this.buf.length << 1, var4)];
               }

               System.arraycopy(this.buf, 0, var5, 0, this.count);
               this.buf = var5;
            }

            System.arraycopy(var1, var2, this.buf, this.count, var3);
            this.count = var4;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public synchronized void reset() {
      this.ensureOpen();
      this.count = 0;
   }

   public synchronized byte[] toByteArray() {
      if (this.isClosed && this.buf.length == this.count) {
         return this.buf;
      } else {
         byte[] var1 = new byte[this.count];
         System.arraycopy(this.buf, 0, var1, 0, this.count);
         return var1;
      }
   }

   public int size() {
      return this.count;
   }

   public String toString() {
      return new String(this.buf, 0, this.count);
   }

   public synchronized void close() throws IOException {
      this.isClosed = true;
   }
}
