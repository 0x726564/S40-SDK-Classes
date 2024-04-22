package java.io;

import com.nokia.mid.impl.isa.i18n.CharsetConv;
import com.nokia.mid.impl.isa.i18n.FixedWidthReader;
import com.nokia.mid.impl.isa.i18n.VariableWidthReader;

public class InputStreamReader extends Reader {
   private Reader readImpl;

   public InputStreamReader(InputStream is) {
      try {
         this.initImpl(is, CharsetConv.defaultEncoding);
      } catch (UnsupportedEncodingException var3) {
         throw new RuntimeException("Missing default encoding " + CharsetConv.defaultEncoding);
      }
   }

   public InputStreamReader(InputStream is, String enc) throws UnsupportedEncodingException {
      this.initImpl(is, enc);
   }

   private void initImpl(InputStream is, String enc) throws UnsupportedEncodingException {
      if (is != null && enc != null) {
         byte[] encoding = CharsetConv.isSupportedEncoding(enc);
         if (encoding != null) {
            if (CharsetConv.isFixedSizeEncoding(encoding) != 0) {
               this.readImpl = new FixedWidthReader(is, encoding);
            } else {
               this.readImpl = new VariableWidthReader(is, encoding);
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

   public int read(char[] cbuf, int off, int len) throws IOException {
      this.ensureOpen();
      if (off >= 0 && off <= cbuf.length && len >= 0 && off + len <= cbuf.length && off + len >= 0) {
         return len == 0 ? 0 : this.readImpl.read(cbuf, off, len);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public long skip(long n) throws IOException {
      this.ensureOpen();
      return this.readImpl.skip(n);
   }

   public boolean ready() throws IOException {
      this.ensureOpen();
      return this.readImpl.ready();
   }

   public boolean markSupported() {
      return this.readImpl == null ? false : this.readImpl.markSupported();
   }

   public void mark(int readAheadLimit) throws IOException {
      this.ensureOpen();
      this.readImpl.mark(readAheadLimit);
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
