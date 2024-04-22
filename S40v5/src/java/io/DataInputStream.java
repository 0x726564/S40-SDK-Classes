package java.io;

public class DataInputStream extends InputStream implements DataInput {
   protected InputStream in;

   public DataInputStream(InputStream var1) {
      this.in = var1;
   }

   public int read() throws IOException {
      return this.in.read();
   }

   public final int read(byte[] var1) throws IOException {
      return this.in.read(var1, 0, var1.length);
   }

   public final int read(byte[] var1, int var2, int var3) throws IOException {
      return this.in.read(var1, var2, var3);
   }

   public final void readFully(byte[] var1) throws IOException {
      this.readFully(var1, 0, var1.length);
   }

   public final void readFully(byte[] var1, int var2, int var3) throws IOException {
      if (var3 < 0) {
         throw new IndexOutOfBoundsException();
      } else {
         int var5;
         for(int var4 = 0; var4 < var3; var4 += var5) {
            if ((var5 = this.read(var1, var2 + var4, var3 - var4)) < 0) {
               throw new EOFException();
            }
         }

      }
   }

   public final int skipBytes(int var1) throws IOException {
      int var2 = 0;

      int var4;
      for(boolean var3 = false; var2 < var1 && (var4 = (int)this.skip((long)(var1 - var2))) > 0; var2 += var4) {
      }

      return var2;
   }

   public final boolean readBoolean() throws IOException {
      int var1;
      if ((var1 = this.read()) < 0) {
         throw new EOFException();
      } else {
         return var1 != 0;
      }
   }

   public final byte readByte() throws IOException {
      int var1;
      if ((var1 = this.read()) < 0) {
         throw new EOFException();
      } else {
         return (byte)var1;
      }
   }

   public final int readUnsignedByte() throws IOException {
      int var1;
      if ((var1 = this.read()) < 0) {
         throw new EOFException();
      } else {
         return var1;
      }
   }

   public final short readShort() throws IOException {
      return (short)this.readUnsignedShort();
   }

   public final int readUnsignedShort() throws IOException {
      int var1 = this.read();
      int var2 = this.read();
      if ((var1 | var2) < 0) {
         throw new EOFException();
      } else {
         return (var1 << 8) + var2;
      }
   }

   public final char readChar() throws IOException {
      return (char)this.readUnsignedShort();
   }

   public final int readInt() throws IOException {
      int var1 = this.read();
      int var2 = this.read();
      int var3 = this.read();
      int var4 = this.read();
      if ((var1 | var2 | var3 | var4) < 0) {
         throw new EOFException();
      } else {
         return (var1 << 24) + (var2 << 16) + (var3 << 8) + var4;
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

   public static final String readUTF(DataInput var0) throws IOException {
      int var1;
      char[] var2 = new char[var1 = var0.readUnsignedShort()];
      byte[] var3 = new byte[var1];
      int var6 = 0;
      int var7 = 0;
      var0.readFully(var3, 0, var1);

      while(var6 < var1) {
         byte var4;
         int var8;
         switch((var8 = var3[var6] & 255) >> 4) {
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
            ++var6;
            var2[var7++] = (char)var8;
            break;
         case 8:
         case 9:
         case 10:
         case 11:
         default:
            throw new UTFDataFormatException();
         case 12:
         case 13:
            var6 += 2;
            if (var6 > var1) {
               throw new UTFDataFormatException();
            }

            if (((var4 = var3[var6 - 1]) & 192) != 128) {
               throw new UTFDataFormatException();
            }

            var2[var7++] = (char)((var8 & 31) << 6 | var4 & 63);
            break;
         case 14:
            var6 += 3;
            if (var6 > var1) {
               throw new UTFDataFormatException();
            }

            var4 = var3[var6 - 2];
            byte var5 = var3[var6 - 1];
            if ((var4 & 192) != 128 || (var5 & 192) != 128) {
               throw new UTFDataFormatException();
            }

            var2[var7++] = (char)((var8 & 15) << 12 | (var4 & 63) << 6 | var5 & 63);
         }
      }

      return new String(var2, 0, var7);
   }

   public long skip(long var1) throws IOException {
      return this.in.skip(var1);
   }

   public int available() throws IOException {
      return this.in.available();
   }

   public void close() throws IOException {
      this.in.close();
   }

   public synchronized void mark(int var1) {
      this.in.mark(var1);
   }

   public synchronized void reset() throws IOException {
      this.in.reset();
   }

   public boolean markSupported() {
      return this.in.markSupported();
   }
}
