package java.io;

import com.nokia.mid.impl.isa.i18n.CharsetConv;
import com.nokia.mid.impl.isa.i18n.FixedWidthReader;
import com.nokia.mid.impl.isa.i18n.VariableWidthReader;

public class InputStreamReader extends Reader {
   private Reader jw;

   public InputStreamReader(InputStream var1) {
      try {
         this.a(var1, CharsetConv.defaultEncoding);
      } catch (UnsupportedEncodingException var2) {
         throw new RuntimeException("Missing default encoding " + CharsetConv.defaultEncoding);
      }
   }

   public InputStreamReader(InputStream var1, String var2) throws UnsupportedEncodingException {
      this.a(var1, var2);
   }

   private void a(InputStream var1, String var2) throws UnsupportedEncodingException {
      if (var1 != null && var2 != null) {
         byte[] var3;
         if ((var3 = CharsetConv.isSupportedEncoding(var2)) != null) {
            if (CharsetConv.isFixedSizeEncoding(var3) != 0) {
               this.jw = new FixedWidthReader(var1, var3);
            } else {
               this.jw = new VariableWidthReader(var1, var3);
            }

         } else {
            throw new UnsupportedEncodingException();
         }
      } else {
         throw new NullPointerException();
      }
   }

   private void ensureOpen() throws IOException {
      if (this.jw == null) {
         throw new IOException("Stream closed");
      }
   }

   public int read() throws IOException {
      this.ensureOpen();
      return this.jw.read();
   }

   public int read(char[] var1, int var2, int var3) throws IOException {
      this.ensureOpen();
      if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         return var3 == 0 ? 0 : this.jw.read(var1, var2, var3);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public long skip(long var1) throws IOException {
      this.ensureOpen();
      return this.jw.skip(var1);
   }

   public boolean ready() throws IOException {
      this.ensureOpen();
      return this.jw.ready();
   }

   public boolean markSupported() {
      return this.jw == null ? false : this.jw.markSupported();
   }

   public void mark(int var1) throws IOException {
      this.ensureOpen();
      this.jw.mark(var1);
   }

   public void reset() throws IOException {
      this.ensureOpen();
      this.jw.reset();
   }

   public void close() throws IOException {
      if (this.jw != null) {
         this.jw.close();
         this.jw = null;
      }

   }
}
