package java.io;

public abstract class Writer {
   private char[] writeBuffer;
   private final int writeBufferSize = 1024;
   protected Object lock;

   protected Writer() {
      this.lock = this;
   }

   protected Writer(Object lock) {
      if (lock == null) {
         throw new NullPointerException();
      } else {
         this.lock = lock;
      }
   }

   public void write(int c) throws IOException {
      synchronized(this.lock) {
         if (this.writeBuffer == null) {
            this.writeBuffer = new char[1024];
         }

         this.writeBuffer[0] = (char)c;
         this.write((char[])this.writeBuffer, 0, 1);
      }
   }

   public void write(char[] cbuf) throws IOException {
      this.write((char[])cbuf, 0, cbuf.length);
   }

   public abstract void write(char[] var1, int var2, int var3) throws IOException;

   public void write(String str) throws IOException {
      this.write((String)str, 0, str.length());
   }

   public void write(String str, int off, int len) throws IOException {
      synchronized(this.lock) {
         char[] cbuf;
         if (len <= 1024) {
            if (this.writeBuffer == null) {
               this.writeBuffer = new char[1024];
            }

            cbuf = this.writeBuffer;
         } else {
            cbuf = new char[len];
         }

         str.getChars(off, off + len, cbuf, 0);
         this.write((char[])cbuf, 0, len);
      }
   }

   public abstract void flush() throws IOException;

   public abstract void close() throws IOException;
}
