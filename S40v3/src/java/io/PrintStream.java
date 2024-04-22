package java.io;

public class PrintStream extends OutputStream {
   private boolean trouble = false;
   private OutputStreamWriter charOut;
   private OutputStream byteOut;
   private boolean closing = false;

   public PrintStream(OutputStream var1) {
      if (var1 == null) {
         throw new NullPointerException("Null output stream");
      } else {
         this.byteOut = var1;
         this.charOut = new OutputStreamWriter(var1);
      }
   }

   private void ensureOpen() throws IOException {
      if (this.charOut == null) {
         throw new IOException("Stream closed");
      }
   }

   public void flush() {
      synchronized(this) {
         try {
            this.ensureOpen();
            this.charOut.flush();
         } catch (IOException var4) {
            this.trouble = true;
         }

      }
   }

   public void close() {
      synchronized(this) {
         if (!this.closing) {
            this.closing = true;

            try {
               this.charOut.close();
            } catch (IOException var4) {
               this.trouble = true;
            }

            this.charOut = null;
            this.byteOut = null;
         }

      }
   }

   public boolean checkError() {
      if (this.charOut != null) {
         this.flush();
      }

      return this.trouble;
   }

   protected void setError() {
      this.trouble = true;
   }

   public void write(int var1) {
      try {
         synchronized(this) {
            this.ensureOpen();
            this.byteOut.write(var1);
         }
      } catch (IOException var5) {
         this.trouble = true;
      }

   }

   public void write(byte[] var1, int var2, int var3) {
      try {
         synchronized(this) {
            this.ensureOpen();
            this.byteOut.write(var1, var2, var3);
         }
      } catch (IOException var7) {
         this.trouble = true;
      }

   }

   private void write(char[] var1) {
      try {
         synchronized(this) {
            this.ensureOpen();
            this.charOut.write(var1);
         }
      } catch (IOException var5) {
         this.trouble = true;
      }

   }

   private void write(String var1) {
      try {
         synchronized(this) {
            this.ensureOpen();
            this.charOut.write(var1);
         }
      } catch (IOException var5) {
         this.trouble = true;
      }

   }

   private void newLine() {
      try {
         synchronized(this) {
            this.ensureOpen();
            this.charOut.write(10);
         }
      } catch (IOException var4) {
         this.trouble = true;
      }

   }

   public void print(boolean var1) {
      this.write(var1 ? "true" : "false");
   }

   public void print(char var1) {
      this.write(String.valueOf(var1));
   }

   public void print(int var1) {
      this.write(String.valueOf(var1));
   }

   public void print(long var1) {
      this.write(String.valueOf(var1));
   }

   public void print(float var1) {
      this.write(String.valueOf(var1));
   }

   public void print(double var1) {
      this.write(String.valueOf(var1));
   }

   public void print(char[] var1) {
      this.write(var1);
   }

   public void print(String var1) {
      if (var1 == null) {
         var1 = "null";
      }

      this.write(var1);
   }

   public void print(Object var1) {
      this.write(String.valueOf(var1));
   }

   public void println() {
      this.newLine();
   }

   public void println(boolean var1) {
      synchronized(this) {
         this.print(var1);
         this.newLine();
      }
   }

   public void println(char var1) {
      synchronized(this) {
         this.print(var1);
         this.newLine();
      }
   }

   public void println(int var1) {
      synchronized(this) {
         this.print(var1);
         this.newLine();
      }
   }

   public void println(long var1) {
      synchronized(this) {
         this.print(var1);
         this.newLine();
      }
   }

   public void println(float var1) {
      synchronized(this) {
         this.print(var1);
         this.newLine();
      }
   }

   public void println(double var1) {
      synchronized(this) {
         this.print(var1);
         this.newLine();
      }
   }

   public void println(char[] var1) {
      synchronized(this) {
         this.print(var1);
         this.newLine();
      }
   }

   public void println(String var1) {
      synchronized(this) {
         this.print(var1);
         this.newLine();
      }
   }

   public void println(Object var1) {
      synchronized(this) {
         this.print(var1);
         this.newLine();
      }
   }
}
