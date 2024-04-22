package com.nokia.mid.impl.isa.i18n;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class WriterImpl extends Writer {
   protected short[] nativeConvInfo;
   protected byte[] encoding;
   protected int maxByteLen;
   protected OutputStream out;
   private byte[] buf;

   WriterImpl() {
   }

   public WriterImpl(OutputStream var1, byte[] var2) throws UnsupportedEncodingException {
      this.out = var1;
      this.encoding = var2;
      this.nativeConvInfo = CharsetConv.initConv(this.encoding, 'W');
      if (this.nativeConvInfo == null) {
         throw new UnsupportedEncodingException();
      } else {
         this.maxByteLen = CharsetConv.getMaxByteLength(this.encoding);
         if (this.maxByteLen == 0) {
            throw new UnsupportedEncodingException();
         } else {
            this.buf = new byte[this.maxByteLen];
         }
      }
   }

   public synchronized void write(int var1) throws IOException {
      char[] var2 = new char[]{(char)var1};
      int var3 = CharsetConv.charToByte(this.nativeConvInfo, var2, 0, this.buf, 0, this.buf.length);
      if (var3 > 0) {
         this.out.write(this.buf, 0, var3);
      }

   }

   public synchronized void write(char[] var1, int var2, int var3) throws IOException {
      int var4 = var3 * this.maxByteLen;
      if (this.buf.length < var4) {
         this.buf = new byte[var4];
      }

      int var5 = CharsetConv.charArrayToByte(this.encoding, var1, var2, var3, this.buf, 0, this.buf.length);
      if (var5 > 0) {
         this.out.write(this.buf, 0, var5);
      }

      if (this.buf.length > this.maxByteLen) {
         this.buf = new byte[this.maxByteLen];
      }

   }

   public synchronized void write(String var1, int var2, int var3) throws IOException {
      char[] var4 = new char[var3];
      var1.getChars(var2, var2 + var3, var4, 0);
      this.write((char[])var4, 0, var3);
   }

   public void flush() throws IOException {
      int var1 = this.flushStream(this.nativeConvInfo, this.buf, this.buf.length);
      if (var1 > 0) {
         this.out.write(this.buf, 0, var1);
      }

      this.out.flush();
   }

   public void close() throws IOException {
      this.flush();
      this.out.close();
   }

   private native int flushStream(short[] var1, byte[] var2, int var3);
}
