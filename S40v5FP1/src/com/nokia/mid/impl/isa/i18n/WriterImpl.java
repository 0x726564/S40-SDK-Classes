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

   public WriterImpl(OutputStream os, byte[] enc) throws UnsupportedEncodingException {
      this.out = os;
      this.encoding = enc;
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

   public synchronized void write(int c) throws IOException {
      char[] cbuf = new char[]{(char)c};
      int len = CharsetConv.charToByte(this.nativeConvInfo, cbuf, 0, this.buf, 0, this.buf.length);
      if (len > 0) {
         this.out.write(this.buf, 0, len);
      }

   }

   public synchronized void write(char[] cbuf, int off, int len) throws IOException {
      int maxlen = len * this.maxByteLen;
      if (this.buf.length < maxlen) {
         this.buf = new byte[maxlen];
      }

      int l = CharsetConv.charArrayToByte(this.encoding, cbuf, off, len, this.buf, 0, this.buf.length);
      if (l > 0) {
         this.out.write(this.buf, 0, l);
      }

      if (this.buf.length > this.maxByteLen) {
         this.buf = new byte[this.maxByteLen];
      }

   }

   public synchronized void write(String str, int off, int len) throws IOException {
      char[] dst = new char[len];
      str.getChars(off, off + len, dst, 0);
      this.write((char[])dst, 0, len);
   }

   public void flush() throws IOException {
      int len = this.flushStream(this.nativeConvInfo, this.buf, this.buf.length);
      if (len > 0) {
         this.out.write(this.buf, 0, len);
      }

      this.out.flush();
   }

   public void close() throws IOException {
      this.flush();
      this.out.close();
   }

   private native int flushStream(short[] var1, byte[] var2, int var3);
}
