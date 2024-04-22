package java.io;

import com.nokia.mid.impl.isa.i18n.CharsetConv;
import com.nokia.mid.impl.isa.i18n.WriterImpl;

public class OutputStreamWriter extends Writer {
   private Writer writeImpl;

   public OutputStreamWriter(OutputStream var1) {
      try {
         this.initImpl(var1, CharsetConv.defaultEncoding);
      } catch (UnsupportedEncodingException var3) {
         throw new RuntimeException("Missing default encoding " + CharsetConv.defaultEncoding);
      }
   }

   public OutputStreamWriter(OutputStream var1, String var2) throws UnsupportedEncodingException {
      this.initImpl(var1, var2);
   }

   private void initImpl(OutputStream var1, String var2) throws UnsupportedEncodingException {
      if (var1 != null && var2 != null) {
         byte[] var3 = CharsetConv.isSupportedEncoding(var2);
         if (var3 != null) {
            this.writeImpl = new WriterImpl(var1, var3);
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

   public void write(int var1) throws IOException {
      this.ensureOpen();
      this.writeImpl.write(var1);
   }

   public void write(char[] var1, int var2, int var3) throws IOException {
      this.ensureOpen();
      if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         if (var3 != 0) {
            this.writeImpl.write(var1, var2, var3);
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void write(String var1, int var2, int var3) throws IOException {
      this.ensureOpen();
      if (var2 >= 0 && var2 <= var1.length() && var3 >= 0 && var2 + var3 <= var1.length() && var2 + var3 >= 0) {
         if (var3 != 0) {
            this.writeImpl.write(var1, var2, var3);
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
