package com.sun.cldc.io;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.io.UTFDataFormatException;

public abstract class GeneralBase implements DataInput, DataOutput {
   public void write(int b) throws IOException {
      throw new RuntimeException("No write()");
   }

   public void write(byte[] b, int off, int len) throws IOException {
      if (b == null) {
         throw new NullPointerException();
      } else if (off >= 0 && off <= b.length && len >= 0 && off + len <= b.length && off + len >= 0) {
         if (len != 0) {
            for(int i = 0; i < len; ++i) {
               this.write(b[off + i]);
            }

         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public final void write(byte[] b) throws IOException {
      this.write(b, 0, b.length);
   }

   public void writeBoolean(boolean v) throws IOException {
      this.write(v ? 1 : 0);
   }

   public void writeByte(int v) throws IOException {
      this.write(v);
   }

   public void writeShort(int v) throws IOException {
      this.write(v >>> 8 & 255);
      this.write(v >>> 0 & 255);
   }

   public void writeChar(int v) throws IOException {
      this.write(v >>> 8 & 255);
      this.write(v >>> 0 & 255);
   }

   public void writeInt(int v) throws IOException {
      this.write(v >>> 24 & 255);
      this.write(v >>> 16 & 255);
      this.write(v >>> 8 & 255);
      this.write(v >>> 0 & 255);
   }

   public void writeLong(long v) throws IOException {
      this.write((int)(v >>> 56) & 255);
      this.write((int)(v >>> 48) & 255);
      this.write((int)(v >>> 40) & 255);
      this.write((int)(v >>> 32) & 255);
      this.write((int)(v >>> 24) & 255);
      this.write((int)(v >>> 16) & 255);
      this.write((int)(v >>> 8) & 255);
      this.write((int)(v >>> 0) & 255);
   }

   public void writeBytes(String s) throws IOException {
      throw new RuntimeException("Function not supported");
   }

   public void writeChars(String s) throws IOException {
      int len = s.length();

      for(int i = 0; i < len; ++i) {
         int v = s.charAt(i);
         this.write(v >>> 8 & 255);
         this.write(v >>> 0 & 255);
      }

   }

   public void writeUTF(String str) throws IOException {
      writeUTF(str, this);
   }

   public static int writeUTF(String str, DataOutput out) throws IOException {
      int strlen = str.length();
      int utflen = 0;
      char[] charr = new char[strlen];
      int count = 0;
      str.getChars(0, strlen, charr, 0);

      char c;
      for(int i = 0; i < strlen; ++i) {
         c = charr[i];
         if (c >= 1 && c <= 127) {
            ++utflen;
         } else if (c > 2047) {
            utflen += 3;
         } else {
            utflen += 2;
         }
      }

      if (utflen > 65535) {
         throw new UTFDataFormatException();
      } else {
         byte[] bytearr = new byte[utflen + 2];
         int var9 = count + 1;
         bytearr[count] = (byte)(utflen >>> 8 & 255);
         bytearr[var9++] = (byte)(utflen >>> 0 & 255);

         for(int i = 0; i < strlen; ++i) {
            c = charr[i];
            if (c >= 1 && c <= 127) {
               bytearr[var9++] = (byte)c;
            } else if (c > 2047) {
               bytearr[var9++] = (byte)(224 | c >> 12 & 15);
               bytearr[var9++] = (byte)(128 | c >> 6 & 63);
               bytearr[var9++] = (byte)(128 | c >> 0 & 63);
            } else {
               bytearr[var9++] = (byte)(192 | c >> 6 & 31);
               bytearr[var9++] = (byte)(128 | c >> 0 & 63);
            }
         }

         out.write(bytearr);
         return utflen + 2;
      }
   }

   public void flush() throws IOException {
   }

   public void close() throws IOException {
   }

   public int read() throws IOException {
      throw new RuntimeException("No read()");
   }

   public long skip(long n) throws IOException {
      long m;
      for(m = n; m > 0L && this.read() >= 0; --m) {
      }

      return n - m;
   }

   public void readFully(byte[] b) throws IOException {
      this.readFully(b, 0, b.length);
   }

   public void readFully(byte[] b, int off, int len) throws IOException {
      if (len < 0) {
         throw new IndexOutOfBoundsException();
      } else {
         int ch;
         for(int n = 0; n < len; b[off + n++] = (byte)ch) {
            ch = this.read();
            if (ch < 0) {
               throw new EOFException();
            }
         }

      }
   }

   public int skipBytes(int n) throws IOException {
      int total = 0;

      int cur;
      for(boolean var3 = false; total < n && (cur = (int)this.skip((long)(n - total))) > 0; total += cur) {
      }

      return total;
   }

   public boolean readBoolean() throws IOException {
      int ch = this.read();
      if (ch < 0) {
         throw new EOFException();
      } else {
         return ch != 0;
      }
   }

   public byte readByte() throws IOException {
      int ch = this.read();
      if (ch < 0) {
         throw new EOFException();
      } else {
         return (byte)ch;
      }
   }

   public int readUnsignedByte() throws IOException {
      int ch = this.read();
      if (ch < 0) {
         throw new EOFException();
      } else {
         return ch;
      }
   }

   public short readShort() throws IOException {
      int ch1 = this.read();
      int ch2 = this.read();
      if ((ch1 | ch2) < 0) {
         throw new EOFException();
      } else {
         return (short)((ch1 << 8) + (ch2 << 0));
      }
   }

   public int readUnsignedShort() throws IOException {
      int ch1 = this.read();
      int ch2 = this.read();
      if ((ch1 | ch2) < 0) {
         throw new EOFException();
      } else {
         return (ch1 << 8) + (ch2 << 0);
      }
   }

   public char readChar() throws IOException {
      int ch1 = this.read();
      int ch2 = this.read();
      if ((ch1 | ch2) < 0) {
         throw new EOFException();
      } else {
         return (char)((ch1 << 8) + (ch2 << 0));
      }
   }

   public int readInt() throws IOException {
      int ch1 = this.read();
      int ch2 = this.read();
      int ch3 = this.read();
      int ch4 = this.read();
      if ((ch1 | ch2 | ch3 | ch4) < 0) {
         throw new EOFException();
      } else {
         return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
      }
   }

   public long readLong() throws IOException {
      return ((long)this.readInt() << 32) + ((long)this.readInt() & 4294967295L);
   }

   public String readUTF() throws IOException {
      return DataInputStream.readUTF(this);
   }

   public String readLine() throws IOException {
      throw new RuntimeException("Function not supported");
   }

   public final float readFloat() throws IOException {
      return Float.intBitsToFloat(this.readInt());
   }

   public final double readDouble() throws IOException {
      return Double.longBitsToDouble(this.readLong());
   }

   public final void writeFloat(float v) throws IOException {
      this.writeInt(Float.floatToIntBits(v));
   }

   public final void writeDouble(double v) throws IOException {
      this.writeLong(Double.doubleToLongBits(v));
   }
}
