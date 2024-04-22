package com.nokia.mid.impl.isa.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

public abstract class ReaderImpl extends Reader {
   protected short[] nativeConvInfo;
   protected int maxByteLen;
   protected InputStream in;
   protected static final char REPL_CHAR = '�';
   protected static final int RETURN_CODE_TOO_SMALL = -2;
   protected static final int RETURN_CODE_ILL_SEQ = -1;
   protected static final int RETURN_CODE_SKIP = 0;

   ReaderImpl() {
   }

   ReaderImpl(InputStream var1, byte[] var2) throws UnsupportedEncodingException {
      this.in = var1;
      this.nativeConvInfo = CharsetConv.initConv(var2, 'R');
      if (this.nativeConvInfo == null) {
         throw new UnsupportedEncodingException();
      } else {
         this.maxByteLen = CharsetConv.getMaxByteLength(var2);
         if (this.maxByteLen == 0) {
            throw new UnsupportedEncodingException();
         }
      }
   }

   public boolean ready() {
      try {
         return this.in.available() > 0;
      } catch (IOException var2) {
         return false;
      }
   }

   public void reset() throws IOException {
      this.in.reset();
   }

   public void close() throws IOException {
      this.in.close();
   }
}
