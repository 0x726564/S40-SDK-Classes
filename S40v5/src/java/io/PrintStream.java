package java.io;

public class PrintStream extends OutputStream {
   private boolean au = false;
   private OutputStreamWriter av;
   private OutputStream aw;
   private boolean ax = false;

   public PrintStream(OutputStream var1) {
      if (var1 == null) {
         throw new NullPointerException("Null output stream");
      } else {
         this.aw = var1;
         this.av = new OutputStreamWriter(var1);
      }
   }

   private void ensureOpen() throws IOException {
      if (this.av == null) {
         throw new IOException("Stream closed");
      }
   }

   public void flush() {
      synchronized(this) {
         try {
            this.ensureOpen();
            this.av.flush();
         } catch (IOException var2) {
            this.au = true;
         }

      }
   }

   public void close() {
      synchronized(this) {
         if (!this.ax) {
            this.ax = true;

            try {
               this.av.close();
            } catch (IOException var2) {
               this.au = true;
            }

            this.av = null;
            this.aw = null;
         }

      }
   }

   public boolean checkError() {
      if (this.av != null) {
         this.flush();
      }

      return this.au;
   }

   protected void setError() {
      this.au = true;
   }

   public void write(int var1) {
      try {
         synchronized(this) {
            this.ensureOpen();
            this.aw.write(var1);
         }
      } catch (IOException var4) {
         this.au = true;
      }

   }

   public void write(byte[] var1, int var2, int var3) {
      try {
         synchronized(this) {
            this.ensureOpen();
            this.aw.write(var1, var2, var3);
         }
      } catch (IOException var6) {
         this.au = true;
      }

   }

   private void write(String var1) {
      try {
         synchronized(this) {
            this.ensureOpen();
            this.av.write(var1);
         }
      } catch (IOException var4) {
         this.au = true;
      }

   }

   private void newLine() {
      try {
         synchronized(this) {
            this.ensureOpen();
            this.av.write(10);
         }
      } catch (IOException var4) {
         this.au = true;
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
      var1 = var1;
      PrintStream var5 = this;

      try {
         synchronized(var5) {
            var5.ensureOpen();
            var5.av.write(var1);
         }
      } catch (IOException var4) {
         var5.au = true;
      }

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
