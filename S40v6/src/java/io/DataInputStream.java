package java.io;

public class DataInputStream extends InputStream implements DataInput {
   protected InputStream in;

   public DataInputStream(InputStream in) {
      this.in = in;
   }

   public int read() throws IOException {
      return this.in.read();
   }

   public final int read(byte[] b) throws IOException {
      return this.in.read(b, 0, b.length);
   }

   public final int read(byte[] b, int off, int len) throws IOException {
      return this.in.read(b, off, len);
   }

   public final void readFully(byte[] b) throws IOException {
      this.readFully(b, 0, b.length);
   }

   public final void readFully(byte[] b, int off, int len) throws IOException {
      if (len < 0) {
         throw new IndexOutOfBoundsException();
      } else {
         int count;
         for(int n = 0; n < len; n += count) {
            count = this.read(b, off + n, len - n);
            if (count < 0) {
               throw new EOFException();
            }
         }

      }
   }

   public final int skipBytes(int n) throws IOException {
      int total = 0;

      int cur;
      for(boolean var3 = false; total < n && (cur = (int)this.skip((long)(n - total))) > 0; total += cur) {
      }

      return total;
   }

   public final boolean readBoolean() throws IOException {
      int ch = this.read();
      if (ch < 0) {
         throw new EOFException();
      } else {
         return ch != 0;
      }
   }

   public final byte readByte() throws IOException {
      int ch = this.read();
      if (ch < 0) {
         throw new EOFException();
      } else {
         return (byte)ch;
      }
   }

   public final int readUnsignedByte() throws IOException {
      int ch = this.read();
      if (ch < 0) {
         throw new EOFException();
      } else {
         return ch;
      }
   }

   public final short readShort() throws IOException {
      return (short)this.readUnsignedShort();
   }

   public final int readUnsignedShort() throws IOException {
      int ch1 = this.read();
      int ch2 = this.read();
      if ((ch1 | ch2) < 0) {
         throw new EOFException();
      } else {
         return (ch1 << 8) + (ch2 << 0);
      }
   }

   public final char readChar() throws IOException {
      return (char)this.readUnsignedShort();
   }

   public final int readInt() throws IOException {
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

   public final long readLong() throws IOException {
      return ((long)this.readInt() << 32) + ((long)this.readInt() & 4294967295L);
   }

   public final float readFloat() throws IOException {
      return Float.intBitsToFloat(this.readInt());
   }

   public final double readDouble() throws IOException {
      return Double.longBitsToDouble(this.readLong());
   }

   public final String readUTF() throws IOException {
      return readUTF(this);
   }

   public static final String readUTF(DataInput in) throws IOException {
      int utflen = in.readUnsignedShort();
      char[] str = new char[utflen];
      byte[] bytearr = new byte[utflen];
      int count = 0;
      int strlen = 0;
      in.readFully(bytearr, 0, utflen);

      while(count < utflen) {
         int c = bytearr[count] & 255;
         byte char2;
         switch(c >> 4) {
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
            ++count;
            str[strlen++] = (char)c;
            break;
         case 8:
         case 9:
         case 10:
         case 11:
         default:
            throw new UTFDataFormatException();
         case 12:
         case 13:
            count += 2;
            if (count > utflen) {
               throw new UTFDataFormatException();
            }

            char2 = bytearr[count - 1];
            if ((char2 & 192) != 128) {
               throw new UTFDataFormatException();
            }

            str[strlen++] = (char)((c & 31) << 6 | char2 & 63);
            break;
         case 14:
            count += 3;
            if (count > utflen) {
               throw new UTFDataFormatException();
            }

            char2 = bytearr[count - 2];
            int char3 = bytearr[count - 1];
            if ((char2 & 192) != 128 || (char3 & 192) != 128) {
               throw new UTFDataFormatException();
            }

            str[strlen++] = (char)((c & 15) << 12 | (char2 & 63) << 6 | (char3 & 63) << 0);
         }
      }

      return new String(str, 0, strlen);
   }

   public long skip(long n) throws IOException {
      return this.in.skip(n);
   }

   public int available() throws IOException {
      return this.in.available();
   }

   public void close() throws IOException {
      this.in.close();
   }

   public synchronized void mark(int readlimit) {
      this.in.mark(readlimit);
   }

   public synchronized void reset() throws IOException {
      this.in.reset();
   }

   public boolean markSupported() {
      return this.in.markSupported();
   }
}
