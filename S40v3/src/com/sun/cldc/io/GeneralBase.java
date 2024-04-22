package com.sun.cldc.io;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.io.UTFDataFormatException;

public abstract class GeneralBase implements DataInput, DataOutput {
   public void write(int var1) throws IOException {
      throw new RuntimeException("No write()");
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         if (var3 != 0) {
            for(int var4 = 0; var4 < var3; ++var4) {
               this.write(var1[var2 + var4]);
            }

         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public final void write(byte[] var1) throws IOException {
      this.write(var1, 0, var1.length);
   }

   public void writeBoolean(boolean var1) throws IOException {
      this.write(var1 ? 1 : 0);
   }

   public void writeByte(int var1) throws IOException {
      this.write(var1);
   }

   public void writeShort(int var1) throws IOException {
      this.write(var1 >>> 8 & 255);
      this.write(var1 >>> 0 & 255);
   }

   public void writeChar(int var1) throws IOException {
      this.write(var1 >>> 8 & 255);
      this.write(var1 >>> 0 & 255);
   }

   public void writeInt(int var1) throws IOException {
      this.write(var1 >>> 24 & 255);
      this.write(var1 >>> 16 & 255);
      this.write(var1 >>> 8 & 255);
      this.write(var1 >>> 0 & 255);
   }

   public void writeLong(long var1) throws IOException {
      this.write((int)(var1 >>> 56) & 255);
      this.write((int)(var1 >>> 48) & 255);
      this.write((int)(var1 >>> 40) & 255);
      this.write((int)(var1 >>> 32) & 255);
      this.write((int)(var1 >>> 24) & 255);
      this.write((int)(var1 >>> 16) & 255);
      this.write((int)(var1 >>> 8) & 255);
      this.write((int)(var1 >>> 0) & 255);
   }

   public void writeBytes(String var1) throws IOException {
      throw new RuntimeException("Function not supported");
   }

   public void writeChars(String var1) throws IOException {
      int var2 = var1.length();

      for(int var3 = 0; var3 < var2; ++var3) {
         char var4 = var1.charAt(var3);
         this.write(var4 >>> 8 & 255);
         this.write(var4 >>> 0 & 255);
      }

   }

   public void writeUTF(String var1) throws IOException {
      writeUTF(var1, this);
   }

   public static int writeUTF(String var0, DataOutput var1) throws IOException {
      int var2 = var0.length();
      int var3 = 0;
      char[] var4 = new char[var2];
      byte var6 = 0;
      var0.getChars(0, var2, var4, 0);

      char var5;
      for(int var7 = 0; var7 < var2; ++var7) {
         var5 = var4[var7];
         if (var5 >= 1 && var5 <= 127) {
            ++var3;
         } else if (var5 > 2047) {
            var3 += 3;
         } else {
            var3 += 2;
         }
      }

      if (var3 > 65535) {
         throw new UTFDataFormatException();
      } else {
         byte[] var10 = new byte[var3 + 2];
         int var9 = var6 + 1;
         var10[var6] = (byte)(var3 >>> 8 & 255);
         var10[var9++] = (byte)(var3 >>> 0 & 255);

         for(int var8 = 0; var8 < var2; ++var8) {
            var5 = var4[var8];
            if (var5 >= 1 && var5 <= 127) {
               var10[var9++] = (byte)var5;
            } else if (var5 > 2047) {
               var10[var9++] = (byte)(224 | var5 >> 12 & 15);
               var10[var9++] = (byte)(128 | var5 >> 6 & 63);
               var10[var9++] = (byte)(128 | var5 >> 0 & 63);
            } else {
               var10[var9++] = (byte)(192 | var5 >> 6 & 31);
               var10[var9++] = (byte)(128 | var5 >> 0 & 63);
            }
         }

         var1.write(var10);
         return var3 + 2;
      }
   }

   public void flush() throws IOException {
   }

   public void close() throws IOException {
   }

   public int read() throws IOException {
      throw new RuntimeException("No read()");
   }

   public long skip(long var1) throws IOException {
      long var3;
      for(var3 = var1; var3 > 0L && this.read() >= 0; --var3) {
      }

      return var1 - var3;
   }

   public void readFully(byte[] var1) throws IOException {
      this.readFully(var1, 0, var1.length);
   }

   public void readFully(byte[] var1, int var2, int var3) throws IOException {
      if (var3 < 0) {
         throw new IndexOutOfBoundsException();
      } else {
         int var5;
         for(int var4 = 0; var4 < var3; var1[var2 + var4++] = (byte)var5) {
            var5 = this.read();
            if (var5 < 0) {
               throw new EOFException();
            }
         }

      }
   }

   public int skipBytes(int var1) throws IOException {
      int var2 = 0;

      int var4;
      for(boolean var3 = false; var2 < var1 && (var4 = (int)this.skip((long)(var1 - var2))) > 0; var2 += var4) {
      }

      return var2;
   }

   public boolean readBoolean() throws IOException {
      int var1 = this.read();
      if (var1 < 0) {
         throw new EOFException();
      } else {
         return var1 != 0;
      }
   }

   public byte readByte() throws IOException {
      int var1 = this.read();
      if (var1 < 0) {
         throw new EOFException();
      } else {
         return (byte)var1;
      }
   }

   public int readUnsignedByte() throws IOException {
      int var1 = this.read();
      if (var1 < 0) {
         throw new EOFException();
      } else {
         return var1;
      }
   }

   public short readShort() throws IOException {
      int var1 = this.read();
      int var2 = this.read();
      if ((var1 | var2) < 0) {
         throw new EOFException();
      } else {
         return (short)((var1 << 8) + (var2 << 0));
      }
   }

   public int readUnsignedShort() throws IOException {
      int var1 = this.read();
      int var2 = this.read();
      if ((var1 | var2) < 0) {
         throw new EOFException();
      } else {
         return (var1 << 8) + (var2 << 0);
      }
   }

   public char readChar() throws IOException {
      int var1 = this.read();
      int var2 = this.read();
      if ((var1 | var2) < 0) {
         throw new EOFException();
      } else {
         return (char)((var1 << 8) + (var2 << 0));
      }
   }

   public int readInt() throws IOException {
      int var1 = this.read();
      int var2 = this.read();
      int var3 = this.read();
      int var4 = this.read();
      if ((var1 | var2 | var3 | var4) < 0) {
         throw new EOFException();
      } else {
         return (var1 << 24) + (var2 << 16) + (var3 << 8) + (var4 << 0);
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

   public final void writeFloat(float var1) throws IOException {
      this.writeInt(Float.floatToIntBits(var1));
   }

   public final void writeDouble(double var1) throws IOException {
      this.writeLong(Double.doubleToLongBits(var1));
   }
}
