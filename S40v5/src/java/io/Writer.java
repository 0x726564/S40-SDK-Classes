package java.io;

public abstract class Writer {
   private char[] s;
   protected Object lock;

   protected Writer() {
      this.lock = this;
   }

   protected Writer(Object var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.lock = var1;
      }
   }

   public void write(int var1) throws IOException {
      synchronized(this.lock) {
         if (this.s == null) {
            this.s = new char[1024];
         }

         this.s[0] = (char)var1;
         this.write((char[])this.s, 0, 1);
      }
   }

   public void write(char[] var1) throws IOException {
      this.write((char[])var1, 0, var1.length);
   }

   public abstract void write(char[] var1, int var2, int var3) throws IOException;

   public void write(String var1) throws IOException {
      this.write((String)var1, 0, var1.length());
   }

   public void write(String var1, int var2, int var3) throws IOException {
      synchronized(this.lock) {
         char[] var5;
         if (var3 <= 1024) {
            if (this.s == null) {
               this.s = new char[1024];
            }

            var5 = this.s;
         } else {
            var5 = new char[var3];
         }

         var1.getChars(var2, var2 + var3, var5, 0);
         this.write((char[])var5, 0, var3);
      }
   }

   public abstract void flush() throws IOException;

   public abstract void close() throws IOException;
}
