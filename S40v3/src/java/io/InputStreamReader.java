package java.io;

import com.nokia.mid.impl.isa.i18n.CharsetConv;
import com.nokia.mid.impl.isa.i18n.FixedWidthReader;
import com.nokia.mid.impl.isa.i18n.VariableWidthReader;

public class InputStreamReader extends Reader {
   private Reader readImpl;

   public InputStreamReader(InputStream var1) {
      try {
         this.initImpl(var1, CharsetConv.defaultEncoding);
      } catch (UnsupportedEncodingException var3) {
         throw new RuntimeException("Missing default encoding " + CharsetConv.defaultEncoding);
      }
   }

   public InputStreamReader(InputStream var1, String var2) throws UnsupportedEncodingException {
      this.initImpl(var1, var2);
   }

   private void initImpl(InputStream var1, String var2) throws UnsupportedEncodingException {
      if (var1 != null && var2 != null) {
         byte[] var3 = CharsetConv.isSupportedEncoding(var2);
         if (var3 != null) {
            if (CharsetConv.isFixedSizeEncoding(var3) != 0) {
               this.readImpl = new FixedWidthReader(var1, var3);
            } else {
               this.readImpl = new VariableWidthReader(var1, var3);
            }

         } else {
            throw new UnsupportedEncodingException();
         }
      } else {
         throw new NullPointerException();
      }
   }

   private void ensureOpen() throws IOException {
      if (this.readImpl == null) {
         throw new IOException("Stream closed");
      }
   }

   public int read() throws IOException {
      this.ensureOpen();
      return this.readImpl.read();
   }

   public int read(char[] var1, int var2, int var3) throws IOException {
      this.ensureOpen();
      if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         return var3 == 0 ? 0 : this.readImpl.read(var1, var2, var3);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public long skip(long var1) throws IOException {
      this.ensureOpen();
      return this.readImpl.skip(var1);
   }

   public boolean ready() throws IOException {
      this.ensureOpen();
      return this.readImpl.ready();
   }

   public boolean markSupported() {
      return this.readImpl == null ? false : this.readImpl.markSupported();
   }

   public void mark(int var1) throws IOException {
      this.ensureOpen();
      this.readImpl.mark(var1);
   }

   public void reset() throws IOException {
      this.ensureOpen();
      this.readImpl.reset();
   }

   public void close() throws IOException {
      if (this.readImpl != null) {
         this.readImpl.close();
         this.readImpl = null;
      }

   }
}
