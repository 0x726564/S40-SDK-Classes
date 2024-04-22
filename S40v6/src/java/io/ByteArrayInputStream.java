package java.io;

public class ByteArrayInputStream extends InputStream {
   protected byte[] buf;
   protected int pos;
   protected int mark = 0;
   protected int count;

   public ByteArrayInputStream(byte[] buf) {
      this.buf = buf;
      this.pos = 0;
      this.count = buf.length;
   }

   public ByteArrayInputStream(byte[] buf, int offset, int length) {
      this.buf = buf;
      this.pos = offset;
      this.count = Math.min(offset + length, buf.length);
      this.mark = offset;
   }

   public synchronized int read() {
      return this.pos < this.count ? this.buf[this.pos++] & 255 : -1;
   }

   public synchronized int read(byte[] b, int off, int len) {
      if (b == null) {
         throw new NullPointerException();
      } else if (off >= 0 && off <= b.length && len >= 0 && off + len <= b.length && off + len >= 0) {
         if (this.pos >= this.count) {
            return -1;
         } else {
            if (this.pos + len > this.count) {
               len = this.count - this.pos;
            }

            if (len <= 0) {
               return 0;
            } else {
               System.arraycopy(this.buf, this.pos, b, off, len);
               this.pos += len;
               return len;
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public synchronized long skip(long n) {
      if ((long)this.pos + n > (long)this.count) {
         n = (long)(this.count - this.pos);
      }

      if (n < 0L) {
         return 0L;
      } else {
         this.pos = (int)((long)this.pos + n);
         return n;
      }
   }

   public synchronized int available() {
      return this.count - this.pos;
   }

   public boolean markSupported() {
      return true;
   }

   public void mark(int readAheadLimit) {
      this.mark = this.pos;
   }

   public synchronized void reset() {
      this.pos = this.mark;
   }

   public synchronized void close() throws IOException {
   }
}
