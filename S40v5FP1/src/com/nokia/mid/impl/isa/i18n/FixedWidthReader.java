package com.nokia.mid.impl.isa.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class FixedWidthReader extends ReaderImpl {
   private byte[] encoding;

   FixedWidthReader() {
   }

   public FixedWidthReader(InputStream in, byte[] enc) throws UnsupportedEncodingException {
      super(in, enc);
      this.encoding = enc;
   }

   public void mark(int readAheadLimit) throws IOException {
      if (this.in.markSupported()) {
         this.in.mark(readAheadLimit * this.maxByteLen);
      } else {
         throw new IOException("mark() not supported");
      }
   }

   public boolean markSupported() {
      return this.in.markSupported();
   }

   public synchronized int read() throws IOException {
      char[] c = new char[1];
      byte[] buf = new byte[this.maxByteLen];

      int returnCode;
      for(returnCode = 0; returnCode == 0; returnCode = CharsetConv.byteToChar(this.nativeConvInfo, buf, 0, this.maxByteLen, c, 0)) {
         if (this.in.read(buf) == -1) {
            return -1;
         }
      }

      return returnCode > 0 ? c[0] : '�';
   }

   public synchronized int read(char[] cbuf, int off, int len) throws IOException {
      byte[] buf = new byte[this.maxByteLen * len];
      int nbRead = this.in.read(buf);
      if (nbRead == -1) {
         return -1;
      } else {
         int ret = CharsetConv.byteToCharArray(this.encoding, buf, 0, nbRead, cbuf, off, len);
         if (ret < 0) {
            throw new IOException("Problem during buffer conversion");
         } else {
            return ret;
         }
      }
   }

   public long skip(long n) throws IOException {
      if (n < 0L) {
         throw new IllegalArgumentException("skip value is negative");
      } else {
         n = Math.min(n, 8192L);
         long nByteSkipped = this.in.skip(n * (long)this.maxByteLen);
         return nByteSkipped / (long)this.maxByteLen;
      }
   }
}
