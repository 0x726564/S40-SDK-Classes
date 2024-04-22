package java.io;

public class PrintStream extends OutputStream {
   private boolean trouble = false;
   private OutputStreamWriter charOut;
   private OutputStream byteOut;
   private boolean closing = false;

   public PrintStream(OutputStream out) {
      if (out == null) {
         throw new NullPointerException("Null output stream");
      } else {
         this.byteOut = out;
         this.charOut = new OutputStreamWriter(out);
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

   public void write(int b) {
      try {
         synchronized(this) {
            this.ensureOpen();
            this.byteOut.write(b);
         }
      } catch (IOException var5) {
         this.trouble = true;
      }

   }

   public void write(byte[] buf, int off, int len) {
      try {
         synchronized(this) {
            this.ensureOpen();
            this.byteOut.write(buf, off, len);
         }
      } catch (IOException var7) {
         this.trouble = true;
      }

   }

   private void write(char[] buf) {
      try {
         synchronized(this) {
            this.ensureOpen();
            this.charOut.write(buf);
         }
      } catch (IOException var5) {
         this.trouble = true;
      }

   }

   private void write(String s) {
      try {
         synchronized(this) {
            this.ensureOpen();
            this.charOut.write(s);
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

   public void print(boolean b) {
      this.write(b ? "true" : "false");
   }

   public void print(char c) {
      this.write(String.valueOf(c));
   }

   public void print(int i) {
      this.write(String.valueOf(i));
   }

   public void print(long l) {
      this.write(String.valueOf(l));
   }

   public void print(float f) {
      this.write(String.valueOf(f));
   }

   public void print(double d) {
      this.write(String.valueOf(d));
   }

   public void print(char[] s) {
      this.write(s);
   }

   public void print(String s) {
      if (s == null) {
         s = "null";
      }

      this.write(s);
   }

   public void print(Object obj) {
      this.write(String.valueOf(obj));
   }

   public void println() {
      this.newLine();
   }

   public void println(boolean x) {
      synchronized(this) {
         this.print(x);
         this.newLine();
      }
   }

   public void println(char x) {
      synchronized(this) {
         this.print(x);
         this.newLine();
      }
   }

   public void println(int x) {
      synchronized(this) {
         this.print(x);
         this.newLine();
      }
   }

   public void println(long x) {
      synchronized(this) {
         this.print(x);
         this.newLine();
      }
   }

   public void println(float x) {
      synchronized(this) {
         this.print(x);
         this.newLine();
      }
   }

   public void println(double x) {
      synchronized(this) {
         this.print(x);
         this.newLine();
      }
   }

   public void println(char[] x) {
      synchronized(this) {
         this.print(x);
         this.newLine();
      }
   }

   public void println(String x) {
      synchronized(this) {
         this.print(x);
         this.newLine();
      }
   }

   public void println(Object x) {
      synchronized(this) {
         this.print(x);
         this.newLine();
      }
   }
}
