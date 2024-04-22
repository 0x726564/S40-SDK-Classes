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

   public ByteArrayOutputStream(int size) {
      this.isClosed = false;
      if (size < 0) {
         throw new IllegalArgumentException("Negative initial size: " + size);
      } else {
         this.buf = new byte[size];
      }
   }

   public synchronized void write(int b) {
      this.ensureOpen();
      int newcount = this.count + 1;
      if (newcount > this.buf.length) {
         byte[] newbuf;
         if (newcount > 16384) {
            newbuf = new byte[Math.max(this.buf.length + 16384, newcount)];
         } else {
            newbuf = new byte[Math.max(this.buf.length << 1, newcount)];
         }

         System.arraycopy(this.buf, 0, newbuf, 0, this.count);
         this.buf = newbuf;
      }

      this.buf[this.count] = (byte)b;
      this.count = newcount;
   }

   public synchronized void write(byte[] b, int off, int len) {
      this.ensureOpen();
      if (off >= 0 && off <= b.length && len >= 0 && off + len <= b.length && off + len >= 0) {
         if (len != 0) {
            int newcount = this.count + len;
            if (newcount > this.buf.length) {
               byte[] newbuf;
               if (newcount > 16384) {
                  newbuf = new byte[Math.max(this.buf.length + 16384, newcount)];
               } else {
                  newbuf = new byte[Math.max(this.buf.length << 1, newcount)];
               }

               System.arraycopy(this.buf, 0, newbuf, 0, this.count);
               this.buf = newbuf;
            }

            System.arraycopy(b, off, this.buf, this.count, len);
            this.count = newcount;
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
         byte[] newbuf = new byte[this.count];
         System.arraycopy(this.buf, 0, newbuf, 0, this.count);
         return newbuf;
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
