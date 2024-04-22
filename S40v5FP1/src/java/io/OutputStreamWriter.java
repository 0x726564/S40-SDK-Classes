package java.io;

import com.nokia.mid.impl.isa.i18n.CharsetConv;
import com.nokia.mid.impl.isa.i18n.WriterImpl;

public class OutputStreamWriter extends Writer {
   private Writer writeImpl;

   public OutputStreamWriter(OutputStream os) {
      try {
         this.initImpl(os, CharsetConv.defaultEncoding);
      } catch (UnsupportedEncodingException var3) {
         throw new RuntimeException("Missing default encoding " + CharsetConv.defaultEncoding);
      }
   }

   public OutputStreamWriter(OutputStream os, String enc) throws UnsupportedEncodingException {
      this.initImpl(os, enc);
   }

   private void initImpl(OutputStream os, String enc) throws UnsupportedEncodingException {
      if (os != null && enc != null) {
         byte[] encoding = CharsetConv.isSupportedEncoding(enc);
         if (encoding != null) {
            this.writeImpl = new WriterImpl(os, encoding);
         } else {
            throw new UnsupportedEncodingException();
         }
      } else {
         throw new NullPointerException();
      }
   }

   private void ensureOpen() throws IOException {
      if (this.writeImpl == null) {
         throw new IOException("Stream closed");
      }
   }

   public void write(int c) throws IOException {
      this.ensureOpen();
      this.writeImpl.write(c);
   }

   public void write(char[] cbuf, int off, int len) throws IOException {
      this.ensureOpen();
      if (off >= 0 && off <= cbuf.length && len >= 0 && off + len <= cbuf.length && off + len >= 0) {
         if (len != 0) {
            this.writeImpl.write(cbuf, off, len);
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void write(String str, int off, int len) throws IOException {
      this.ensureOpen();
      if (off >= 0 && off <= str.length() && len >= 0 && off + len <= str.length() && off + len >= 0) {
         if (len != 0) {
            this.writeImpl.write(str, off, len);
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void flush() throws IOException {
      this.ensureOpen();
      this.writeImpl.flush();
   }

   public void close() throws IOException {
      if (this.writeImpl != null) {
         this.writeImpl.close();
         this.writeImpl = null;
      }

   }
}
